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

import java.util.Optional;

/**
 * Exception type that is thrown if there is a HTTP exception during the REST call.
 */
public class RestException extends RuntimeException {

  private static final String LINE_SEPARATOR = System.lineSeparator();

  private static final long serialVersionUID = 3006142336269779329L;

  private static String createDetailedMessage(String message, String requestUrl,
      int httpCode,
      Optional<String> responseBody) {
    return message + RestException.LINE_SEPARATOR
        + "REQUEST URL: " + requestUrl + RestException.LINE_SEPARATOR
        + "STATUS CODE: " + httpCode + RestException.LINE_SEPARATOR
        + "RESPONSE BODY: " + RestException.LINE_SEPARATOR
        + responseBody;
  }

  private final int httpCode;

  private final String responseBody;

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param httpCode
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   */
  public RestException(String message, int httpCode, Optional<String> responseBody) {
    super(message);
    this.httpCode = httpCode;
    this.responseBody = responseBody.isPresent() ? responseBody.get() : null;
  }

  /**
   * Constructor.
   *
   * @param message
   *          Message of the exception.
   * @param requestUrl
   *          The URL of the http request.
   * @param httpCode
   *          The status code of the HTTP request.
   * @param responseBody
   *          The body of the request if there is one.
   */
  public RestException(String message,
      String requestUrl,
      int httpCode,
      Optional<String> responseBody) {
    super(
        RestException.createDetailedMessage(message, requestUrl, httpCode, responseBody));
    this.httpCode = httpCode;
    this.responseBody = responseBody.isPresent() ? responseBody.get() : null;
  }

  public int getHttpCode() {
    return this.httpCode;
  }

  public Optional<String> getResponseBody() {
    return Optional.ofNullable(this.responseBody);
  }

}
