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

import static io.neutronjms.provider.stomp.StompConstants.DESTINATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.JmsTopic;
import io.neutronjms.jms.message.JmsMessage;
import io.neutronjms.provider.stomp.StompConnection;
import io.neutronjms.provider.stomp.StompFrame;
import io.neutronjms.provider.stomp.adapters.GenericStompServerAdaptor;
import io.neutronjms.provider.stomp.message.StompJmsMessageFacade;
import io.neutronjms.provider.stomp.message.StompJmsMessageFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test the basic functionality of the STOMP message factory.
 */
@RunWith(MockitoJUnitRunner.class)
public class StompJmsMessageFactoryTest {

    @Mock
    private StompConnection connection;

    @Before
    public void setUp() throws Exception {
        GenericStompServerAdaptor adapter = new GenericStompServerAdaptor(connection);

        when(connection.getServerAdapter()).thenReturn(adapter);
        when(connection.getTempQueuePrefix()).thenReturn("temp-queue://");
        when(connection.getTempTopicPrefix()).thenReturn("temp-topic://");
        when(connection.getQueuePrefix()).thenReturn("queue://");
        when(connection.getTopicPrefix()).thenReturn("topic://");
    }

    @Test
    public void testCreate() {
        StompJmsMessageFactory factory = new StompJmsMessageFactory(connection);
        assertNotNull(factory.getStompConnection());
    }

    @Test
    public void testCreateJmsMessage() throws Exception {
        JmsTopic topic = new JmsTopic("test");
        StompJmsMessageFactory factory = new StompJmsMessageFactory(connection);
        assertNotNull(factory.getStompConnection());

        JmsMessage message = factory.createMessage();
        message.setJMSDestination(topic);
        JmsDestination destination = (JmsDestination) message.getJMSDestination();
        assertEquals("test", destination.getName());

        StompJmsMessageFacade facade = (StompJmsMessageFacade) message.getFacade();
        StompFrame frame = facade.getStompMessage();
        assertEquals("topic://test", frame.getProperty(DESTINATION));
    }
}
