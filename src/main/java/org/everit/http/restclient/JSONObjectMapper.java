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
 * Libraries that use REST Client should provide a {@link JSONObjectMapper} that tells the REST
 * client how to convert from String to JSON and vice versa.
 */
public interface JSONObjectMapper {

  /**
   * Converts a value from an object to another type. Some implementations may do this directly,
   * other implementations do it by converting the object first to JSON than to the other type. This
   * is useful when an object contains a map property that can be converted to a real type.
   *
   * @param <T>
   *          The type of the destination instance.
   * @param fromValue
   *          The object that would be converted.
   * @param toValueType
   *          The destination type.
   * @return The converted new object with the destination type.
   */
  <T> T convertValue(Object fromValue, Class<T> toValueType);

  /**
   * Converts a value from an object to another type. Some implementations may do this directly,
   * other implementations do it by converting the object first to JSON than to the other type. This
   * is useful when an object contains a map property that can be converted to a real type.
   *
   * @param <T>
   *          The type of the destination instance.
   * @param fromValue
   *          The object that would be converted.
   * @param toValueTypeRef
   *          Reference to the destination type.
   * @return The converted new object with the destination type.
   */
  <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef);

  /**
   * Converts a JSON string to a type.
   *
   * @param <T>
   *          The type to convert the JSON to.
   * @param json
   *          The JSON as string.
   * @param valueType
   *          The type as class type that the JSON will be converted to.
   * @return The generated instance.
   */
  <T> T fromJSON(String json, Class<T> valueType);

  /**
   * Converts a JSON string to a type.
   *
   * @param <T>
   *          The type to convert the JSON to.
   * @param json
   *          The JSON as string.
   * @param valueTypeRef
   *          The type as reference type that the JSON will be converted to.
   * @return The generated instance.
   */
  <T> T fromJSON(String json, TypeReference<T> valueTypeRef);

  /**
   * Converts an object to JSON.
   *
   * @param object
   *          The object that is converted to JSON.
   * @return
   */
  String toJSON(Object object);
}
