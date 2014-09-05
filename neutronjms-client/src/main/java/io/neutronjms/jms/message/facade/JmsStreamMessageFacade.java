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

import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

/**
 * Interface for a Message Facade that wraps a StreamMessage based provider
 * message instance.  The interface provides the basic entry points into a
 * stream style message where primitive values are read and written as opaque
 * objects.
 */
public interface JmsStreamMessageFacade extends JmsMessageFacade {

    /**
     * @returns a deep copy of this Message Facade including a complete copy
     * of the byte contents of the wrapped message.
     *
     * @throws JMSException if an error occurs while copying this message.
     */
    @Override
    JmsStreamMessageFacade copy() throws JMSException;

    /**
     * Reads and returns the next object in the stream.
     *
     * @returns the next value in the stream.
     *
     * @throws JMSException if the provider fails to read the message due to some internal error.
     * @throws MessageEOFException if unexpected end of message stream has been reached.
     * @throws MessageFormatException if this type conversion is invalid.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    Object readObject() throws JMSException;

    /**
     * Writes a new object value to the stream.
     *
     * @param value
     *        The object value to be written to the stream.
     *
     * @throws JMSException if the provider fails to write the message due to some internal error.
     * @throws MessageFormatException if the object is invalid.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    void writeObject(Object value) throws JMSException;

    /**
     * Reset the position of the stream to the beginning.
     *
     * @throws JMSException if an internal error occurs.
     */
    void reset() throws JMSException;

}
