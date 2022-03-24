/*
 * Copyright © 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.http.restclient;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.everit.http.client.HttpMethod;
import org.everit.http.client.async.AsyncContentProvider;
import org.everit.http.client.async.AsyncContentUtil;

/**
 * Exception type that is thrown if there is a HTTP exception during the REST call.
 */
public class RestException extends RuntimeException {

  private static final String LINE_SEPARATOR = System.lineSeparator();

  private static final long serialVersionUID = 3006142336269779329L;

  private static String createDetailedMessage(
      final String message,
      final HttpMethod httpMethod,
      final String requestUrl,
      final Optional<AsyncContentProvider> requestBody,
      final int status,
      final Optional<String> responseBody) {
    return message + RestException.LINE_SEPARATOR
        + "HTTP METHOD: " + httpMethod + RestException.LINE_SEPARATOR
        + "REQUEST URL: " + requestUrl + RestException.LINE_SEPARATOR
        + "REQUEST BODY: " + RestException.LINE_SEPARATOR
        + (requestBody.isPresent()
            ? AsyncContentUtil.readString(requestBody.get(), StandardCharsets.UTF_8).blockingGet()
            : "[-NO-REQUEST-BODY-AVAILABLE-]")
        + RestException.LINE_SEPARATOR
        + "STATUS CODE: " + status + RestException.LINE_SEPARATOR
        + "RESPONSE BODY: " + RestException.LINE_SEPARATOR
        + (responseBody.isPresent() ? responseBody.get() : "[-NO-RESPONSE-BODY-AVAILABLE-]");
  }

  private final HttpMethod httpMethod;

  private final String originalMessage;

  private final String requestUrl;

  private final String responseBody;

  private final int status;

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param httpMethod
   *          The method of the request.
   * @param requestUrl
   *          The URL of the http request.
   * @param requestBody
   *          The body of the request if there is one.
   * @param status
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   * @param cause
   *          The cause of the exeption if there is any.
   */
  public RestException(
      final String message,
      final HttpMethod httpMethod,
      final String requestUrl,
      final Optional<AsyncContentProvider> requestBody,
      final int status,
      final Optional<String> responseBody,
      final Throwable cause) {
    super(RestException.createDetailedMessage(
        message, httpMethod, requestUrl, requestBody, status, responseBody),
        cause);
    this.originalMessage = message;
    this.httpMethod = httpMethod;
    this.requestUrl = requestUrl;
    this.status = status;
    this.responseBody = responseBody.isPresent() ? responseBody.get() : null;
  }

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param status
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   */
  public RestException(
      final String message,
      final int status,
      final Optional<String> responseBody) {
    this(message, status, responseBody, null);
  }

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param status
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   * @param cause
   *          The cause of the exception.
   */
  public RestException(
      final String message,
      final int status,
      final Optional<String> responseBody,
      final Throwable cause) {
    this(message, null, null, Optional.empty(), status, responseBody, cause);
  }

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param requestUrl
   *          The URL of the http request.
   * @param status
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   */
  public RestException(
      final String message,
      final String requestUrl,
      final int status,
      final Optional<String> responseBody) {
    this(message, null, requestUrl, Optional.empty(), status, responseBody, null);
  }

  /**
   * Returns the status code of the response.
   *
   * @deprecated Use {@link #getStatus()} instead.
   */
  @Deprecated
  public int getHttpCode() {
    return getStatus();
  }

  public HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  public String getOriginalMessage() {
    return this.originalMessage;
  }

  public String getRequestUrl() {
    return this.requestUrl;
  }

  public Optional<String> getResponseBody() {
    return Optional.ofNullable(this.responseBody);
  }

  public int getStatus() {
    return this.status;
  }

}
