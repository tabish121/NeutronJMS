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
import io.neutronjms.util.TypeConversionSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;

/**
 * Utility class used to intercept calls to Message property sets and map the
 * correct OpenWire fields to the property name being set.
 */
public class AmqpJmsMessagePropertySetter {

    private static final Map<String, PropertySetter> PROPERTY_SETTERS = new HashMap<String, PropertySetter>();

    /**
     * Interface for a Property Set intercepter object used to write JMS style
     * properties that are part of the JMS Message object members or perform
     * some needed conversion action before some named property is set.
     */
    interface PropertySetter {

        /**
         * Called when the names property is assigned from an JMS Message object.
         *
         * @param message
         *        The message instance being acted upon.
         * @param value
         *        The value to assign to the intercepted property.
         *
         * @throws JMSException if an error occurs writing the property.
s         */
        void setProperty(AmqpJmsMessageFacade message, Object value) throws JMSException;
    }

    static {
        PROPERTY_SETTERS.put(JMS_AMQP_TTL, new PropertySetter() {
            @Override
            public void setProperty(AmqpJmsMessageFacade message, Object value) throws JMSException {
                Long rc = (Long) TypeConversionSupport.convert(value, Long.class);
                if (rc == null) {
                    throw new JMSException("Property " + JMS_AMQP_TTL + " cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setAmqpTimeToLive(rc.longValue());
            }
        });
        PROPERTY_SETTERS.put(JMS_AMQP_REPLY_TO_GROUP_ID, new PropertySetter() {
            @Override
            public void setProperty(AmqpJmsMessageFacade message, Object value) throws JMSException {
                String rc = (String) TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new JMSException("Property " + JMS_AMQP_REPLY_TO_GROUP_ID + " cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setReplyToGroupId(rc);
            }
        });
        PROPERTY_SETTERS.put(JMS_AMQP_TYPED_ENCODING, new PropertySetter() {
            @Override
            public void setProperty(AmqpJmsMessageFacade message, Object value) throws JMSException {
                Integer rc = (Integer) TypeConversionSupport.convert(value, Boolean.class);
                if (rc == null) {
                    throw new JMSException("Property " + JMS_AMQP_TYPED_ENCODING + " cannot be set from a " + value.getClass().getName() + ".");
                }

                // TODO - Finished Typed encoding work.
                if (message instanceof AmqpJmsSerializedObjectMessageFacade) {
                    // ((AmqpJmsSerializedObjectMessageFacade) message)
                } else {
                    throw new MessageFormatException(JMS_AMQP_TYPED_ENCODING + " is only applicable to ObjectMessage");
                }
            }
        });
    }

    /**
     * Static set method that takes a property name and sets the value either via
     * a registered property set object or through the AmqpJmsMessageFacade setProperty
     * method.
     *
     * @param message
     *        the AmqpJmsMessageFacade instance to write to.
     * @param name
     *        the property name that is being written.
     * @param value
     *        the new value to assign for the named property.
     *
     * @throws JMSException if an error occurs while writing the defined property.
     */
    public static void setProperty(AmqpJmsMessageFacade message, String name, Object value) throws JMSException {
        PropertySetter propertyExpression = PROPERTY_SETTERS.get(name);
        if (propertyExpression != null) {
            propertyExpression.setProperty(message, value);
        } else {
            try {
                message.setProperty(name, value);
            } catch (IOException e) {
                throw JmsExceptionSupport.create(e);
            }
        }
    }

    /**
     * Allows for the additional PropertySetter instances to be added to the global set.
     *
     * @param propertyName
     *        The name of the Message property that will be intercepted.
     * @param getter
     *        The PropertySetter instance that should be used for the named property.
     */
    public static void addPropertySetter(String propertyName, PropertySetter getter) {
        PROPERTY_SETTERS.put(propertyName, getter);
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
    public boolean removePropertySetter(String propertyName) {
        if (PROPERTY_SETTERS.remove(propertyName) != null) {
            return true;
        }

        return false;
    }

    private final String name;
    private final PropertySetter propertyExpression;

    /**
     * For each of the currently configured message property intercepter instance a
     * string key value is inserted into an Set and returned.
     *
     * @return a Set<String> containing the names of all intercepted properties.
     */
    public static Set<String> getPropertyNames() {
        return PROPERTY_SETTERS.keySet();
    }

    /**
     * Creates an new property getter instance that is assigned to read the named value.
     *
     * @param name
     *        the property value that this getter is assigned to lookup.
     */
    public AmqpJmsMessagePropertySetter(String name) {
        this.name = name;
        propertyExpression = PROPERTY_SETTERS.get(name);
    }

    /**
     * Sets the correct property value from the AmqpJmsMessageFacade instance based on
     * the predefined property mappings.
     *
     * @param message
     *        the AmqpJmsMessageFacade whose property is being read.
     * @param value
     *        the value to be set on the intercepted AmqpJmsMessageFacade property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public void set(AmqpJmsMessageFacade message, Object value) throws JMSException {
        if (propertyExpression != null) {
            propertyExpression.setProperty(message, value);
        } else {
            try {
                message.setProperty(name, value);
            } catch (IOException e) {
                throw JmsExceptionSupport.create(e);
            }
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
        return name.equals(((AmqpJmsMessagePropertySetter) o).name);
    }
}
