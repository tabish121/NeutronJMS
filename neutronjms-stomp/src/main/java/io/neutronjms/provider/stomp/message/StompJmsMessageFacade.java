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
package io.neutronjms.provider.stomp.message;

import static io.neutronjms.provider.stomp.StompConstants.CONTENT_LENGTH;
import static io.neutronjms.provider.stomp.StompConstants.CORRELATION_ID;
import static io.neutronjms.provider.stomp.StompConstants.DESTINATION;
import static io.neutronjms.provider.stomp.StompConstants.EXPIRATION_TIME;
import static io.neutronjms.provider.stomp.StompConstants.FALSE;
import static io.neutronjms.provider.stomp.StompConstants.JMSX_DELIVERY_COUNT;
import static io.neutronjms.provider.stomp.StompConstants.JMSX_GROUP_ID;
import static io.neutronjms.provider.stomp.StompConstants.JMSX_GROUP_SEQUENCE;
import static io.neutronjms.provider.stomp.StompConstants.MESSAGE_ID;
import static io.neutronjms.provider.stomp.StompConstants.PERSISTENT;
import static io.neutronjms.provider.stomp.StompConstants.PRIORITY;
import static io.neutronjms.provider.stomp.StompConstants.RECEIPT_REQUESTED;
import static io.neutronjms.provider.stomp.StompConstants.REDELIVERED;
import static io.neutronjms.provider.stomp.StompConstants.REPLY_TO;
import static io.neutronjms.provider.stomp.StompConstants.SEND;
import static io.neutronjms.provider.stomp.StompConstants.SUBSCRIPTION;
import static io.neutronjms.provider.stomp.StompConstants.TIMESTAMP;
import static io.neutronjms.provider.stomp.StompConstants.TRANSFORMATION;
import static io.neutronjms.provider.stomp.StompConstants.TRUE;
import static io.neutronjms.provider.stomp.StompConstants.TYPE;
import static io.neutronjms.provider.stomp.StompConstants.USERID;
import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.message.facade.JmsMessageFacade;
import io.neutronjms.jms.meta.JmsMessageId;
import io.neutronjms.provider.stomp.StompConnection;
import io.neutronjms.provider.stomp.StompConstants;
import io.neutronjms.provider.stomp.StompFrame;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;

/**
 * A STOMP based JmsMessageFacade that wraps a given STOMP frame containing a
 * MESSAGE command.  The facade will map the standard JMS properties onto the
 * appropriate STOMP properties and
 */
public class StompJmsMessageFacade implements JmsMessageFacade {

    static HashSet<String> RESERVED_HEADER_NAMES = new HashSet<String>();
    static{
        RESERVED_HEADER_NAMES.add(DESTINATION);
        RESERVED_HEADER_NAMES.add(REPLY_TO);
        RESERVED_HEADER_NAMES.add(MESSAGE_ID);
        RESERVED_HEADER_NAMES.add(CORRELATION_ID);
        RESERVED_HEADER_NAMES.add(EXPIRATION_TIME);
        RESERVED_HEADER_NAMES.add(TIMESTAMP);
        RESERVED_HEADER_NAMES.add(PRIORITY);
        RESERVED_HEADER_NAMES.add(REDELIVERED);
        RESERVED_HEADER_NAMES.add(TYPE);
        RESERVED_HEADER_NAMES.add(PERSISTENT);
        RESERVED_HEADER_NAMES.add(RECEIPT_REQUESTED);
        RESERVED_HEADER_NAMES.add(TRANSFORMATION);
        RESERVED_HEADER_NAMES.add(SUBSCRIPTION);
        RESERVED_HEADER_NAMES.add(CONTENT_LENGTH);
        RESERVED_HEADER_NAMES.add(JMSX_DELIVERY_COUNT);
        RESERVED_HEADER_NAMES.add(JMSX_GROUP_ID);
        RESERVED_HEADER_NAMES.add(JMSX_GROUP_SEQUENCE);
    }

    protected final StompFrame message;
    protected final StompConnection connection;

