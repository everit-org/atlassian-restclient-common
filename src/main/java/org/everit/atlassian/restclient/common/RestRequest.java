package org.everit.atlassian.restclient.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.everit.http.client.HttpMethod;

public class RestRequest {
  public HttpMethod method = HttpMethod.GET;
  public String basePath;
  public String path;
  public Map<String, String> pathParams = new HashMap<>();
  public Map<String, Collection<String>> queryParams = new HashMap<>();
  public Optional<Object> requestBody;
  public Map<String, String> headers = new HashMap<>();

  public String buildURI() {
    StringBuilder url = new StringBuilder(this.basePath != null ? this.basePath : "");

    if (this.basePath != null || this.basePath.endsWith("/")) {
      url.append('/');
    }

    String pathWithPathParams = buildPathWithPathParams();

    if (pathWithPathParams.startsWith("/")) {
      url.append(pathWithPathParams, 1, pathWithPathParams.length());
    } else {
      url.append(pathWithPathParams);
    }

    char separatorChar = url.toString().contains("?") ? '&' : '?';

    for (Entry<String, Collection<String>> queryParam : queryParams.entrySet()) {
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

  public String buildPathWithPathParams() {
    String pathWithPathParams = this.path != null ? this.path : "";
    for (Entry<String, String> pathParam : pathParams.entrySet()) {
      pathWithPathParams =
          pathWithPathParams.replace('{' + pathParam.getKey() + '}', pathParam.getValue());
    }
    return pathWithPathParams;
  }
}
