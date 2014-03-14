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
package org.fusesource.amqpjms.jms.producer;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Tests the MessageProducer method contract when it's connection has failed.
 */
public class JmsMessageProducerFailedTest extends JmsMessageProducerClosedTest {

    @Override
    protected MessageProducer createProducer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        connection = createAmqpConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        message = session.createMessage();
        destination = session.createQueue("test");
        MessageProducer producer = session.createProducer(destination);
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException exception) {
                latch.countDown();
            }
        });
        connection.start();
        stopPrimaryBroker();
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        return producer;
    }
}
