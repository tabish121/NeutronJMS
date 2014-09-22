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
package org.apache.qpid.jms.transactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.qpid.jms.support.AmqpTestSupport;
import org.junit.Test;

/**
 * Basic tests for Session in Transacted mode.
 */
public class JmsTransactedSessionTest extends AmqpTestSupport {

    @Test(timeout = 60000)
    public void testCreateTxSession() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        assertNotNull(session);
        assertTrue(session.getTransacted());

        session.close();
    }

    @Test(timeout = 60000)
    public void testCommitOnSessionWithNoWork() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        assertNotNull(session);
        assertTrue(session.getTransacted());

        session.commit();
    }

    @Test(timeout = 60000)
    public void testRollbackOnSessionWithNoWork() throws Exception {
        connection = createAmqpConnection();
        assertNotNull(connection);
        connection.start();

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        assertNotNull(session);
        assertTrue(session.getTransacted());

        session.rollback();
    }

    @Test(timeout=60000)
    public void testCloseSessionRollsBack() throws Exception {
        connection = createAmqpConnection();
        connection.start();

        sendToAmqQueue(2);

        QueueViewMBean proxy = getProxyToQueue(name.getMethodName());
        assertEquals(2, proxy.getQueueSize());

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        Queue queue = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(5000);
        assertNotNull(message);
        message = consumer.receive(5000);
        assertNotNull(message);

        assertEquals(2, proxy.getQueueSize());
        session.close();
        assertEquals(2, proxy.getQueueSize());
    }
}
