package com.github.skjolber.gtfs.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class TransformEngine {

	protected Map<String, List<Transform>> transforms = new HashMap<>();
	protected File outputDirectory;
	
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	public void add(Transform transform) {
		List<Transform> list = transforms.get(transform.getSource());
		if(list == null) {
			list = new ArrayList<>();
			transforms.put(transform.getSource(), list);
		}
		list.add(transform);
	}
	
	public void process(String source, InputStream in) throws Exception {
		CSVReader reader = new CSVReaderBuilder(new InputStreamReader(in, "UTF-8")).build();
		
		List<Transform> transforms = this.transforms.get(source);
		if(transforms == null) {
			return;
		}
		try {
			for (Transform transform : transforms) {
				transform.open(outputDirectory);
			}
			String[] first = reader.readNext();
			// first
			if(first == null) {
				throw new RuntimeException("No first line");
			}
			for (Transform transform : transforms) {
				transform.initialize(first);
			}
	
			int lineNumber = 1;
			
			Map<String, String> fields = new HashMap<>();
			do {
				String[] line = reader.readNext();
				if(line == null) {
					break;
				}
				for (int i = 0; i < line.length; i++) {
					String string = line[i];
					if(string != null) {
						fields.put(first[i], string);
					}
				}
				fields.put("lineNumber", Integer.toString(lineNumber++));
				
				for (Transform transform : transforms) {
					transform.write(fields);
				}
				fields.clear();
			} while(true);
		} finally {
			for (Transform transform : transforms) {
				transform.close();
			}
			reader.close();
		}
	}
	
	public String toStatement() throws IOException {
		StringBuilder builder = new StringBuilder();
		
		builder.append("$1/bin/neo4j-admin import --mode=csv --database $3 --id-type string ");
		
		for (Entry<String, List<Transform>> entry : transforms.entrySet()) {
			for (Transform transform : entry.getValue()) {
				builder.append(transform.toImportStatement());
				builder.append(" ");
			}
		}
		
		return builder.toString();
	}
	
	public String toCypherStatement() throws IOException {
		StringBuilder builder = new StringBuilder();
		
		for (Entry<String, List<Transform>> entry : transforms.entrySet()) {
			for (Transform transform : entry.getValue()) {
				builder.append(transform.toCypherStatement());
			}
		}
		
		return builder.toString();
	}

}
