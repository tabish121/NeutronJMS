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

import io.neutronjms.provider.AsyncResult;
import io.openwire.commands.ExceptionResponse;
import io.openwire.commands.Response;

/**
 * Base class for all OpenWire resources.
 */
public interface OpenWireResource {

    /**
     * Called to perform the initial resource creation.
     *
     * @param request
     *        the request that triggered this open call.
     *
     * @throws Exception if an error occurs attempting to open the resource.
     */
    void open(AsyncResult<Void> request) throws Exception;

    /**
     * Close this resource and any child resources that it might contain.
     *
     * @param request
     *        the request that triggered this close call.
     *
     * @throws Exception if an error occurs attempting to open the resource.
     */
    void close(AsyncResult<Void> request) throws Exception;

    /**
     * Called when a response has arrived for a previously sent command.
     *
     * @param response
     *        the received Response object.
     * @param request
     *        the request that triggered the initial operation.
     */
    void onResponse(Response response, AsyncResult<Void> request);

    /**
     * Called when an response is received for a previously sent command that
     * indicates an error condition was encountered.
     *
     * @param errorResponse
     *        The ExceptionResponse that was received.
     * @param request
     *        the request that triggered the initial operation.
     */
    void onExceptionReponse(ExceptionResponse error, AsyncResult<Void> request);

}
