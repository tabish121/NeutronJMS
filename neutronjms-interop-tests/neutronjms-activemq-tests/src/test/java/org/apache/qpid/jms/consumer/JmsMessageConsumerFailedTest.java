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
package org.apache.qpid.jms.consumer;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.support.Wait;

/**
 * Tests MessageConsumer method contracts after the MessageConsumer connection fails.
 */
public class JmsMessageConsumerFailedTest extends JmsMessageConsumerClosedTest {

    @Override
    protected MessageConsumer createConsumer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        connection = createAmqpConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException exception) {
                latch.countDown();
            }
        });
        connection.start();
        stopPrimaryBroker();
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        final JmsConnection jmsConnection = (JmsConnection) connection;
        assertTrue(Wait.waitFor(new Wait.Condition() {

            @Override
            public boolean isSatisified() throws Exception {
                return !jmsConnection.isConnected();
            }
        }));
        return consumer;
    }
}
