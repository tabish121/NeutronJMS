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

import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_MSG_TYPE;
import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_OBJECT_MESSAGE;
import io.neutronjms.jms.exceptions.JmsExceptionSupport;
import io.neutronjms.jms.message.facade.JmsObjectMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;

import org.apache.qpid.proton.amqp.messaging.AmqpSequence;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

/**
 * Wrapper around an AMQP Message instance that will be treated as a JMS ObjectMessage
 * type.
 */
public class AmqpJmsAmqpTypedObjectMessageFacade extends AmqpJmsMessageFacade implements JmsObjectMessageFacade {

    /**
     * Peek and return the next element in the stream.  If the stream has been fully read
     * then this method should throw a MessageEOFException.  Multiple calls to peek should
     * return the same element.
     *
     * @returns the next value in the stream without removing it.
     *
     * @throws MessageEOFException if end of message stream has been reached.
     */
    public AmqpJmsAmqpTypedObjectMessageFacade(AmqpConnection connection) {
        super(connection);
        //setContentType(AmqpObjectMessageSerializedDelegate.CONTENT_TYPE);
        setAnnotation(JMS_MSG_TYPE, JMS_OBJECT_MESSAGE);
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
    public AmqpJmsAmqpTypedObjectMessageFacade(AmqpConnection connection, Message message) {
        super(connection, message);
    }

    @Override
    public JmsObjectMessageFacade copy() throws JMSException {
        AmqpJmsAmqpTypedObjectMessageFacade copy = new AmqpJmsAmqpTypedObjectMessageFacade(connection);
        copyInto(copy);

        try {
            copy.setObject(getObject());
        } catch (Exception e) {
            throw JmsExceptionSupport.create("Error while copying object content", e);
        }

        return copy;
    }

    @Override
    public Serializable getObject() throws IOException, ClassNotFoundException {
        // TODO: this should actually return a snapshot of the object, so we
        // need to save the bytes so we can return an equal/unmodified object later

        Section body = getAmqpMessage().getBody();
        if (body == null) {
            return null;
        } else if (body instanceof AmqpValue) {
            // TODO: This is assuming the object can be immediately returned, and is
            //       Serializable. We will actually have to ensure elements are
            //       Serializable and e.g convert the Uint/Ubyte etc wrappers.
            return (Serializable) ((AmqpValue) body).getValue();
        } else if (body instanceof Data) {
            // TODO: return as byte[]? ByteBuffer?
            throw new UnsupportedOperationException("Data support still to be added");
        } else if (body instanceof AmqpSequence) {
            // TODO: return as list?
            throw new UnsupportedOperationException("AmqpSequence support still to be added");
        } else {
            throw new IllegalStateException("Unexpected body type: " + body.getClass().getSimpleName());
        }
    }

    @Override
    public void setObject(Serializable value) throws IOException {
        if (value == null) {
            // TODO: verify whether not sending a body is OK, send some form of
            // null (AmqpValue containing null) instead if it isn't?
            getAmqpMessage().setBody(null);
        } else if (isSupportedAmqpValueObjectType(value)) {
            // TODO: This is a temporary hack, we actually need to take a snapshot of the object
            // at this point in time, not simply set the object itself into the Proton message.
            // We will need to encode it now, first to save the snapshot to send, and also to
            // verify up front that we can actually send it later.

            // Even if we do that we would currently then need to decode it later to set the
            // body to send, unless we augment Proton to allow setting the bytes directly.
            // We will always need to decode bytes to return a snapshot from getObject(). We
            // will need to save the bytes somehow to support that on received messages.
            getAmqpMessage().setBody(new AmqpValue(value));
        } else {
            // TODO: Data and AmqpSequence?
            throw new IllegalArgumentException("Encoding this object type with the AMQP type system is not supported: " + value.getClass().getName());
        }

        // TODO: ensure content type is not set (assuming we aren't using data sections)?
    }

    @Override
    public void clearBody() {
        try {
            setObject(null);
        } catch (IOException e) {
        }
    }

    private boolean isSupportedAmqpValueObjectType(Serializable serializable) {
        // TODO: augment supported types to encode as an AmqpValue?
        return serializable instanceof Map<?,?> ||
               serializable instanceof List<?> ||
               serializable.getClass().isArray();
    }
}
