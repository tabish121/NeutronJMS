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

import io.neutronjms.jms.message.facade.JmsObjectMessageFacade;

import java.io.Serializable;

import javax.jms.JMSException;

/**
 * Default implementation for a JMS Object Message Facade.
 */
public class JmsDefaultObjectMessageFacade extends JmsDefaultMessageFacade implements JmsObjectMessageFacade {

    // TODO - Immediate Serialization to comply with JMS Spec.

    // private Buffer object;
    private Serializable object;

    @Override
    public JmsMsgType getMsgType() {
        return JmsMsgType.OBJECT;
    }

    @Override
    public boolean isEmpty() {
        // return object != null && !object.isEmpty();
        return object != null;
    }

    @Override
    public JmsDefaultObjectMessageFacade copy() throws JMSException {
        JmsDefaultObjectMessageFacade copy = new JmsDefaultObjectMessageFacade();
        copyInto(copy);
        // TODO - We don't snapshot the object when set although we really should be.
        //      if (!isEmpty()) {
        //      target.object = object.deepCopy();
        //  }
        copy.object = object;

        return copy;
    }

    @Override
    public void clearBody() {
        this.object = null;
    }

    @Override
    public Serializable getObject() throws JMSException {
        return this.object;
    }

    @Override
    public void setObject(Serializable value) throws JMSException {
        this.object = value;
    }
}
