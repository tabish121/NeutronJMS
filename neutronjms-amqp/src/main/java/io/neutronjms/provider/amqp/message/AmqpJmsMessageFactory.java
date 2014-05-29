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

import io.neutronjms.jms.message.JmsBytesMessage;
import io.neutronjms.jms.message.JmsMapMessage;
import io.neutronjms.jms.message.JmsMessage;
import io.neutronjms.jms.message.JmsMessageFactory;
import io.neutronjms.jms.message.JmsObjectMessage;
import io.neutronjms.jms.message.JmsStreamMessage;
import io.neutronjms.jms.message.JmsTextMessage;

import java.io.Serializable;

/**
 * AMQP Message Factory instance used to create new JmsMessage types that wrap an
 * Proton AMQP Message.
 */
public class AmqpJmsMessageFactory implements JmsMessageFactory {

    @Override
    public JmsMessage createMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsTextMessage createTextMessage(String payload) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsTextMessage createTextMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsBytesMessage createBytesMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsMapMessage createMapMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsStreamMessage createStreamMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsObjectMessage createObjectMessage(Serializable payload) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsObjectMessage createObjectMessage() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

}
