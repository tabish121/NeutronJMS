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

import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_MESSAGE;
import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_MSG_TYPE;
import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.message.facade.JmsMessageFacade;
import io.neutronjms.jms.meta.JmsMessageId;
import io.neutronjms.provider.amqp.AmqpConnection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.UnsignedByte;
import org.apache.qpid.proton.amqp.UnsignedInteger;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton.message.Message;

/**
 *
 */
public class AmqpJmsMessageFacade implements JmsMessageFacade {

    private static final int DEFAULT_PRIORITY = javax.jms.Message.DEFAULT_PRIORITY;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    protected final Message message;
    protected final AmqpConnection connection;

    private MessageAnnotations annotations;
    private Map<Symbol,Object> annotationsMap;
    private Map<String,Object> propertiesMap;

    private JmsDestination replyTo;
    private JmsDestination destination;

    private Long syntheticTTL;

    /**
     * Create a new AMQP Message Facade with an empty message instance.
     */
    public AmqpJmsMessageFacade(AmqpConnection connection) {
        this.message = Proton.message();
        this.connection = connection;
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
    @SuppressWarnings("unchecked")
    public AmqpJmsMessageFacade(AmqpConnection connection, Message message) {
        this.message = message;
        this.connection = connection;

        annotations = message.getMessageAnnotations();
        if (annotations != null) {
            annotationsMap = annotations.getValue();
        }

        if (message.getApplicationProperties() != null) {
            propertiesMap = message.getApplicationProperties().getValue();
        }

        Long ttl = message.getTtl();
        Long absoluteExpiryTime = getAbsoluteExpiryTime();
        if (absoluteExpiryTime == null && ttl != null) {
            syntheticTTL = System.currentTimeMillis() + ttl;
        }

        // TODO - Set destination
        // TODO - Set replyTo
    }

    /**
     * @return the appropriate byte value that indicates the type of message this is.
     */
    public byte getJmsMsgType() {
        return JMS_MESSAGE;
    }

    /**
     * The annotation value for the JMS Message content type.  For a generic JMS message this
     * value is omitted so we return null here, subclasses should override this to return the
     * correct content type value for their payload.
     *
     * @return a String value indicating the message content type.
     */
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Map<String, Object> getProperties() throws IOException {
        lazyCreateProperties();
        return Collections.unmodifiableMap(new HashMap<String, Object>(propertiesMap));
    }

    @Override
    public boolean propertyExists(String key) throws IOException {
        if (propertiesMap != null) {
            return propertiesMap.containsKey(key);
        }

        // TODO - Examine message annotations for other message properties.

        return false;
    }

    @Override
    public Object getProperty(String key) throws IOException {
        if (propertiesMap != null) {
            return propertiesMap.get(key);
        }

        // TODO - Should other message annotations be included here?

        return null;
    }

    @Override
    public void setProperty(String key, Object value) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Property key must not be null");
        }

        if (propertiesMap == null) {
            lazyCreateProperties();
        }

        // TODO - intercept annotations or message level values by name \

