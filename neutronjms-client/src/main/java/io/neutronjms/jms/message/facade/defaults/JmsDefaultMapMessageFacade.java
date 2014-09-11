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

import io.neutronjms.jms.message.facade.JmsMapMessageFacade;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

/**
 * Simple implementation of the JmsMapMessageFacade used for testing.
 */
public class JmsDefaultMapMessageFacade extends JmsDefaultMessageFacade implements JmsMapMessageFacade {

    protected transient Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public JmsMsgType getMsgType() {
        return JmsMsgType.MAP;
    }

    @Override
    public JmsDefaultMapMessageFacade copy() throws JMSException {
        JmsDefaultMapMessageFacade copy = new JmsDefaultMapMessageFacade();
        copyInto(copy);
        copy.map.putAll(map);
        return copy;
    }

    @Override
    public Enumeration<String> getMapNames() throws JMSException {
        return Collections.enumeration(map.keySet());
    }

    @Override
    public boolean itemExists(String key) throws JMSException {
        return map.containsKey(key);
    }

    @Override
    public Object get(String key) throws JMSException {
        return map.get(key);
    }

    @Override
    public void put(String key, Object value) throws JMSException {
        map.put(key, value);
    }

    @Override
    public void remove(String key) throws JMSException {
        map.remove(key);
    }

    @Override
    public void clearBody() {
        map.clear();
    }
}
