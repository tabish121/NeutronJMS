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
package io.neutronjms.provider.failover;

import io.neutronjms.provider.AsyncProvider;
import io.neutronjms.provider.BlockingProvider;
import io.neutronjms.provider.DefaultBlockingProvider;
import io.neutronjms.provider.ProviderFactory;
import io.neutronjms.util.PropertyUtil;
import io.neutronjms.util.URISupport;
import io.neutronjms.util.URISupport.CompositeData;

import java.net.URI;
import java.util.Map;

/**
 * Factory for creating instances of the Failover Provider type.
 */
public class FailoverProviderFactory extends ProviderFactory {

    @Override
    public BlockingProvider createProvider(URI remoteURI) throws Exception {

        CompositeData composite = URISupport.parseComposite(remoteURI);
        Map<String, String> options = composite.getParameters();
        Map<String, String> nested = PropertyUtil.filterProperties(options, "nested.");

        FailoverProvider failover = new FailoverProvider(composite.getComponents(), nested);
        if (!PropertyUtil.setProperties(failover, options)) {
            String msg = ""
                + " Not all options could be set on the Failover provider."
                + " Check the options are spelled correctly."
                + " Given parameters=[" + options + "]."
                + " This Provider cannot be started.";
            throw new IllegalArgumentException(msg);
        }

        BlockingProvider provider = new DefaultBlockingProvider(failover);

        return provider;
    }

    @Override
    public AsyncProvider createAsyncProvider(URI remoteURI) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "Failover";
    }
}
