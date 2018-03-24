package com.github.skjolber.gtfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.skjolber.gtfs.transform.Transform;

public class StopTimeSequenceTransform implements Transform {

	private Transform delegate;
	
	public StopTimeSequenceTransform(Transform delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void write(Map<String, String> line) {
		
		String string = line.get("stop_sequence");
		if(!string.equals("0")) {
			int lineNumber = Integer.parseInt(line.get("lineNumber"));
			line.put("lineNumberMinus1", Integer.toString(lineNumber - 1));
	        delegate.write(line);
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
	public void initialize(String[] fields) {
		List<String> list = new ArrayList<>(Arrays.asList(fields));
		list.add("lineNumberMinus1");
		delegate.initialize(list.toArray(new String[list.size()]));
	}

	@Override
	public String toCypherStatement() throws IOException {
		return delegate.toCypherStatement();
	}
}
