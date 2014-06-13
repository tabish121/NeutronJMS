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
 * The Message Facade interface defines the required mapping between a Provider's
 * own Message type and the JMS Message types.  A Provider can implement the Facade
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

    /**
     * Returns the Type values as defined by the provider or set by the sending client.
     *
     * @return a String value that defines the message type.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    String getType() throws JMSException;

    /**
     * Sets the String value used to define the Message type by the client.
     *
     * @param type
     *        the type value the client assigns to this message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setType(String type) throws JMSException;

    /**
     * Returns the assigned priority value of this message in JMS ranged scoping.
     *
     * If the provider does not define a message priority value in its message objects
     * or the value is not set in the message this method should return the JMS default
     * value of 4.
     *
     * @return the priority value assigned to this message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    byte getPriority() throws JMSException;

    /**
     * Sets the message priority for this message using a JMS priority scoped value.
     *
     * @param priority
     *        the new priority value to set on this message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setPriority(byte priority) throws JMSException;

    /**
     * Returns the set expiration time for this message.
     *
     * The value should be returned as an absolute time given in GMT time.
     *
     * @return the time that this message expires or zero if it never expires.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    long getExpiration() throws JMSException;

    /**
     * Sets an expiration time on this message.
     *
     * The expiration time will be given as an absolute time in GMT time.
     *
     * @param expiration
     *        the time that this message should be considered as expired.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setExpiration(long expiration) throws JMSException;

    /**
     * Gets the Destination value that was assigned to this message at the time it was
     * sent.
     *
     * @return the destination to which this message was originally sent.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    JmsDestination getDestination() throws JMSException;

    /**
     * Sets the Destination that this message is being sent to.
     *
     * @param destination
     *        the destination that this message is being sent to.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setDestination(JmsDestination destination) throws JMSException;

    /**
     * Gets the Destination where replies for this Message are to be sent to.
     *
     * @return the reply to destination for this message or null if none set.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    JmsDestination getReplyTo() throws JMSException;

    /**
     * Sets the Destination where replies to this Message are to be sent.
     *
     * @param replyTo
     *        the Destination where replies should be sent, or null to clear.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setReplyTo(JmsDestination replyTo) throws JMSException;

    /**
     * Returns the ID of the user that sent this message if available.
     *
     * @return the user ID that was in use when this message was sent or null if not set.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    String getUserId() throws JMSException;

    /**
     * Sets the User ID for the connection that is being used to send this message.
     *
     * @param userId
     *        the user ID that sent this message or null to clear.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setUserId(String userId) throws JMSException;

    /**
     * Gets the Group ID that this message is assigned to.
     *
     * @return the Group ID this message was sent in.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    String getGroupId() throws JMSException;

    /**
     * Sets the Group ID to use for this message.
     *
     * @param groupId
     *        the Group ID that this message is assigned to.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setGroupId(String groupId) throws JMSException;

    /**
     * Gets the assigned group sequence of this message.
     *
     * @return the assigned group sequence of this message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    int getGroupSequence() throws JMSException;

    /**
     * Sets the group sequence value for this message.
     *
     * @param groupSequence
     *        the group sequence value to assign this message.
     *
     * @throws JMSException if an error occurs while accessing the property.
     */
    void setGroupSequence(int groupSequence) throws JMSException;

    /**
     * This method should provide a quick check on the message to determine if
     * there is any content actually contained within.
     *
     * @return true if the message content is non-empty.
     */
    boolean isEmpty();

}
