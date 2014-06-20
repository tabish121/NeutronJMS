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
package io.neutronjms.provider.openwire;

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.message.JmsInboundMessageDispatch;
import io.neutronjms.jms.message.JmsMessageFactory;
import io.neutronjms.jms.message.JmsOutboundMessageDispatch;
import io.neutronjms.jms.meta.JmsConnectionInfo;
import io.neutronjms.jms.meta.JmsConsumerId;
import io.neutronjms.jms.meta.JmsConsumerInfo;
import io.neutronjms.jms.meta.JmsDefaultResourceVisitor;
import io.neutronjms.jms.meta.JmsProducerId;
import io.neutronjms.jms.meta.JmsProducerInfo;
import io.neutronjms.jms.meta.JmsResource;
import io.neutronjms.jms.meta.JmsResourceVistor;
import io.neutronjms.jms.meta.JmsSessionId;
import io.neutronjms.jms.meta.JmsSessionInfo;
import io.neutronjms.jms.meta.JmsTransactionInfo;
import io.neutronjms.provider.AbstractAsyncProvider;
import io.neutronjms.provider.AsyncResult;
import io.neutronjms.provider.ProviderConstants.ACK_TYPE;
import io.neutronjms.provider.ProviderRequest;
import io.neutronjms.transports.TcpTransport;
import io.neutronjms.transports.Transport;
import io.neutronjms.transports.TransportListener;
import io.openwire.codec.OpenWireFormat;
import io.openwire.codec.OpenWireFormatFactory;
import io.openwire.commands.Command;
import io.openwire.commands.Response;
import io.openwire.commands.ShutdownInfo;
import io.openwire.commands.WireFormatInfo;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.buffer.Buffer;

/**
 * Implements a Provider instance that is used to communicate with any Broker that
 * can provide an OpenWire protocol head.
 */
