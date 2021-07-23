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

import java.util.Optional;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.everit.http.client.HttpClient;
import org.everit.http.client.HttpMethod;
import org.everit.http.client.jettyclient.JettyClientHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Single;

public class RestClientTest {

  public static final String CONTEXT_PATH = "/test";

  private static int port;

  private static Server server;

  private static final HttpClientTestServlet TEST_SERVLET = new HttpClientTestServlet();

  /**
   * Stops the HTTP server that the tests communicate with.
   */
  @AfterClass
  public static void afterClass() {
    if (RestClientTest.server != null) {
      try {
        RestClientTest.server.stop();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static String baseUr() {
    return "http://localhost:" + RestClientTest.port;
  }

  /**
   * Starts up an HTTP server and registers {@link HttpClientTestServlet} on it to let the tests
   * communicate with the servlet.
   */
  @BeforeClass
  public static void beforeClass() {
    RestClientTest.server = new Server();
    ServletContextHandler servletContextHandler =
        new ServletContextHandler(RestClientTest.server, RestClientTest.CONTEXT_PATH);
    servletContextHandler.addServlet(new ServletHolder(RestClientTest.TEST_SERVLET), "/*");
    RestClientTest.server.setHandler(servletContextHandler);
    ServerConnector serverConnector = new ServerConnector(RestClientTest.server);
    final int thirtySecondsInMillisecs = 30000;
    serverConnector.setIdleTimeout(thirtySecondsInMillisecs);
    RestClientTest.server.addConnector(serverConnector);
    try {
      RestClientTest.server.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    Connector[] connectors = RestClientTest.server.getConnectors();
    for (Connector connector : connectors) {
      if (connector instanceof NetworkConnector) {
        RestClientTest.port = ((NetworkConnector) connector).getLocalPort();
      }
    }
  }

  private HttpClient httpClient;

  /**
   * Closes the created {@link HttpClient} instance after running the test.
   */
  @After
  public void after() {
    if (this.httpClient != null) {
      this.httpClient.close();
    }
  }

  @Before
  public void before() {
    this.httpClient = new JettyClientHttpClient(new org.eclipse.jetty.client.HttpClient());
  }

  @Test
  public void testBody() {

    RestClient restClient = new RestClient(this.httpClient, TestJSONObjectMapper.INSTANCE);

    RestRequest.Builder requestBuilder = RestRequest.builder()
        .method(HttpMethod.GET)
        .basePath(RestClientTest.baseUr())
        .path(RestClientTest.CONTEXT_PATH + HttpClientTestServlet.PATH_TEST_WITH_BODY);

    TypeReference<Bar> returnType = new TypeReference<Bar>() {
    };

    Single<Bar> response =
        restClient.callEndpoint(requestBuilder.build(), Optional.empty(), returnType);

    Bar bar = response.blockingGet();
    Assert.assertEquals("bar", bar.value);
  }

}
