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
package org.hawtjms.provider.discovery;

/**
 * Interface for all agents used to detect instances of remote peers on the network.
 */
public interface DiscoveryAgent {

    /**
     * Sets the discovery listener
     *
     * @param listener
     *        the listener to notify on discovery events, or null to clear.
     */
    void setDiscoveryListener(DiscoveryListener listener);

}