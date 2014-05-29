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

import io.neutronjms.jms.message.facade.JmsBytesMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import org.fusesource.hawtbuf.Buffer;

/**
 * A JmsBytesMessageFacade that wraps around Proton AMQP Message instances to provide
 * access to the underlying bytes contained in the message.
 */
public class AmqpJmsBytesMessageFacade extends AmqpJmsMessageFacade implements JmsBytesMessageFacade {

    /**
     * Creates a new facade instance
     *
     * @param connection
     */
    public AmqpJmsBytesMessageFacade(AmqpConnection connection) {
        super(connection);
    }

    @Override
    public JmsBytesMessageFacade copy() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Buffer getContent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setContent(Buffer content) {
        // TODO Auto-generated method stub

    }

}
