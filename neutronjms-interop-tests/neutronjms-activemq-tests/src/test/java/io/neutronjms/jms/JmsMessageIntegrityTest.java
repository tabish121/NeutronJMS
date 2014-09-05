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
package io.neutronjms.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.neutronjms.test.support.AmqpTestSupport;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageEOFException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests that messages sent and received don't lose data and have expected
 * JMS Message property values.
 */
public class JmsMessageIntegrityTest extends AmqpTestSupport {

    private Connection connection;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        connection = createAmqpConnection();
    }

    @Test
    public void testTextMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            TextMessage message = session.createTextMessage();
            message.setText("Hi");
            producer.send(message);
        }
        {
            TextMessage message = (TextMessage)consumer.receive(1000);
            assertNotNull(message);
            assertEquals("Hi", message.getText());
        }

        assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testBytesMessageLength() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            BytesMessage message = session.createBytesMessage();
            message.writeInt(1);
            message.writeInt(2);
            message.writeInt(3);
            message.writeInt(4);
            producer.send(message);
        }
        {
            BytesMessage message = (BytesMessage)consumer.receive(1000);
            assertNotNull(message);
            assertEquals(16, message.getBodyLength());
        }

        assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testObjectMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        UUID payload = UUID.randomUUID();

        {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(payload);
            producer.send(message);
        }
        {
            ObjectMessage message = (ObjectMessage)consumer.receive(1000);
            assertNotNull(message);
            assertEquals(payload, message.getObject());
        }
        assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testBytesMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            BytesMessage message = session.createBytesMessage();
            message.writeBoolean(true);
            producer.send(message);
        }
        {
            BytesMessage message = (BytesMessage)consumer.receive(1000);
            assertNotNull(message);
            assertTrue(message.readBoolean());

            try {
                message.readByte();
                fail("Expected exception not thrown.");
            } catch (MessageEOFException e) {
            }
        }
        assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testStreamMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            StreamMessage message = session.createStreamMessage();
            message.writeString("This is a test to see how it works.");
            producer.send(message);
        }
        {
            StreamMessage message = (StreamMessage)consumer.receive(1000);
            assertNotNull(message);

            // Invalid conversion should throw exception and not move the stream position.
            try {
                message.readByte();
                fail("Should have received NumberFormatException");
            } catch (NumberFormatException e) {
            }

            assertEquals("This is a test to see how it works.", message.readString());

            // Invalid conversion should throw exception and not move the stream position.
            try {
                message.readByte();
                fail("Should have received MessageEOFException");
            } catch (MessageEOFException e) {
            }
        }
        assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testMapMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            MapMessage message = session.createMapMessage();
            message.setBoolean("boolKey", true);
            producer.send(message);
        }
        {
            MapMessage message = (MapMessage)consumer.receive(1000);
            assertNotNull(message);
            assertTrue(message.getBoolean("boolKey"));
        }
        assertNull(consumer.receiveNoWait());
    }

    static class ForeignMessage implements TextMessage {

        public int deliveryMode;

        private String messageId;
        private long timestamp;
        private String correlationId;
        private Destination replyTo;
        private Destination destination;
        private boolean redelivered;
        private String type;
        private long expiration;
        private int priority;
        private String text;
        private final HashMap<String, Object> props = new HashMap<String, Object>();

        @Override
        public String getJMSMessageID() throws JMSException {
            return messageId;
        }

        @Override
        public void setJMSMessageID(String arg0) throws JMSException {
            messageId = arg0;
        }

        @Override
        public long getJMSTimestamp() throws JMSException {
            return timestamp;
        }

        @Override
        public void setJMSTimestamp(long arg0) throws JMSException {
            timestamp = arg0;
        }

        @Override
        public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
            return null;
        }

        @Override
        public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
        }

        @Override
        public void setJMSCorrelationID(String arg0) throws JMSException {
            correlationId = arg0;
        }

        @Override
        public String getJMSCorrelationID() throws JMSException {
            return correlationId;
        }

        @Override
        public Destination getJMSReplyTo() throws JMSException {
            return replyTo;
        }

        @Override
        public void setJMSReplyTo(Destination arg0) throws JMSException {
            replyTo = arg0;
        }

        @Override
        public Destination getJMSDestination() throws JMSException {
            return destination;
        }

        @Override
        public void setJMSDestination(Destination arg0) throws JMSException {
            destination = arg0;
        }

        @Override
        public int getJMSDeliveryMode() throws JMSException {
            return deliveryMode;
        }

        @Override
        public void setJMSDeliveryMode(int arg0) throws JMSException {
            deliveryMode = arg0;
        }

        @Override
        public boolean getJMSRedelivered() throws JMSException {
            return redelivered;
        }

        @Override
        public void setJMSRedelivered(boolean arg0) throws JMSException {
            redelivered = arg0;
        }

        @Override
        public String getJMSType() throws JMSException {
            return type;
        }

        @Override
        public void setJMSType(String arg0) throws JMSException {
            type = arg0;
        }

        @Override
        public long getJMSExpiration() throws JMSException {
            return expiration;
        }

        @Override
        public void setJMSExpiration(long arg0) throws JMSException {
            expiration = arg0;
        }

        @Override
        public int getJMSPriority() throws JMSException {
            return priority;
        }

        @Override
        public void setJMSPriority(int arg0) throws JMSException {
            priority = arg0;
        }

        @Override
        public void clearProperties() throws JMSException {
        }

        @Override
        public boolean propertyExists(String arg0) throws JMSException {
            return false;
        }

        @Override
        public boolean getBooleanProperty(String arg0) throws JMSException {
            return false;
        }

        @Override
        public byte getByteProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public short getShortProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public int getIntProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public long getLongProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public float getFloatProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public double getDoubleProperty(String arg0) throws JMSException {
            return 0;
        }

        @Override
        public String getStringProperty(String arg0) throws JMSException {
            return (String)props.get(arg0);
        }

        @Override
        public Object getObjectProperty(String arg0) throws JMSException {
            return props.get(arg0);
        }

        @Override
        public Enumeration<?> getPropertyNames() throws JMSException {
            return new Vector<String>(props.keySet()).elements();
        }

        @Override
        public void setBooleanProperty(String arg0, boolean arg1) throws JMSException {
        }

        @Override
        public void setByteProperty(String arg0, byte arg1) throws JMSException {
        }

        @Override
        public void setShortProperty(String arg0, short arg1) throws JMSException {
        }

        @Override
        public void setIntProperty(String arg0, int arg1) throws JMSException {
        }

        @Override
        public void setLongProperty(String arg0, long arg1) throws JMSException {
        }

        @Override
        public void setFloatProperty(String arg0, float arg1) throws JMSException {
        }

        @Override
        public void setDoubleProperty(String arg0, double arg1) throws JMSException {
        }

        @Override
        public void setStringProperty(String arg0, String arg1) throws JMSException {
            props.put(arg0, arg1);
        }

        @Override
        public void setObjectProperty(String arg0, Object arg1) throws JMSException {
            props.put(arg0, arg1);
        }

        @Override
        public void acknowledge() throws JMSException {
        }

        @Override
        public void clearBody() throws JMSException {
        }

        @Override
        public void setText(String arg0) throws JMSException {
            text = arg0;
        }

        @Override
        public String getText() throws JMSException {
            return text;
        }
    }

    // TODO - implement proper handling of foreign JMS Message and Destination types.
    @Ignore("ActiveMQ is dropping messages as expired with current proton lib")
    @Test
    public void testForeignMessage() throws Exception {
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);

        {
            ForeignMessage message = new ForeignMessage();
            message.text = "Hello";
            message.setStringProperty("test", "value");
            long timeToLive = 10000L;
            long start = System.currentTimeMillis();
            producer.send(message, Session.AUTO_ACKNOWLEDGE, 7, timeToLive);
            long end = System.currentTimeMillis();

            // validate jms spec 1.1 section 3.4.11 table 3.1
            // JMSDestination, JMSDeliveryMode,  JMSExpiration, JMSPriority, JMSMessageID, and JMSTimestamp
            // must be set by sending a message.

            assertNotNull(message.getJMSDestination());
            assertEquals(Session.AUTO_ACKNOWLEDGE, message.getJMSDeliveryMode());
            assertTrue(start  + timeToLive <= message.getJMSExpiration());
            assertTrue(end + timeToLive >= message.getJMSExpiration());
            assertEquals(7, message.getJMSPriority());
            assertNotNull(message.getJMSMessageID());
            assertTrue(start <= message.getJMSTimestamp());
            assertTrue(end >= message.getJMSTimestamp());
        }
        {
            TextMessage message = (TextMessage)consumer.receive(10000);
            assertNotNull(message);
            assertEquals("Hello", message.getText());
            assertEquals("value", message.getStringProperty("test"));
        }

        assertNull(consumer.receiveNoWait());
    }
}
