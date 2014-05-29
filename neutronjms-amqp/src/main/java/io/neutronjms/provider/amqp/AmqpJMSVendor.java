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

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.JmsQueue;
import io.neutronjms.jms.JmsTemporaryQueue;
import io.neutronjms.jms.JmsTemporaryTopic;
import io.neutronjms.jms.JmsTopic;
import io.neutronjms.jms.message.JmsDefaultMessageFactory;
import io.neutronjms.jms.message.JmsMessage;
import io.neutronjms.jms.message.JmsMessageFactory;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.qpid.proton.jms.JMSVendor;

public class AmqpJMSVendor extends JMSVendor {

    public static final AmqpJMSVendor INSTANCE = new AmqpJMSVendor();

    private final JmsMessageFactory factory = new JmsDefaultMessageFactory();

    private AmqpJMSVendor() {
    }

    @Override
    public BytesMessage createBytesMessage() {
        return factory.createBytesMessage();
    }

    @Override
    public StreamMessage createStreamMessage() {
        return factory.createStreamMessage();
    }

    @Override
    public Message createMessage() {
        return factory.createMessage();
    }

    @Override
    public TextMessage createTextMessage() {
        return factory.createTextMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() {
        return factory.createObjectMessage();
    }

    @Override
    public MapMessage createMapMessage() {
        return factory.createMapMessage();
    }

    @Override
    public Destination createDestination(String name) {
        return super.createDestination(name, Destination.class);
    }

    @Override
    public <T extends Destination> T createDestination(String name, Class<T> kind) {
        if (kind == Queue.class) {
            return kind.cast(new JmsQueue(name));
        }
        if (kind == Topic.class) {
            return kind.cast(new JmsTopic(name));
        }
        if (kind == TemporaryQueue.class) {
            return kind.cast(new JmsTemporaryQueue(name));
        }
        if (kind == TemporaryTopic.class) {
            return kind.cast(new JmsTemporaryTopic(name));
        }

        return kind.cast(new JmsQueue(name));
    }

    @Override
    public void setJMSXUserID(Message msg, String value) {
        try {
            ((JmsMessage) msg).getFacade().setUserId(value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJMSXGroupID(Message msg, String value) {
        try {
            ((JmsMessage) msg).getFacade().setGroupId(value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJMSXGroupSequence(Message msg, int value) {
        try {
            ((JmsMessage) msg).getFacade().setGroupSequence(value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJMSXDeliveryCount(Message msg, long value) {
        // Delivery count tracks total deliveries which is always one higher than
        // re-delivery count since first delivery counts to.
        try {
            ((JmsMessage) msg).getFacade().setRedeliveryCounter((int) (value == 0 ? value : value - 1));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toAddress(Destination dest) {
        return ((JmsDestination) dest).getName();
    }
}
