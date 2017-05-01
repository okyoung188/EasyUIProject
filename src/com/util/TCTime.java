package com.util;

import java.io.*;

import java.util.*;
import java.text.*;
import org.apache.log4j.*;
/*******************************************************************************
 * Object class used to manipulate time for the purposes of comparison and
 * conversion to alternate timezones.  Also allows for return of formatted,
 * String-based times.<PRE>
 *
 * NOTE: When passing a time string into one of the two constructors, the
 * following special cases should be noted:
 *    1.) If a time/date string is passed without a time (e.g. 5/5/2005), a time of
 *         00:00:00 will be assumed.
 *    2.) If a time/date string is passed without a date (e.g. 1:10:00 AM) a date
 *         of 1/1/1970 will be assumed.
 *
 * When dealing with the first case, use the second constructor.  When dealing
 * with the second case, create a new TCTime object using the default
 * constructor (to create a TCTime object with the current date) and simply
 * use the set methods of TCTime to set the hours, minutes, and seconds.  It
 * can be safely assumed that events with only a time available have a date
 * equal to the date they are retrievable.  If this is not true, there is very
 * little that can be done.
 *
 * VERSION NOTES:
 *    2.31: Added generics to Comparable implementation.  This meant that I had
 *          to also change the parameter type of the equals method from
 *          <code>Object</code> to <code>TCTime</code>.  TCTime will now only
 *          work with Java 1.5 and later.  Also changed the return type of
 *          the clone method from <code>Object</code> to <code>TCTime</code>.
 *          Finally, added a private constructor to be used by the clone method.
 *          Made getDate() method public again, but deprecated it.  Also added
 *          log4j calls to 2 constructors.
 *
 *    2.30: Made getDate() method private.  Removed code that handles an incorrectly
 *          abbreviated Thursday.  Added getDifference method.  Also removed
 *          throwables from 2-arg constructor.  Finally, cleaned up the code and
 *          comments a bit and changed the setToTimeZero method to "setToEpoch".
 *          Finally, moved this to package com.trafficcast.base.tctime.
 *
 *    2.26: Made this class serializable.  This change was prompted when working
 *          on the ReaderInfoMon class.
 *
 *    2.25: Implemented the Cloneable interface for a deep copy of the TCTime
 *          object.</PRE>
 *
 * @author Ray Nicholus (01/2005)
 * @version 2.31 (10/19/2005)
 * @version 2.30 (10/17/2005)
 * @version 2.26 (07/01/2005)
 * @version 2.25 (06/01/2005)
 * @version 2.2 (05/11/2005)
 * @version 2.1 (05/06/2005)
 * @version 2.0 (03/22/2005)
 *******************************************************************************/
public class TCTime implements Comparable<TCTime>, Cloneable, Serializable
{
	/** Current version */
	public transient static final double VERSION = 2.31; //PLEASE UPDATE MANUALLY


	/** Used for serialization */
	private static final long serialVersionUID = 231;

	/** log4j instance */
	private transient static final Logger LOGGER = Logger.getLogger(TCTime.class);

	/** Date object representing the stored time of this instance */
	private Date time;
	/** Default format used to return Strings representing the time stored in this instance */
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");



	/**
	 * Initializes the time to current time.
	 *
	 * @since 1.0
	 **/
	public TCTime() { time = new Date(); }



	/**
	 * Overloaded construtor.  Allows a time object to be created by passing a
	 * format and a time.  Only use this form of the constructor if the timezone
	 * is part of the passed time string.  For time strings without a time zone
	 * identifier included, please use the constructor which allows a
	 * <CODE>TimeZone</CODE> object to be passed as a parameter.  It is
	 * reccomended that you use the constructor with a TimeZone parameter in most
	 * cases, since it will automatically normalize time strings that do not
	 * contain a direct reference in the string to the time zone.  ParseExceptions
	 * are caught and handled by mimicing the no-arg constructor of this class.
	 *
	 * @since 1.0
	 * @param format the format the passed time is in
	 * @param passed_time the passed time
	 **/
	public TCTime(SimpleDateFormat format, String passed_time)
	{
		try { time = format.parse(passed_time); }
		catch (ParseException ex)
		{
			time = new Date();
			LOGGER.log(Level.ERROR, "Could not parse passed time object.  Current time used instead.", ex);
		}
	}


