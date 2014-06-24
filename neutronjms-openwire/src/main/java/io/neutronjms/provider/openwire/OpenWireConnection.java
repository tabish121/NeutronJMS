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
import io.neutronjms.jms.meta.JmsConnectionId;
import io.neutronjms.jms.meta.JmsConnectionInfo;
import io.neutronjms.jms.meta.JmsConsumerId;
import io.neutronjms.jms.meta.JmsProducerId;
import io.neutronjms.jms.meta.JmsSessionId;
import io.neutronjms.jms.meta.JmsSessionInfo;
import io.neutronjms.provider.AsyncResult;
import io.neutronjms.provider.openwire.message.OpenWireJmsMessageFactory;
import io.openwire.commands.Command;
import io.openwire.commands.ExceptionResponse;
import io.openwire.commands.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the state for a Connection to a Broker over the OpenWire protocol.
 */
public class OpenWireConnection implements OpenWireResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireConnection.class);

    private final OpenWireProvider provider;
    private final JmsConnectionInfo connectionInfo;
    private final OpenWireJmsMessageFactory messageFactory;
    private final io.openwire.utils.OpenWireConnection openWireConnection;

    private int requestSequence;
    private final Map<Integer, AsyncResult<Void>> requests = new HashMap<Integer, AsyncResult<Void>>();
    private final Map<JmsSessionId, OpenWireSession> sessions = new HashMap<JmsSessionId, OpenWireSession>();

    /**
     * Create a new instance of the OpenWireConnection class.
     *
     * @param openWireProvider
     *        the provider instance that created this Connection.
     * @param connectionInfo
     *        the JmsConnectionInfo that describes this connection.
     */
    public OpenWireConnection(OpenWireProvider provider, JmsConnectionInfo connectionInfo) {
        this.provider = provider;
        this.connectionInfo = connectionInfo;
        this.messageFactory = new OpenWireJmsMessageFactory();

        this.openWireConnection = new io.openwire.utils.OpenWireConnection(connectionInfo.getConnectionId().toString());
    }

    @Override
    public void open(AsyncResult<Void> request) throws Exception {
        openWireConnection.setClientId(connectionInfo.getClientId());
        openWireConnection.setFaultTolerant(false);
        openWireConnection.setManageable(true);
        openWireConnection.setUserName(connectionInfo.getUsername());
        openWireConnection.setPassword(connectionInfo.getPassword());

        syncSend(openWireConnection, request);
    }

    @Override
    public void close(AsyncResult<Void> request) throws Exception {
        syncSend(openWireConnection.createRemoveInfo(), request);
    }

    /**
     * @param sessionInfo
     * @return
     */
    public OpenWireSession createSession(JmsSessionInfo sessionInfo) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param destination
     * @param request
     */
    public void createTemporaryDestination(JmsDestination destination, AsyncResult<Void> request) throws Exception {
        // TODO Auto-generated method stub
    }

    /**
     * @param destination
     * @param request
     */
    public void destroyTemporaryDestination(JmsDestination destination, AsyncResult<Void> request) throws Exception {
        // TODO Auto-generated method stub
    }

    /**
     * @param subscription
     */
    public void unsubscribe(String subscription) throws Exception {
        // TODO Auto-generated method stub
    }

    /**
     * Returns the OpenWireSession identified by the given session Id.
     *
     * @param sessionId
     *        the Id of the session to lookup.
     *
     * @return the OpenWireSession that matches the given Id.
     */
    public OpenWireSession getSession(JmsSessionId sessionId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the OpenWireConsumer identified by the given consumer Id.
     *
     * @param consumerId
     *        the Id of the consumer to lookup.
     *
     * @return the OpenWireConsumer that matches the given Id.
     */
    public OpenWireConsumer getConsumer(JmsConsumerId consumerId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the OpenWireProducer identified by the given producer Id.
     *
     * @param producerId
     *        the Id of the producer to lookup.
     *
     * @return the OpenWireProducer that matches the given Id.
     */
    public OpenWireProducer getProducer(JmsProducerId producerId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return this JmsConnectionId that identifies this Connection.
     */
    public JmsConnectionId getConnectionId() {
        return this.connectionInfo.getConnectionId();
    }

    /**
     * @return the OpenWireProvider that created this Connection.
     */
    public OpenWireProvider getProvider() {
        return provider;
    }

    /**
     * @return the OpenWire based JmsMessageFactory for this Connection.
     */
    public OpenWireJmsMessageFactory getOpenWireMessageFactory() {
        return messageFactory;
    }

    /**
     * Sends the given command with requesting a response from the Broker.
     *
     * @param command
     *        the command to send.
     *
     * @throws IOException if an error occurs while sending the command.
     */
    public void asyncSend(Command command) throws IOException {
        this.provider.asyncSend(command);
    }

    /**
     * Sends the given command with requesting a response from the Broker.
     *
     * @param command
     *        the command to send.
     *
     * @throws IOException if an error occurs while sending the command.
     */
    public void syncSend(Command command, AsyncResult<Void> request) throws IOException {
        command.setResponseRequired(true);
        command.setCommandId(++requestSequence);

        requests.put(requestSequence, request);

        this.provider.asyncSend(command);
    }

    /**
     * Handles incoming OpenWire Commands for this Connection and its associated resources.
     *
     * @param incoming
     *        the newly received command instance.
     *
     * @throws Exception
     */
    public void processCommand(Command incoming) throws Exception {
        if (incoming.isResponse()) {
            Response response = (Response) incoming;
            AsyncResult<Void> request = requests.remove(response.getCorrelationId());
            if (request == null) {
                LOG.warn("Received response for unkown request: {}", request);
                return;
            }

            // TODO - We could just create a pooling class of custom AsyncResult
            //        instances that the resources can use to wrap the original
            //        request and add additional logic as needed.
            if (response.isException()) {
                onExceptionReponse((ExceptionResponse) response, request);
            } else {
                onResponse(response, request);
            }
        } else {
            // TODO - Handle OpenWire Commands.
        }
    }

    @Override
    public void onResponse(Response response, AsyncResult<Void> request) {
        request.onSuccess();
    }

    @Override
    public void onExceptionReponse(ExceptionResponse error, AsyncResult<Void> request) {
        request.onFailure(error.getException());
    }
}
