/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.v3.policy;

import com.microsoft.rest.v3.http.HttpPipelineCallContext;
import com.microsoft.rest.v3.http.HttpResponse;
import com.microsoft.rest.v3.http.NextPolicy;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * The Pipeline policy that puts a UUID in the request header. Azure uses the request id as
 * the unique identifier for the request.
 */
public class RequestIdPolicy implements HttpPipelinePolicy {
    private static final String REQUEST_ID_HEADER = "x-ms-client-request-id";

    @Override
    public Mono<HttpResponse> process(HttpPipelineCallContext context, NextPolicy next) {
        String requestId = context.httpRequest().headers().value(REQUEST_ID_HEADER);
        if (requestId == null) {
            context.httpRequest().headers().set(REQUEST_ID_HEADER, UUID.randomUUID().toString());
        }
        return next.process();
    }
}