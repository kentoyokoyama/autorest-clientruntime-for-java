/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.v3.http;

import java.io.Closeable;

import com.microsoft.rest.v3.http.policy.DecodingPolicy;
import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

/**
 * The type representing response of {@link HttpRequest}.
 */
public abstract class HttpResponse implements Closeable {
    private Object deserializedHeaders;
    private Object deserializedBody;
    private boolean isDecoded;
    private HttpRequest request;

    /**
     * Get the response status code.
     *
     * @return the response status code
     */
    public abstract int statusCode();

    /**
     * Lookup a response header with the provided name.
     *
     * @param name the name of the header to lookup.
     * @return the value of the header, or null if the header doesn't exist in the response.
     */
    public abstract String headerValue(String name);

    /**
     * Get all response headers.
     *
     * @return the response headers
     */
    public abstract HttpHeaders headers();

    /**
     * Get the publisher emitting response content chunks.
     *
     * <p>
     * Returns a stream of the response's body content. Emissions may occur on the
     * Netty EventLoop threads which are shared across channels and should not be
     * blocked. Blocking should be avoided as much as possible/practical in reactive
     * programming but if you do use methods like {@code blockingSubscribe} or {@code blockingGet}
     * on the stream then be sure to use {@code subscribeOn} and {@code observeOn}
     * before the blocking call. For example:
     *
     * <pre>
     * {@code
     *   response.body()
     *     .map(bb -> bb.limit())
     *     .reduce((x,y) -> x + y)
     *     .subscribeOn(Schedulers.io())
     *     .observeOn(Schedulers.io())
     *     .blockingGet();
     * }
     * </pre>
     * <p>
     * The above code is a simplistic example and would probably run fine without
     * the `subscribeOn` and `observeOn` but should be considered a template for
     * more complex situations.
     *
     * @return The response's content as a stream of {@link ByteBuf}.
     */
    public abstract Flux<ByteBuf> body();

    /**
     * Get the response content as a byte[].
     *
     * @return this response content as a byte[]
     */
    public abstract Mono<byte[]> bodyAsByteArray();

    /**
     * Get the response content as a string.
     *
     * @return This response content as a string
     */
    public abstract Mono<String> bodyAsString();

    /**
     * Get the deserialized headers.
     *
     * @return the deserialized headers, if present. Otherwise, null
     */
    public Object deserializedHeaders() {
        return deserializedHeaders;
    }

    /**
     * Set the deserialized headers on this response.
     *
     * @param deserializedHeaders the deserialized headers
     * @return this HttpResponse
     */
    public HttpResponse withDeserializedHeaders(Object deserializedHeaders) {
        this.deserializedHeaders = deserializedHeaders;
        return this;
    }

    /**
     * Get the deserialized body.
     *
     * @return the deserialized body, if present. Otherwise, null.
     */
    public Object deserializedBody() {
        return deserializedBody;
    }

    /**
     * Sets the deserialized content on this response.
     *
     * @param deserializedBody the deserialized content
     * @return this HttpResponse
     */
    public HttpResponse withDeserializedBody(Object deserializedBody) {
        this.deserializedBody = deserializedBody;
        return this;
    }

    /**
     * Get the request which resulted in this response.
     *
     * @return the request which resulted in this response.
     */
    public final HttpRequest request() {
        return request;
    }

    /**
     * Sets the request which resulted in this HttpResponse.
     *
     * @param request the request
     * @return this HTTP response
     */
    public final HttpResponse withRequest(HttpRequest request) {
        this.request = request;
        return this;
    }

    /**
     * Checks the response content is decoded.
     *
     * @return true if the response content decoded by {@link DecodingPolicy} false otherwise
     */
    public boolean isDecoded() {
        return isDecoded;
    }

    /**
     * Sets the flag indicating whether this HttpResponse has been decoded by a {@link DecodingPolicy}.
     *
     * @param isDecoded whether this HttpResponse has been decoded
     * @return this response
     */
    public HttpResponse withIsDecoded(boolean isDecoded) {
        this.isDecoded = isDecoded;
        return this;
    }

    /**
     * Get a new Response object wrapping this response with it's content
     * buffered into memory.
     *
     * @return the new Response object
     */
    public BufferedHttpResponse buffer() {
        return new BufferedHttpResponse(this);
    }

    /**
     * Closes the response content stream, if any.
     */
    @Override
    public void close() {
    }

    // package private for test purpose
    Connection internConnection() {
        return null;
    }
}