        propertiesMap.put(key, value);
    }

    @Override
    public void onSend() throws JMSException {
        String contentType = getContentType();
        byte jmsMsgType = getJmsMsgType();

        if (contentType != null) {
            message.setContentType(contentType);
        }
        setAnnotation(JMS_MSG_TYPE, jmsMsgType);
    }

    @Override
    public void clearBody() throws JMSException {
        message.setBody(null);
    }

    @Override
    public void clearProperties() throws JMSException {
        clearProperties();
        //_propJMS_AMQP_TTL = null;
        message.setReplyToGroupId(null);
        message.setUserId(null);
        message.setGroupId(null);
        setGroupSequence(0);

        // TODO - Clear others as needed.
    }

    @Override
    public JmsMessageFacade copy() throws JMSException {
        AmqpJmsMessageFacade copy = new AmqpJmsMessageFacade(connection, message);
        copyInto(copy);
        return copy;
    }

    protected void copyInto(AmqpJmsMessageFacade target) {
        // TODO - Copy message.
    }

    @Override
    public JmsMessageId getMessageId() throws JMSException {
        Object result = message.getMessageId();
        if (result != null) {
            if (result instanceof String) {
                return new JmsMessageId((String) result);
            } else {
                throw new JMSException("No support for non-String IDs yet.");
            }
        }

        return null;
    }

    @Override
    public void setMessageId(JmsMessageId messageId) throws JMSException {
        if (messageId != null) {
            message.setMessageId(messageId.toString());
        } else {
            message.setMessageId(null);
        }
    }

    @Override
    public long getTimestamp() throws JMSException {
        if (message.getProperties() != null) {
            Date timestamp = message.getProperties().getCreationTime();
            if (timestamp != null) {
                return timestamp.getTime();
            }
        }

        return 0L;
    }

    @Override
    public void setTimestamp(long timestamp) throws JMSException {
        if (message.getProperties() != null) {
            if (timestamp != 0) {
                message.setCreationTime(timestamp);
            } else {
                message.getProperties().setCreationTime(null);
            }
        }
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
        return message.isDurable();
    }

    @Override
    public void setPersistent(boolean value) throws JMSException {
        this.message.setDurable(value);
    }

    @Override
    public int getRedeliveryCounter() throws JMSException {
        if (message.getHeader() != null) {
            UnsignedInteger count = message.getHeader().getDeliveryCount();
            if (count != null) {
                return count.intValue();
            }
        }

        return 0;
    }

    @Override
    public void setRedeliveryCounter(int redeliveryCount) throws JMSException {
        if (redeliveryCount == 0) {
            if (message.getHeader() != null) {
                message.getHeader().setDeliveryCount(null);
            }
        } else {
            message.setDeliveryCount(redeliveryCount);
        }
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
    public String getType() throws JMSException {
        return (String) getAnnotation(JMS_MSG_TYPE);
    }

    @Override
    public void setType(String type) throws JMSException {
        setAnnotation(JMS_MSG_TYPE, type);
    }

    @Override
    public byte getPriority() throws JMSException {
        if (message.getHeader() != null) {
            UnsignedByte priority = message.getHeader().getPriority();
            if (priority != null) {
                return priority.byteValue();
            }
        }

        return DEFAULT_PRIORITY;
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
        Long absoluteExpiry = getAbsoluteExpiryTime();
        if (absoluteExpiry != null) {
            return absoluteExpiry;
        }

        if (syntheticTTL != null) {
            return syntheticTTL;
        }

        return 0;
    }

    @Override
    public void setExpiration(long expiration) throws JMSException {
        syntheticTTL = null;

        if (expiration != 0) {
            setAbsoluteExpiryTime(expiration);
        } else {
            setAbsoluteExpiryTime(null);
        }
    }

    @Override
    public JmsDestination getDestination() throws JMSException {
        return destination;
    }

    @Override
    public void setDestination(JmsDestination destination) throws JMSException {
        this.destination = destination;

        // TODO
    }

    @Override
    public JmsDestination getReplyTo() throws JMSException {
        return replyTo;
    }

    @Override
    public void setReplyTo(JmsDestination replyTo) throws JMSException {
        this.replyTo = replyTo;
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
        if (message.getProperties() != null) {
            UnsignedInteger sequence = message.getProperties().getGroupSequence();
            if (sequence != null) {
                return sequence.intValue();
            }
        }

        return 0;
    }

    @Override
    public void setGroupSequence(int groupSequence) throws JMSException {
        if (groupSequence < 0 && message.getProperties() != null) {
            message.getProperties().setGroupSequence(null);
        } else if (groupSequence > 0) {
            message.setGroupSequence(groupSequence);
        }
    }

    /**
     * @return the true AMQP Message instance wrapped by this Facade.
     */
    public Message getAmqpMessage() {
        return this.message;
    }

    /**
     * The AmqpConnection instance that is associated with this Message.
     * @return
     */
    public AmqpConnection getConnection() {
        return connection;
    }

    /**
     * Checks for the presence of a given message annotation and returns true
     * if it is contained in the current annotations.  If the annotations have
     * not yet been initialized then this method always returns false.
     *
     * @param key
     *        the name of the annotation to query for.
     *
     * @return true if the annotation is present, false in not or annotations not initialized.
     */
    boolean annotationExists(String key) {
        if (annotationsMap == null) {
            return false;
        }

        return annotationsMap.containsKey(AmqpMessageSupport.getSymbol(key));
    }

    /**
     * Given an annotation name, lookup and return the value associated with that
     * annotation name.  If the message annotations have not been created yet then
     * this method will always return null.
     *
     * @param key
     *        the Symbol name that should be looked up in the message annotations.
     *
     * @return the value of the annotation if it exists, or null if not set or not accessible.
     */
    Object getAnnotation(String key) {
        if (annotationsMap == null) {
            return null;
        }

        return annotationsMap.get(AmqpMessageSupport.getSymbol(key));
    }

    /**
     * Removes a message annotation if the message contains it.  Will not do
     * a lazy create on the message annotations so caller cannot count on the
     * existence of the message annotations after a call to this method.
     *
     * @param key
     *        the annotation key that is to be removed from the current set.
     */
    void removeAnnotation(String key) {
        if (annotationsMap == null) {
            return;
        }

        annotationsMap.remove(AmqpMessageSupport.getSymbol(key));
    }

    /**
     * Perform a proper annotation set on the AMQP Message based on a Symbol key and
     * the target value to append to the current annotations.
     *
     * @param key
     *        The name of the Symbol whose value is being set.
     * @param value
     *        The new value to set in the annotations of this message.
     */
    void setAnnotation(String key, Object value) {
        lazyCreateAnnotations();
        annotationsMap.put(AmqpMessageSupport.getSymbol(key), value);
    }

    /**
     * Removes all message annotations from this message.
     */
    void clearAnnotations() {
        annotationsMap = null;
        annotations = null;
        message.setMessageAnnotations(null);
    }

    /**
     * Removes all application level properties from the Message.
     */
    void clearAllApplicationProperties() {
        propertiesMap = null;
        message.setApplicationProperties(null);
    }

    /**
     * Returns a set of all the property names that have been set in this message.
     *
     * @return a set of property names in the message or an empty set if none are set.
     */
    public Set<String> getPropertyNames() {
        if (propertiesMap != null) {
            return propertiesMap.keySet();
        } else {
            return Collections.emptySet();
        }
    }

    private Long getAbsoluteExpiryTime() {
        Long result = null;
        if (message.getProperties() != null) {
            Date date = message.getProperties().getAbsoluteExpiryTime();
            if (date != null) {
                result = date.getTime();
            }
        }

        return result;
    }

    private void setAbsoluteExpiryTime(Long expiration) {
        if (expiration == null) {
            if (message.getProperties() != null) {
                message.getProperties().setAbsoluteExpiryTime(null);
            }
        } else {
            message.setExpiryTime(expiration);
        }
    }

    private void lazyCreateAnnotations() {
        if (annotationsMap == null) {
            annotationsMap = new HashMap<Symbol,Object>();
            annotations = new MessageAnnotations(annotationsMap);
            message.setMessageAnnotations(annotations);
        }
    }

    private void lazyCreateProperties() {
        propertiesMap = new HashMap<String,Object>();
        message.setApplicationProperties(new ApplicationProperties(propertiesMap));
    }
}
