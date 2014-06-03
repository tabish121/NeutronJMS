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
package io.neutronjms.jms.message;

import io.neutronjms.jms.message.facade.JmsTextMessageFacade;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import javax.jms.TextMessage;

public class JmsTextMessage extends JmsMessage implements TextMessage {

    private final JmsTextMessageFacade facade;

    public JmsTextMessage(JmsTextMessageFacade facade) {
        super(facade);
        this.facade = facade;
    }

    @Override
    public JmsMessage copy() throws JMSException {
        JmsTextMessage other = new JmsTextMessage(facade.copy());
        other.copy(this);
        return other;
    }

    private void copy(JmsTextMessage other) throws JMSException {
        super.copy(other);
    }

    @Override
    public void setText(String text) throws JMSException, MessageNotWriteableException {
        checkReadOnlyBody();
        this.facade.setText(text);
    }

    @Override
    public String getText() throws JMSException {
        return facade.getText();
    }

    /**
     * Clears out the message body. Clearing a message's body does not clear its
     * header values or property entries.
     * <p/>
     * <p/>
     * If this message body was read-only, calling this method leaves the
     * message body in the same state as an empty body in a newly created
     * message.
     *
     * @throws JMSException
     *         if the JMS provider fails to clear the message body due to some
     *         internal error.
     */
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.facade.setText(null);
    }

    @Override
    public String toString() {
        try {
            return super.toString() + ":text=" + facade.getText();
        } catch (JMSException e) {
            return super.toString();
        }
    }
}