	/**
	 * Overloaded construtor.  Allows a time object to be created by passing a
	 * format, a time, and a time zone.  This form of the constructor is very
	 * useful if you plan on parsing a time string that does not include a
	 * time zone identifier.  This form of the constructor allows you to pass
	 * a <CODE>TimeZone</CODE> object as a parameter, which is then used to
	 * identify the passed time string as a member of the passed time zone.  For
	 * instances where a time string does not include a time zone identifier,
	 * usage of this method makes time conversion and comparison much more
	 * straightforward.  ParseExceptions are caught and handled by mimicing the
	 * no-arg constructor of this class.
	 *
	 * @since 1.0
	 * @param format the format the passed time is in
	 * @param passed_time the passed time
	 * @param timeZone the time zone which the passed time belongs to
	 **/
	public TCTime(SimpleDateFormat format, String passed_time, TimeZone timeZone)
	{
		try
		{			
			format.setTimeZone(timeZone);
			time = format.parse(passed_time);
		}
		catch (ParseException ex)
		{
			time = new Date();
			LOGGER.log(Level.ERROR, "Could not parse passed time object.  Current time used instead.", ex);
		}
	}


	/**
	 * Private constructor to be used by the clone method.
	 *
	 * @since 2.31
	 * @param time A Date object representing the TCTime object
	 **/
	private TCTime(Date time) { this.time = time; }




	/**
	 * Sets the date/time to the epoch (January 1, 1970, 00:00:00 GMT/UTC)
     *
	 * @since 1.0
	 **/
	public void setToEpoch() { time = new Date(0); }


	/**
	 * Gets the <CODE>Date</CODE> object associated with this instance.
	 *
	 * @since 1.0
	 * @return <CODE>Date</CODE> object associated with this instance
	 **/
	public Date getDate() { return this.time; }


