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
package org.apache.qpid.jms.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.jms.DeliveryMode;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.qpid.jms.support.AmqpTestSupport;
import org.junit.Test;

/**
 *
 */
public class JmsMessageProducerTest extends AmqpTestSupport {

    @Test(timeout = 60000)
    public void testCreateMessageProducer() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);
        Queue queue = session.createQueue(name.getMethodName());
        session.createProducer(queue);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(0, proxy.getQueueSize());
    }

    @Test
    public void testSendWorksWhenConnectionNotStarted() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(0, proxy.getQueueSize());

        Message message = session.createMessage();
        producer.send(message);

        assertEquals(1, proxy.getQueueSize());
    }

    @Test
    public void testSendWorksAfterConnectionStopped() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(0, proxy.getQueueSize());
        connection.stop();

        Message message = session.createMessage();
        producer.send(message);

        assertEquals(1, proxy.getQueueSize());
    }

    @Test
    public void testPersistentSendsAreMarkedPersistent() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(0, proxy.getQueueSize());

        Message message = session.createMessage();
        producer.send(message);

        assertEquals(1, proxy.getQueueSize());

        MessageConsumer consumer = session.createConsumer(queue);
        message = consumer.receive(5000);
        assertNotNull(message);
        assertTrue(message.getJMSDeliveryMode() == DeliveryMode.PERSISTENT);
    }

    @Test
    public void testProducerWithNoTTLSendsMessagesWithoutTTL() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(0, proxy.getQueueSize());

        Message message = session.createMessage();
        producer.send(message);

        assertEquals(1, proxy.getQueueSize());

        MessageConsumer consumer = session.createConsumer(queue);
        message = consumer.receive(5000);
        assertNotNull(message);
        assertEquals(0, message.getJMSExpiration());
    }

    private String createLargeString(int sizeInBytes) {
        byte[] base = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sizeInBytes; i++) {
            builder.append(base[i % base.length]);
        }

        LOG.debug("Created string with size : " + builder.toString().getBytes().length + " bytes");
        return builder.toString();
    }

    @Test(timeout = 60 * 1000)
    public void testSendLargeMessage() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String queueName = name.toString();
        Queue queue = session.createQueue(queueName);

        MessageProducer producer = session.createProducer(queue);
        int messageSize = 1024 * 1024;
        String messageText = createLargeString(messageSize);
        Message m = session.createTextMessage(messageText);
        LOG.debug("Sending message of {} bytes on queue {}", messageSize, queueName);
        producer.send(m);

        MessageConsumer consumer = session.createConsumer(queue);

        Message message = consumer.receive();
        assertNotNull(message);
        assertTrue(message instanceof TextMessage);
        TextMessage textMessage = (TextMessage) message;
        LOG.debug(">>>> Received message of length {}", textMessage.getText().length());
        assertEquals(messageSize, textMessage.getText().length());
        assertEquals(messageText, textMessage.getText());
    }

    @Test(timeout=90000, expected=JMSSecurityException.class)
    public void testProducerNotAuthorized() throws Exception{
        connection = createAmqpConnection("guest", "password");
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("USERS." + name.getMethodName());
        session.createProducer(queue);
    }
}
