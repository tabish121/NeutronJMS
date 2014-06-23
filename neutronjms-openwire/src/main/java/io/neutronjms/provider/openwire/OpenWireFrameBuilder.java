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
package io.neutronjms.provider.openwire;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.buffer.Buffer;

/**
 * Builds an Open Wire protocol frame from the incoming data buffer that Vert.x provides. Frames
 * are prefixed by an integer value indicating the size of the frame to follow. We will build up
 * a complete frame from any partial frame packets received and consumed all frames in the case
 * of a series of frames being read in a single event.
 */
public class OpenWireFrameBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireFrameBuilder.class);

    private final byte FRAME_SIZE_BYTES = 4;
    private final int COMPACTION_THRESHOLD = 1024 * 4;

    private Buffer buff = null;
    private int readStart;
    private int readEnd;

    private final OpenWireProvider provider;

    /**
     * Creates a new instances that's slaved to the given OpenWireProvider.
     *
     * @param provider
     *        the OpenWireProvider that will be given new frames as they arrive.
     */
    public OpenWireFrameBuilder(OpenWireProvider provider) {
        this.provider = provider;
    }

    public void onData(Buffer event) {
        if (buff == null) {
            buff = event;
        } else {
            buff.appendBuffer(event);
        }
        try {
            Buffer frame = readFrame();
            while (frame != null) {
                LOG.trace("Read new frame of size: {}", frame.length());
                provider.processNewFrame(frame);
                frame = readFrame();
            }

            // If we consume the entire buffer we need to clear the current
            // state data for the next new buffer.  We also need to check if
            // we are wasting to much space in the current buffer and compact
            // as needed.
            if (readStart == buff.length()) {
                buff = null;
                readEnd -= readStart;
                readStart = 0;
            } else if (readStart > COMPACTION_THRESHOLD) {
                buff = buff.getBuffer(readStart, buff.length());
                readEnd -= readStart;
                readStart = 0;
            }
        } catch (Exception e) {
            LOG.debug("Protocol decoding failure: {}", e.getMessage(), e);
        }
    }

    private Buffer readFrame() throws IOException {
        Buffer result = peekBytes(FRAME_SIZE_BYTES);
        if (result != null) {
            final int length = result.getInt(0);
            if (length > provider.getMaxFrameSize()) {
                throw new IOException("Max frame size {" + provider.getMaxFrameSize() + " exceeded. ");
            }

            result = readBytes(FRAME_SIZE_BYTES + length);
        }

        return result;
    }

    private Buffer readBytes(int length) {
        readEnd = readStart + length;
        if (buff.length() < readEnd) {
            return null;
        } else {
            int offset = readStart;
            readStart = readEnd;
            return buff.getBuffer(offset, readEnd);
        }
    }

    private Buffer peekBytes(int length) {
        readEnd = readStart + length;
        if (buff.length() < readEnd) {
            return null;
        } else {
            return buff.getBuffer(readStart, readEnd);
        }
    }
}
