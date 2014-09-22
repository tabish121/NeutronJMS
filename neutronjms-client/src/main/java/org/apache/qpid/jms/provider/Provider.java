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
package org.apache.qpid.jms.provider;

import java.io.IOException;
import java.net.URI;

import javax.jms.JMSException;

import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.apache.qpid.jms.message.JmsMessageFactory;
import org.apache.qpid.jms.message.JmsOutboundMessageDispatch;
import org.apache.qpid.jms.meta.JmsConsumerId;
import org.apache.qpid.jms.meta.JmsResource;
import org.apache.qpid.jms.meta.JmsSessionId;
import org.apache.qpid.jms.provider.ProviderConstants.ACK_TYPE;

/**
 * Defines the interface that an Implementation of a Specific wire level protocol
 * provider must implement.  This Provider interface requires that the implementation
 * methods all operate in an asynchronous manner.
 */
public interface Provider {

    /**
     * Performs the initial low level connection for this provider such as TCP or
     * SSL connection to a remote Broker.  If this operation fails then the Provider
     * is considered to be unusable and no further operations should be attempted
     * using this Provider.
     *
     * @throws IOException if the remote resource can not be contacted.
     */
    void connect() throws IOException;

    /**
     * Starts the Provider.  The start method provides a place for the Provider to perform
     * and pre-start configuration checks to ensure that the current state is valid and that
     * all contracts have been met prior to starting.
     *
     * @throws IOException if an error occurs during start processing.
     * @throws IllegalStateException if the Provider is improperly configured.
     */
    void start() throws IOException, IllegalStateException;

    /**
     * Closes this Provider terminating all connections and canceling any pending
     * operations.  The Provider is considered unusable after this call.  This call
     * is a blocking call and will not return until the Provider has closed or an
     * error occurs.
     */
    void close();

    /**
     * Returns the URI used to configure this Provider and specify the remote address of the
     * Broker it connects to.
     *
     * @return the URI used to configure this Provider.
     */
    URI getRemoteURI();

    /**
     * Create the Provider version of the given JmsResource.
     *
     * For each JMS Resource type the Provider implementation must create it's own internal
     * representation and upon successful creation provide the caller with a response.  The
     * Provider should examine the given JmsResource to determine if the given configuration
     * is supported or can be simulated, or is not supported in which case an exception should be
     * thrown.
     *
     * It is possible for a Provider to indicate that it cannot complete a requested create
     * either due to some mis-configuration such as bad login credentials on connection create
     * by throwing a JMSException.  If the Provider does not support creating of the indicated
     * resource such as a Temporary Queue etc the provider may throw an UnsupportedOperationException
     * to indicate this.
     *
     * @param resource
     *        The JmsResouce instance that indicates what is being created.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such as bad credentials.
     */
    void create(JmsResource resource, AsyncResult request) throws IOException, JMSException;

    /**
     * Starts the Provider version of the given JmsResource.
     *
     * For some JMS Resources it is necessary or advantageous to have a started state that
     * must be triggered prior to it's normal use.
     *
     * An example of this would be a MessageConsumer which should not receive any incoming
     * messages until the JMS layer is in a state to handle them.  One such time would be
     * after connection recovery.  A JMS consumer should normally recover with it's prefetch
     * value set to zero, or an AMQP link credit of zero and only open up the credit window
     * once all Connection resources are restored.
     *
     * The provider is required to implement this method and not throw any error other than
     * an IOException if a communication error occurs.  The start operation is not required to
     * have any effect on the provider resource but must not throw UnsupportedOperation etc.
     *
     * @param resource
     *        The JmsResouce instance that indicates what is being started.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such as already closed resource.
     */
    void start(JmsResource resource, AsyncResult request) throws IOException, JMSException;

    /**
     * Instruct the Provider to dispose of a given JmsResource.
     *
     * The provider is given a JmsResource which it should use to remove any associated
     * resources and inform the remote Broker instance of the removal of this resource.
     *
     * If the Provider cannot destroy the resource due to a non-communication error such as
     * the logged in user not have role access to destroy the given resource it may throw an
     * instance of JMSException to indicate such an error.
     *
     * @param resource
     *        The JmsResouce that identifies a previously created JmsResource.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such as not authorized.
     */
    void destroy(JmsResource resourceId, AsyncResult request) throws IOException, JMSException;

