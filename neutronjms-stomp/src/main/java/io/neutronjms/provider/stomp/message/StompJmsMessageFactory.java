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

import static io.neutronjms.provider.stomp.StompConstants.SEND;
import static io.neutronjms.provider.stomp.StompConstants.TRANSFORMATION;
import io.neutronjms.jms.message.JmsBytesMessage;
import io.neutronjms.jms.message.JmsMapMessage;
import io.neutronjms.jms.message.JmsMessage;
import io.neutronjms.jms.message.JmsMessageFactory;
import io.neutronjms.jms.message.JmsObjectMessage;
import io.neutronjms.jms.message.JmsStreamMessage;
import io.neutronjms.jms.message.JmsTextMessage;
import io.neutronjms.provider.stomp.StompConnection;
import io.neutronjms.provider.stomp.StompFrame;

import java.io.Serializable;

import javax.jms.MessageNotWriteableException;

/**
 * STOMP based Message Factory.
 */
public class StompJmsMessageFactory implements JmsMessageFactory {

    public static enum JmsMsgType {
        MESSAGE("jms/message"),
        BYTES("jms/bytes-message"),
        MAP("jms/map-message"),
        OBJECT("jms/object-message"),
        STREAM("jms/stream-message"),
        TEXT("jms/text-message"),
        TEXT_NULL("jms/text-message-null");

        public final String mime;

        JmsMsgType(String mime){
            this.mime = mime;
        }
    }

    private StompConnection connection;

    public StompJmsMessageFactory() {
    }

    public StompJmsMessageFactory(StompConnection connection) {
        this.connection = connection;
    }

    public StompConnection getStompConnection() {
        return this.connection;
    }

    public void setStompConnection(StompConnection connection) {
        this.connection = connection;
    }

    @Override
    public JmsMessage createMessage() throws UnsupportedOperationException {
        StompFrame frame = new StompFrame(SEND);
        frame.setProperty(TRANSFORMATION, JmsMsgType.MESSAGE.name());
        return new JmsMessage(new StompJmsMessageFacade(frame, connection));
    }

    @Override
    public JmsTextMessage createTextMessage(String payload) throws UnsupportedOperationException {
        StompFrame frame = new StompFrame(SEND);
        StompJmsTextMessage message = new StompJmsTextMessage(new StompJmsMessageFacade(frame, connection));
        if (payload != null) {
            try {
                frame.setProperty(TRANSFORMATION, JmsMsgType.TEXT.name());
                message.setText(payload);
            } catch (MessageNotWriteableException e) {
            }
        } else {
            frame.setProperty(TRANSFORMATION, JmsMsgType.TEXT_NULL.name());
        }
        return message;
    }

    @Override
    public JmsTextMessage createTextMessage() throws UnsupportedOperationException {
        return createTextMessage(null);
    }

    @Override
    public JmsBytesMessage createBytesMessage() throws UnsupportedOperationException {
        StompFrame frame = new StompFrame(SEND);
        frame.setProperty(TRANSFORMATION, JmsMsgType.BYTES.name());
        return new JmsBytesMessage(new StompJmsBytesMessageFacade(frame, connection));
    }

    @Override
    public JmsMapMessage createMapMessage() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("STOMP Provider does not currently support MapMessage");
    }

    @Override
    public JmsStreamMessage createStreamMessage() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("STOMP Provider does not currently support StreamMessage");
    }

    @Override
    public JmsObjectMessage createObjectMessage(Serializable payload) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("STOMP Provider does not currently support ObjectMessage");
    }

    @Override
    public JmsObjectMessage createObjectMessage() throws UnsupportedOperationException {
        return createObjectMessage(null);
    }

    /**
     * Creates a new JmsMessage that wraps the incoming MESSAGE frame.
     */
    public JmsMessage wrapMessage(StompFrame message) {
        return new JmsMessage(new StompJmsMessageFacade(message, connection));
    }

    /**
     * Creates a new JmsTextMessage that wraps the incoming MESSAGE frame.
     */
    public StompJmsTextMessage wrapTextMessage(StompFrame message) {
        return new StompJmsTextMessage(new StompJmsMessageFacade(message, connection));
    }

    /**
     * Creates a new JmsBytesMessage that wraps the incoming MESSAGE frame.
     */
    public JmsBytesMessage wrapBytesMessage(StompFrame message) {
        return new JmsBytesMessage(new StompJmsBytesMessageFacade(message, connection));
    }

    /**
     * Creates a new JmsMapMessage that wraps the incoming MESSAGE frame.
     */
    public JmsMapMessage wrapMapMessage(StompFrame message) {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Creates a new JmsMapMessage that wraps the incoming MESSAGE frame.
     */
    public JmsStreamMessage wrapStreamMessage(StompFrame message) {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Creates a new JmsMapMessage that wraps the incoming MESSAGE frame.
     */
    public JmsObjectMessage wrapObjectMessage(StompFrame message) {
        throw new RuntimeException("Not yet implemented");
    }
}
