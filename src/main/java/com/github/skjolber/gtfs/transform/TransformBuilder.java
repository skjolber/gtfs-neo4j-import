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
	protected TreeMap<String, String> optionalMapping = new TreeMap<>();
	protected String type;
	protected List<LabelTransform> labels  = new ArrayList<>();
	protected List<String> indexes = new ArrayList<>();
	protected List<String> unqiueConstraints = new ArrayList<>();
	
	public Transform build() {
		DefaultTransform transform;
		if(filter != null) {
			FilterTransform filterTransform = new FilterTransform();
			
			filterTransform.setFilter(filter);
			filterTransform.setSkipPartial(skipPartial);
			
			transform = filterTransform;
		} else {
			transform = new DefaultTransform();
		}

		transform.setRequiredMapping(mapping);
		transform.setOptionalMapping(optionalMapping);
		transform.setSkipPartial(skipPartial);
		transform.setLabels(labels);

		transform.setIndexes(indexes);
		transform.setUniqueConstraints(unqiueConstraints);
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

	public TransformBuilder withRequiredMapping(String from, String to) {
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
	
	public TransformBuilder withUniqueConstraint(String c) {
		this.unqiueConstraints.add(c);
		return this;
	}

	public TransformBuilder withLabel(LabelTransform label) {
		this.labels.add(label);
		return this;
	}
	
	public TransformBuilder withOptionalMapping(String from, String to) {
		this.optionalMapping.put(from, to);
		return this;
	}
}
