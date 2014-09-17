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
package io.neutronjms.provider.amqp.message;

import io.neutronjms.jms.message.facade.JmsMapMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import java.util.Enumeration;

import org.apache.qpid.proton.message.Message;

/**
 *
 */
public class AmqpJmsMapMessageFacade extends AmqpJmsMessageFacade implements JmsMapMessageFacade {

    /**
     * @param connection
     */
    public AmqpJmsMapMessageFacade(AmqpConnection connection) {
        super(connection);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a new Facade around an incoming AMQP Message for dispatch to the
     * JMS Consumer instance.
     *
     * @param connection
     *        the connection that created this Facade.
     * @param message
     *        the incoming Message instance that is being wrapped.
     */
    public AmqpJmsMapMessageFacade(AmqpConnection connection, Message message) {
        super(connection, message);
    }

    @Override
    public JmsMapMessageFacade copy() {
        AmqpJmsMapMessageFacade copy = new AmqpJmsMapMessageFacade(connection);
        copyInto(copy);

        // TODO - Copy the map

        return copy;
    }

    @Override
    public Enumeration<String> getMapNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean itemExists(String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object get(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(String key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object remove(String key) {
        // TODO Auto-generated method stub
        return null;
    }
}
