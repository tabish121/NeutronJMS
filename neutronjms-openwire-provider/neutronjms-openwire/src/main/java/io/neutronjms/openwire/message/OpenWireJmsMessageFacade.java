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
package io.neutronjms.openwire.message;

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.message.facade.JmsMessageFacade;
import io.neutronjms.jms.meta.JmsMessageId;

import java.io.IOException;
import java.util.Map;

import javax.jms.JMSException;

/**
 * Facade that wraps an ActiveMQ Message instance.
 */
public class OpenWireJmsMessageFacade implements JmsMessageFacade {

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
    public byte[] getCorrelationIdBytes() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCorrelationIdBytes(byte[] correlationId) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isPersistent() throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setPersistent(boolean value) throws JMSException {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPriority(byte priority) throws JMSException {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUserId(String userId) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getGroupId() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGroupId(String groupId) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getGroupSequence() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setGroupSequence(int groupSequence) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }
}
