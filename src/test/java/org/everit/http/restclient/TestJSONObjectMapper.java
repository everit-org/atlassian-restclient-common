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
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestJSONObjectMapper implements JSONObjectMapper {

  private static class PreDefinedTypeReference<T>
      extends com.fasterxml.jackson.core.type.TypeReference<T> {

    private Type valueType;

    PreDefinedTypeReference(TypeReference<T> typeReference) {
      this.valueType = typeReference.getType();

    }

    @Override
    public Type getType() {
      return this.valueType;
    }
  }

  /**
   * An instance of {@link JSONObjectMapper} that works for Jira Cloud REST requests.
   */
  public static final JSONObjectMapper INSTANCE;

  /**
   * The date time formatter that can be used to convert date-time values to JSON representation of
   * Jira Cloud and vice versa. Example: 2000-01-01T11:11:25.123+0100
   */
  public static final DateTimeFormatter JIRA_CLOUD_DATETIME_FORMATTER;

  static {
    JIRA_CLOUD_DATETIME_FORMATTER =
        new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
            .toFormatter();

    INSTANCE = new TestJSONObjectMapper();
  }

  private final ObjectMapper objectMapper;

  public TestJSONObjectMapper() {
    this.objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
  }

  @Override
  public <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return this.objectMapper.convertValue(fromValue, toValueType);
  }

  @Override
  public <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
    return this.objectMapper.convertValue(fromValue, new PreDefinedTypeReference<>(toValueTypeRef));
  }

  @Override
  public <T> T fromJSON(String json, Class<T> valueType) {
    try {
      return this.objectMapper.readValue(json, valueType);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public <T> T fromJSON(String json, TypeReference<T> valueTypeReference) {
    try {
      return this.objectMapper.readValue(json, new PreDefinedTypeReference<>(valueTypeReference));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String toJSON(Object object) {
    try {
      return this.objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
