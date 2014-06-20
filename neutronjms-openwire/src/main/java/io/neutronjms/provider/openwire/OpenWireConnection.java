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

/**
 * Manages the state for a Connection to a Broker over the OpenWire protocol.
 */
public class OpenWireConnection {

    private final OpenWireProvider provider;
    private final JmsConnectionInfo connectionInfo;
    private final OpenWireJmsMessageFactory messageFactory;

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
    }

    /**
     * @param request
     */
    public void open(AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     */
    public void close(AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param sessionInfo
     * @return
     */
    public OpenWireSession createSession(JmsSessionInfo sessionInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param destination
     * @param request
     */
    public void createTemporaryDestination(JmsDestination destination, AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param destination
     * @param request
     */
    public void destroyTemporaryDestination(JmsDestination destination, AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param subscription
     */
    public void unsubscribe(String subscription) {
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
    public OpenWireSession getSession(JmsSessionId sessionId) {
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
    public OpenWireConsumer getConsumer(JmsConsumerId consumerId) {
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
    public OpenWireProducer getProducer(JmsProducerId producerId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return this JmsConnectionId that indentifies this Connection.
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
}
