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
package io.neutronjms.provider.stomp.message;

import io.neutronjms.jms.message.facade.JmsBytesMessageFacade;
import io.neutronjms.provider.stomp.StompConnection;
import io.neutronjms.provider.stomp.StompFrame;

import org.fusesource.hawtbuf.Buffer;

/**
 * STOMP based JmsBytesMessageFacade that gets direct access to the bytes content
 * of the wrapped STOMP Frame.
 */
public class StompJmsBytesMessageFacade extends StompJmsMessageFacade implements JmsBytesMessageFacade {

    /**
     * @param connection
     */
    public StompJmsBytesMessageFacade(StompConnection connection) {
        super(connection);
    }

    /**
     * @param message
     * @param connection
     */
    public StompJmsBytesMessageFacade(StompFrame message, StompConnection connection) {
        super(message, connection);
    }

    @Override
    public JmsBytesMessageFacade copy() {
        StompJmsBytesMessageFacade copy = new StompJmsBytesMessageFacade(message.clone(), connection);
        return copy;
    }

    @Override
    public Buffer getContent() {
        return message.getContent();
    }

    @Override
    public void setContent(Buffer content) {
        this.message.setContent(content);
    }
}
