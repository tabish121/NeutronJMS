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

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;

/**
 * Temporary Topic Object
 */
public class JmsTemporaryTopic extends JmsDestination implements TemporaryTopic {

    public JmsTemporaryTopic() {
        super(null, true, true);
    }

    public JmsTemporaryTopic(String name) {
        super(name, true, true);
    }

    @Override
    public JmsTemporaryTopic copy() {
        final JmsTemporaryTopic copy = new JmsTemporaryTopic();
        copy.setProperties(getProperties());
        return copy;
    }

    /**
     * @see javax.jms.TemporaryTopic#delete()
     */
    @Override
    public void delete() {
        try {
            tryDelete();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return name
     * @see javax.jms.Topic#getTopicName()
     */
    @Override
    public String getTopicName() {
        return getName();
    }
}
