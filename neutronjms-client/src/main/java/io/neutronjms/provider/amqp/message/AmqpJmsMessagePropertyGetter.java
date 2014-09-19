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

import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_AMQP_REPLY_TO_GROUP_ID;
import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_AMQP_TTL;
import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_AMQP_TYPED_ENCODING;
import io.neutronjms.jms.exceptions.JmsExceptionSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

/**
 * Utility class used to intercept calls to Message property gets and map the
 * correct OpenWire fields to the property name being queried.
 */
public class AmqpJmsMessagePropertyGetter {

    private static final Map<String, PropertyGetter> PROPERTY_GETTERS = new HashMap<String, PropertyGetter>();

    /**
     * Interface for a Property Get intercepter object used to lookup JMS style
     * properties that are part of the OpenWire Message object members or perform
     * some needed conversion action before returned some named property.
     */
    public interface PropertyGetter {

        /**
         * Called when the names property is queried from an JMS Message object.
         *
         * @param message
         *        The message being acted upon.
         *
         * @return the correct property value from the given Message.
         *
         * @throws JMSException if an error occurs while accessing the property
         */
        Object getProperty(AmqpJmsMessageFacade message) throws JMSException;
    }

    static {
        PROPERTY_GETTERS.put(JMS_AMQP_TTL, new PropertyGetter() {
            @Override
            public Object getProperty(AmqpJmsMessageFacade message) throws JMSException {
                return message.getAmqpTimeToLive();
            }
        });
        PROPERTY_GETTERS.put(JMS_AMQP_REPLY_TO_GROUP_ID, new PropertyGetter() {
            @Override
            public Object getProperty(AmqpJmsMessageFacade message) throws JMSException {
                return message.getReplyToGroupId();
            }
        });
        PROPERTY_GETTERS.put(JMS_AMQP_TYPED_ENCODING, new PropertyGetter() {
            @Override
            public Object getProperty(AmqpJmsMessageFacade message) throws JMSException {
                if (message instanceof AmqpJmsSerializedObjectMessageFacade) {
                    // ((AmqpJmsSerializedObjectMessageFacade) message);
                }

                return false; // TODO
            }
        });
    }

    /**
     * For each of the currently configured message property intercepter instance a
     * string key value is inserted into an Set and returned.
     *
     * @return a Set<String> containing the names of all intercepted properties.
     */
    public static Set<String> getPropertyNames() {
        return PROPERTY_GETTERS.keySet();
    }

    /**
     * Static get method that takes a property name and gets the value either via
     * a registered property get object or through the AmqpJmsMessageFacade getProperty
     * method.
     *
     * @param message
     *        the AmqpJmsMessageFacade instance to read from
     * @param name
     *        the property name that is being requested.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public static Object getProperty(AmqpJmsMessageFacade message, String name) throws JMSException {
        Object value = null;

        PropertyGetter propertyExpression = PROPERTY_GETTERS.get(name);
        if (propertyExpression != null) {
            value = propertyExpression.getProperty(message);
        } else {
            try {
                value = message.getProperty(name);
            } catch (Exception e) {
                throw JmsExceptionSupport.create(e);
            }
        }

        return value;
    }

    /**
     * Allows for the additional PropertyGetter instances to be added to the global set.
     *
     * @param propertyName
     *        The name of the Message property that will be intercepted.
     * @param getter
     *        The PropertyGetter instance that should be used for the named property.
     */
    public static void addPropertyGetter(String propertyName, PropertyGetter getter) {
        PROPERTY_GETTERS.put(propertyName, getter);
    }

    /**
     * Given a property name, remove the configured getter that has been assigned to
     * intercept the queries for that property value.
     *
     * @param propertyName
     *        The name of the Property Getter to remove.
     *
     * @return true if a getter was removed from the global set.
     */
    public boolean removePropertyGetter(String propertyName) {
        if (PROPERTY_GETTERS.remove(propertyName) != null) {
            return true;
        }

        return false;
    }

    private final String name;
    private final PropertyGetter propertyExpression;

    /**
     * Creates an new property getter instance that is assigned to read the named value.
     *
     * @param name
     *        the property value that this getter is assigned to lookup.
     */
    public AmqpJmsMessagePropertyGetter(String name) {
        this.name = name;
        propertyExpression = PROPERTY_GETTERS.get(name);
    }

    /**
     * Gets the correct property value from the AmqpJmsMessageFacade instance based on
     * the predefined property mappings.
     *
     * @param message
     *        the AmqpJmsMessageFacade whose property is being read.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public Object get(AmqpJmsMessageFacade message) throws JMSException {
        if (propertyExpression != null) {
            return propertyExpression.getProperty(message);
        }

        try {
            return message.getProperty(name);
        } catch (Exception e) {
            throw JmsExceptionSupport.create(e);
        }
    }

    /**
     * @return the property name that is being intercepted for the AmqpJmsMessageFacade.
     */
    public String getName() {
        return name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        return name.equals(((AmqpJmsMessagePropertyGetter) o).name);
    }
}
