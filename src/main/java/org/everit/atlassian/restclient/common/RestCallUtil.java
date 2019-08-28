package org.everit.atlassian.restclient.common;

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

public class RestCallUtil {

  private static ObjectMapper objectMapper = new ObjectMapper();;

  public static <T> Single<T> callEndpoint(HttpClient httpClient,
      RestRequest restRequest, TypeReference<T> returnType) {

    Single<HttpResponse> response =
        callEndpointAndHandleErrors(httpClient, restRequest);

    return response.flatMap((httpResponse) -> {

      return AsyncContentUtil.readString(
          new AutoCloseAsyncContentProvider(httpResponse.getBody(), httpResponse),
          StandardCharsets.UTF_8);

    }).map((stringResponse) -> {
      return objectMapper.readValue(stringResponse, returnType);
    });
  }

  public static Completable callEndpoint(HttpClient httpClient,
      RestRequest endpointParameter) {

    Single<HttpResponse> single = callEndpointAndHandleErrors(httpClient, endpointParameter);

    return Single.create((emitter) -> {
      single.subscribe(httpResponse -> {
        try {
          emitter.onSuccess(httpResponse);
        } finally {
          httpResponse.close();
        }
      }, error -> emitter.onError(error));
    }).ignoreElement();
  }

  private static Single<HttpResponse> callEndpointAndHandleErrors(HttpClient httpClient,
      RestRequest restRequest) {

    String url = restRequest.buildURI();
    HttpRequest request = HttpRequest.builder()
        .url(url)
        .method(restRequest.method)
        .headers(restRequest.headers)
        .body(createHttpBody(restRequest.requestBody))
        .build();

    Single<HttpResponse> response = httpClient.send(request).flatMap((httpResponse) -> {
      if (httpResponse.getStatus() >= 400) {
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

  public static Collection<String> objectCollectionToStringCollection(
      Collection<?> collection) {

    List<String> result = new ArrayList<String>(collection.size());
    for (Object obj : collection) {
      result.add(String.valueOf(obj));
    }
    return result;
  }

  private static Optional<AsyncContentProvider> createHttpBody(Optional<Object> requestBodyOpt) {

    if (!requestBodyOpt.isPresent()) {
      return Optional.empty();
    }

    Object requestBody = requestBodyOpt.get();
    if (requestBody instanceof AsyncContentProvider) {
      return Optional.of((AsyncContentProvider) requestBody);
    } else {
      String json;
      try {
        json = objectMapper.writeValueAsString(requestBody);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      byte[] jsonByteArray = json.getBytes(StandardCharsets.UTF_8);

      return Optional.of(new ByteArrayAsyncContentProvider(jsonByteArray,
          Optional.of(MediaType.parse("application/json"))));
    }
  }
}
