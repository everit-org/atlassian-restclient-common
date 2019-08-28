package org.everit.atlassian.restclient.common;

import java.util.Optional;

/**
 * Exception type that is thrown if there is a HTTP exception during the REST call.
 */
public class RestException extends RuntimeException {

  private static final long serialVersionUID = 3006142336269779329L;

  private final int httpCode;

  private final Optional<String> responseBody;

  public RestException(String message, int httpCode, Optional<String> responseBody) {
    super(message);
    this.httpCode = httpCode;
    this.responseBody = responseBody;
  }

  public int getHttpCode() {
    return httpCode;
  }

  public Optional<String> getResponseBody() {
    return responseBody;
  }

}
