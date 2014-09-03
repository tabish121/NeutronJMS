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

import io.neutronjms.jms.message.JmsInboundMessageDispatch;
import io.neutronjms.jms.meta.JmsConsumerId;
import io.neutronjms.jms.meta.JmsConsumerInfo;
import io.neutronjms.provider.AsyncResult;
import io.neutronjms.provider.ProviderConstants.ACK_TYPE;

/**
 * Implements the functionality needed to consume messages from an OpenWire Broker.
 */
public class OpenWireConsumer {

    private final OpenWireSession session;
    private final JmsConsumerInfo consumerInfo;

    public OpenWireConsumer(OpenWireSession session, JmsConsumerInfo consumerInfo) {
        this.session = session;
        this.consumerInfo = consumerInfo;
    }

    /**
     * @param request
     */
    public void open(AsyncResult request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     */
    public void close(AsyncResult request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     */
    public void start(AsyncResult request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param timeout
     */
    public void pull(long timeout) {
        // TODO Auto-generated method stub
    }

    /**
     * @param envelope
     * @param ackType
     * @param request
     */
    public void acknowledge(JmsInboundMessageDispatch envelope, ACK_TYPE ackType, AsyncResult request) {
        // TODO Auto-generated method stub
    }

    /**
     * @returns the parent OpenWireSession for this OpenWireConsumer.
     */
    public OpenWireSession getSession() {
        return this.session;
    }

    /**
     * @returns the JmsConsumerId that identifies this Consumer instance.
     */
    public JmsConsumerId getConsumerId() {
        return this.consumerInfo.getConsumerId();
    }
}
