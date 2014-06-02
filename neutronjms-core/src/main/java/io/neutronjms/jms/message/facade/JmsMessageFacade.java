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
package io.neutronjms.jms.message.facade;

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.meta.JmsMessageId;

import java.io.IOException;
import java.util.Map;

import javax.jms.JMSException;

/**
 * The Message Proxy interface defines the required mapping between a Provider's
 * own Message type and the JMS Message types.  A Provider can implement the Proxy
 * interface and offer direct access to its message types without the need to
 * copy to / from a more generic JMS message instance.
 */
public interface JmsMessageFacade {

    /**
     * Returns the Message properties contained within this Message instance in
     * a new Unmodifiable Map instance.
     *
     * @return a Map containing the properties of this Message that cannot be modified.
     *
     * @throws IOException if an error occurs while accessing the Message properties.
     */
    public Map<String, Object> getProperties() throws IOException;

    /**
     * @returns true if the given property exists within the message.
     *
     * @throws IOException if an error occurs while accessing the Message properties.
     */
    boolean propertyExists(String key) throws IOException;

    /**
     * Returns the property stored in the message accessed via the given key/
     *
     * @param key
     *        the key used to access the given property.
     *
     * @throws IOException if an error occurs while accessing the Message properties.
     */
    Object getProperty(String key) throws IOException;

    /**
     * Sets the message property value using the supplied key to identify the value
     * that should be set or updated.
     *
     * @param key
     *        the key that identifies the message property.
     * @param value
     *        the value that is to be stored in the message.
     *
     * @throws IOException if an error occurs while accessing the Message properties.
     */
    void setProperty(String key, Object value) throws IOException;

    /**
     * Called when a message is sent to allow a Message instance to move the
     * contents from a logical data structure to a binary form for transmission.
     *
     * @throws JMSException if an error occurs while preparing the message for send.
     */
    void onSend() throws JMSException;

    /**
     * Clears the contents of this Message.
     *
     * @throws JMSException if an error occurs while accessing the message body.
     */
    void clearBody() throws JMSException;

    /**
     * Clears any Message properties that exist for this Message instance.
     *
     * @throws JMSException if an error occurs while accessing the message properties.
     */
    void clearProperties() throws JMSException;

    /**
     * Create a new instance and perform a deep copy of this object's
     * contents.
     */
    JmsMessageFacade copy() throws JMSException;

    /**
     * Return the internal message Id as a JmsMessageId wrapped value.
     *
     * @return a JmsMessageId that wraps the internal message Id.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    JmsMessageId getMessageId() throws JMSException;

    /**
     * Updates the message Id using the value of the given JmsMessageId.
     *
     * @param messageId
     *        the new JmsMessageId value to assign as the message Id.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setMessageId(JmsMessageId messageId) throws JMSException;

    /**
     * Gets the timestamp assigned to the message when it was sent.
     *
     * @return the message timestamp value.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    long getTimestamp() throws JMSException;

    /**
     * Sets the timestamp value of this message.
     *
     * @param timestamp
     *        the time that the message was sent by the provider.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setTimestamp(long timestamp) throws JMSException;

    /**
     * Returns the correlation ID set on this message if one exists, null otherwise.
     *
     * @return the set correlation ID or null if not set.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    String getCorrelationId() throws JMSException;

    /**
     * Sets the correlation ID for this message.
     *
     * @param correlationId
     *        The correlation ID to set on this message, or null to clear.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setCorrelationId(String correlationId) throws JMSException;

    /**
     * Gets the set correlation ID of the message in raw bytes form.  If no ID was
     * set then this method may return null or an empty byte array.
     *
     * @return a byte array containing the correlation ID value in raw form.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    byte[] getCorrelationIdBytes() throws JMSException;

    /**
     * Sets the correlation ID of the message in raw byte form.  Setting the value
     * as null or an empty byte array will clear any previously set value.  If the
     * underlying protocol cannot convert or map the given byte value to it's own
     * internal representation it should throw a JMSException indicating the error.
     *
     * @param correlationId
     *        the byte array to use to set the message correlation ID.
     *
     * @throws JMSException if an error occurs setting the bytes as the protocol's
     *                      correlation ID value.
     */
    void setCorrelationIdBytes(byte[] correlationId) throws JMSException;

    /**
     * @return true if this message is tagged as being persistent.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    boolean isPersistent() throws JMSException;

    /**
     * Sets the persistent flag on this message.
     *
     * @param value
     *        true if the message is to be marked as persistent.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setPersistent(boolean value) throws JMSException;

    /**
     * Returns the current redelivery count of the Message as set in the underlying
     * message instance.
     *
     * @return the current redelivery count.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    int getRedeliveryCounter() throws JMSException;

    /**
     * Used to update the message redelivery after a local redelivery of the Message
     * has been performed.
     *
     * @param redeliveryCount
     *        the new redelivery count to assign the Message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setRedeliveryCounter(int redeliveryCount) throws JMSException;

    String getType() throws JMSException;

    void setType(String type) throws JMSException;

    byte getPriority() throws JMSException;

    void setPriority(byte priority) throws JMSException;

    long getExpiration() throws JMSException;

    void setExpiration(long expiration) throws JMSException;

    JmsDestination getDestination() throws JMSException;

    void setDestination(JmsDestination destination) throws JMSException;

    JmsDestination getReplyTo() throws JMSException;

    void setReplyTo(JmsDestination replyTo) throws JMSException;

    String getUserId() throws JMSException;

    void setUserId(String userId) throws JMSException;

    String getGroupId() throws JMSException;

    void setGroupId(String groupId) throws JMSException;

    int getGroupSequence() throws JMSException;

    void setGroupSequence(int groupSequence) throws JMSException;

}
