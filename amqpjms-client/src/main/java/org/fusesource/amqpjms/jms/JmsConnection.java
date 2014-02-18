/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.amqpjms.jms;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.net.ssl.SSLContext;

import org.fusesource.amqpjms.jms.exceptions.JmsConnectionFailedException;
import org.fusesource.amqpjms.jms.exceptions.JmsExceptionSupport;
import org.fusesource.amqpjms.jms.meta.JmsConnectionId;
import org.fusesource.amqpjms.jms.meta.JmsConnectionInfo;
import org.fusesource.amqpjms.jms.meta.JmsResource;
import org.fusesource.amqpjms.jms.meta.JmsSessionId;
import org.fusesource.amqpjms.jms.util.IdGenerator;
import org.fusesource.amqpjms.jms.util.ThreadPoolUtils;
import org.fusesource.amqpjms.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a JMS Connection
 */
public class JmsConnection implements Connection, TopicConnection, QueueConnection {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConnection.class);

    private JmsConnectionInfo connectionInfo;

    private final IdGenerator clientIdGenerator;
    private boolean clientIdSet;
    private ExceptionListener exceptionListener;
    private final List<JmsSession> sessions = new CopyOnWriteArrayList<JmsSession>();
    private final AtomicBoolean connected = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean failed = new AtomicBoolean();
    private final Object connectLock = new Object();
    private IOException firstFailureError;
    private JmsPrefetchPolicy prefetchPolicy = new JmsPrefetchPolicy();

    private final ThreadPoolExecutor executor;

    private URI brokerURI;
    private URI localURI;
    private SSLContext sslContext;
    private Provider provider;

    private final AtomicLong sessionIdGenerator = new AtomicLong();

    protected JmsConnection(String connectionId, Provider provider, IdGenerator clientIdGenerator) throws JMSException {

        // This executor can be used for dispatching asynchronous tasks that might block or result
        // in reentrant calls to this Connection that could block.  The thread in this executor
        // will also serve as a means of preventing JVM shutdown should a client application
        // not have it's own mechanism for doing so.
        executor = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "AmqpJMS Connection Executor: ");
                return thread;
            }
        });

        this.provider = provider;
        this.clientIdGenerator = clientIdGenerator;
        this.connectionInfo = new JmsConnectionInfo(new JmsConnectionId(connectionId));
    }

    /**
     * @throws JMSException
     * @see javax.jms.Connection#close()
     */
    @Override
    public synchronized void close() throws JMSException {
        if (closed.compareAndSet(false, true)) {
            try {
                for (Session s : this.sessions) {
                    s.close();
                }
                this.sessions.clear();
                if (provider != null) {
                    provider.close();
                    provider = null;
                }
            } catch (Exception e) {
                throw JmsExceptionSupport.create(e);
            } finally {
                try {
                    if (executor != null) {
                        ThreadPoolUtils.shutdown(executor);
                    }
                } catch (Throwable e) {
                    LOG.warn("Error shutting down thread pool: " + executor + ". This exception will be ignored.", e);
                }
            }
        }
    }

    /**
     * @param destination
     * @param messageSelector
     * @param sessionPool
     * @param maxMessages
     * @return ConnectionConsumer
     * @throws JMSException
     * @see javax.jms.Connection#createConnectionConsumer(javax.jms.Destination,
     *      java.lang.String, javax.jms.ServerSessionPool, int)
     */
    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector,
                                                       ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        connect();
        throw new JMSException("Not supported");
    }

    /**
     * @param topic
     * @param subscriptionName
     * @param messageSelector
     * @param sessionPool
     * @param maxMessages
     * @return ConnectionConsumer
     * @throws JMSException
     * @see javax.jms.Connection#createDurableConnectionConsumer(javax.jms.Topic,
     *      java.lang.String, java.lang.String, javax.jms.ServerSessionPool,
     *      int)
     */
    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName,
                                                              String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        connect();
        throw new JMSException("Not supported");
    }

    /**
     * @param transacted
     * @param acknowledgeMode
     * @return Session
     * @throws JMSException
     * @see javax.jms.Connection#createSession(boolean, int)
     */
    @Override
    public synchronized Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        connect();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        JmsSession result = new JmsSession(this, getNextSessionId(), ackMode);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    /**
     * @return clientId
     * @see javax.jms.Connection#getClientID()
     */
    @Override
    public String getClientID() {
        return this.connectionInfo.getClientId();
    }

    /**
     * @return connectionInfoData
     * @see javax.jms.Connection#getMetaData()
     */
    @Override
    public ConnectionMetaData getMetaData() {
        return JmsConnectionMetaData.INSTANCE;
    }

    /**
     * @param clientID
     * @throws JMSException
     * @see javax.jms.Connection#setClientID(java.lang.String)
     */
    @Override
    public synchronized void setClientID(String clientID) throws JMSException {
        if (this.clientIdSet) {
            throw new IllegalStateException("The clientID has already been set");
        }
        if (clientID == null) {
            throw new IllegalStateException("Cannot have a null clientID");
        }
        if (connected.get()) {
            throw new IllegalStateException("Cannot set the client id once connected.");
        }
        this.connectionInfo.setClientId(clientID);
        this.clientIdSet = true;
    }

    /**
     * @throws JMSException
     * @see javax.jms.Connection#start()
     */
    @Override
    public void start() throws JMSException {
        checkClosed();
        connect();
        if (this.started.compareAndSet(false, true)) {
            try {
                for (JmsSession s : this.sessions) {
                    s.start();
                }
            } catch (Exception e) {
                throw JmsExceptionSupport.create(e);
            }
        }
    }

    /**
     * @throws JMSException
     * @see javax.jms.Connection#stop()
     */
    @Override
    public void stop() throws JMSException {
        checkClosed();
        connect();
        if (this.started.compareAndSet(true, false)) {
            try {
                for (JmsSession s : this.sessions) {
                    s.stop();
                }
            } catch (Exception e) {
                throw JmsExceptionSupport.create(e);
            }
        }
    }

    /**
     * @param topic
     * @param messageSelector
     * @param sessionPool
     * @param maxMessages
     * @return ConnectionConsumer
     * @throws JMSException
     * @see javax.jms.TopicConnection#createConnectionConsumer(javax.jms.Topic,
     *      java.lang.String, javax.jms.ServerSessionPool, int)
     */
    @Override
    public ConnectionConsumer createConnectionConsumer(Topic topic, String messageSelector,
                                                       ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        connect();
        return null;
    }

    /**
     * @param transacted
     * @param acknowledgeMode
     * @return TopicSession
     * @throws JMSException
     * @see javax.jms.TopicConnection#createTopicSession(boolean, int)
     */
    @Override
    public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        connect();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        JmsTopicSession result = new JmsTopicSession(this, getNextSessionId(), ackMode);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    /**
     * @param queue
     * @param messageSelector
     * @param sessionPool
     * @param maxMessages
     * @return ConnectionConsumer
     * @throws JMSException
     * @see javax.jms.QueueConnection#createConnectionConsumer(javax.jms.Queue,
     *      java.lang.String, javax.jms.ServerSessionPool, int)
     */
    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector,
                                                       ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        connect();
        return null;
    }

    /**
     * @param transacted
     * @param acknowledgeMode
     * @return QueueSession
     * @throws JMSException
     * @see javax.jms.QueueConnection#createQueueSession(boolean, int)
     */
    @Override
    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        connect();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        JmsQueueSession result = new JmsQueueSession(this, getNextSessionId(), ackMode);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    /**
     * @param ex
     */
    public void onException(Exception ex) {
        onException(JmsExceptionSupport.create(ex));
    }

    /**
     * @param ex
     */
    public void onException(JMSException ex) {
        ExceptionListener l = this.exceptionListener;
        if (l != null) {
            l.onException(JmsExceptionSupport.create(ex));
        }
    }

    protected int getSessionAcknowledgeMode(boolean transacted, int acknowledgeMode) throws JMSException {
        int result = acknowledgeMode;
        if (!transacted && acknowledgeMode == Session.SESSION_TRANSACTED) {
            throw new JMSException("acknowledgeMode SESSION_TRANSACTED cannot be used for an non-transacted Session");
        }
        if (transacted) {
            result = Session.SESSION_TRANSACTED;
        }
        return result;
    }