    /**
     * Sends the JmsMessage contained in the outbound dispatch envelope.
     *
     * @param envelope
     *        the message envelope containing the JmsMessage to send.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error that maps to JMS occurs such as not authorized.
     */
    void send(JmsOutboundMessageDispatch envelope, AsyncResult request) throws IOException, JMSException;

    /**
     * Called to acknowledge all messages that have been delivered in a given session.
     *
     * This method is typically used by a Session that is configured for client acknowledge
     * mode.  The acknowledgment should only be applied to Messages that have been marked
     * as delivered and not those still awaiting dispatch.
     *
     * @param sessionId
     *        the ID of the Session whose delivered messages should be acknowledged.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such as unmatched ack.
     */
    void acknowledge(JmsSessionId sessionId, AsyncResult request) throws IOException, JMSException;

    /**
     * Called to acknowledge a JmsMessage has been delivered, consumed, re-delivered...etc.
     *
     * The provider should perform an acknowledgment for the message based on the configured
     * mode of the consumer that it was dispatched to and the capabilities of the protocol.
     *
     * @param envelope
     *        The message dispatch envelope containing the Message delivery information.
     * @param ackType
     *        The type of acknowledgment being done.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such as unmatched ack.
     */
    void acknowledge(JmsInboundMessageDispatch envelope, ACK_TYPE ackType, AsyncResult request)
        throws IOException, JMSException;

    /**
     * Called to commit an open transaction.
     *
     * If the provider is unable to support transactions then it should throw an
     * UnsupportedOperationException to indicate this.  The Provider may also throw a
     * JMSException to indicate a transaction was already rolled back etc.
     *
     * @param sessionId
     *        the Id of the JmsSession that is committing the current transaction.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such not authorized.
     */
    void commit(JmsSessionId sessionId, AsyncResult request) throws IOException, JMSException;

    /**
     * Called to roll back an open transaction.
     *
     * @param sessionId
     *        the Id of the JmsSession that is rolling back the current transaction.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such not authorized.
     */
    void rollback(JmsSessionId sessionId, AsyncResult request) throws IOException, JMSException;

    /**
     * Called to recover all unacknowledged messages for a Session in client Ack mode.
     *
     * @param sessionId
     *        the Id of the JmsSession that is recovering unacknowledged messages..
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     */
    void recover(JmsSessionId sessionId, AsyncResult request) throws IOException;

    /**
     * Remove a durable topic subscription by name.
     *
     * A provider can throw an instance of JMSException to indicate that it cannot perform the
     * un-subscribe operation due to bad security credentials etc.
     *
     * @param subscription
     *        the name of the durable subscription that is to be removed.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     * @throws JMSException if an error occurs due to JMS violation such not authorized.
     */
    void unsubscribe(String subscription, AsyncResult request) throws IOException, JMSException;

    /**
     * Request a remote peer send a Message to this client.  A message pull request is
     * usually only needed in the case where the client sets a zero prefetch limit on the
     * consumer.  If the consumer has a set prefetch that's greater than zero this method
     * should just return without performing and action.
     *
     * @param timeout
     *        the amount of time to tell the remote peer to keep this pull request valid.
     * @param request
     *        The request object that should be signaled when this operation completes.
     *
     * @throws IOException if an error occurs or the Provider is already closed.
     */
    void pull(JmsConsumerId consumerId, long timeout, AsyncResult request) throws IOException;

    /**
     * Gets the Provider specific Message factory for use in the JMS layer when a Session
     * is asked to create a Message type.  The Provider should implement it's own internal
     * JmsMessage core to optimize read / write and marshal operations for the connection.
     *
     * @returns a JmsMessageFactory instance for use by the JMS layer.
     */
    JmsMessageFactory getMessageFactory();

    /**
     * Sets the listener of events from this Provider instance.
     *
     * @param listener
     *        The listener instance that will receive all event callbacks.
     */
    void setProviderListener(ProviderListener listener);

    /**
     * Gets the currently set ProdiverListener instance.
     *
     * @return the currently set ProviderListener instance.
     */
    ProviderListener getProviderListener();

}
