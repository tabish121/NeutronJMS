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
import io.neutronjms.util.ClassLoadingAwareObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

/**
 * Wrapper around an AMQP Message instance that will be treated as a JMS ObjectMessage
 * type.
 */
public class AmqpJmsSerializedObjectMessageFacade extends AmqpJmsMessageFacade implements JmsObjectMessageFacade {

    public static final String CONTENT_TYPE = "application/x-java-serialized-object";

    /**
     * Peek and return the next element in the stream.  If the stream has been fully read
     * then this method should throw a MessageEOFException.  Multiple calls to peek should
     * return the same element.
     *
     * @returns the next value in the stream without removing it.
     *
     * @throws MessageEOFException if end of message stream has been reached.
     */
    public AmqpJmsSerializedObjectMessageFacade(AmqpConnection connection) {
        super(connection);
        setContentType(CONTENT_TYPE);
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
    public AmqpJmsSerializedObjectMessageFacade(AmqpConnection connection, Message message) {
        super(connection, message);
    }

    @Override
    public boolean isEmpty() {
        // TODO - If null body changes to empty AmqpValue this needs to also change.
        return getAmqpMessage().getBody() == null;
    }

    @Override
    public JmsObjectMessageFacade copy() throws JMSException {
        AmqpJmsSerializedObjectMessageFacade copy = new AmqpJmsSerializedObjectMessageFacade(connection);
        copyInto(copy);

        try {
            copy.setObject(getObject());
        } catch (Exception e) {
            throw JmsExceptionSupport.create("Failed to copy object value", e);
        }

        return copy;
    }

    @Override
    public Serializable getObject() throws IOException, ClassNotFoundException {
        Binary bin = null;

        Section body = getAmqpMessage().getBody();
        if (body == null) {
            return null;
        } else if (body instanceof Data) {
            bin = ((Data) body).getValue();
        } else {
            throw new IllegalStateException("Unexpected body type: " + body.getClass().getSimpleName());
        }

        if (bin == null) {
            return null;
        } else {
            Serializable serialized = null;

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bin.getArray(), bin.getArrayOffset(), bin.getLength());
                 ClassLoadingAwareObjectInputStream objIn = new ClassLoadingAwareObjectInputStream(bais)) {

                serialized = (Serializable) objIn.readObject();
            }

            return serialized;
        }
    }

    @Override
    public void setObject(Serializable value) throws IOException {
        if(value == null) {
            // TODO: verify whether not sending a body is ok,
            //       send a serialized null instead if it isn't
            getAmqpMessage().setBody(null);
        } else {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {

               oos.writeObject(value);
               oos.flush();
               oos.close();

               byte[] bytes = baos.toByteArray();
               getAmqpMessage().setBody(new Data(new Binary(bytes)));
            }
        }

        // TODO: ensure content type is [still] set?
    }

    @Override
    public void clearBody() {
        try {
            setObject(null);
        } catch (IOException e) {
        }
    }
}
