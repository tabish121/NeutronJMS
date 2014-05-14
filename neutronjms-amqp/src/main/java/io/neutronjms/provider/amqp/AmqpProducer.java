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
package io.neutronjms.provider.amqp;

import io.neutronjms.jms.message.JmsOutboundMessageDispatch;
import io.neutronjms.jms.meta.JmsProducerId;
import io.neutronjms.jms.meta.JmsProducerInfo;
import io.neutronjms.provider.AsyncResult;

import java.io.IOException;

import org.apache.qpid.proton.engine.Sender;

/**
 * Base class for Producer instances.
 */
public abstract class AmqpProducer extends AbstractAmqpResource<JmsProducerInfo, Sender> {

    protected final AmqpSession session;
    protected final AmqpConnection connection;

    public AmqpProducer(AmqpSession session, JmsProducerInfo info) {
        super(info);
        this.session = session;
        this.connection = session.getConnection();

        // Add a shortcut back to this Producer for quicker lookup.
        this.info.getProducerId().setProviderHint(this);
    }

    /**
     * Sends the given message
     *
     * @param envelope
     *        The envelope that contains the message and it's targeted destination.
     * @param request
     *        The AsyncRequest that will be notified on send success or failure.
     *
     * @returns true if the producer had credit to send or false if there was no available
     *          credit and the send needed to be deferred.
     *
     * @throws IOException
     */
    public abstract boolean send(JmsOutboundMessageDispatch envelope, AsyncResult<Void> request) throws IOException;

    /**
     * @return true if this is an anonymous producer or false if fixed to a given destination.
     */
    public abstract boolean isAnonymous();

    /**
     * @return the JmsProducerId that was assigned to this AmqpProducer.
     */
    public JmsProducerId getProducerId() {
        return this.info.getProducerId();
    }
}
