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
package org.apache.qpid.jms.provider.amqp.message;

import java.io.IOException;

import org.apache.qpid.jms.message.JmsMessage;
import org.apache.qpid.jms.provider.amqp.AmqpConnection;
import org.apache.qpid.proton.message.Message;

/**
 * Builder class used to construct the appropriate JmsMessage / JmsMessageFacade
 * objects to wrap an incoming AMQP Message.
 */
public class AmqpJmsMessageBuilder {

    private AmqpJmsMessageBuilder() {
    }

    /**
     * Create a new JmsMessage and underlying JmsMessageFacade that represents the proper
     * message type for the incoming AMQP message.
     *
     * @param connection
     *        The provider AMQP Connection instance where this message arrived at.
     * @param message
     *        The Proton Message object that will be wrapped.
     *
     * @return a JmsMessage instance properly configured for dispatch to the provider listener.
     *
     * @throws IOException if an error occurs while creating the message objects.
     */
    public static JmsMessage createJmsMessage(AmqpConnection connection, Message message) throws IOException {
        return null;
    }
}