package com.github.skjolber.gtfs.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DefaultTransform extends AbstractTransform {

	protected boolean skipPartial;
	protected int lineNumber = 1;
	protected TreeMap<String, String> requiredMapping;
	/** Optional mappings. 
	 * Optional as required for the logic of the mapper, 
	 * not according to the specification. */
	protected TreeMap<String, String> optionalMapping;
	protected TreeMap<String, String> mapping; // required + optional
	protected List<LabelTransform> labels;
	
	@Override
	public void initialize(String[] fields) {
		for (Entry<String, String> entry : requiredMapping.entrySet()) {
			if(!entry.getKey().equals("lineNumber")) {
				if(!hasField(fields, entry.getKey())) {
					throw new IllegalArgumentException(entry.getKey() + " is not defined: " + Arrays.asList(fields));
				}
			}
		}

		if(optionalMapping != null && !optionalMapping.isEmpty()) {
			mapping = new TreeMap<>(requiredMapping);
			
			for (Entry<String, String> entry : optionalMapping.entrySet()) {
				if(hasField(fields, entry.getKey())) {
					mapping.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			mapping = requiredMapping;
		}

		List<String> result = new ArrayList<>();
		for (Entry<String, String> entry : mapping.entrySet()) {
			result.add(entry.getValue());
		}

		if(labels != null && !labels.isEmpty()) {
			result.add(":LABEL");
		}
		
		writer.writeNext(result.toArray(new String[result.size()]));
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
	public void write(Map<String, String> line) {

		boolean write = false;
		List<String> result = new ArrayList<>();
		for (Entry<String, String> entry : mapping.entrySet()) {
			String string = line.get(entry.getKey());
			if(string != null && !string.isEmpty()) {
				result.add(string);
				
				write = true;
			} else {
				if(skipPartial && requiredMapping.containsKey(entry.getKey())) {
					return;
				} else {
					result.add("");
				}
			}
		}
		
		if(labels != null && !labels.isEmpty()) {
			result.add(toLabel(line));
		}
		
		if(write) {
			writer.writeNext(result.toArray(new String[result.size()]));
		}
	}

	public String toLabel(Map<String, String> line) {
		StringBuilder builder = new StringBuilder();
		
		for (Entry<String, String> entry : line.entrySet()) {
			for(LabelTransform label : labels) {

				if(label.supports(entry.getKey())) {
					String transformedValue = label.transformLabel(entry.getValue());
					if(transformedValue != null) {
						if(builder.length() > 0) {
							builder.append(";");
						}
						builder.append(transformedValue);
					}
				}
			}
		}
		
		return builder.toString();
	}

	public void setRequiredMapping(TreeMap<String, String> mapping) {
		this.requiredMapping = mapping;
	}

	public void setSkipPartial(boolean skipPartial) {
		this.skipPartial = skipPartial;
	}

	public void setLabels(List<LabelTransform> labels) {
		this.labels = labels;
	}
	
	public TreeMap<String, String> getMapping() {
		return mapping;
	}
	
	public void setOptionalMapping(TreeMap<String, String> optionalMapping) {
		this.optionalMapping = optionalMapping;
	}
}
