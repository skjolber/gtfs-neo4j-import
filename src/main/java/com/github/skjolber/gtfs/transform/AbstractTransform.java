package com.github.skjolber.gtfs.transform;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;

public abstract class AbstractTransform implements Transform {

	protected String[] translate(String[] line, int[] routeTranslation) {
		String[] value = new String[routeTranslation.length];
		
		for (int i = 0; i < routeTranslation.length; i++) {
			int j = routeTranslation[i];
			
			value[i] = line[j];
		}
		
		return value;
	}

	protected String[] forFirst(Map<String, String> mapping, String[] first) {
		List<String> list = new ArrayList<>();
		
		for(int i = 0; i < first.length; i++) {
			String value = mapping.get(first[i]);
			if(value != null) {
				list.add(value);
			}
		}
		
		return list.toArray(new String[list.size()]);
	}

	protected int[] forIndexes(Map<String, String> mapping, String[] first) {
		
		List<Integer> list = new ArrayList<>();
		
		for(int i = 0; i < first.length; i++) {
			if(mapping.containsKey(first[i])) {
				list.add(i);
			}
		}
		
		return list.stream().mapToInt(i->i).toArray();
	}
	
	
	protected String source;
	protected String destination;
	
	protected File outputFile;
	protected CSVWriter writer;
	
	protected String relation;
	protected String type;
	protected List<String> indexes = new ArrayList<>();
	protected List<String> uniqueConstraints = new ArrayList<>();
	
	public String toImportStatement() throws IOException {
		if(outputFile == null) {
			throw new RuntimeException("Not opened " + source + " -> " + destination);
		}
		if(relation == null) {
			return "--nodes:" + type + " $2/" + outputFile.getName();
		}
		return "--relationships:" + relation + " $2/" + outputFile.getName();
	}
	
	public String toCypherStatement() throws IOException {
		StringBuilder b = new StringBuilder();
		
		for(String index : indexes) {
			b.append("create index on :");
			b.append(type);
			b.append("(");
			b.append(index);
			b.append(")");
			b.append("\n");
		}

		for(String constraint : uniqueConstraints) {
			b.append("create constraint on (t:");
			b.append(type);
			b.append(") assert t.");
			b.append(constraint);
			b.append(" is unique\n");
		}

		return b.toString();
	}

	public void open(File outputDirectory) throws Exception {
		this.outputFile = new File(outputDirectory, destination);

		OutputStream fout = new BufferedOutputStream(new FileOutputStream(outputFile), 8 * 1024*1024);
		
		OutputStreamWriter writer = new OutputStreamWriter(fout, StandardCharsets.UTF_8);

		this.writer = new CSVWriter(writer, ',', '"', '"', "\n");
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getSource() {
		return source;
	}

	public void close() throws IOException {
		if(writer != null) {
			writer.close();
		}
	}
	
	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setIndexes(List<String> indexes) {
		this.indexes = indexes;
	}

	public void setUniqueConstraints(List<String> constraints) {
		this.uniqueConstraints = constraints;
	}
	

}
