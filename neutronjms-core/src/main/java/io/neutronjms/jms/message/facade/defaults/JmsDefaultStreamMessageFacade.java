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

import io.neutronjms.jms.message.facade.JmsStreamMessageFacade;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;

/**
 * Default implementation of the JmsStreamMessageFacade
 */
public class JmsDefaultStreamMessageFacade extends JmsDefaultMessageFacade implements JmsStreamMessageFacade {

    private final List<Object> stream = new ArrayList<Object>();
    private int index = -1;

    @Override
    public JmsMsgType getMsgType() {
        return JmsMsgType.STREAM;
    }

    @Override
    public JmsDefaultStreamMessageFacade copy() throws JMSException {
        JmsDefaultStreamMessageFacade copy = new JmsDefaultStreamMessageFacade();
        copyInto(copy);
        copy.stream.addAll(stream);
        return copy;
    }

    @Override
    public boolean hasNext() throws JMSException {
        return !stream.isEmpty() && index < stream.size();
    }

    @Override
    public Object peek() throws JMSException {
        if (stream.isEmpty() || index + 1 >= stream.size()) {
            throw new MessageEOFException("Reached end of stream");
        }

        return stream.get(index + 1);
    }

    @Override
    public void pop() throws JMSException {
        index++;
    }

    @Override
    public void put(Object value) throws JMSException {
        stream.add(value);
    }

    @Override
    public void clearBody() {
        stream.clear();
        index = -1;
    }

    @Override
    public void reset() throws JMSException {
        index = -1;
    }
}
