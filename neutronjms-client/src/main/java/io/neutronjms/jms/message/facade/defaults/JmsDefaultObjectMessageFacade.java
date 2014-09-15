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

import io.neutronjms.jms.exceptions.JmsExceptionSupport;
import io.neutronjms.jms.message.facade.JmsObjectMessageFacade;
import io.neutronjms.util.ClassLoadingAwareObjectInputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.JMSException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;

/**
 * Default implementation for a JMS Object Message Facade.
 */
public class JmsDefaultObjectMessageFacade extends JmsDefaultMessageFacade implements JmsObjectMessageFacade {

    private Buffer object;

    public Buffer getSerializedObject() {
        return object;
    }

    public void setSerializedObject(Buffer object) {
        this.object = object;
    }

    @Override
    public JmsMsgType getMsgType() {
        return JmsMsgType.OBJECT;
    }

    @Override
    public boolean isEmpty() {
        return object == null || object.isEmpty();
    }

    @Override
    public JmsDefaultObjectMessageFacade copy() throws JMSException {
        JmsDefaultObjectMessageFacade copy = new JmsDefaultObjectMessageFacade();
        copyInto(copy);
        if (!isEmpty()) {
            copy.object = object.deepCopy();
        }

        return copy;
    }

    @Override
    public void clearBody() {
        this.object = null;
    }

    @Override
    public Serializable getObject() throws JMSException {

        if (isEmpty()) {
            return null;
        }

        Serializable serialized = null;

        try (DataByteArrayInputStream dataIn = new DataByteArrayInputStream(object);
             ClassLoadingAwareObjectInputStream objIn = new ClassLoadingAwareObjectInputStream(dataIn)) {

            serialized = (Serializable) objIn.readObject();
        } catch (ClassNotFoundException ce) {
            throw JmsExceptionSupport.create("Failed to build body from content. Serializable class not available to client. Reason: " + ce, ce);
        } catch (IOException ex) {
            throw JmsExceptionSupport.create(ex);
        }

        return serialized;
    }

    @Override
    public void setObject(Serializable value) throws JMSException {
        Buffer serialized = null;
        if (value != null) {
            DataByteArrayOutputStream baos = new DataByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                throw JmsExceptionSupport.create(e);
            }

            serialized = baos.toBuffer();
        }

        this.object = serialized;
    }
}
