package com.github.skjolber.gtfs.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DefaultTransform extends AbstractTransform {

	protected boolean skipPartial;
	protected int lineNumber = 1;
	protected TreeMap<String, String> mapping;
	
	@Override
	public void writeFirst(String[] fields) {
		List<String> result = new ArrayList<>();
		for (Entry<String, String> entry : mapping.entrySet()) {
			if(!entry.getKey().equals("lineNumber")) {
				if(!hasField(fields, entry.getKey())) {
					throw new IllegalArgumentException(entry.getKey() + " is not defined");
				}
			}
			result.add(entry.getValue());
		}
		writer.writeNext(result.toArray(new String[mapping.size()]));
	}

	private boolean hasField(String[] fields, String key) {
		for (String string : fields) {
			if(string != null && string.equals(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeNext(Map<String, String> line) {

		boolean write = false;
		List<String> result = new ArrayList<>();
		for (Entry<String, String> entry : mapping.entrySet()) {
			String string = line.get(entry.getKey());
			if(string != null && !string.isEmpty()) {
				result.add(string);
				
				write = true;
			} else {
				if(skipPartial) {
					return;
				} else {
					result.add("");
				}
			}
		}
		
		if(write) {
			writer.writeNext(result.toArray(new String[mapping.size()]));
		}
	}

	public void setMapping(TreeMap<String, String> mapping) {
		this.mapping = mapping;
	}

	public void setSkipPartial(boolean skipPartial) {
		this.skipPartial = skipPartial;
	}

}
