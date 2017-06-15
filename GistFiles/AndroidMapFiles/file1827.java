package com.example.android;

import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.mapper.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.converters.collections.*;

import java.util.*;


/**
 * This came from http://osdir.com/ml/java.xstream.user/2007-07/msg00079.html 
 *
 * <b>Example:</b>
 * <pre>
 *   xstream.registerConverter(new MyMapConverter&lt;Object&gt;(xstream.getMapper(), "class");
 *   xstream.alias("preferences", Map.class);
 *   xstream.alias("preference", String.class);
 *   ArrayList response = (ArrayList)xstream.fromXML("<response><preferences><preference key=\"miao\">bau</preference><preference key=\"geova\">allah</preference></preferences></response>");
 *   Map<String, String> map = (Map<String, String>)response.get(0);
 * </pre>
 */
class MyMapConverter<T> extends MapConverter {
	private final String attributename;

	public MyMapConverter(Mapper mapper, String attributename) {
		super(mapper);
		this.attributename = attributename;
	}

	public boolean canConvert(Class type) {
		return type == HashMap.class;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map<String, T> map = (Map<String, T>) source;
		for (Map.Entry<String, T> entry : map.entrySet()) {
			T value = entry.getValue();
			writer.startNode(mapper().serializedClass(value.getClass()));
			writer.addAttribute(attributename, entry.getKey());
			context.convertAnother(value);
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, T> map = new HashMap<String, T>();
		populateStringMap(reader, context, map);

		return map;
	}

	protected void populateStringMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map<String, T> map) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getAttribute(attributename);
			T value = (T) readItem(reader, context, map);
			reader.moveUp();
			map.put(key, value);
		}
	}
}