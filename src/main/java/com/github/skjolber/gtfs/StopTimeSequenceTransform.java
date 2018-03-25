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
	private int previousStopSequence;
	private String previousTripId;
	
	private int previousDepartureTime = -1;
	
	public StopTimeSequenceTransform(Transform delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void write(Map<String, String> line) {
		
		String string = line.get("stop_sequence");
		if(string != null) {
			int stopSequence = Integer.parseInt(string);
			
			String currentTripId = line.get("trip_id");
			
			if(stopSequence > 0) {
				if(previousStopSequence != -1 && previousTripId != null) {
					if(stopSequence < previousStopSequence && previousTripId.equals(currentTripId)) {
						// blow up if not in order - for the same trip
						throw new IllegalArgumentException("From stop sequence " + previousStopSequence + " to " + stopSequence + " at " + line.get("lineNumber"));
					}
					addLineNumberMinusOne(line);
					
					
					if(previousDepartureTime != -1) {
						int arrivalTime = StopTimeTransform.getTime(line, "arrival_time");
						if(arrivalTime != -1) {
							line.put("duration_departure_arrival", Integer.toString(arrivalTime - previousDepartureTime));
						}
						
					}					
					
			        delegate.write(line);
				}
			}
			
			previousDepartureTime = StopTimeTransform.getTime(line, "departure_time");
			previousStopSequence = stopSequence;
			previousTripId = currentTripId;
			
		}
	}

	private void addLineNumberMinusOne(Map<String, String> line) {
		int lineNumber = Integer.parseInt(line.get("lineNumber"));
		line.put("lineNumberMinus1", Integer.toString(lineNumber - 1));
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
		list.add("duration_departure_arrival");
		delegate.initialize(list.toArray(new String[list.size()]));
	}

	@Override
	public String toCypherStatement() throws IOException {
		return delegate.toCypherStatement();
	}
}
