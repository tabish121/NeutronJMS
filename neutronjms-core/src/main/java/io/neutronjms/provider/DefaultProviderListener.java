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
package io.neutronjms.provider;

import io.neutronjms.jms.message.JmsInboundMessageDispatch;

import java.io.IOException;
import java.net.URI;

/**
 * Default implementation that does nothing for all callbacks.
 */
public class DefaultProviderListener implements ProviderListener {

    @Override
    public void onMessage(JmsInboundMessageDispatch envelope) {
    }

    @Override
    public void onConnectionInterrupted(URI remoteURI) {
    }

    @Override
    public void onConnectionFailure(IOException ex) {
    }

    @Override
    public void onConnectionRecovery(AsyncProvider provider) {
    }

    @Override
    public void onConnectionRecovered(AsyncProvider provider) {
    }

    @Override
    public void onConnectionRestored(URI remoteURI) {
    }
}
