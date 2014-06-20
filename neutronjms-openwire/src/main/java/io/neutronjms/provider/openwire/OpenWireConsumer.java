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
import io.neutronjms.provider.AsyncResult;
import io.neutronjms.provider.ProviderConstants.ACK_TYPE;

/**
 *
 */
public class OpenWireConsumer {

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
    public void acknowledge(JmsInboundMessageDispatch envelope, ACK_TYPE ackType, AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param request
     */
    public void close(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param request
     */
    public void start(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

    /**
     * @param request
     */
    public void open(AsyncResult<Void> request) {
        // TODO Auto-generated method stub

    }

}
