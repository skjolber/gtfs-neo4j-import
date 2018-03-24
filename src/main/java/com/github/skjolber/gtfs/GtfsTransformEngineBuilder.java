package com.github.skjolber.gtfs;

import java.io.File;

import com.github.skjolber.gtfs.transform.AbstractLabelTransform;
import com.github.skjolber.gtfs.transform.LabelTransform;
import com.github.skjolber.gtfs.transform.Transform;
import com.github.skjolber.gtfs.transform.TransformBuilder;
import com.github.skjolber.gtfs.transform.TransformEngine;

/**
 * Builder for transform engine.<br>
 * Note: ids on the form :ID(namespace) will be generated values, however
 * the right connections will be made. Use my_id:ID(MyId) to keep original ids.
 * 
 * Query 'call db.schema()' to inspect result. 
 * 
 * https://stackoverflow.com/questions/22340475/neo4j-labels-vs-properties-vs-relationship-node
 */

public class GtfsTransformEngineBuilder {

	private LabelTransform wheelChairLabel = new AbstractLabelTransform("wheelchair_accessible") {
		@Override
		public String transformLabel(String value) {
			if(value != null && value.equals("1")) {
				return "WHEELCHAIR";
			}
			return null;
		}
	}; 

	private LabelTransform wheelChairBoardingLabel = new AbstractLabelTransform("wheelchair_boarding") {
		@Override
		public String transformLabel(String value) {
			if(value != null && value.equals("1")) {
				return "WHEELCHAIR_BOARDING";
			}
			return null;
		}
	}; 
	
	private LabelTransform stopLocationType = new AbstractLabelTransform("location_type") {
		@Override
		public String transformLabel(String value) {
			if(value != null) {
				if(value.equals("1")) {
					return "STATION";
				} else if(value.equals("2")) {
					return "STATION_ENTRANCE";
				}
			}
			return null;
		}
	}; 
	
	private LabelTransform bikeLabel = new AbstractLabelTransform("bikes_allowed") {
		@Override
		public String transformLabel(String value) {
			if(value != null && value.equals("1")) {
				return "BIKE";
			}
			return null;
		}
	}; 
	
	private LabelTransform directionLabel = new AbstractLabelTransform("direction_id") {
		@Override
		public String transformLabel(String value) {
			if(value != null) {
				if(value.equals("1")) {
					return "INBOUND";
				} else if(value.equals("2")) {
					return "OUTBOUND";
				}
			}
			return null;
		}
	}; 

/*	
	private LabelTransform routeTypeLabel = new AbstractLabelTransform("route_type") {
		@Override
		public String transformLabel(String value) {
			if(value != null) {
				switch(value) {
				case "0" : {
					return "TRAM_STREETCAR_LIGHT_RAIL";
				}
				case "1" : {
					return "SUBWAY";
				}
				case "2" : {
					return "RAIL";
				}
				case "3" : {
					return "BUS";
				}
				case "4" : {
					return "FERRY";
				}
				case "5" : {
					return "CABLE_CAR";
				}
				case "6" : {
					return "GONDOLA";
				}
				case "7" : {
					return "FUNICULAR";
				}
				}
			}
			return null;
		}
	}; 
	*/

	// trips:
	//  - trip
	//  - service
	//  - trip-uses-route
	//  - trip-implements-service
	
	private Transform trip = new TransformBuilder()
			.withDestination("trips.csv")
			.withSource("trips.txt")
			.withRequiredMapping("trip_id", "trip_id:ID(Trip)")
			.withIndex("trip_id")
			.withType("Trip")
			.withLabel(wheelChairLabel)
			.withLabel(bikeLabel)
			.withLabel(directionLabel)
			.build();
	
	private Transform services = new TransformBuilder()
			.withDestination("services.csv")
			.withSource("trips.txt")
			.withRequiredMapping("service_id", ":ID(Service)")
			.withFilter("service_id")
			.withType("Service")
			.build();
	

	private Transform tripUsesRoute = new TransformBuilder()
			.withDestination("tripUsesRoute.csv")
			.withSource("trips.txt")
			.withRequiredMapping("trip_id", ":START_ID(Trip)")
			.withRequiredMapping("route_id", ":END_ID(Route)")
			.withRelation("USES")
			.build();

