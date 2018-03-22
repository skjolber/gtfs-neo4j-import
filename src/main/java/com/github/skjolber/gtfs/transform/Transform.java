package com.github.skjolber.gtfs.transform;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Transform {

	void writeFirst(String[] line);

	void writeNext(Map<String, String> line);
	
	String toImportStatement() throws IOException;
	String toCypherStatement() throws IOException;
	
	void open(File outputDirectory) throws Exception;

	void close() throws IOException;

	String getSource();
	
}
