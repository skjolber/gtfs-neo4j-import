package com.github.skjolber.gtfs.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TransformBuilder {

	protected boolean skipPartial = false;
	protected String filter;
	protected String relation;
	protected String source;
	protected String destination;
	protected TreeMap<String, String> mapping = new TreeMap<>();
	private String type;
	private List<String> indexes = new ArrayList<>();
	
	public Transform build() {
		AbstractTransform transform;
		if(filter != null) {
			FilterTransform filterTransform = new FilterTransform();
			
			filterTransform.setMapping(mapping);
			filterTransform.setFilter(filter);
			filterTransform.setSkipPartial(skipPartial);
			
			transform = filterTransform;
		} else {
			DefaultTransform defaultTransform = new DefaultTransform();
			
			defaultTransform.setMapping(mapping);
			defaultTransform.setSkipPartial(skipPartial);
			
			transform = defaultTransform;
		}
		
		transform.setIndexes(indexes);
		transform.setDestination(destination);
		transform.setSource(source);
		transform.setRelation(relation);
		transform.setType(type);
		return transform;
	}
	
	public TransformBuilder withFilter(String filter) {
		this.filter = filter;
		
		return this;
	}
	
	public TransformBuilder withRelation(String relation) {
		this.relation = relation;
		
		return this;
	}

	public TransformBuilder withSource(String source) {
		this.source = source;
		
		return this;
	}

	public TransformBuilder withDestination(String destination) {
		this.destination = destination;
		
		return this;
	}

	public TransformBuilder withMapping(String from, String to) {
		this.mapping.put(from, to);
		return this;
	}
	
	public TransformBuilder withSkipPartial(boolean skipPartial) {
		this.skipPartial = skipPartial;
		return this;
	}
	
	public TransformBuilder withType(String type) {
		this.type = type;
		return this;
	}

	public TransformBuilder withIndex(String index) {
		this.indexes.add(index);
		return this;
	}
}
