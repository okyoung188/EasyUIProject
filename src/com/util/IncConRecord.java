package com.util;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.*;

public class IncConRecord implements Cloneable, Serializable
{
/*******************************************************************************
 *                            VARIABLE DECLARATIONS
 *******************************************************************************/
 	/** Current version */
	public transient static final double VERSION = 2.1; //PLEASE UPDATE MANUALLY


	/** Used for serialization */
	private static final long serialVersionUID = 200;

	/** log4j instance */
	private transient static final Logger LOGGER = Logger.getLogger(IncConRecord.class);

	/** Traffic event startTime location based on the latest NavTech database link ID. */
	private int start_nt_id;

	/** Traffic event endTime location based on the latest NavTech database link ID. */
	private int end_nt_id;

	/**
	 * External record identifier provided by data sources. '-1' is used if
	 * unknown. This field is internally used by TrafficCast for data
	 * verification purpose. Sometimes a DOT data source contains its own
	 * record ID and this field may be used to store the numbers. */
	private int num;

	/**
	 * The International Traveler Information System code for identifying a
	 * traffic event. Refer to
	 * http://www.sae.org/servlets/productDetail?PROD_TYP=STD&PROD_CD=J2540_2_200202
	 * for details. */
	private int ITIS_code;

	/**
	 * The traffic event code based on RDS-TMC (Radio Data System - Traffic
	 * Message Channel) code, not used at this point. */
	private int tmc_code;

	/** Used to assign this record to a particular category */
	private String type;

	/**
	 * Possible values: 0 or 1.
	 * <li>0: continuous construction.
	 *          Example: from 12/1/2002 8 AM to 12/20/2002 5 PM. 24 hours a day.
	 * <li>1: discrete construction.
	 *          Example: every day from 12/1/2002 to 12/20/2002 10 PM to 5 AM next day.
	 *               Night time only.
	 *
	 * Not used at this point */
	private int constr_type;

	/**
	 * Allowed <CODE>constr_type</CODE> values.  Must be in ascending order for
	 * binary searches. */
	private static final int[] POSS_CONSTR_TYPE_VALS = {0, 1};

	/** The state in which the traffic event occurs. */
	private String state;

	/**
	 * The city in which traffic event occurs. 3-charater notation is used in
	 * the database for the city name. Refer to TrafficCast Market Code table
	 * for details. */
	private String city;

	/** The county in which traffic event occurs.  <CODE>Null</CODE> if unknown.*/
	private String county;

	/** The time this event was reported or started */
	private String start_time;

	/** The expected end time of the traffic event */
	private String end_time;

	/** Date/time that this event was last updated */
	private String updated_time;

	private int id;
	private int link_id;
	private String link_dir="";
	private int end_link_id;
	private String end_link_dir="";
	private String status = "";
	private String creation_time = "";
	

	/** Allowed directional/bearing values. */
	private static final String[] POSS_DIRS = {"EB", "NB", "NEB", "NWB", "SB", "SEB", "SWB", "WB"};

	/**
	 * Main street on which this event has reportedly occured.
	 * <CODE>Null</CODE> if unknown. */
	private String mainSt;

	/**
	 * Bearing on mainSt.  The travel direction is defined as either 'NB', 'SB',
	 * 'EB', 'WB', 'NEB', 'NWB', 'SEB', SWB', or <CODE>null</CODE> if unknown. */
	private String mainDir;

	/**
	 * If the traffic event occurs between two cross roads along a main road,
	 * this field contains the "from" cross street name. <CODE>Null</CODE> is
	 * used if the from cross street is unknown. */
	private String fromSt;

	/**
	 * Bearing on fromSt.  The travel direction is defined as either 'NB', 'SB',
	 * 'EB', 'WB', 'NEB', 'NWB', 'SEB', SWB', or <CODE>null</CODE> if unknown. */
	private String fromDir;

	/**
	 * If the traffic event occurs between two cross roads along a main road,
	 * this field contains the "to" cross street name. <CODE>Null</CODE> is
	 * used if the to cross street is unknown. */
	private String toSt;

	/**
	 * Bearing on toSt.  The travel direction is defined as either 'NB', 'SB',
	 * 'EB', 'WB', 'NEB', 'NWB', 'SEB', SWB', or <CODE>null</CODE> if unknown. */
	private String toDir;

	/** Latitude of traffic event starting location based on NAD83. */
	private double s_lat =0;

	/** Longitude of traffic event starting location based on NAD83. */
	private double s_long =0 ;

	/** Latitude of traffic event endTime location based on NAD83. */
	private double e_lat =0 ;

	/** Longitude of traffic event endTime based on NAD83. */
	private double e_long =0;

