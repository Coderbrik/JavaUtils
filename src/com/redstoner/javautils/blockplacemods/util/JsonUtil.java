package com.redstoner.javautils.blockplacemods.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
	
	public static Object read(JsonReader reader) throws IOException {
		
		switch (reader.peek()) {
			case BEGIN_OBJECT:
				Map<String, Object> object = new HashMap<>();
				
				reader.beginObject();
				while (reader.hasNext()) {
					final String key = reader.nextName();
					final Object value = read(reader);
					object.put(key, value);
				}
				reader.endObject();
				
				return object;
			case BEGIN_ARRAY:
				Collection<Object> collection = new ArrayList<>();
				
				reader.beginArray();
				while (reader.hasNext()) {
					final Object item = read(reader);
					collection.add(item);
				}
				reader.endArray();
				
				return collection;
			case BOOLEAN:
				return reader.nextBoolean();
			case STRING:
				return reader.nextString();
			case NULL:
				reader.nextNull();
				return null;
			case NUMBER:
				return reader.nextDouble();
			default:
				throw new IllegalStateException();
		}
		
	}
	
	public static void insert(JsonWriter writer, Object value) throws IOException {
		
		if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			writer.beginObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				writer.name(entry.getKey());
				insert(writer, entry.getValue());
			}
			writer.endObject();
		} else if (value instanceof Iterable) {
			Iterable<Object> it = (Iterable<Object>) value;
			writer.beginArray();
			for (Object o : it) {
				insert(writer, o);
			}
			writer.endArray();
		} else if (value instanceof Number) {
			writer.value((Number) value);
		} else if (value instanceof Boolean) {
			writer.value((Boolean) value);
		} else if (value instanceof String) {
			writer.value((String) value);
		} else {
			throw new IllegalArgumentException("value not a String, Map, Number or Boolean");
		}
	}
	
}
