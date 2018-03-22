package com.github.skjolber.gtfs;

import java.io.File;

import com.github.skjolber.gtfs.transform.Transform;
import com.github.skjolber.gtfs.transform.TransformBuilder;
import com.github.skjolber.gtfs.transform.TransformEngine;

/**
 * Builder for transform engine.<br>
 * Note: ids on the form :ID(namespace) will be generated values, however
 * the right connections will be made. Use my_id:ID(MyId) to keep original ids.
 * 
 * Query 'call db.schema()' to inspect result. 
 */

public class GtfsTransformEngineBuilder {

	// trips:
	//  - trip
	//  - service
	//  - trip-uses-route
	//  - trip-implements-service
	
	private Transform trip = new TransformBuilder()
			.withDestination("trips.csv")
			.withSource("trips.txt")
			.withMapping("trip_id", "trip_id:ID(Trip)")
			.withType("Trip")
			.build();
	
	private Transform services = new TransformBuilder()
			.withDestination("services.csv")
			.withSource("trips.txt")
			.withMapping("service_id", ":ID(Service)")
			.withFilter("service_id")
			.withType("Service")
			.build();
	

	private Transform tripUsesRoute = new TransformBuilder()
			.withDestination("tripUsesRoute.csv")
			.withSource("trips.txt")
			.withMapping("trip_id", ":START_ID(Trip)")
			.withMapping("route_id", ":END_ID(Route)")
			.withRelation("USES")
			.build();

	private Transform tripImplementsService = new TransformBuilder()
			.withDestination("tripImplementsService.csv")
			.withSource("trips.txt")
			.withMapping("trip_id", ":START_ID(Trip)")
			.withMapping("service_id", ":END_ID(Service)")
			.withRelation("IMPLEMENTS")
			.build();

	// agency
	
	private Transform agency = new TransformBuilder()
			.withDestination("agencies.csv")
			.withSource("agency.txt")
			.withMapping("agency_id", "agency_id:ID(Agency)")
			.withMapping("agency_name", "name")
			.withMapping("agency_url", "url")
			.withMapping("agency_timezone", "timezone")
			.withMapping("agency_phone", "phone")
			.withIndex("agency_id")
			.withType("Agency")
			.build();
	
	// routes:
	//  - route
	//  - agency-operates-route

	private Transform routes = new TransformBuilder()
			.withDestination("routes.csv")
			.withSource("routes.txt")
			.withMapping("route_id", "route_id:ID(Route)")
			.withMapping("route_short_name", "short_name")
			.withMapping("route_long_name", "long_name")
			.withMapping("route_type", "type:int")
			.withType("Route")
			.withIndex("route_id")
			.build();
	
	private Transform agencyOperatesRoute = new TransformBuilder()
			.withDestination("agencyOperatesRoute.csv")
			.withSource("routes.txt")
			.withMapping("agency_id", ":START_ID(Agency)")
			.withMapping("route_id", ":END_ID(Route)")
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
			.withMapping("lineNumber", ":ID(Calendar)")
			.withMapping("start_date", "start_date")
			.withMapping("end_date", "end_date")
			.withType("Calendar")
			.build();
	
	private Transform calendarSchedulesService = new TransformBuilder()
			.withDestination("calendarSchedulesService.csv")
			.withSource("calendar.txt")
			.withMapping("service_id", ":START_ID(Service)")
			.withMapping("lineNumber", ":END_ID(Calendar)")
			.withRelation("SCHEDULES")
			.build();
	
	private Transform day = new DayTransform(
			new TransformBuilder()
				.withDestination("days.csv")
				.withSource("calendar.txt")
				.withMapping("day", "day_id:ID(Day)")
				.withFilter("day")
				.withIndex("day_id")
				.withType("Day")
				.build()
			);

	private Transform caldendarRepeatsDay = new DayTransform(
			new TransformBuilder()
				.withDestination("caldendarRepeatsDay.csv")
				.withSource("calendar.txt")
				.withMapping("lineNumber", ":START_ID(Calendar)")
				.withMapping("day", ":END_ID(Day)")
				.withRelation("REPEATS")
				.build()
			);
	
	private Transform dates = new TransformBuilder()
			.withDestination("dates.csv")
			.withSource("calendar_dates.txt")
			.withMapping("date", "date:ID(CalendarDate)")
			.withFilter("date")
			.withType("CalendarDate")
			.build();
	
	private Transform serviceRunsOnDates = new TransformBuilder()
			.withDestination("serviceRunsOnDates.csv")
			.withSource("calendar_dates.txt")
			.withMapping("service_id", ":START_ID(Service)")
			.withMapping("date", ":END_ID(CalendarDate)")
			.withMapping("exception_type", "exception_type")
			.withRelation("RUNS_ON")
			.build();
	
	private Transform stops = new TransformBuilder()
			.withDestination("stops.csv")
			.withSource("stops.txt")
			.withMapping("stop_id", "stop_id:ID(Stop)")
			.withMapping("stop_name", "name")
			.withMapping("stop_lat", "lat:float")
			.withMapping("stop_lon", "lon:float")
			.withMapping("platform_code", "platform_code")
			.withMapping("location_type", "location_type")	
			.withIndex("stop_id")
			.withType("Stop")
			.build();
	
	private Transform stopPartOfStop = new TransformBuilder()
			.withDestination("stopPartOfStop.csv")
			.withSource("stops.txt")
			.withMapping("stop_id", ":START_ID(Stop)")
			.withMapping("parent_station", ":END_ID(Stop)")
			.withRelation("PART_OF")
			.withSkipPartial(true)
			.build();
	
	private Transform stopTimes = new StopTimeTransform(
			new TransformBuilder()
				.withDestination("stopTimes.csv")
				.withSource("stop_times.txt")
				.withMapping("lineNumber", ":ID(StopTime)")
				.withMapping("arrival_time", "arrival_time:int")
				.withMapping("departure_time", "departure_time:int")
				.withMapping("stop_sequence", "stop_sequence:int")
				.withType("StopTime")
				.build()
			);
	
	private Transform stopTimeSequences = new StopTimeSequenceTransform(
			new TransformBuilder()
				.withDestination("stopTimeSequences.csv")
				.withSource("stop_times.txt")
				.withMapping("lineNumber", ":START_ID(StopTime)")
				.withMapping("lineNumberMinus1", ":END_ID(StopTime)")
				.withRelation("PRECEDES")
				.build()
			);
	
	private Transform stopTimePartOfTripTimes = new TransformBuilder()
			.withDestination("stopTimePartOfTripTimes.csv")
			.withSource("stop_times.txt")
			.withMapping("lineNumber", ":START_ID(StopTime)")
			.withMapping("trip_id", ":END_ID(Trip)")
			.withRelation("PART_OF_TRIP")
			.build();
	
	private Transform stopTimeLocatedAtStop = new TransformBuilder()
			.withDestination("stopTimeLocatedAtStop.csv")
			.withSource("stop_times.txt")
			.withMapping("lineNumber", ":START_ID(StopTime)")
			.withMapping("stop_id", ":END_ID(Stop)")
			.withRelation("LOCATED_AT")
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
		return engine;
	}
	
}
