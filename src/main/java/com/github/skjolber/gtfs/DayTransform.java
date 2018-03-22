package com.github.skjolber.gtfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.skjolber.gtfs.transform.Transform;

public class DayTransform implements Transform {

	private Transform delegate;
	
	public DayTransform(Transform delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void writeFirst(String[] fields) {
		List<String> list = new ArrayList<>(Arrays.asList(fields));
		list.add("day");
		delegate.writeFirst(list.toArray(new String[list.size()]));
	}
	
	@Override
	public void writeNext(Map<String, String> line) {
		
		String[] days = new String[] {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};

		for(String day : days) {
			String value = line.get(day);
			if(value.equals("1")) {
				Map<String, String> dayLine = new HashMap<>(line);
				dayLine.put("day", day);
				delegate.writeNext(dayLine);
			}
		}
		
	}

	@Override
	public String toImportStatement() throws IOException {
		return delegate.toImportStatement();
	}

	@Override
	public void open(File outputDirectory) throws Exception {
		delegate.open(outputDirectory);
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public String getSource() {
		return delegate.getSource();
	}
	
	@Override
	public String toCypherStatement() throws IOException {
		return delegate.toCypherStatement();
	}
	
}
