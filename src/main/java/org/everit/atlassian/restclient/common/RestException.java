package org.everit.atlassian.restclient.common;

import java.util.Optional;

/**
 * Exception type that is thrown if there is a HTTP exception during the REST call.
 */
public class RestException extends RuntimeException {

  private static final long serialVersionUID = 3006142336269779329L;

  private final int httpCode;

  private final Optional<String> responseBody;

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
    this.responseBody = responseBody;
  }

  public int getHttpCode() {
    return this.httpCode;
  }

  public Optional<String> getResponseBody() {
    return this.responseBody;
  }

}
