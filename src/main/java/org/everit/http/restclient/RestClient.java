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

import org.everit.http.client.HttpClient;
import org.everit.http.client.HttpRequest;
import org.everit.http.client.HttpResponse;
import org.everit.http.client.MediaType;
import org.everit.http.client.async.AsyncContentProvider;
import org.everit.http.client.async.AsyncContentUtil;
import org.everit.http.client.async.AutoCloseAsyncContentProvider;
import org.everit.http.client.async.ByteArrayAsyncContentProvider;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * Calls REST endpoints.
 */
public class RestClient {

  private static final int HTTP_LOWEST_ERROR_CODE = 400;

  private HttpClient httpClient;

  private final JSONObjectMapper objectMapper;

  public RestClient(HttpClient httpClient, JSONObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Calls a rest endpoint asynchronously where there is no response body.
   *
   * @param restRequest
   *          The request that is used to call the endpoint.
   * @param requestEnhancer
   *          If specified, it is used to enhance the rest request before sending it.
   * @return An asynchronous instance that is notified when the response without a body has arrived
   *         back.
   */
  public Completable callEndpoint(
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer) {

    Single<HttpResponse> single =
        callEndpointAndHandleErrors(restRequest, requestEnhancer);

    return Single.create((emitter) -> {
      Disposable disposable = single.subscribe(httpResponse -> {
        try {
          emitter.onSuccess(httpResponse);
        } finally {
          httpResponse.close();
        }
      }, error -> emitter.onError(error));

      emitter.setCancellable(() -> disposable.dispose());
    }).ignoreElement();
  }

  /**
   * Calls a rest endpoint asynchronously.
   *
   * @param <T>
   *          Type of the response body.
   * @param restRequest
   *          The request that is used to call the endpoint.
   * @param requestEnhancer
   *          If specified, it is used to enhance the rest request before sending it.
   * @param returnType
   *          Type of the response body, Jackson is used to convert it from JSON to a typed java
   *          object.
   * @return An asynchronous object that is notified when the response is processed to the return
   *         type.
   */
  public <T> Single<T> callEndpoint(
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer,
      TypeReference<T> returnType) {

    Single<HttpResponse> response =
        callEndpointAndHandleErrors(restRequest, requestEnhancer);

    return response.flatMap((httpResponse) -> {

      return AsyncContentUtil.readString(
          new AutoCloseAsyncContentProvider(httpResponse.getBody(), httpResponse),
          StandardCharsets.UTF_8);

    }).map((stringResponse) -> {
      return this.objectMapper.fromJSON(stringResponse, returnType);
    });
  }

  private Single<HttpResponse> callEndpointAndHandleErrors(
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer) {

    return enhanceRequest(restRequest, requestEnhancer).flatMap(
        enhancedRestRequest -> callHttpEndpointAndHandleErrorsWithEnhancedRequest(
            enhancedRestRequest));

  }

  private Single<HttpResponse> callHttpEndpointAndHandleErrorsWithEnhancedRequest(
      final RestRequest enhancedRestRequest) {

    String url = enhancedRestRequest.buildURI();
    HttpRequest request = HttpRequest.builder()
        .url(url)
        .method(enhancedRestRequest.getMethod())
        .headers(enhancedRestRequest.getHeaders())
        .body(createHttpBody(enhancedRestRequest.getRequestBody()))
        .build();

    Single<HttpResponse> response = this.httpClient.send(request).flatMap((httpResponse) -> {
      int status = httpResponse.getStatus();
      if (status >= RestClient.HTTP_LOWEST_ERROR_CODE) {
        return AsyncContentUtil
            .readString(new AutoCloseAsyncContentProvider(httpResponse.getBody(), httpResponse),
                StandardCharsets.UTF_8)
            .map((content) -> {
              throw new RestException("Error sending request!",
                  request.getMethod(),
                  request.getUrl(),
                  createHttpBody(enhancedRestRequest.getRequestBody()),
                  status,
                  Optional.ofNullable("".equals(content) ? null : content),
                  null);
            });
      }

      return Single.just(httpResponse);
    });

    return response;
  }

  private Optional<AsyncContentProvider> createHttpBody(Optional<?> requestBodyOpt) {

    if (!requestBodyOpt.isPresent()) {
      return Optional.empty();
    }

    Object requestBody = requestBodyOpt.get();
    if (requestBody instanceof AsyncContentProvider) {
      return Optional.of((AsyncContentProvider) requestBody);
    } else {
      String json;
      json = this.objectMapper.toJSON(requestBody);

      byte[] jsonByteArray = json.getBytes(StandardCharsets.UTF_8);

      return Optional.of(new ByteArrayAsyncContentProvider(jsonByteArray,
          Optional.of(MediaType.parse("application/json"))));
    }
  }

  private Single<RestRequest> enhanceRequest(RestRequest restRequest,
      Optional<RestRequestEnhancer> requestEnhancer) {

    if (requestEnhancer.isPresent()) {
      return requestEnhancer.get().enhanceRestRequest(restRequest);
    }
    return Single.just(restRequest);
  }

  public JSONObjectMapper getObjectMapper() {
    return this.objectMapper;
  }
}