public class OpenWireProvider extends AbstractAsyncProvider implements TransportListener {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireProvider.class);

    private final OpenWireFormatFactory factory = new OpenWireFormatFactory();

    private Transport transport;
    private ScheduledFuture<?> negotiateTimeoutTask;

    private OpenWireFormat wireFormat;
    private OpenWireConnection connection;

    private long connectTimeout = JmsConnectionInfo.DEFAULT_CONNECT_TIMEOUT;
    private long closeTimeout = JmsConnectionInfo.DEFAULT_CLOSE_TIMEOUT;
    private long requestTimeout = JmsConnectionInfo.DEFAULT_REQUEST_TIMEOUT;
    private long sendTimeout = JmsConnectionInfo.DEFAULT_SEND_TIMEOUT;
    private long negotiateTimeout = 15000L;

    /**
     * @param remoteURI
     */
    public OpenWireProvider(URI remoteURI) {
        super(remoteURI);
    }

    /**
     * Create a new instance of an OpenWireProvider bonded to the given remote URI.
     *
     * @param remoteURI
     *        The URI of the OpenWire broker this Provider instance will connect to.
     */
    public OpenWireProvider(URI remoteURI, Map<String, String> extraOptions) {
        super(remoteURI);
    }

    @Override
    public void connect() throws IOException {
        checkClosed();

        this.wireFormat = factory.createWireFormat();

        // Set a trigger to fail the Provider if we don't get a WireFormatInfo back
        // from the broker in time.
        negotiateTimeoutTask = this.serializer.schedule(new Runnable() {

            @Override
            public void run() {
                fireProviderException(new IOException("Wire format negotiation timeout: peer did not send his wire format."));
            }
        }, getNegotiateTimeout(), TimeUnit.MILLISECONDS);

        transport = createTransport(getRemoteURI());
        transport.connect();

        asyncSend(wireFormat.getPreferedWireFormatInfo());
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            final ProviderRequest<Void> request = new ProviderRequest<Void>();
            serializer.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        asyncSend(new ShutdownInfo());
                    } catch (Exception e) {
                        LOG.debug("Caught exception while closing proton connection");
                    } finally {
                        if (transport != null) {
                            try {
                                transport.close();
                            } catch (Exception e) {
                                LOG.debug("Cuaght exception while closing down Transport: {}", e.getMessage());
                            }
                        }

                        request.onSuccess();
                    }
                }
            });

            try {
                if (closeTimeout < 0) {
                    request.getResponse();
                } else {
                    request.getResponse(closeTimeout, TimeUnit.MILLISECONDS);
                }
            } catch (IOException e) {
                LOG.warn("Error caught while closing Provider: ", e.getMessage());
            } finally {
                if (serializer != null) {
                    serializer.shutdown();
                }
            }
        }
    }

    @Override
    public void create(final JmsResource resource, final AsyncResult<Void> request) throws IOException, JMSException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    resource.visit(new JmsResourceVistor() {

                        @Override
                        public void processSessionInfo(JmsSessionInfo sessionInfo) throws Exception {
                            OpenWireSession session = connection.createSession(sessionInfo);
                            session.open(request);
                        }

                        @Override
                        public void processProducerInfo(JmsProducerInfo producerInfo) throws Exception {
                            OpenWireSession session = connection.getSession(producerInfo.getParentId());
                            OpenWireProducer producer = session.createProducer(producerInfo);
                            producer.open(request);
                        }

                        @Override
                        public void processConsumerInfo(JmsConsumerInfo consumerInfo) throws Exception {
                            OpenWireSession session = connection.getSession(consumerInfo.getParentId());
                            OpenWireConsumer consumer = session.createConsumer(consumerInfo);
                            consumer.open(request);
                        }

                        @Override
                        public void processConnectionInfo(JmsConnectionInfo connectionInfo) throws Exception {
                            closeTimeout = connectionInfo.getCloseTimeout();
                            connectTimeout = connectionInfo.getConnectTimeout();
                            sendTimeout = connectionInfo.getSendTimeout();
                            requestTimeout = connectionInfo.getRequestTimeout();

                            connection = new OpenWireConnection(OpenWireProvider.this, connectionInfo);
                            connection.open(request);
                        }

                        @Override
                        public void processDestination(JmsDestination destination) throws Exception {
                            if (destination.isTemporary()) {
                                connection.createTemporaryDestination(destination, request);
                            } else {
                                request.onSuccess();
                            }
                        }

                        @Override
                        public void processTransactionInfo(JmsTransactionInfo transactionInfo) throws Exception {
                            OpenWireSession session = connection.getSession(transactionInfo.getParentId());
                            session.begin(transactionInfo.getTransactionId(), request);
                        }
                    });

                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void start(final JmsResource resource, final AsyncResult<Void> request) throws IOException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    resource.visit(new JmsDefaultResourceVisitor() {

                        @Override
                        public void processConsumerInfo(JmsConsumerInfo consumerInfo) throws Exception {
                            OpenWireSession session = connection.getSession(consumerInfo.getParentId());
                            OpenWireConsumer consumer = session.getConsumer(consumerInfo.getConsumerId());
                            consumer.start(request);
                        }
                    });
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void destroy(final JmsResource resource, final AsyncResult<Void> request) throws IOException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    resource.visit(new JmsDefaultResourceVisitor() {

                        @Override
                        public void processSessionInfo(JmsSessionInfo sessionInfo) throws Exception {
                            OpenWireSession session = connection.getSession(sessionInfo.getSessionId());
                            session.close(request);
                        }

                        @Override
                        public void processProducerInfo(JmsProducerInfo producerInfo) throws Exception {
                            OpenWireSession session = connection.getSession(producerInfo.getParentId());
                            OpenWireProducer producer = session.getProducer(producerInfo);
                            producer.close(request);
                        }

                        @Override
                        public void processConsumerInfo(JmsConsumerInfo consumerInfo) throws Exception {
                            OpenWireSession session = connection.getSession(consumerInfo.getParentId());
                            OpenWireConsumer consumer = session.getConsumer(consumerInfo.getConsumerId());
                            consumer.close(request);
                        }

                        @Override
                        public void processConnectionInfo(JmsConnectionInfo connectionInfo) throws Exception {
                            connection.close(request);
                        }

                        @Override
                        public void processDestination(JmsDestination destination) throws Exception {
                            if (destination.isTemporary()) {
                                connection.destroyTemporaryDestination(destination, request);
                            } else {
                                request.onSuccess(null);
                            }
                        }
                    });
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void send(final JmsOutboundMessageDispatch envelope, final AsyncResult<Void> request) throws IOException, JMSException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    JmsProducerId producerId = envelope.getProducerId();
                    OpenWireProducer producer = connection.getProducer(producerId);
                    producer.send(envelope, request);
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void acknowledge(final JmsSessionId sessionId, final AsyncResult<Void> request) throws IOException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    OpenWireSession session = connection.getSession(sessionId);
                    session.acknowledge(request);
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void acknowledge(final JmsInboundMessageDispatch envelope, final ACK_TYPE ackType, final AsyncResult<Void> request) throws IOException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    JmsConsumerId consumerId = envelope.getConsumerId();
                    OpenWireConsumer consumer = connection.getConsumer(consumerId);
                    consumer.acknowledge(envelope, ackType, request);
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void commit(final JmsSessionId sessionId, final AsyncResult<Void> request) throws IOException, JMSException, UnsupportedOperationException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    OpenWireSession session = connection.getSession(sessionId);
                    session.commit(request);
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void rollback(final JmsSessionId sessionId, final AsyncResult<Void> request) throws IOException, JMSException, UnsupportedOperationException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    OpenWireSession session = connection.getSession(sessionId);
                    session.rollback(request);
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void recover(final JmsSessionId sessionId, final AsyncResult<Void> request) throws IOException, UnsupportedOperationException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    OpenWireSession session = connection.getSession(sessionId);
                    session.recover();
                    request.onSuccess();
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void pull(final JmsConsumerId consumerId, final long timeout, final AsyncResult<Void> request) throws IOException, UnsupportedOperationException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    OpenWireSession session = connection.getSession(consumerId.getParentId());
                    OpenWireConsumer consumer = session.getConsumer(consumerId);
                    consumer.pull(timeout);
                    request.onSuccess();
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    @Override
    public void unsubscribe(final String subscription, final AsyncResult<Void> request) throws IOException, JMSException, UnsupportedOperationException {
        checkClosed();
        serializer.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    checkClosed();
                    connection.unsubscribe(subscription);
                    request.onSuccess();
                } catch (Exception error) {
                    request.onFailure(error);
                }
            }
        });
    }

    //---------- Transport event callbacks -----------------------------------//

    @Override
    public void onData(Buffer incoming) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTransportClosed() {
        if (!closed.get()) {
            serializer.execute(new Runnable() {
                @Override
                public void run() {
                    LOG.info("Transport connection remotely closed:");
                    if (!closed.get()) {
                        fireProviderException(new IOException("Connection remotely closed."));
                    }
                }
            });
        }
    }

    @Override
    public void onTransportError(final Throwable error) {
        if (!closed.get()) {
            serializer.execute(new Runnable() {
                @Override
                public void run() {
                    LOG.info("Transport failed: {}", error.getMessage());
                    if (!closed.get()) {
                        fireProviderException(error);
                    }
                }
            });
        }
    }

    //---------- OpenWire Command handlers -----------------------------------//

    private void processWireFormatInfo(WireFormatInfo info) throws Exception {

        if (negotiateTimeoutTask != null) {
            negotiateTimeoutTask.cancel(false);
            negotiateTimeoutTask = null;
        }

        wireFormat.renegotiateWireFormat(info);
    }

    private void processResponse(Response response) throws Exception {

    }

    //---------- Internal utility methods ------------------------------------//

    /**
     * Sends the given OpenWire Command object without required response.  This method
     * must be called from the context of the Provider's executor thread.
     *
     * @param command
     *        the command to send.
     *
     * @throws IOException if an error occurs while sending the Command.
     */
    protected void asyncSend(Command command) throws IOException {
        transport.send(wireFormat.marshal(command));
    }

    /**
     * Provides an extension point for subclasses to insert other types of transports such
     * as SSL etc.
     *
     * @param remoteLocation
     *        The remote location where the transport should attempt to connect.
     *
     * @return the newly created transport instance.
     */
    protected Transport createTransport(URI remoteLocation) {
        return new TcpTransport(this, remoteLocation);
    }

    @Override
    public String toString() {
        return "OpenWireProvider: " + getRemoteURI().getHost() + ":" + getRemoteURI().getPort();
    }

    //---------- Property Setters and Getters --------------------------------//

    @Override
    public JmsMessageFactory getMessageFactory() {
        if (connection == null) {
            throw new RuntimeException("Message Factory is not accessible when not connected.");
        }
        return connection.getOpenWireMessageFactory();
    }

    public long getCloseTimeout() {
        return this.closeTimeout;
    }

    public void setCloseTimeout(long closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public long getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(long sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public long getNegotiateTimeout() {
        return negotiateTimeout;
    }

    public void setNegotiateTimeout(long negotiateTimeout) {
        this.negotiateTimeout = negotiateTimeout;
    }
}
