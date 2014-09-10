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

import io.neutronjms.jms.JmsDestination;
import io.neutronjms.jms.exceptions.JmsExceptionSupport;
import io.neutronjms.jms.message.facade.JmsMessageFacade;
import io.neutronjms.util.TypeConversionSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;

/**
 * Utility class used to intercept calls to Message property sets and map the
 * correct OpenWire fields to the property name being set.
 */
public class JmsMessagePropertySetter {

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
        void setProperty(JmsMessageFacade message, Object value) throws JMSException;
    }

    static {
        PROPERTY_SETTERS.put("JMSXDeliveryCount", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Integer rc = (Integer) TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new JMSException("Property JMSXDeliveryCount cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setRedeliveryCounter(rc.intValue() - 1);
            }
        });
        PROPERTY_SETTERS.put("JMSXGroupID", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                String rc = (String) TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new JMSException("Property JMSXGroupID cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setGroupId(rc);
            }
        });
        PROPERTY_SETTERS.put("JMSXGroupSeq", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Integer rc = (Integer) TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new JMSException("Property JMSXGroupSeq cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setGroupSequence(rc.intValue());
            }
        });
        PROPERTY_SETTERS.put("JMSCorrelationID", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                String rc = (String) TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new JMSException("Property JMSCorrelationID cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setCorrelationId(rc);
            }
        });
        PROPERTY_SETTERS.put("JMSDeliveryMode", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Integer rc = null;
                try {
                    rc = (Integer) TypeConversionSupport.convert(value, Integer.class);
                } catch (NumberFormatException nfe) {
                    if (value instanceof String) {
                        if (((String) value).equalsIgnoreCase("PERSISTENT")) {
                            rc = DeliveryMode.PERSISTENT;
                        } else if (((String) value).equalsIgnoreCase("NON_PERSISTENT")) {
                            rc = DeliveryMode.NON_PERSISTENT;
                        } else {
                            throw nfe;
                        }
                    }
                }
                if (rc == null) {
                    Boolean bool = (Boolean) TypeConversionSupport.convert(value, Boolean.class);
                    if (bool == null) {
                        throw new JMSException("Property JMSDeliveryMode cannot be set from a " + value.getClass().getName() + ".");
                    } else {
                        message.setPersistent(bool.booleanValue());
                    }
                } else {
                    message.setPersistent(rc == DeliveryMode.PERSISTENT);
                }
            }
        });
        PROPERTY_SETTERS.put("JMSExpiration", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Long rc = (Long) TypeConversionSupport.convert(value, Long.class);
                if (rc == null) {
                    throw new JMSException("Property JMSExpiration cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setExpiration(rc.longValue());
            }
        });
        PROPERTY_SETTERS.put("JMSPriority", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Integer rc = (Integer) TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new JMSException("Property JMSPriority cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setPriority(rc.byteValue());
            }
        });
        PROPERTY_SETTERS.put("JMSRedelivered", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Boolean rc = (Boolean) TypeConversionSupport.convert(value, Boolean.class);
                if (rc == null) {
                    throw new JMSException("Property JMSRedelivered cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setRedelivered(rc.booleanValue());
            }
        });
        PROPERTY_SETTERS.put("JMSReplyTo", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                JmsDestination rc = (JmsDestination) TypeConversionSupport.convert(value, JmsDestination.class);
                if (rc == null) {
                    throw new JMSException("Property JMSReplyTo cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setReplyTo(rc);
            }
        });
        PROPERTY_SETTERS.put("JMSTimestamp", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                Long rc = (Long) TypeConversionSupport.convert(value, Long.class);
                if (rc == null) {
                    throw new JMSException("Property JMSTimestamp cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setTimestamp(rc.longValue());
            }
        });
        PROPERTY_SETTERS.put("JMSType", new PropertySetter() {
            @Override
            public void setProperty(JmsMessageFacade message, Object value) throws JMSException {
                String rc = (String) TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new JMSException("Property JMSType cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setType(rc);
            }
        });
    }

    /**
     * Static set method that takes a property name and sets the value either via
     * a registered property set object or through the JmsMessageFacade setProperty
     * method.
     *
     * @param message
     *        the JmsMessageFacade instance to write to.
     * @param name
     *        the property name that is being written.
     * @param value
     *        the new value to assign for the named property.
     *
     * @throws JMSException if an error occurs while writing the defined property.
     */
    public static void setProperty(JmsMessageFacade message, String name, Object value) throws JMSException {
        PropertySetter jmsPropertyExpression = PROPERTY_SETTERS.get(name);
        if (jmsPropertyExpression != null) {
            jmsPropertyExpression.setProperty(message, value);
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
    private final PropertySetter jmsPropertyExpression;

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
    public JmsMessagePropertySetter(String name) {
        this.name = name;
        jmsPropertyExpression = PROPERTY_SETTERS.get(name);
    }

    /**
     * Sets the correct property value from the JmsMessageFacade instance based on
     * the predefined property mappings.
     *
     * @param message
     *        the JmsMessageFacade whose property is being read.
     * @param value
     *        the value to be set on the intercepted JmsMessageFacade property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public void set(JmsMessageFacade message, Object value) throws JMSException {
        if (jmsPropertyExpression != null) {
            jmsPropertyExpression.setProperty(message, value);
        } else {
            try {
                message.setProperty(name, value);
            } catch (IOException e) {
                throw JmsExceptionSupport.create(e);
            }
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
        return name.equals(((JmsMessagePropertySetter) o).name);
    }
}
