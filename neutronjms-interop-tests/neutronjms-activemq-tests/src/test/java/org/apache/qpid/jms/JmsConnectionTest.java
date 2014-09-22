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
package org.apache.qpid.jms;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.support.AmqpTestSupport;
import org.junit.Test;

/**
 * Test for basic JmsConnection functionality and error handling.
 */
public class JmsConnectionTest extends AmqpTestSupport {

    @Test(timeout=30000)
    public void testCreateConnection() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection = (JmsConnection) factory.createConnection();
        assertNotNull(connection);
        connection.close();
    }

    @Test(timeout=30000)
    public void testCreateConnectionAndStart() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection = (JmsConnection) factory.createConnection();
        assertNotNull(connection);
        connection.start();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testCreateWithDuplicateClientIdFails() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection1 = (JmsConnection) factory.createConnection();
        connection1.setClientID("Test");
        assertNotNull(connection1);
        connection1.start();
        JmsConnection connection2 = (JmsConnection) factory.createConnection();
        connection2.setClientID("Test");
        connection2.start();

        connection1.close();
        connection2.close();
    }

    @Test(expected = JMSException.class)
    public void testSetClientIdAfterStartedFails() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection = (JmsConnection) factory.createConnection();
        connection.setClientID("Test");
        connection.start();
        connection.setClientID("NewTest");
        connection.close();
    }

    @Test(timeout=30000)
    public void testCreateConnectionAsSystemAdmin() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        factory.setUsername("system");
        factory.setPassword("manager");
        JmsConnection connection = (JmsConnection) factory.createConnection();
        assertNotNull(connection);
        connection.start();
        connection.close();
    }

    @Test(timeout=30000)
    public void testCreateConnectionCallSystemAdmin() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection = (JmsConnection) factory.createConnection("system", "manager");
        assertNotNull(connection);
        connection.start();
        connection.close();
    }

    @Test(timeout=30000, expected = JMSSecurityException.class)
    public void testCreateConnectionAsUnknwonUser() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        factory.setUsername("unknown");
        factory.setPassword("unknown");
        JmsConnection connection = (JmsConnection) factory.createConnection();
        assertNotNull(connection);
        connection.start();
        connection.close();
    }

    @Test(timeout=30000, expected = JMSSecurityException.class)
    public void testCreateConnectionCallUnknwonUser() throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(getBrokerAmqpConnectionURI());
        JmsConnection connection = (JmsConnection) factory.createConnection("unknown", "unknown");
        assertNotNull(connection);
        connection.start();
        connection.close();
    }

    @Test(timeout=60000)
    public void testConnectionExceptionBrokerStop() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Connection connection = createAmqpConnection();
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException exception) {
                latch.countDown();
            }
        });
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertNotNull(session);

        stopPrimaryBroker();

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        connection.close();
    }
}
