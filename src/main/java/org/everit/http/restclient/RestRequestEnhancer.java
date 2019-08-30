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