    /**
     * Creates a new StompJmsMessageFacade for use in sending a new Message.
     *
     * @param connection
     *        the STOMP connection instance to assign to this Facade.
     */
    public StompJmsMessageFacade(StompConnection connection) {
        this.message = new StompFrame().setCommand(SEND);
        this.connection = connection;
    }

    /**
     * Creates a new wrapper around the StompFrame Message instance.
     *
     * @param message
     *        the STOMP message to wrap.
     * @param connection
     *        the STOMP connection instance to assign to this Facade.
     */
    public StompJmsMessageFacade(StompFrame message, StompConnection connection) {
        this.message = message;
        this.connection = connection;
    }

    /**
     * @return the underlying STOMP frame that backs this Facade.
     */
    public StompFrame getStompMessage() {
        return this.message;
    }

    /**
     * @return the StompConnection for this message facade.
     */
    public StompConnection getStompConnection() {
        return this.connection;
    }

    @Override
    public Map<String, Object> getProperties() throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>();
        for (Entry<String, String> entry : message.getProperties().entrySet()) {
            if (!RESERVED_HEADER_NAMES.contains(entry.getKey())) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean propertyExists(String key) throws IOException {
        return message.getProperty(key) != null;
    }

    @Override
    public Object getProperty(String key) throws IOException {
        return message.getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) throws IOException {
        if (!RESERVED_HEADER_NAMES.contains(key)) {
            message.setProperty(key, value.toString());
        }
    }

    @Override
    public void onSend() throws JMSException {
        // TODO - Nothing yet needed here.
    }

    @Override
    public boolean isEmpty() {
        return message.getContent() != null && !message.getContent().isEmpty();
    }

    @Override
    public void clearBody() {
        message.setContent(null);
    }

    @Override
    public void clearProperties() {
        message.getProperties().entrySet().retainAll(RESERVED_HEADER_NAMES);
    }

    @Override
    public JmsMessageFacade copy() {
        return new StompJmsMessageFacade(message.clone(), connection);
    }

    @Override
    public JmsMessageId getMessageId() {
        return new JmsMessageId(message.getProperty(MESSAGE_ID));
    }

    @Override
    public void setMessageId(JmsMessageId messageId) {
        message.setProperty(MESSAGE_ID, messageId.toString());
    }

    @Override
    public long getTimestamp() {
        return or(getLongProperty(TIMESTAMP), 0L);
    }

    @Override
    public void setTimestamp(long timestamp) {
        setLongProperty(TIMESTAMP, timestamp);
    }

    @Override
    public String getCorrelationId() {
        return message.getProperty(CORRELATION_ID);
    }

    @Override
    public void setCorrelationId(String correlationId) {
        setStringProperty(CORRELATION_ID, correlationId);
    }

    @Override
    public byte[] getCorrelationIdBytes() throws JMSException {
        String correlationId = getCorrelationId();
        if (correlationId != null) {
            return correlationId.getBytes(StompConstants.UTF8);
        }

        return null;
    }

    @Override
    public void setCorrelationIdBytes(byte[] correlationId) throws JMSException {
        String newValue = null;

        if (correlationId != null && correlationId.length > 0) {
            newValue = new String(correlationId, StompConstants.UTF8);
        }

        setStringProperty(CORRELATION_ID, newValue);
    }

    @Override
    public boolean isPersistent() {
        return or(getBooleanProperty(PERSISTENT), false);
    }

    @Override
    public void setPersistent(boolean value) {
        setBooleanProperty(PERSISTENT, value);
    }

    @Override
    public int getRedeliveryCounter() {
        return or(getIntProperty(JMSX_DELIVERY_COUNT), 0);
    }

    @Override
    public void setRedeliveryCounter(int redeliveryCount) {
        setIntegerProperty(JMSX_DELIVERY_COUNT, redeliveryCount);
    }

    @Override
    public boolean isRedelivered() throws JMSException {
        return getRedeliveryCounter() > 0;
    }

    @Override
    public void setRedelivered(boolean redelivered) throws JMSException {
        if (redelivered) {
            if (!isRedelivered()) {
                setRedeliveryCounter(1);
            }
        } else {
            if (isRedelivered()) {
                setRedeliveryCounter(0);
            }
        }
    }

    @Override
    public String getType() {
        return message.getProperty(TYPE);
    }

    @Override
    public void setType(String type) {
        setStringProperty(TYPE, type);
    }

    @Override
    public byte getPriority() {
        return or(getByteProperty(PRIORITY), (byte) 4);
    }

    @Override
    public void setPriority(byte priority) {
        setByteProperty(PRIORITY, priority);
    }

    @Override
    public long getExpiration() {
        return or(getLongProperty(EXPIRATION_TIME), 0L);
    }

    @Override
    public void setExpiration(long expiration) {
        setLongProperty(EXPIRATION_TIME, expiration);
    }

    @Override
    public JmsDestination getDestination() throws JMSException {
        return toJmsDestination(message.getProperty(DESTINATION));
    }

    @Override
    public void setDestination(JmsDestination destination) {
        setStringProperty(DESTINATION, toStompDestination(destination));
    }

    @Override
    public JmsDestination getReplyTo() throws JMSException {
        return toJmsDestination(message.getProperty(REPLY_TO));
    }

    @Override
    public void setReplyTo(JmsDestination replyTo) {
        setStringProperty(REPLY_TO, toStompDestination(replyTo));
    }

    @Override
    public String getUserId() {
        return message.getProperty(USERID);
    }

    @Override
    public void setUserId(String userId) {
        setStringProperty(USERID, userId);
    }

    @Override
    public String getGroupId() {
        return message.getProperty(JMSX_GROUP_ID);
    }

    @Override
    public void setGroupId(String groupId) {
        setStringProperty(JMSX_GROUP_ID, groupId);
    }

    @Override
    public int getGroupSequence() {
        return or(getIntProperty(JMSX_GROUP_SEQUENCE), 0);
    }

    @Override
    public void setGroupSequence(int groupSequence) {
        setIntegerProperty(JMSX_GROUP_SEQUENCE, groupSequence);
    }

    private void setStringProperty(String key, String value) {
        if (value == null) {
            message.removeProperty(key);
        } else {
            message.setProperty(key, value);
        }
    }

    private void setLongProperty(String key, Long value) {
        if (value == null) {
            message.removeProperty(key);
        } else {
            message.setProperty(key, value.toString());
        }
    }

    private Long getLongProperty(String key) {
        String vale = message.getProperty(key);
        if (vale == null) {
            return null;
        } else {
            return Long.parseLong(vale.toString());
        }
    }

    private Integer getIntProperty(String key) {
        String vale = message.getProperty(key);
        if (vale == null) {
            return null;
        } else {
            return Integer.parseInt(vale.toString());
        }
    }

    private void setIntegerProperty(String key, Integer value) {
        if (value == null) {
            message.removeProperty(key);
        } else {
            message.setProperty(key, value.toString());
        }
    }

    private Byte getByteProperty(String key) {
        String vale = message.getProperty(key);
        if (vale == null) {
            return null;
        } else {
            return Byte.parseByte(vale.toString());
        }
    }

    private void setByteProperty(String key, Byte value) {
        if (value == null) {
            message.removeProperty(key);
        } else {
            message.setProperty(key, value.toString());
        }
    }

    private Boolean getBooleanProperty(String key) {
        String vale = message.getProperty(key);
        if (vale == null) {
            return null;
        } else {
            return Boolean.parseBoolean(vale.toString());
        }
    }

    private void setBooleanProperty(String key, Boolean value) {
        if (value == null) {
            message.removeProperty(key);
        } else {
            message.setProperty(key, value.booleanValue() ? TRUE : FALSE);
        }
    }

    private <T> T or(T value, T other) {
        if (value != null) {
            return value;
        } else {
            return other;
        }
    }

    private String toStompDestination(JmsDestination destination) {
        return connection.getServerAdapter().toStompDestination(destination);
    }

    private JmsDestination toJmsDestination(String destinationName) throws JMSException {
        return connection.getServerAdapter().toJmsDestination(destinationName);
    }
}
