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
	        String arrivalTime = line.get("arrival_time");
	        if(arrivalTime != null) {
	        	int arrivalTimeSeconds = toSeconds(arrivalTime);
	        	line.put("arrival_time", Integer.toString(arrivalTimeSeconds));
	        	
	        	if(previousDepartureTime != -1) {
	        		line.put("duration", Integer.toString(arrivalTimeSeconds - previousDepartureTime));
	        	}
	        }
	        String departureTime = line.get("departure_time");
	        if(departureTime != null) {
	        	
	        	int departureTimeInSeconds = toSeconds(departureTime);
	        	line.put("departure_time", Integer.toString(departureTimeInSeconds));

		        previousDepartureTime = departureTimeInSeconds;
	        } else {
		        previousDepartureTime = -1;
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
		delegate.initialize(list.toArray(new String[list.size()]));
	}
	
	@Override
	public String toCypherStatement() throws IOException {
		return delegate.toCypherStatement();
	}

}
