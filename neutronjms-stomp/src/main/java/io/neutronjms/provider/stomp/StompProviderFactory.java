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
package io.neutronjms.provider.stomp;

import io.neutronjms.provider.AsyncProvider;
import io.neutronjms.provider.ProviderFactory;

import java.net.URI;

/**
 * Factory for creating the STOMP provider.
 */
public class StompProviderFactory extends ProviderFactory {

    @Override
    public AsyncProvider createAsyncProvider(URI remoteURI) throws Exception {
        return new StompProvider(remoteURI);
    }

    @Override
    public String getName() {
        return "STOMP";
    }
}
