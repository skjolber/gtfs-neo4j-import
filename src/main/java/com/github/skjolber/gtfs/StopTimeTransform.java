package com.github.skjolber.gtfs;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.skjolber.gtfs.transform.Transform;

public class StopTimeTransform implements Transform {

	protected static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("H:mm:ss").withResolverStyle(ResolverStyle.LENIENT);;
	
	public static int getTime(Map<String, String> line, String string) {
        String arrivalTime = line.get(string);
        if(arrivalTime != null) {
        	return toSeconds(arrivalTime);
        }
		return -1;
	}
	
	public static int toSeconds(String time) {
		// HH:MM:SS where the HH might be > 24
		 
		LocalTime localTime = LocalTime.parse(time, pattern);
		
		int hours = Integer.parseInt(time.substring(0, time.indexOf(':')));
		
		return hours * 3600 + localTime.getMinute() * 60 + localTime.getSecond();
	}
	
	protected int previousDepartureTime = -1;
	protected Transform delegate;
	
	public StopTimeTransform(Transform delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void write(Map<String, String> line) {
		
		try {
			int arrivalTimeSeconds = getTime(line, "arrival_time");
			if(arrivalTimeSeconds != -1) {
	        	line.put("arrival_time_int", Integer.toString(arrivalTimeSeconds));
			} 
			
			int departeTimeSeconds = getTime(line, "departure_time");
			if(departeTimeSeconds != -1) {
	        	line.put("departure_time_int", Integer.toString(departeTimeSeconds));
			}
			
			if(arrivalTimeSeconds != -1 && departeTimeSeconds != -1) {
	        	line.put("duration_arrival_departure", Integer.toString(departeTimeSeconds - arrivalTimeSeconds));
			} 
	        
	        delegate.write(line);
		} catch(Exception e) {
			throw new RuntimeException(e);
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
		list.add("departure_time_int");
		list.add("arrival_time_int");
		list.add("duration_arrival_departure");
		delegate.initialize(list.toArray(new String[list.size()]));
	}
	
	@Override
	public String toCypherStatement() throws IOException {
		return delegate.toCypherStatement();
	}

}
