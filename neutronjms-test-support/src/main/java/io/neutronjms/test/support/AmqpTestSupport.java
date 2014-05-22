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
import java.util.Map;

import javax.jms.Connection;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpTestSupport extends NeutronJmsTestSupport {

    protected static final Logger LOG = LoggerFactory.getLogger(AmqpTestSupport.class);

    protected boolean isAmqpDiscovery() {
        return false;
    }

    protected String getAmqpTransformer() {
        return "jms";
    }

    @Override
    protected void addAdditionalConnectors(BrokerService brokerService, Map<String, Integer> portMap) throws Exception {
        int port = 0;
        if (portMap.containsKey("amqp")) {
            port = portMap.get("amqp");
        }
        TransportConnector connector = brokerService.addConnector(
            "amqp://0.0.0.0:" + port + "?transport.transformer=" + getAmqpTransformer());
        connector.setName("amqp");
        if (isAmqpDiscovery()) {
            connector.setDiscoveryUri(new URI("multicast://default"));
        }
        port = connector.getPublishableConnectURI().getPort();
        LOG.debug("Using amqp port: {}", port);
    }

    public String getAmqpConnectionURIOptions() {
        return "";
    }

    public URI getBrokerAmqpConnectionURI() {
        try {
            String uri = "amqp://127.0.0.1:" +
                brokerService.getTransportConnectorByName("amqp").getPublishableConnectURI().getPort();

            if (!getAmqpConnectionURIOptions().isEmpty()) {
                uri = uri + "?" + getAmqpConnectionURIOptions();
            }

            return new URI(uri);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String getAmqpFailoverURI() throws Exception {
        StringBuilder uri = new StringBuilder();
        uri.append("failover://(");
        uri.append(brokerService.getTransportConnectorByName("amqp").getPublishableConnectString());

        for (BrokerService broker : brokers) {
            uri.append(",");
            uri.append(broker.getTransportConnectorByName("amqp").getPublishableConnectString());
        }

        uri.append(")");

        return uri.toString();
    }

    public Connection createAmqpConnection() throws Exception {
        return createAmqpConnection(getBrokerAmqpConnectionURI());
    }

    public Connection createAmqpConnection(String username, String password) throws Exception {
        return createAmqpConnection(getBrokerAmqpConnectionURI(), username, password);
    }

    public Connection createAmqpConnection(URI brokerURI) throws Exception {
        return createAmqpConnection(brokerURI, null, null);
    }

    public Connection createAmqpConnection(URI brokerURI, String username, String password) throws Exception {
        JmsConnectionFactory factory = new JmsConnectionFactory(brokerURI);
        factory.setForceAsyncSend(isForceAsyncSends());
        factory.setAlwaysSyncSend(isAlwaysSyncSend());
        factory.setMessagePrioritySupported(isMessagePrioritySupported());
        factory.setSendAcksAsync(isSendAcksAsync());
        if (username != null) {
            factory.setUsername(username);
        }
        if (password != null) {
            factory.setPassword(password);
        }
        return factory.createConnection();
    }
}