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
package io.neutronjms.test.support;

import io.neutronjms.jms.JmsConnectionFactory;

import java.net.URI;

import javax.jms.Connection;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test support class used in OpenWire based tests.
 */
public class OpenwireTestSupport extends NeutronJmsTestSupport {

    protected static final Logger LOG = LoggerFactory.getLogger(OpenwireTestSupport.class);

    protected boolean isOpenwireDiscovery() {
        return false;
    }

    public URI getBrokerOpenWireConnectionURI() {
        try {
            return new URI("openwire://127.0.0.1:" +
                brokerService.getTransportConnectorByName("openwire").getPublishableConnectURI().getPort());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String getOpenwireFailoverURI() throws Exception {
        StringBuilder uri = new StringBuilder();
        uri.append("failover://(");
        uri.append(brokerService.getTransportConnectorByName("openwire").getPublishableConnectString());

        for (BrokerService broker : brokers) {
            uri.append(",");
            uri.append(broker.getTransportConnectorByName("openwire").getPublishableConnectString());
        }

        uri.append(")");

        return uri.toString();
    }

    public Connection createOpenwireConnection() throws Exception {
        return createOpenwireConnection(getBrokerOpenWireConnectionURI());
    }

    public Connection createOpenwireConnection(String username, String password) throws Exception {
        return createOpenwireConnection(getBrokerOpenWireConnectionURI(), username, password);
    }

    public Connection createOpenwireConnection(URI brokerURI) throws Exception {
        return createOpenwireConnection(brokerURI, null, null);
    }

    public Connection createOpenwireConnection(URI brokerURI, String username, String password) throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(brokerURI);
        if (username != null) {
            factory.setUsername(username);
        }
        if (password != null) {
            factory.setPassword(password);
        }
        return factory.createConnection();
    }
}
