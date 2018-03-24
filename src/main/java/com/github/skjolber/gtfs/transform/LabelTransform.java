package com.github.skjolber.gtfs.transform;

public interface LabelTransform {
	
	boolean supports(String key);
	String transformLabel(String value);
}
