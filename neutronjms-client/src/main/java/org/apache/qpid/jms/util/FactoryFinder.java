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
package org.apache.qpid.jms.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Factory finding helper class used to locate objects that serve as Factories for
 * other Object types.  The search an instantiate mechanism is configurable so that
 * in a non-stand-alone environment such as OSGI the finder and be configured to work.
 */
public class FactoryFinder<T extends Object> {

    /**
     * The strategy that the FactoryFinder uses to find load and instantiate Objects can be
     * changed out by calling the
     * {@link org.apache.qpid.jms.util.FactoryFinder#setObjectFactory(org.apache.qpid.jms.util.FactoryFinder.ObjectFactory)}
     * method with a custom implementation of ObjectFactory.
     *
     * The default ObjectFactory is typically changed out when running in a specialized
     * container environment where service discovery needs to be done via the container system.
     * For example, in an OSGi scenario.
     */
    public interface ObjectFactory {

        /**
         * Creates the requested factory instance.
         *
         * @param path
         *        the full service path
         *
         * @return instance of the factory object being searched for.
         *
         * @throws IllegalAccessException if an error occurs while accessing the search path.
         * @throws InstantiationException if the factory object fails on create.
         * @throws IOException if the search encounter an IO error.
         * @throws ClassNotFoundException if the class that is to be loaded cannot be found.
         */
        public Object create(String path) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException;

    }

    private static ObjectFactory objectFactory = new StandaloneObjectFactory();

    private final ConcurrentHashMap<String, T> cachedFactories = new ConcurrentHashMap<String, T>();
    private final String path;
    private final Class<T> factoryType;

    /**
     * Creates a new instance of the FactoryFinder using the given search path.
     *
     * @param path
     *        The path to use when searching for the factory definitions.
     */
    public FactoryFinder(Class<T> factoryType, String path) {
        this.path = path;
        this.factoryType = factoryType;
    }

    /**
     * @return the currently configured ObjectFactory instance used to locate the Factory objects.
     */
    public static ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    /**
     * Sets the ObjectFactory instance to use when searching for the Factory class.  This allows
     * the default instance to be overridden in an environment where the basic version will not
     * work.
     *
     * @param objectFactory
     *        the new object factory to use when searching for a Factory instance.
     */
    public static void setObjectFactory(ObjectFactory objectFactory) {
        FactoryFinder.objectFactory = objectFactory;
    }

    /**
     * Creates a new instance of the given key.  The method first checks the cache of previously
     * found factory instances for one that matches the key.  If no cached version exists then
     * the factory will be searched for using the configured ObjectFactory instance.
     *
     * @param key
     *        is the key to add to the path to find a text file containing the factory name
     *
     * @return a newly created instance
     *
     * @throws IllegalAccessException if an error occurs while accessing the search path.
     * @throws InstantiationException if the factory object fails on create.
     * @throws IOException if the search encounter an IO error.
     * @throws ClassNotFoundException if the class that is to be loaded cannot be found.
     * @throws ClassCastException if the found object is not assignable to the request factory type.
     */
    public T newInstance(String key) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException, ClassCastException {
        T factory = cachedFactories.get(key);
        if (factory == null) {

            Object found = objectFactory.create(path + key);
            if (found != null && factoryType.isInstance(found)) {
                factory = factoryType.cast(found);
                cachedFactories.put(key, factory);
            } else {
                throw new ClassCastException("Cannot cast " + found.getClass().getName() +
                    " to " + factoryType.getName());
            }
        }

        return factory;
    }

    /**
     * Allow registration of a Provider factory without wiring via META-INF classes
     *
     * @param scheme
     *        The URI scheme value that names the target Provider instance.
     * @param factory
     *        The factory to register in this finder.
     */
    public void registerProviderFactory(String scheme, T factory) {
        cachedFactories.put(scheme, factory);
    }

    /**
     * The default implementation of Object factory which works well in stand-alone applications.
     */
    @SuppressWarnings("rawtypes")
    protected static class StandaloneObjectFactory implements ObjectFactory {
        final ConcurrentHashMap<String, Class> classMap = new ConcurrentHashMap<String, Class>();

        @Override
        public Object create(final String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
            Class clazz = classMap.get(path);
            if (clazz == null) {
                clazz = loadClass(loadProperties(path));
                classMap.put(path, clazz);
            }
            return clazz.newInstance();
        }

        static public Class loadClass(Properties properties) throws ClassNotFoundException, IOException {

            String className = properties.getProperty("class");
            if (className == null) {
                throw new IOException("Expected property is missing: class");
            }
            Class clazz = null;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                try {
                    clazz = loader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            if (clazz == null) {
                clazz = FactoryFinder.class.getClassLoader().loadClass(className);
            }

            return clazz;
        }

        static public Properties loadProperties(String uri) throws IOException {
            // lets try the thread context class loader first
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = StandaloneObjectFactory.class.getClassLoader();
            }
            InputStream in = classLoader.getResourceAsStream(uri);
            if (in == null) {
                in = FactoryFinder.class.getClassLoader().getResourceAsStream(uri);
                if (in == null) {
                    throw new IOException("Could not find factory class for resource: " + uri);
                }
            }

            // lets load the file
            BufferedInputStream reader = null;
            try {
                reader = new BufferedInputStream(in);
                Properties properties = new Properties();
                properties.load(reader);
                return properties;
            } finally {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
