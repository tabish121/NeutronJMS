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

import static io.neutronjms.provider.amqp.message.AmqpMessageSupport.JMS_BYTES_MESSAGE;
import io.neutronjms.jms.message.facade.JmsBytesMessageFacade;
import io.neutronjms.provider.amqp.AmqpConnection;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.fusesource.hawtbuf.Buffer;

/**
 * A JmsBytesMessageFacade that wraps around Proton AMQP Message instances to provide
 * access to the underlying bytes contained in the message.
 */
public class AmqpJmsBytesMessageFacade extends AmqpJmsMessageFacade implements JmsBytesMessageFacade {

    private static final String CONTENT_TYPE = "application/octet-stream";
    private static final Buffer EMPTY_BUFFER = new Buffer(new byte[0]);
    private static final Data EMPTY_DATA = new Data(new Binary(new byte[0]));

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
        AmqpJmsBytesMessageFacade copy = new AmqpJmsBytesMessageFacade(connection);
        copyInto(copy);
        return copy;
    }

    /**
     * Copies the Buffer contained in this message to the target message.
     *
     * @param target
     *        the target message that will receive a copy of this message's content.
     */
    protected void copyInto(AmqpJmsBytesMessageFacade target) {
        super.copyInto(target);
        target.setContent(getContent().deepCopy());
    }

    @Override
    public byte getJmsMsgType() {
        return JMS_BYTES_MESSAGE;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public boolean isEmpty() {
        Binary payload = getBinaryFromBody();
        return payload != null && payload.getLength() > 0;
    }

    @Override
    public Buffer getContent() {
        Buffer result = EMPTY_BUFFER;
        Binary payload = getBinaryFromBody();
        if (payload != null && payload.getLength() > 0) {
            result = new Buffer(payload.getArray(), payload.getArrayOffset(), payload.getLength());
        }

        return result;
    }

    @Override
    public void setContent(Buffer content) {
        Data body = EMPTY_DATA;
        if (content != null) {
            body = new Data(new Binary(content.data, content.offset, content.length));
        }

        getAmqpMessage().setBody(body);
    }

    private Binary getBinaryFromBody() {
        Section body = getAmqpMessage().getBody();
        Binary result = null;

        if (body == null) {
            return result;
        }

        if (body instanceof Data) {
            Binary payload = ((Data) body).getValue();
            if (payload != null && payload.getLength() != 0) {
                result = payload;
            }
        } else if(body instanceof AmqpValue) {
            Object value = ((AmqpValue) body).getValue();
            if (value == null) {
                return result;
            }

            if (value instanceof Binary) {
                Binary payload = (Binary)value;
                if (payload != null && payload.getLength() != 0) {
                    result = payload;
                }
            } else {
                throw new IllegalStateException("Unexpected amqp-value body content type: " + value.getClass().getSimpleName());
            }
        } else {
            throw new IllegalStateException("Unexpected body content type: " + body.getClass().getSimpleName());
        }

        return result;
    }
}
