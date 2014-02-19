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
package org.fusesource.amqpjms.provider;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fusesource.amqpjms.jms.util.IOExceptionSupport;

/**
 * Asynchronous Provider response class.
 */
public class ProviderResponse<T> implements AsyncResult<T> {

    protected final CountDownLatch latch = new CountDownLatch(1);
    protected Throwable error;
    protected T result;

    @Override
    public void onFailure(Throwable result) {
        error = result;
        latch.countDown();
    }

    @Override
    public void onSuccess(T result) {
        this.result = result;
        latch.countDown();
    }

    /**
     * Timed wait for a response to a Provider operation.
     *
     * @param amount
     *        The amount of time to wait before abandoning the wait.
     * @param unit
     *        The unit to use for this wait period.
     *
     * @return the result of this operation or null if the wait timed out.
     *
     * @throws IOException if an error occurs while waiting for the response.
     */
    public T getResponse(long amount, TimeUnit unit) throws IOException {
        try {
            latch.await(amount, unit);
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw IOExceptionSupport.create(e);
        }
        return getResult();
    }

    /**
     * Waits for a response to some Provider requested operation.
     *
     * @return the response from the Provider for this operation.
     *
     * @throws IOException if an error occurs while waiting for the response.
     */
    public T getResponse() throws IOException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw IOExceptionSupport.create(e);
        }
        return getResult();
    }

    private T getResult() throws IOException {
        Throwable cause = error;
        if (cause != null) {
            throw IOExceptionSupport.create(cause);
        }
        return result;
    }
}
