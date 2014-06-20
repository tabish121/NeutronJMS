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

import io.neutronjms.jms.meta.JmsConsumerId;
import io.neutronjms.jms.meta.JmsConsumerInfo;
import io.neutronjms.jms.meta.JmsProducerInfo;
import io.neutronjms.jms.meta.JmsTransactionId;
import io.neutronjms.provider.AsyncResult;

/**
 * Manages the resources that are linked to a single OpenWire SessionInfo instance.
 */
public class OpenWireSession {

    /**
     *
     */
    public void recover() {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     */
    public void commit(AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param request
     */
    public void rollback(AsyncResult<Void> request) {
        // TODO Auto-generated method stub
    }

    /**
     * @param consumerId
     * @return
     */
    public OpenWireConsumer getConsumer(JmsConsumerId consumerId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param request
     */
    public void acknowledge(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param request
     */
    public void close(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param transactionId
     * @param request
     */
    public void begin(JmsTransactionId transactionId, AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param request
     */
    public void open(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param producerInfo
     * @return
     */
    public OpenWireProducer createProducer(JmsProducerInfo producerInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param consumerInfo
     * @return
     */
    public OpenWireConsumer createConsumer(JmsConsumerInfo consumerInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param producerInfo
     * @return
     */
    public OpenWireProducer getProducer(JmsProducerInfo producerInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param consumerInfo
     * @return
     */
    public OpenWireConsumer getConsumer(JmsConsumerInfo consumerInfo) {
        // TODO Auto-generated method stub
        return null;
    }
}