	/**
	 * The severity level ranges from 1 - 4.
	 * <li>1: Low
	 * <li>2: Medium
	 * <li>3: High
	 * <li>4: Multi-lane or road closed */
	private int severity;

	/** Allowed <CODE>severity</CODE> values. */
	private static final int[] POSS_SEVERITY_VALS = {0, 1, 2, 3, 4};

	/**
	 * Source String of the traffic data.  Normally refers to the page on a DOT's
	 * website/server which contains the real-time data. */
	private String mapUrl;

	/**
	 * The traffic event description can be up to 1000 characters long and is
	 * used for free text details about the traffic event. <CODE>Null</CODE> if
	 * unknown.*/
	private String description;

	/** Field used by TrafficCast operator for internal quality checking purpose.*/
	private int checked;

	/** Allowed <code>checked</code> values. */
	private static final int[] POSS_CHECKED_VALS = {-2, 0, 1, 99};

	/**
	 * Created by combining the following 7 fields in an individual record:
	 * MAIN_ST, MIN_DIR, CROSS_FROM, FROM_DIR, CROSS_TO, TO_DIR, and TYPE. */
	private String key;


	/** Has this record's time been refined? */
	private boolean refined = true;

	/** Time zone this record's saved times are in terms of */
	private TimeZone timeZone;

	private boolean dataSourceStTime = false;
	
	private int unreliable;

	/** Allowed <code>unreliable</code> values. */
	private static final int[] POSS_UNRELIABLE_VALS = {0, 1, 2, 3};


/*******************************************************************************
 *                               CONSTRUCTORS
 *******************************************************************************/
	/**
	 * Initialized common fields to default values.  Initializes the following
	 * variables to the following values:<PRE>
	 *
	 * VARIABLE                     VALUE
	 * --------                     -----
	 * operator # (num)              -1
	 * ITIS code                     -1
	 * type                         null
	 * construction type             -1
	 * start time               [current time]
	 * end time                  [the epoch]
	 * updated time             [current time]
	 * map url             http://www.trafficcast.com
	 * description              [empty String]
	 * severity                      -1
	 * TMC code                      -1
	 * checked value                  0</PRE>
	 *
	 * @since 1.0
	 **/
	public IncConRecord()
	{
		num = -1; ITIS_code = -1; type = null; constr_type = -1;
		start_time = ""; end_time = "";
		updated_time =""; 
		description = ""; severity = -1; tmc_code = -1; checked = 0;
		dataSourceStTime = false; unreliable = 0;
        
	}


	/**
	 * Constructor currently used by the clone() method.
	 *
	 * @since 2.0
	 **/
	private IncConRecord(int start_nt_id, int end_nt_id, int num, int ITIS_code,
	                     int tmc_code, String type, int constr_type,
	                     String state, String city, String county, String startTime,
	                     String endTime, String updatedTime, String mainSt,
	                     String fromSt, String toSt, String mainDir,
	                     String fromDir, String toDir, double s_lat, double s_long,
	                     double e_lat, double e_long, int severity, String mapUrl,
	                     String description, int checked, String key, boolean refined,
	                     TimeZone timeZone, boolean dataSourceStTime, int unreliable)
	{
		this.start_nt_id = start_nt_id;
		this.end_nt_id = end_nt_id;
		this.num = num;
		this.ITIS_code = ITIS_code;
		this.tmc_code = tmc_code;
		this.type = type;
		this.constr_type = constr_type;
		this.state = state;
		this.city = city;
		this.county = county;
		this.start_time = startTime;
		this.end_time = endTime;
		this.updated_time = updatedTime;
		this.mainSt = mainSt;
		this.fromSt = fromSt;
		this.toSt = toSt;
		this.mainDir = mainDir;
		this.fromDir = fromDir;
		this.toDir = toDir;
		this.s_lat = s_lat;
		this.s_long = s_long;
		this.e_lat = e_lat;
		this.e_long = e_long;
		this.severity = severity;
		this.mapUrl = mapUrl;
		this.description = description;
		this.checked = checked;
		this.key = key;
		this.refined = refined;
		this.timeZone = timeZone;
		this.dataSourceStTime = dataSourceStTime;
		this.unreliable = unreliable;
	}





	public int getEnd_nt_id() {
		return end_nt_id;
	}


	public void setEnd_nt_id(int end_nt_id) {
		this.end_nt_id = end_nt_id;
	}


	public int getITIS_code() {
		return ITIS_code;
	}


	public void setITIS_code(int iTIS_code) {
		ITIS_code = iTIS_code;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getCounty() {
		return county;
	}


	public void setCounty(String county) {
		this.county = county;
	}


	public String getStart_time() {
		return start_time;
	}


	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}


