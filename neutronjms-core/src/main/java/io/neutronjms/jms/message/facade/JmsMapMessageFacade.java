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
package io.neutronjms.jms.message.facade;

import java.util.Enumeration;

import javax.jms.JMSException;

/**
 * Interface for a message Facade that wraps a MapMessage style provider
 * message.
 *
 * TODO - It doesn't really have to be the case that we track read-only
 *        or write-only in the facade, we could just treat the facade as
 *        an open Map that can be updated at any time, it's really the job
 *        of the JMS layer message objects to ensure we play nice with all
 *        the JMS rules.
 */
public interface JmsMapMessageFacade extends JmsMessageFacade {

    /**
     * Returns an Enumeration of all the names in the MapMessage object.
     *
     * @return an enumeration of all the names in this MapMessage
     *
     * @throws JMSException if an internal error occurs.
     */
    Enumeration<String> getMapNames() throws JMSException;

    /**
     * Determines whether an item exists in this Map based message.
     *
     * @returns true if the item exists in the Map, false otherwise.
     *
     * @throws JMSException if an internal error occurs.
     */
    boolean itemExists(String key) throws JMSException;

    /**
     * Gets the value stored in the Map at the specified key.
     *
     * @param key
     *        the key to use to access a value in the Map.
     *
     * @returns the item associated with the given key, or null if not present.
     *
     * @throws JMSException if the provider fails to write the message due to some internal error.
     * @throws java.lang.IllegalArgumentException if the name is null or if the name is an empty string.
     * @throws MessageFormatException if the object is invalid.
     */
    Object getObject(String key) throws JMSException;

    /**
     * Sets an object value with the specified name into the Map.
     *
     * @param key
     *        the key to use to store the value into the Map.
     * @param value
     *        the new value to store in the element defined by the key.
     *
     * @throws JMSException if the provider fails to write the message due to some internal error.
     * @throws java.lang.IllegalArgumentException if the name is null or if the name is an empty string.
     * @throws MessageFormatException if the object is invalid.
     */
    void setObject(String key, Object value) throws JMSException;

}