//    protected void removeSession(JmsSession s, StompChannel channel) throws JMSException {
//        synchronized (this) {
//            this.sessions.remove(s);
//            if( channel!=null && this.channel==null ) {
//                // just in case some one is in a loop creating/closing sessions.
//                this.channel = channel;
//                channel = null;
//            }
//        }
//        if(channel!=null) {
//            channel.setListener(null);
//            channel.close();
//        }
//    }

    protected void addSession(JmsSession s) {
        this.sessions.add(s);
    }

    private void connect() throws JMSException {
        synchronized(this.connectLock) {
            if (isConnected() || closed.get()) {
                return;
            }

            if (connectionInfo.getClientId() == null || connectionInfo.getClientId().trim().isEmpty()) {
                connectionInfo.setClientId(clientIdGenerator.generateId());
            }

            this.connectionInfo = createResource(connectionInfo);
            this.connected.set(true);

            // TODO - Advisory Support.
            //
            // Providers should have an interface for adding a listener for temporary
            // destination advisory messages for create / destroy so we can track them
            // and throw exceptions when producers try to send to deleted destinations.
        }
    }

    void deleteDestination(JmsDestination destination) throws JMSException {
        checkClosedOrFailed();
        connect();

        try {

            for (JmsSession session : this.sessions) {
                if (session.isDestinationInUse(destination)) {
                    throw new JMSException("A consumer is consuming from the temporary destination");
                }
            }

            // Provider delete if supported.

            // TODO if we track temp destinations and this happens to be one
            //      we need to clean up our internal state.
            if (destination.isTemporary()) {
            }
        } catch (Exception e) {
            throw JmsExceptionSupport.create(e);
        }
    }

    protected void checkClosedOrFailed() throws JMSException {
        checkClosed();
        if (failed.get()) {
            throw new JmsConnectionFailedException(firstFailureError);
        }
    }

    protected void checkClosed() throws IllegalStateException {
        if (this.closed.get()) {
            throw new IllegalStateException("The MessageProducer is closed");
        }
    }

    protected JmsSessionId getNextSessionId() {
        return new JmsSessionId(connectionInfo.getConnectionId(), sessionIdGenerator.incrementAndGet());
    }

    @SuppressWarnings("unchecked")
    <T extends JmsResource> T createResource(T resource) throws JMSException {
        checkClosedOrFailed();
        connect();

        try {
            return (T) provider.create(resource).getResponse();
        } catch (Exception ioe) {
            throw JmsExceptionSupport.create(ioe);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Property setters and getters
    ////////////////////////////////////////////////////////////////////////////

    /**
     * @return ExceptionListener
     * @see javax.jms.Connection#getExceptionListener()
     */
    @Override
    public ExceptionListener getExceptionListener() {
        return this.exceptionListener;
    }

    /**
     * @param listener
     * @see javax.jms.Connection#setExceptionListener(javax.jms.ExceptionListener)
     */
    @Override
    public void setExceptionListener(ExceptionListener listener) {
        this.exceptionListener = listener;
    }

    public boolean isForceAsyncSend() {
        return connectionInfo.isForceAsyncSend();
    }

    public void setForceAsyncSend(boolean forceAsyncSend) {
        connectionInfo.setForceAsyncSends(forceAsyncSend);
    }

    public String getTopicPrefix() {
        return connectionInfo.getTopicPrefix();
    }

    public void setTopicPrefix(String topicPrefix) {
        connectionInfo.setTopicPrefix(topicPrefix);
    }

    public String getTempTopicPrefix() {
        return connectionInfo.getTempTopicPrefix();
    }

    public void setTempTopicPrefix(String tempTopicPrefix) {
        connectionInfo.setTempTopicPrefix(tempTopicPrefix);
    }

    public String getTempQueuePrefix() {
        return connectionInfo.getTempQueuePrefix();
    }

    public void setTempQueuePrefix(String tempQueuePrefix) {
        connectionInfo.setTempQueuePrefix(tempQueuePrefix);
    }

    public String getQueuePrefix() {
        return connectionInfo.getQueuePrefix();
    }

    public void setQueuePrefix(String queuePrefix) {
        connectionInfo.setQueuePrefix(queuePrefix);
    }

    public boolean isOmitHost() {
        return connectionInfo.isOmitHost();
    }

    public void setOmitHost(boolean omitHost) {
        connectionInfo.setOmitHost(omitHost);
    }

    public JmsPrefetchPolicy getPrefetchPolicy() {
        return prefetchPolicy;
    }

    public void setPrefetchPolicy(JmsPrefetchPolicy prefetchPolicy) {
        this.prefetchPolicy = prefetchPolicy;
    }

    public long getDisconnectTimeout() {
        return connectionInfo.getDisconnectTimeout();
    }

    public void setDisconnectTimeout(long disconnectTimeout) {
        connectionInfo.setDisconnectTimeout(disconnectTimeout);
    }

    public URI getBrokerURI() {
        return brokerURI;
    }

    public void setBrokerURI(URI brokerURI) {
        this.brokerURI = brokerURI;
    }

    public URI getLocalURI() {
        return localURI;
    }

    public void setLocalURI(URI localURI) {
        this.localURI = localURI;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    Provider getProvider() {
        return provider;
    }

    void setProvider(Provider provider) {
        this.provider = provider;
    }

    boolean isConnected() {
        return this.connected.get();
    }
}