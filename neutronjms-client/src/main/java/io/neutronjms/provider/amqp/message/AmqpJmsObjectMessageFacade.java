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
import io.neutronjms.jms.message.facade.JmsObjectMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.MessageEOFException;

import org.apache.qpid.proton.message.Message;

/**
 * Wrapper around an AMQP Message instance that will be treated as a JMS ObjectMessage
 * type.
 */
public class AmqpJmsObjectMessageFacade extends AmqpJmsMessageFacade implements JmsObjectMessageFacade {

    /**
     * Peek and return the next element in the stream.  If the stream has been fully read
     * then this method should throw a MessageEOFException.  Multiple calls to peek should
     * return the same element.
     *
     * @returns the next value in the stream without removing it.
     *
     * @throws MessageEOFException if end of message stream has been reached.
     */
    public AmqpJmsObjectMessageFacade(AmqpConnection connection) {
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
    public AmqpJmsObjectMessageFacade(AmqpConnection connection, Message message) {
        super(connection, message);
    }

    @Override
    public JmsObjectMessageFacade copy() {
        AmqpJmsObjectMessageFacade copy = new AmqpJmsObjectMessageFacade(connection);
        copyInto(copy);

        // TODO - Copy object

        return copy;
    }

    @Override
    public Serializable getObject() throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setObject(Serializable value) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearBody() {

    }
}