	/**
	 * Sets the hour.  Possible values are 0-23.  If an invalid value is passed,
	 * hour will revert to 00.
	 *
	 * @since 1.0
	 * @param hour The hour in military time (0-23)
	 **/
	public void setHour(int hour)
	{
		if (hour < 0 || hour > 23)
			hour = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		time = cal.getTime();
	}
	/**
	 * Sets the minute.  Possible values are 0-59.  If an invalid value is
	 * passed, minute will revert to 00.
	 *
	 * @since 1.0
	 * @param minute The minute (0-59)
	 **/
	public void setMinute(int minute)
	{
		if (minute < 0 || minute > 59)
			minute = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.MINUTE, minute);
		time = cal.getTime();
	}


	/**
	 * Sets the second.  Possible values are 0-59.  If an invalid value is
	 * passed, second will revert to 0.
	 *
	 * @since 1.0
	 * @param second The second (0-59)
	 **/
	public void setSecond(int second)
	{
		if (second < 0 || second > 59)
			second = 0;

		Calendar cal = Calendar.getInstance();

		cal.setTime(time);
		cal.set(Calendar.SECOND, second);
		time = cal.getTime();
	}


	/**
	 * Advances the time value held in this instance by passed number of
	 * milliseconds.
	 *
	 * @since 1.0
	 * @param milliseconds Number of milliseconds to advance
	 **/
	public void add(long milliseconds)
	{
		this.time = new Date(this.time.getTime() + milliseconds);
	}


	/**
	 * Compares this <CODE>TCTime</CODE> object to another.
	 *
	 * @since 1.0
	 * @param tcTime TCTime object used for comparison
	 * @return <CODE>-1</CODE> if this <CODE>TCTime</CODE> object occurs before
	 *         <CODE>tcTime</CODE>; <CODE>0<CODE> if this <CODE>TCTime</CODE> object
	 *         occurs at the same time as <CODE>tcTime</CODE>; <CODE>1</CODE> if this
	 *         <CODE>TCTime</CODE> object occurs after <CODE>tcTime</CODE>.
	 **/
	public int compareTo(TCTime tcTime)
	{
		return this.time.compareTo(tcTime.time);
	}


	/**
	 * Creates a deep copy of this object
	 *
	 * @since 2.25
	 * @return a deep copy of this object
	 **/
	public TCTime clone() { return new TCTime((Date)time.clone()); }


	/**
	 * Checks for equality between two TCTime objects.
	 *
	 * @since 1.0
	 * @param tcTime TCTime object used for comparison
	 * @return <CODE>true</CODE> if times are equal, <CODE>false</CODE> otherwise
     * @throws NullPointerException if tcTime is null
     * @throws ClassCastException if tcTime is not a TCTime object
	 **/
	public boolean equals(TCTime tcTime)
	{
		return (this.compareTo(tcTime) == 0);
	}


	/**
	 * Determines whether this time object compared to the current time is more than
	 * the passed number of milliseconds old.  Both times are normalized to the
	 * default timezone for comparison purposes.
	 *
	 * @since 1.0
	 * @param max_time_diff Maximum allowed number of milliseconds old this TCTime instance can be
	 * @return <CODE>true</CODE> if the object is too old, <CODE>false</CODE> otherwise.
	 **/
	public boolean isTooOld(long max_time_diff)
	{
		Calendar timeNow = Calendar.getInstance();
		return ((timeNow.getTimeInMillis() - time.getTime()) > max_time_diff);
	}



	/**
	 * Finds the difference between this TCTime object and the passed TCTime
	 * object in milliseconds.
	 *
	 * @since 2.30
	 * @param tcTime object to use in the "this-that" equation
	 * @return the difference between this TCTime object and the passed TCTime
	 *         object in milliseconds.
	 **/
	public long getDifference(TCTime tcTime)
	{
		return this.time.getTime() - tcTime.time.getTime();
	}


	/**
	 * Gets the time in terms of the passed timezone.  In this 1-arg
	 * representation, the time/date string returned is in the default format
	 * (MM/dd/yyyy HH:mm:ss z).
	 *
	 * @since 1.0
	 * @param timezone Timezone you are interested in
	 * @return time in terms of the passed timezone
	 **/
	public String getLocalTime(TimeZone timezone)
	{
		sdf.setTimeZone(timezone);
		return sdf.format(time);
	}


	/**
	 * Gets the time in terms of the passed timezone.  Also, formats the time based
	 * on the passed string.
	 *
	 * @since 1.0
	 * @param timezone Timezone to use when describing the time
	 * @param format Format you would like the time returned in
	 * @return the time in terms of timezone, formatted
	 **/
	public String getLocalTime(TimeZone timezone, SimpleDateFormat format)
	{
		format.setTimeZone(timezone);
		return format.format(time);
	}


	/**
	 * Gets the time in terms of the default time zone.  The default time zone
	 * is defined as the time zone which the machine hosting this class belongs
	 * to.  In this no-arg representation, the time/date string returned is in
	 * the default format (MM/dd/yyyy HH:mm:ss z).
	 *
	 * @since 1.0
	 * @return the time in terms of the default timezone
	 **/
	public String getDefaultTime() { return sdf.format(time); }


	/**
	 * Gets the time in terms of the default timezone.  Also, formats the time based
	 * on the passed string.
	 *
	 * @since 1.0
	 * @param format Format you would like the time returned in
	 * @return the time in terms of the default timezone
	 **/
	public String getDefaultTime(SimpleDateFormat format)
	{
		return format.format(time);
	}
}