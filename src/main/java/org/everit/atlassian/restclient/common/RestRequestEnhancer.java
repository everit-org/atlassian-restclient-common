package org.everit.atlassian.restclient.common;

/**
 * Programmers can pass an optional {@link RestRequestEnhancer} to any of the callEndpoint functions
 * of {@link RestCallUtil} that will modify the original request by creating a new one. This can be
 * useful for example if we want to add authorization headers to the request on the fly.
 */
public interface RestRequestEnhancer {

  /**
   * Enhances the rest request by creating a new instance.
   *
   * @param request
   *          The original rest request.
   * @return The enhanced rest request.
   */
  RestRequest enhanceRestRequest(RestRequest request);
}
