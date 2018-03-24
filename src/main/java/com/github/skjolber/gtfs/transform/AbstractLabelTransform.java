package com.github.skjolber.gtfs.transform;

public abstract class AbstractLabelTransform implements LabelTransform {

	private final String key;
	
	public AbstractLabelTransform(String key) {
		super();
		this.key = key;
	}

	@Override
	public boolean supports(String key) {
		return key.equals(key);
	}

	@Override
	public String transformLabel(String value) {
		return null;
	}

}
