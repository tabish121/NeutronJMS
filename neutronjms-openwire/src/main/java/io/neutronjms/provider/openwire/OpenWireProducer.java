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

import io.neutronjms.jms.message.JmsOutboundMessageDispatch;
import io.neutronjms.jms.meta.JmsProducerId;
import io.neutronjms.jms.meta.JmsProducerInfo;
import io.neutronjms.provider.AsyncResult;

/**
 *
 */
public class OpenWireProducer {

    private final OpenWireSession session;
    private final JmsProducerInfo producerInfo;

    public OpenWireProducer(OpenWireSession session, JmsProducerInfo producerInfo) {
        this.session = session;
        this.producerInfo = producerInfo;
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
     * @param envelope
     * @param request
     */
    public void send(JmsOutboundMessageDispatch envelope, AsyncResult request) {
        // TODO Auto-generated method stub
    }

    /**
     * @returns the parent OpenWireSession for this OpenWireConsumer.
     */
    public OpenWireSession getSession() {
        return this.session;
    }

    /**
     * @returns the JmsProducerId that identifies this Producer instance.
     */
    public JmsProducerId getProducerId() {
        return this.producerInfo.getProducerId();
    }
}