	private Transform tripImplementsService = new TransformBuilder()
			.withDestination("tripImplementsService.csv")
			.withSource("trips.txt")
			.withRequiredMapping("trip_id", ":START_ID(Trip)")
			.withRequiredMapping("service_id", ":END_ID(Service)")
			.withOptionalMapping("trip_headsign", "headsign")
			.withOptionalMapping("trip_short_name", "short_name")
			.withRelation("IMPLEMENTS")
			.build();

	// agency
	
	private Transform agency = new TransformBuilder()
			.withDestination("agencies.csv")
			.withSource("agency.txt")
			.withRequiredMapping("agency_id", "agency_id:ID(Agency)")
			.withRequiredMapping("agency_name", "name")
			.withOptionalMapping("agency_url", "url")
			.withOptionalMapping("agency_timezone", "timezone")
			.withOptionalMapping("agency_phone", "phone")
			.withIndex("agency_id")
			.withType("Agency")
			.build();
	
	// routes:
	//  - route
	//  - agency-operates-route

	private Transform routes = new TransformBuilder()
			.withDestination("routes.csv")
			.withSource("routes.txt")
			.withRequiredMapping("route_id", "route_id:ID(Route)")
			.withOptionalMapping("route_short_name", "short_name")
			.withOptionalMapping("route_long_name", "long_name")
			.withOptionalMapping("route_type", "type:int")
			.withOptionalMapping("route_url", "url")
			.withType("Route")
			.withIndex("route_id")
			//.withLabel(routeTypeLabel)
			.build();
	
	private Transform agencyOperatesRoute = new TransformBuilder()
			.withDestination("agencyOperatesRoute.csv")
			.withSource("routes.txt")
			.withRequiredMapping("agency_id", ":START_ID(Agency)")
			.withRequiredMapping("route_id", ":END_ID(Route)")
			.withRelation("OPERATES")
			.build();
	
	// calendar
	//   - calendar
	//   - day
	//   - calendar runs at day
	//   - calendar schedules service

	private Transform calendar = new TransformBuilder()
			.withDestination("calendar.csv")
			.withSource("calendar.txt")
			.withRequiredMapping("lineNumber", ":ID(Calendar)")
			.withRequiredMapping("start_date", "start_date")
			.withRequiredMapping("end_date", "end_date")
			.withType("Calendar")
			.build();
	
	private Transform calendarSchedulesService = new TransformBuilder()
			.withDestination("calendarSchedulesService.csv")
			.withSource("calendar.txt")
			.withRequiredMapping("service_id", ":START_ID(Service)")
			.withRequiredMapping("lineNumber", ":END_ID(Calendar)")
			.withRelation("SCHEDULES")
			.build();
	
	private Transform day = new DayTransform(
			new TransformBuilder()
				.withDestination("days.csv")
				.withSource("calendar.txt")
				.withRequiredMapping("day", "day_id:ID(Day)")
				.withFilter("day")
				.withIndex("day_id")
				.withType("Day")
				.build()
			);

	private Transform caldendarRepeatsDay = new DayTransform(
			new TransformBuilder()
				.withDestination("caldendarRepeatsDay.csv")
				.withSource("calendar.txt")
				.withRequiredMapping("lineNumber", ":START_ID(Calendar)")
				.withRequiredMapping("day", ":END_ID(Day)")
				.withRelation("REPEATS")
				.build()
			);
	
	private Transform dates = new TransformBuilder()
			.withDestination("dates.csv")
			.withSource("calendar_dates.txt")
			.withRequiredMapping("date", "date:ID(CalendarDate)")
			.withFilter("date")
			.withType("CalendarDate")
			.build();
	
	private Transform serviceRunsOnDates = new TransformBuilder()
			.withDestination("serviceRunsOnDates.csv")
			.withSource("calendar_dates.txt")
			.withRequiredMapping("service_id", ":START_ID(Service)")
			.withRequiredMapping("date", ":END_ID(CalendarDate)")
			.withRequiredMapping("exception_type", "exception_type")
			.withRelation("RUNS_ON")
			.build();
	
