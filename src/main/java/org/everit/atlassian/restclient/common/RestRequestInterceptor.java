package org.everit.atlassian.restclient.common;

public interface RestRequestInterceptor {

  void enhanceRestRequest(RestRequest request);
}
