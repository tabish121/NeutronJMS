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
package io.neutronjms.provider.amqp.message;

import io.neutronjms.jms.message.facade.JmsTextMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import org.apache.qpid.proton.message.Message;

/**
 *
 */
public class AmqpJmsTextMessageFacade extends AmqpJmsMessageFacade implements JmsTextMessageFacade {

    /**
     * @param connection
     */
    public AmqpJmsTextMessageFacade(AmqpConnection connection) {
        super(connection);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a new Facade around an incoming AMQP Message for dispatch to the
     * JMS Consumer instance.
     *
     * @param connection
     *        the connection that created this Facade.
     * @param message
     *        the incoming Message instance that is being wrapped.
     */
    public AmqpJmsTextMessageFacade(AmqpConnection connection, Message message) {
        super(connection, message);
    }

    @Override
    public JmsTextMessageFacade copy() {
        AmqpJmsTextMessageFacade copy = new AmqpJmsTextMessageFacade(connection);
        copyInto(copy);

        copy.setText(getText());

        return copy;
    }

    @Override
    public String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setText(String value) {
        // TODO Auto-generated method stub

    }
}
