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

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.message.facade.JmsMessageFacade;
import io.neutronjms.jms.meta.JmsMessageId;
import io.neutronjms.provider.amqp.AmqpConnection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.jms.JMSException;

import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.UnsignedByte;
import org.apache.qpid.proton.amqp.UnsignedInteger;
import org.apache.qpid.proton.message.Message;

/**
 *
 */
public class AmqpJmsMessageFacade implements JmsMessageFacade {

    private static final int DEFAULT_PRIORITY = javax.jms.Message.DEFAULT_PRIORITY;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Message message;
    private final AmqpConnection connection;

    /**
     * Create a new AMQP Message Facade with an empty message instance.
     */
    public AmqpJmsMessageFacade(AmqpConnection connection) {
        this.message = Proton.message();
        this.connection = connection;
    }

    @Override
    public Map<String, Object> getProperties() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean propertyExists(String key) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getProperty(String key) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProperty(String key, Object value) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSend() throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearBody() throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearProperties() throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsMessageFacade copy() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsMessageId getMessageId() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMessageId(JmsMessageId messageId) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public long getTimestamp() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setTimestamp(long timestamp) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getCorrelationId() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCorrelationId(String correlationId) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isPersistent() throws JMSException {
        return message.isDurable();
    }

    @Override
    public void setPersistent(boolean value) throws JMSException {
        this.message.setDurable(value);
    }

    @Override
    public int getRedeliveryCounter() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRedeliveryCounter(int redeliveryCount) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getType() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setType(String type) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public byte getPriority() throws JMSException {
        if (message.getHeader() == null) {
            return DEFAULT_PRIORITY;
        } else {
            UnsignedByte priority = message.getHeader().getPriority();
            if (priority == null) {
                return DEFAULT_PRIORITY;
            } else {
                return priority.byteValue();
            }
        }
    }

    @Override
    public void setPriority(byte priority) throws JMSException {
        if (priority == DEFAULT_PRIORITY) {
            if (message.getHeader() == null) {
                return;
            } else {
                message.getHeader().setPriority(null);
            }
        } else {
            message.setPriority(priority);
        }
    }

    @Override
    public long getExpiration() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setExpiration(long expiration) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsDestination getDestination() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDestination(JmsDestination destination) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsDestination getReplyTo() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReplyTo(JmsDestination replyTo) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getUserId() throws JMSException {
        String userId = null;
        byte[] userIdBytes = message.getUserId();

        if (userIdBytes != null) {
            userId = new String(userIdBytes, UTF8);
        }

        return userId;
    }

    @Override
    public void setUserId(String userId) throws JMSException {
        message.setUserId(userId.getBytes(UTF8));
    }

    @Override
    public String getGroupId() throws JMSException {
        return message.getGroupId();
    }

    @Override
    public void setGroupId(String groupId) throws JMSException {
        message.setGroupId(groupId);
    }

    @Override
    public int getGroupSequence() throws JMSException {
        if (message.getProperties() == null) {
            return 0;
        } else {
            UnsignedInteger sequence = message.getProperties().getGroupSequence();
            if (sequence == null) {
                return 0;
            } else {
                return sequence.intValue();
            }
        }
    }

    @Override
    public void setGroupSequence(int groupSequence) throws JMSException {
        if (groupSequence < 0 && message.getProperties() != null) {
            message.getProperties().setGroupSequence(null);
        } else {
            message.setGroupSequence(groupSequence);
        }
    }

    /**
     * @return the true AMQP Message instance wrapped by this Facade.
     */
    Message getAmqpMessage() {
        return this.message;
    }

    /**
     * The AmqpConnection instance that is associated with this Message.
     * @return
     */
    AmqpConnection getConnection() {
        return connection;
    }
}
