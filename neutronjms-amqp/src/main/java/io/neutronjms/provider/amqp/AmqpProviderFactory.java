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
package io.neutronjms.provider.amqp;

import io.neutronjms.provider.AsyncProvider;
import io.neutronjms.provider.BlockingProvider;
import io.neutronjms.provider.DefaultBlockingProvider;
import io.neutronjms.provider.ProviderFactory;

import java.net.URI;

/**
 * Factory for creating the AMQP provider.
 */
public class AmqpProviderFactory extends ProviderFactory {

    @Override
    public BlockingProvider createProvider(URI remoteURI) throws Exception {
        return new DefaultBlockingProvider(createAsyncProvider(remoteURI));
    }

    @Override
    public AsyncProvider createAsyncProvider(URI remoteURI) throws Exception {
        return new AmqpProvider(remoteURI);
    }

    @Override
    public String getName() {
        return "AMQP";
    }
}