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
package io.neutronjms.jms;

import io.neutronjms.jms.meta.JmsConsumerId;

import javax.jms.JMSException;

/**
 * Implementation of a TopicSubscriber that is Durable
 */
public class JmsDurableTopicSubscriber extends JmsTopicSubscriber {

    /**
     * Creates a durable TopicSubscriber
     *
     * @param id
     * @param s
     * @param destination
     * @param name
     * @param noLocal
     * @param selector
     * @throws JMSException
     */
    public JmsDurableTopicSubscriber(JmsConsumerId id, JmsSession s, JmsDestination destination, String name, boolean noLocal, String selector) throws JMSException {
        super(id, s, destination, name, noLocal, selector);
    }

    @Override
    public boolean isDurableSubscription() {
        return true;
    }
}