	public String getEnd_time() {
		return end_time;
	}


	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}


	public String getUpdated_time() {
		return updated_time;
	}


	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getLink_id() {
		return link_id;
	}


	public void setLink_id(int link_id) {
		this.link_id = link_id;
	}


	public String getLink_dir() {
		return link_dir;
	}


	public void setLink_dir(String link_dir) {
		this.link_dir = link_dir;
	}


	public int getEnd_link_id() {
		return end_link_id;
	}


	public void setEnd_link_id(int end_link_id) {
		this.end_link_id = end_link_id;
	}


	public String getEnd_link_dir() {
		return end_link_dir;
	}


	public void setEnd_link_dir(String end_link_dir) {
		this.end_link_dir = end_link_dir;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getCreation_time() {
		return creation_time;
	}


	public void setCreation_time(String creation_time) {
		this.creation_time = creation_time;
	}


	public String getMainSt() {
		return mainSt;
	}


	public void setMainSt(String mainSt) {
		this.mainSt = mainSt;
	}


	public String getMainDir() {
		return mainDir;
	}


	public void setMainDir(String mainDir) {
		this.mainDir = mainDir;
	}


	public String getFromSt() {
		return fromSt;
	}


	public void setFromSt(String fromSt) {
		this.fromSt = fromSt;
	}


	public String getFromDir() {
		return fromDir;
	}


	public void setFromDir(String fromDir) {
		this.fromDir = fromDir;
	}


	public String getToSt() {
		return toSt;
	}


	public void setToSt(String toSt) {
		this.toSt = toSt;
	}


	public String getToDir() {
		return toDir;
	}


	public void setToDir(String toDir) {
		this.toDir = toDir;
	}


	public double getS_lat() {
		return s_lat;
	}


	public void setS_lat(double s_lat) {
		this.s_lat = s_lat;
	}


	public double getS_long() {
		return s_long;
	}


	public void setS_long(double s_long) {
		this.s_long = s_long;
	}


	public double getE_lat() {
		return e_lat;
	}


	public void setE_lat(double e_lat) {
		this.e_lat = e_lat;
	}


	public double getE_long() {
		return e_long;
	}


	public void setE_long(double e_long) {
		this.e_long = e_long;
	}


	public String getMapUrl() {
		return mapUrl;
	}


	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public TimeZone getTimeZone() {
		return timeZone;
	}


	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}


	/**
	 * @since 1.0
	 * @return "checked" value, as it relates to geocoding
	 **/
	public int getChecked() { return checked; }

	/**
	 * Sets the checked value for this record.  If the passed value is not present
	 * in the POSS_CHECKED_VALS array, the checked value of this record is NOT
	 * modified.
	 *
	 * @since 1.10
	 * @param checked Valid checked value
	 **/
	public void setChecked(int checked)
	{
		if (checked == 0 || checked == -1 || checked == 1 
				|| checked == 2 || checked == -2|| 
				checked == 7|| checked == -7 || checked == -99 ||checked == 99) {
			this.checked = checked;
		} else {
		    LOGGER.info("Invalid checked value: " + checked);	
		}		
	}


	/**
	 * @since 1.0
	 * @return all valid values prefaced by identifiers.
	 **/
	public String toString()
	{
		String data = "";
		if (start_nt_id > 0) data += "STARTING NT ID: " +
		                               start_nt_id + "; ";
		if (end_nt_id > 0) data += "ENDING NT ID: " + end_nt_id + " ";
		if (num > 0) data += "NUM: " + num + "; ";
		if (ITIS_code > 0) data += "ITIS: " + ITIS_code + "; ";
		data += "TYPE: " + type + "; ";
		data += "STATE: \'" + state + "\'; CITY: \'" + city + "\'; COUNTY: \'" +
		        county + "\'; ";
		data += "START TIME: \'" + start_time + "\'; " +
		        "END TIME: \'" + end_time +
 		        "\'; UPDATED TIME: \'" + updated_time +
 		        "\'; MAIN ST: \'" + mainSt + "\'; MAIN DIR: \'" + mainDir +
 		        "\'; FROM ST: \'" + fromSt + "\'; FROM DIR: \'" + fromDir +
 		        "\'; TO ST: \'" + toSt + "\'; TO DIR: \'" + toDir + "\'; ";
 		if (severity > 0) data += "SEVERITY: " + severity + "; ";
 		data += "MAP String: \'" + mapUrl + "\'; DESCRIPTION: \'" + description +
 		        "\'; ";
 		if (checked != 0) data += "CHECKED: " + checked +
 		                  "STARTING LAT: " + s_lat +
 	            	      "; STARTING LONG: " + s_long +
 	            	      "; ENDING LAT: " + e_lat +
 		                  "; ENDING LONG: " + e_long + "; ";
 		data += "KEY: \'" + key + "\'; ";

 		return data;
	}
}
