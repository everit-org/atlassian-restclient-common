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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.everit.web.servlet.HttpServlet;

/**
 * Helper servlet class that accepts the HTTP requests of the unit tests.
 */
public class HttpClientTestServlet extends HttpServlet {

  public static final String PATH_TEST_CONNECTION_ERROR_DURING_RESPONSE_BODY =
      "/connection-error-during-response-body";

  public static final String PATH_TEST_CONNECTION_ERROR_ON_ARRIVE = "/connection-error-on-arrive";

  public static final String PATH_TEST_FORM_URL_ENCODED = "/formurl";

  public static final String PATH_TEST_WITH_BODY = "/body";

  public static final String PATH_TEST_WITH_NO_BODY = "/nobody";

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setCharacterEncoding("UTF-8");

    String pathInfo = req.getPathInfo();
    switch (pathInfo) {
      case PATH_TEST_WITH_BODY:
        writeRequestBodyToResponse(req, resp);
        break;
      default:
        break;
    }
  }

  private void writeRequestBodyToResponse(HttpServletRequest req, HttpServletResponse resp) {
    try {
      byte[] body = "{\"value\":\"bar\"}".getBytes(StandardCharsets.UTF_8);
      resp.setContentLength(body.length);
      resp.getOutputStream().write(body);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
