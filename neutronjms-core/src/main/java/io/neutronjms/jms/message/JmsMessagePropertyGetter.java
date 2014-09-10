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

import io.neutronjms.jms.exceptions.JmsExceptionSupport;
import io.neutronjms.jms.message.facade.JmsMessageFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * Utility class used to intercept calls to Message property gets and map the
 * correct OpenWire fields to the property name being queried.
 */
public class JmsMessagePropertyGetter {

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
        Object getProperty(JmsMessageFacade message) throws JMSException;
    }

    static {
        PROPERTY_GETTERS.put("JMSDestination", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                Destination dest = message.getDestination();
                if (dest == null) {
                    return null;
                }
                return dest.toString();
            }
        });
        PROPERTY_GETTERS.put("JMSReplyTo", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                if (message.getReplyTo() == null) {
                    return null;
                }
                return message.getReplyTo().toString();
            }
        });
        PROPERTY_GETTERS.put("JMSType", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return message.getType();
            }
        });
        PROPERTY_GETTERS.put("JMSDeliveryMode", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return message.isPersistent() ? "PERSISTENT" : "NON_PERSISTENT";
            }
        });
        PROPERTY_GETTERS.put("JMSPriority", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return Integer.valueOf(message.getPriority());
            }
        });
        PROPERTY_GETTERS.put("JMSMessageID", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                if (message.getMessageId() == null) {
                    return null;
                }
                return message.getMessageId().toString();
            }
        });
        PROPERTY_GETTERS.put("JMSTimestamp", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return Long.valueOf(message.getTimestamp());
            }
        });
        PROPERTY_GETTERS.put("JMSCorrelationID", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return message.getCorrelationId();
            }
        });
        PROPERTY_GETTERS.put("JMSExpiration", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return Long.valueOf(message.getExpiration());
            }
        });
        PROPERTY_GETTERS.put("JMSRedelivered", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return Boolean.valueOf(message.isRedelivered());
            }
        });
        PROPERTY_GETTERS.put("JMSXDeliveryCount", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return Integer.valueOf(message.getRedeliveryCounter() + 1);
            }
        });
        PROPERTY_GETTERS.put("JMSXGroupID", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return message.getGroupId();
            }
        });
        PROPERTY_GETTERS.put("JMSXUserID", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                Object userId = message.getUserId();
                if (userId == null) {
                    try {
                        userId = message.getProperty("JMSXUserID");
                    } catch (Exception e) {
                        throw JmsExceptionSupport.create(e);
                    }
                }

                return userId;
            }
        });
        PROPERTY_GETTERS.put("JMSXGroupSeq", new PropertyGetter() {
            @Override
            public Object getProperty(JmsMessageFacade message) throws JMSException {
                return new Integer(message.getGroupSequence());
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
     * a registered property get object or through the JmsMessageFacade getProperty
     * method.
     *
     * @param message
     *        the JmsMessageFacade instance to read from
     * @param name
     *        the property name that is being requested.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public static Object getProperty(JmsMessageFacade message, String name) throws JMSException {
        Object value = null;

        PropertyGetter jmsPropertyExpression = PROPERTY_GETTERS.get(name);
        if (jmsPropertyExpression != null) {
            value = jmsPropertyExpression.getProperty(message);
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
    private final PropertyGetter jmsPropertyExpression;

    /**
     * Creates an new property getter instance that is assigned to read the named value.
     *
     * @param name
     *        the property value that this getter is assigned to lookup.
     */
    public JmsMessagePropertyGetter(String name) {
        this.name = name;
        jmsPropertyExpression = PROPERTY_GETTERS.get(name);
    }

    /**
     * Gets the correct property value from the JmsMessageFacade instance based on
     * the predefined property mappings.
     *
     * @param message
     *        the JmsMessageFacade whose property is being read.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public Object get(JmsMessageFacade message) throws JMSException {
        if (jmsPropertyExpression != null) {
            return jmsPropertyExpression.getProperty(message);
        }

        try {
            return message.getProperty(name);
        } catch (Exception e) {
            throw JmsExceptionSupport.create(e);
        }
    }

    /**
     * @return the property name that is being intercepted for the JmsMessageFacade.
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
        return name.equals(((JmsMessagePropertyGetter) o).name);
    }
}