	// https://developers.google.com/transit/gtfs/reference/#stopstxt
	private Transform stops = new TransformBuilder()
			.withDestination("stops.csv")
			.withSource("stops.txt")
			.withRequiredMapping("stop_id", "stop_id:ID(Stop)")
			.withRequiredMapping("stop_name", "name")
			.withOptionalMapping("stop_lat", "lat:float")
			.withOptionalMapping("stop_lon", "lon:float")
			.withOptionalMapping("platform_code", "platform_code")
			.withOptionalMapping("stop_url", "url")	
			.withIndex("stop_name")
			.withUniqueConstraint("stop_id")
			.withLabel(wheelChairBoardingLabel)
			.withLabel(stopLocationType)
			.withType("Stop")
			.build();
	
	private Transform stopPartOfStop = new TransformBuilder()
			.withDestination("stopPartOfStop.csv")
			.withSource("stops.txt")
			.withRequiredMapping("stop_id", ":START_ID(Stop)")
			.withRequiredMapping("parent_station", ":END_ID(Stop)")
			.withRelation("PART_OF")
			.withSkipPartial(true)
			.build();
	
	private Transform stopTimes = new StopTimeTransform(
			new TransformBuilder()
				.withDestination("stopTimes.csv")
				.withSource("stop_times.txt")
				.withRequiredMapping("lineNumber", ":ID(StopTime)")
				.withRequiredMapping("arrival_time", "arrival_time:int")
				.withRequiredMapping("departure_time", "departure_time:int")
				.withType("StopTime")
				.build()
			);
	
	private Transform stopTimeSequences = new StopTimeSequenceTransform(
			new TransformBuilder()
				.withDestination("stopTimeSequences.csv")
				.withSource("stop_times.txt")
				.withRequiredMapping("lineNumber", ":START_ID(StopTime)")
				.withRequiredMapping("lineNumberMinus1", ":END_ID(StopTime)")
				.withRelation("PRECEDES")
				.build()
			);
	
	private Transform stopTimePartOfTripTimes = new TransformBuilder()
			.withDestination("stopTimePartOfTripTimes.csv")
			.withSource("stop_times.txt")
			.withRequiredMapping("lineNumber", ":START_ID(StopTime)")
			.withRequiredMapping("trip_id", ":END_ID(Trip)")
			.withRelation("PART_OF_TRIP")
			.build();
	
	private Transform stopTimeLocatedAtStop = new TransformBuilder()
			.withDestination("stopTimeLocatedAtStop.csv")
			.withSource("stop_times.txt")
			.withRequiredMapping("lineNumber", ":START_ID(StopTime)")
			.withRequiredMapping("stop_id", ":END_ID(Stop)")
			.withRelation("LOCATED_AT")
			.build();	
	
	private Transform stopTransferStop = new TransformBuilder()
			.withDestination("transfers.csv")
			.withSource("transfers.txt")
			.withRequiredMapping("from_stop_id", ":START_ID(Stop)")
			.withRequiredMapping("to_stop_id", ":END_ID(Stop)")
			.withOptionalMapping("transfer_type", "type")
			.withOptionalMapping("min_transfer_time", "transfer_time")
			.withRelation("TRANSFER")
			.build();		
	
	protected File outputDirectory;
	
	public TransformEngine build() {
		TransformEngine engine = new TransformEngine();
		engine.setOutputDirectory(outputDirectory);
		
		engine.add(trip);
		engine.add(services);
		engine.add(tripUsesRoute);
		engine.add(tripImplementsService);
		
		engine.add(agency);
		
		engine.add(routes);
		engine.add(agencyOperatesRoute);
		
		engine.add(day);
		engine.add(caldendarRepeatsDay);
		
		engine.add(calendar);		
		engine.add(calendarSchedulesService);
		
		engine.add(dates);
		engine.add(serviceRunsOnDates);
		
		engine.add(stops);
		engine.add(stopPartOfStop);
		
		engine.add(stopTimes);
		engine.add(stopTimePartOfTripTimes);
		engine.add(stopTimeLocatedAtStop);
		engine.add(stopTimeSequences);
		engine.add(stopTransferStop);
		
		return engine;
	}
	
}
