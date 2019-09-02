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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Generated;

import org.everit.http.client.HttpMethod;
import org.everit.http.client.async.AsyncContentProvider;

/**
 * A rest request that can be sent to a remote endpoint with one of the CallEndpoint parameters of
 * {@link RestCallUtil}.
 */
public final class RestRequest {

  /**
   * Builder to build {@link RestRequest}.
   */
  @Generated("SparkTools")
  public static final class Builder {
    private String basePath;

    private Map<String, String> headers = Collections.emptyMap();

    private HttpMethod method = HttpMethod.GET;

    private String path;

    private Map<String, String> pathParams = Collections.emptyMap();

    private Map<String, Collection<String>> queryParams = Collections.emptyMap();

    private Optional<?> requestBody = Optional.empty();

    private Builder() {
    }

    private Builder(RestRequest restRequest) {
      this.basePath = restRequest.basePath;
      this.headers = restRequest.headers;
      this.method = restRequest.method;
      this.path = restRequest.path;
      this.pathParams = restRequest.pathParams;
      this.queryParams = restRequest.queryParams;
      this.requestBody = restRequest.requestBody;
    }

    /**
     * The base path of the request with probably the protocol, hostname and optionally the port.
     */
    public Builder basePath(String basePath) {
      this.basePath = basePath;
      return this;
    }

    /**
     * Builds the unmodifiable rest request instance.
     */
    public RestRequest build() {
      return new RestRequest(this);
    }

    /**
     * The headers of the rest request.
     */
    public Builder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    /**
     * Method of the rest request. Default: {@link HttpMethod#GET}
     */
    public Builder method(HttpMethod method) {
      this.method = method;
      return this;
    }

    /**
     * Path of the rest request.
     */
    public Builder path(String path) {
      this.path = path;
      return this;
    }

    /**
     * Path parameters that will be injected within {@link #path(String)}.
     */
    public Builder pathParams(Map<String, String> pathParams) {
      this.pathParams = pathParams;
      return this;
    }

    /**
     * Query parameters that will be appended after the path.
     */
    public Builder queryParams(Map<String, Collection<String>> queryParams) {
      this.queryParams = queryParams;
      return this;
    }

    /**
     * Optional body of the request. If the object is an implementation of
     * {@link AsyncContentProvider} it is be used as is, otherwise Jackson is used to convert the
     * object to a JSON String.
     */
    public Builder requestBody(Optional<?> requestBody) {
      this.requestBody = requestBody;
      return this;
    }
  }

  /**
   * Creates builder to build {@link RestRequest}.
   *
   * @return created builder
   */
  @Generated("SparkTools")
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a builder to build {@link RestRequest} and initialize it with the given object.
   *
   * @param restRequest
   *          to initialize the builder with
   * @return created builder
   */
  @Generated("SparkTools")
  public static Builder builderFrom(RestRequest restRequest) {
    return new Builder(restRequest);
  }

  private final String basePath;

  private final Map<String, String> headers;

  private final HttpMethod method;

  private final String path;

  private final Map<String, String> pathParams;

  private final Map<String, Collection<String>> queryParams;

  private final Optional<?> requestBody;

  @Generated("SparkTools")
  private RestRequest(Builder builder) {
    this.basePath = builder.basePath;
    this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
    this.method = builder.method;
    this.path = builder.path;
    this.pathParams = Collections.unmodifiableMap(new HashMap<>(builder.pathParams));
    this.queryParams = Collections.unmodifiableMap(new HashMap<>(builder.queryParams));
    this.requestBody = builder.requestBody;
  }

  /**
   * Builds the path that will be used by this request including the path parameters.
   */
  public String buildPathWithPathParams() {
    String pathWithPathParams = this.path != null ? this.path : "";
    for (Entry<String, String> pathParam : this.pathParams.entrySet()) {
      pathWithPathParams =
          pathWithPathParams.replace('{' + pathParam.getKey() + '}', pathParam.getValue());
    }
    return pathWithPathParams;
  }

  /**
   * Builds the full URL that will be used to send this request, including the basePath, the path
   * with the injected path parameters and the query parameters.
   */
  public String buildURI() {
    String basePath = this.basePath != null ? this.basePath : "";
    StringBuilder url = new StringBuilder(basePath);

    String pathWithPathParams = buildPathWithPathParams();

    if (pathWithPathParams.length() > 0 && !pathWithPathParams.startsWith("/")
        && !basePath.endsWith("/")) {
      url.append('/');
    }

    url.append(pathWithPathParams);

    char separatorChar = url.toString().contains("?") ? '&' : '?';

    for (Entry<String, Collection<String>> queryParam : this.queryParams.entrySet()) {
      for (String paramValue : queryParam.getValue()) {
        url.append(separatorChar);
        if (separatorChar != '&') {
          separatorChar = '&';
        }

        try {
          url.append(URLEncoder.encode(queryParam.getKey(), "UTF-8")).append('=')
              .append(URLEncoder.encode(paramValue, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return url.toString();
  }

  /**
   * The base path of the request with probably the protocol, hostname and optionally the port.
   */
  public String getBasePath() {
    return this.basePath;
  }

  /**
   * The headers of the rest request.
   */
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  /**
   * Method of the rest request. Default: {@link HttpMethod#GET}
   */
  public HttpMethod getMethod() {
    return this.method;
  }

  /**
   * Path of the rest request.
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Path parameters that will be injected within {@link #path(String)}.
   */
  public Map<String, String> getPathParams() {
    return this.pathParams;
  }

  /**
   * Query parameters that will be appended after the path.
   */
  public Map<String, Collection<String>> getQueryParams() {
    return this.queryParams;
  }

  /**
   * Optional body of the request. If the object is an implementation of
   * {@link AsyncContentProvider} it is be used as is, otherwise Jackson is used to convert the
   * object to a JSON String.
   */
  public Optional<?> getRequestBody() {
    return this.requestBody;
  }

}
