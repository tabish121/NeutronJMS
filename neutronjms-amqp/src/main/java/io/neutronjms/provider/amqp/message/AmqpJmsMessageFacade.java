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
import java.util.Map;

import javax.jms.JMSException;

import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.message.Message;

/**
 *
 */
public class AmqpJmsMessageFacade implements JmsMessageFacade {

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
    public void clearBody() {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearProperties() {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsMessageFacade copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JmsMessageId getMessageId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMessageId(JmsMessageId messageId) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getTimestamp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setTimestamp(long timestamp) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getCorrelationId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCorrelationId(String correlationId) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isPersistent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setPersistent(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getRedeliveryCounter() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRedeliveryCounter(int redeliveryCount) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setType(String type) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte getPriority() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPriority(byte priority) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getExpiration() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setExpiration(long expiration) {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsDestination getDestination() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDestination(JmsDestination destination) {
        // TODO Auto-generated method stub

    }

    @Override
    public JmsDestination getReplyTo() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReplyTo(JmsDestination replyTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getUserId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUserId(String userId) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getGroupId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGroupId(String groupId) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getGroupSequence() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setGroupSequence(int groupSequence) {
        // TODO Auto-generated method stub

    }

    public Message getAmqpMessage() {
        return this.message;
    }
}
