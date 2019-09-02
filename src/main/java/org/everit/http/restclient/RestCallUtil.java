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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.everit.http.client.HttpClient;
import org.everit.http.client.HttpRequest;
import org.everit.http.client.HttpResponse;
import org.everit.http.client.MediaType;
import org.everit.http.client.async.AsyncContentProvider;
import org.everit.http.client.async.AsyncContentUtil;
import org.everit.http.client.async.AutoCloseAsyncContentProvider;
import org.everit.http.client.async.ByteArrayAsyncContentProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * Helper methods to call REST endpoints, so the generated code can be much smaller.
 */
public final class RestCallUtil {

  private static final int HTTP_LOWEST_ERROR_CODE = 400;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Calls a rest endpoint asynchronously where there is no response body.
   *
   * @param httpClient
   *          The {@link HttpClient} implementation.
   * @param restRequest
   *          The request that is used to call the endpoint.
   * @param requestEnhancer
   *          If specified, it is used to enhance the rest request before sending it.
   * @return An asynchronous instance that is notified when the response without a body has arrived
   *         back.
   */
  public static Completable callEndpoint(HttpClient httpClient,
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer) {

    Single<HttpResponse> single =
        RestCallUtil.callEndpointAndHandleErrors(httpClient, restRequest, requestEnhancer);

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
   * @param httpClient
   *          The {@link HttpClient} implementation.
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
  public static <T> Single<T> callEndpoint(HttpClient httpClient,
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer,
      TypeReference<T> returnType) {

    Single<HttpResponse> response =
        RestCallUtil.callEndpointAndHandleErrors(httpClient, restRequest, requestEnhancer);

    return response.flatMap((httpResponse) -> {

      return AsyncContentUtil.readString(
          new AutoCloseAsyncContentProvider(httpResponse.getBody(), httpResponse),
          StandardCharsets.UTF_8);

    }).map((stringResponse) -> {
      return RestCallUtil.OBJECT_MAPPER.readValue(stringResponse, returnType);
    });
  }

  private static Single<HttpResponse> callEndpointAndHandleErrors(HttpClient httpClient,
      RestRequest restRequest, Optional<RestRequestEnhancer> requestEnhancer) {

    return RestCallUtil.enhanceRequest(restRequest, requestEnhancer).flatMap(
        enhancedRestRequest -> RestCallUtil.callHttpEndpointAndHandleErrorsWithEnhancedRequest(
            httpClient, enhancedRestRequest));

  }

  private static Single<HttpResponse> callHttpEndpointAndHandleErrorsWithEnhancedRequest(
      HttpClient httpClient,
      RestRequest enhancedRestRequest) {
    String url = enhancedRestRequest.buildURI();
    HttpRequest request = HttpRequest.builder()
        .url(url)
        .method(enhancedRestRequest.getMethod())
        .headers(enhancedRestRequest.getHeaders())
        .body(RestCallUtil.createHttpBody(enhancedRestRequest.getRequestBody()))
        .build();

    Single<HttpResponse> response = httpClient.send(request).flatMap((httpResponse) -> {
      if (httpResponse.getStatus() >= RestCallUtil.HTTP_LOWEST_ERROR_CODE) {
        return AsyncContentUtil
            .readString(new AutoCloseAsyncContentProvider(httpResponse.getBody(), httpResponse),
                StandardCharsets.UTF_8)
            .map((content) -> {
              throw new RestException("Error sending request: " + request.getUrl(),
                  httpResponse.getStatus(),
                  Optional.ofNullable("".equals(content) ? null : content));
            });
      }

      return Single.just(httpResponse);
    });

    return response;
  }

  private static Optional<AsyncContentProvider> createHttpBody(Optional<?> requestBodyOpt) {

    if (!requestBodyOpt.isPresent()) {
      return Optional.empty();
    }

    Object requestBody = requestBodyOpt.get();
    if (requestBody instanceof AsyncContentProvider) {
      return Optional.of((AsyncContentProvider) requestBody);
    } else {
      String json;
      try {
        json = RestCallUtil.OBJECT_MAPPER.writeValueAsString(requestBody);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      byte[] jsonByteArray = json.getBytes(StandardCharsets.UTF_8);

      return Optional.of(new ByteArrayAsyncContentProvider(jsonByteArray,
          Optional.of(MediaType.parse("application/json"))));
    }
  }

  private static Single<RestRequest> enhanceRequest(RestRequest restRequest,
      Optional<RestRequestEnhancer> requestEnhancer) {

    if (requestEnhancer.isPresent()) {
      return requestEnhancer.get().enhanceRestRequest(restRequest);
    }
    return Single.just(restRequest);
  }

  /**
   * Converts any kind of collection to a string collection. This is useful when the programmer does
   * not know the type that is in the instance in advance and it must be passed to query parameters.
   *
   * @param collection
   *          The any kind of object collection.
   * @return The string collection.
   */
  public static Collection<String> objectCollectionToStringCollection(
      Collection<?> collection) {

    List<String> result = new ArrayList<>(collection.size());
    for (Object obj : collection) {
      result.add(String.valueOf(obj));
    }
    return result;
  }

  private RestCallUtil() {
  }
}
