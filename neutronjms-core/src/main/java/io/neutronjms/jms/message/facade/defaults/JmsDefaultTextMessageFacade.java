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
package io.neutronjms.jms.message.facade.defaults;

import io.neutronjms.jms.message.facade.JmsTextMessageFacade;

import javax.jms.JMSException;

/**
 * Default implementation of the JmsTextMessageFacade.
 */
public final class JmsDefaultTextMessageFacade extends JmsDefaultMessageFacade implements JmsTextMessageFacade {

    private String text;

    @Override
    public boolean isEmpty() {
        return text != null && !text.isEmpty();
    }

    @Override
    public JmsDefaultTextMessageFacade copy() throws JMSException {
        JmsDefaultTextMessageFacade copy = new JmsDefaultTextMessageFacade();
        copyInto(copy);
        if (text != null) {
            copy.setText(text);
        }
        return copy;
    }

    @Override
    public void clearBody() {
        this.text = null;
    }

    @Override
    public String getText() throws JMSException {
        return text;
    }

    @Override
    public void setText(String text) throws JMSException {
        this.text = text;
    }
}
