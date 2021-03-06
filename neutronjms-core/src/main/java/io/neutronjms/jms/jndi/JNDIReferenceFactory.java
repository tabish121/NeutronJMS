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
package io.neutronjms.jms.jndi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

/**
 * Converts objects implementing JNDIStorable into a property fields so they can be
 * stored and regenerated from JNDI
 *
 * @since 1.0
 */
public class JNDIReferenceFactory implements ObjectFactory {

    /**
     * This will be called by a JNDIprovider when a Reference is retrieved from
     * a JNDI store - and generates the original instance
     *
     * @param object
     *        the Reference object
     * @param name
     *        the JNDI name
     * @param nameCtx
     *        the context
     * @param environment
     *        the environment settings used by JNDI
     *
     * @return the instance built from the Reference object
     *
     * @throws Exception
     *         if building the instance from Reference fails (usually class not
     *         found)
     */
    @Override
    public Object getObjectInstance(Object object, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        Object result = null;
        if (object instanceof Reference) {
            Reference reference = (Reference) object;
            Class<?> theClass = loadClass(this, reference.getClassName());
            if (JNDIStorable.class.isAssignableFrom(theClass)) {
                JNDIStorable store = (JNDIStorable) theClass.newInstance();
                Map<String, String> properties = new HashMap<String, String>();
                for (Enumeration<RefAddr> iter = reference.getAll(); iter.hasMoreElements();) {
                    StringRefAddr addr = (StringRefAddr) iter.nextElement();
                    properties.put(addr.getType(), (addr.getContent() == null) ? "" : addr.getContent().toString());
                }
                store.setProperties(properties);
                result = store;
            }
        } else {
            throw new RuntimeException("Object " + object + " is not a reference");
        }
        return result;
    }

    /**
     * Create a Reference instance from a JNDIStorable object
     *
     * @param instanceClassName
     * @param po
     * @return Reference
     * @throws NamingException
     */
    public static Reference createReference(String instanceClassName, JNDIStorable po) throws NamingException {
        Reference result = new Reference(instanceClassName, JNDIReferenceFactory.class.getName(), null);
        try {
            Map<String, String> props = po.getProperties();
            for (Map.Entry<String, String> entry : props.entrySet()) {
                javax.naming.StringRefAddr addr = new javax.naming.StringRefAddr(entry.getKey(), entry.getValue());
                result.add(addr);
            }
        } catch (Exception e) {
            throw new NamingException(e.getMessage());
        }
        return result;
    }

    /**
     * Retrieve the class loader for a named class
     *
     * @param thisObj
     * @param className
     * @return the class
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClass(Object thisObj, String className) throws ClassNotFoundException {
        // try local ClassLoader first.
        ClassLoader loader = thisObj.getClass().getClassLoader();
        Class<?> theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        } else {
            // Will be null in jdk1.1.8
            // use default classLoader
            theClass = Class.forName(className);
        }
        return theClass;
    }
}
