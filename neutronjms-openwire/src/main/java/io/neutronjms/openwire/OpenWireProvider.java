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
package io.neutronjms.openwire;

import io.neutronjms.jms.message.JmsDefaultMessageFactory;
import io.neutronjms.jms.message.JmsInboundMessageDispatch;
import io.neutronjms.jms.message.JmsMessageFactory;
import io.neutronjms.jms.message.JmsOutboundMessageDispatch;
import io.neutronjms.jms.meta.JmsConnectionInfo;
import io.neutronjms.jms.meta.JmsResource;
import io.neutronjms.jms.meta.JmsSessionId;
import io.neutronjms.provider.AbstractAsyncProvider;
import io.neutronjms.provider.AsyncResult;
import io.neutronjms.provider.ProviderConstants.ACK_TYPE;
import io.neutronjms.transports.Transport;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a Provider instance that is used to communicate with any Broker that
 * can provide an OpenWire protocol head.
 */
public class OpenWireProvider extends AbstractAsyncProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireProvider.class);

    private Transport transport;

    private long connectTimeout = JmsConnectionInfo.DEFAULT_CONNECT_TIMEOUT;
    private long closeTimeout = JmsConnectionInfo.DEFAULT_CLOSE_TIMEOUT;
    private long requestTimeout = JmsConnectionInfo.DEFAULT_REQUEST_TIMEOUT;
    private long sendTimeout = JmsConnectionInfo.DEFAULT_SEND_TIMEOUT;

    private final JmsDefaultMessageFactory messageFactory = new JmsDefaultMessageFactory();

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
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void create(JmsResource resource, AsyncResult<Void> request) throws IOException, JMSException, UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(JmsResource resource, AsyncResult<Void> request) throws IOException, JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy(JmsResource resourceId, AsyncResult<Void> request) throws IOException, JMSException, UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void send(JmsOutboundMessageDispatch envelope, AsyncResult<Void> request) throws IOException, JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void acknowledge(JmsSessionId sessionId, AsyncResult<Void> request) throws IOException, JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void acknowledge(JmsInboundMessageDispatch envelope, ACK_TYPE ackType, AsyncResult<Void> request) throws IOException, JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void recover(JmsSessionId sessionId, AsyncResult<Void> request) throws IOException, UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    //---------- Property Setters and Getters --------------------------------//

    @Override
    public JmsMessageFactory getMessageFactory() {
        // TODO
//        if (connection == null) {
            throw new RuntimeException("Message Factory is not accessible when not connected.");
//        }
//        return connection.getAmqpMessageFactory();
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

    @Override
    public String toString() {
        return "OpenWireProvider: " + getRemoteURI().getHost() + ":" + getRemoteURI().getPort();
    }
}
