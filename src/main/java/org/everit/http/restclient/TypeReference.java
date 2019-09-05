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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper class to convert JSON to parameterized types by using
 * {@link JSONObjectMapper#fromJSON(String, TypeReference)}.
 *
 * @param <T>
 *          The type that the deserializer function should generate.
 */
public abstract class TypeReference<T> {

  protected final Type type;

  /**
   * Constructor.
   */
  protected TypeReference() {
    Type superClass = getClass().getGenericSuperclass();
    if (superClass instanceof Class<?>) { // sanity check, should never happen
      throw new IllegalArgumentException(
          "Internal error: TypeReference constructed without actual type information");
    }

    this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }

  public Type getType() {
    return this.type;
  }
}
