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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper functions to build a {@link RestRequest} and do a call.
 */
public final class RestClientUtil {

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

  private RestClientUtil() {
  }
}
