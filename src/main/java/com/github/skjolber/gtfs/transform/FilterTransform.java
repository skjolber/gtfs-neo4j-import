package com.github.skjolber.gtfs.transform;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilterTransform extends DefaultTransform {

	protected Set<String> ids = new HashSet<>();
	
	protected String filter;
	
	@Override
	public void write(Map<String, String> line) {
		String value = line.get(filter);
		
		if(value != null && !ids.contains(value)) {
			super.write(line);
			ids.add(value);
		}
	}
	
	public void setFilter(String key) {
		this.filter = key;
	}
}
