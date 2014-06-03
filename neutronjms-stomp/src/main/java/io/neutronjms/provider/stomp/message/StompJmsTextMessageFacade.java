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

import io.neutronjms.jms.message.facade.JmsTextMessageFacade;
import io.neutronjms.provider.stomp.StompConnection;
import io.neutronjms.provider.stomp.StompFrame;

import javax.jms.JMSException;

import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * STOMP JmsTextMessage extension that reads and writes the message String
 * value to the underlying STOMP frame.
 */
public class StompJmsTextMessageFacade extends StompJmsMessageFacade implements JmsTextMessageFacade {

    /**
     * @param connection
     */
    public StompJmsTextMessageFacade(StompConnection connection) {
        super(connection);
    }

    /**
     * @param message
     * @param connection
     */
    public StompJmsTextMessageFacade(StompFrame message, StompConnection connection) {
        super(message, connection);
    }

    @Override
    public StompJmsTextMessageFacade copy() {
        StompJmsTextMessageFacade copy = new StompJmsTextMessageFacade(message.clone(), connection);
        return copy;
    }

    @Override
    public String getText() throws JMSException {
        return message.getContentAsString();
    }

    @Override
    public void setText(String text) throws JMSException {
        UTF8Buffer buffer = new UTF8Buffer(text);
        message.setContent(buffer);
    }
}
