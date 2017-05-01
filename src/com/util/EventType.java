package com.util;

/*******************************************************************************
 * Enumerated type used by the IncConRecord and IncConDBUtils classes to determine
 * the type of a specific event.
 *
 * @author Ray Nicholus (1/06/2006)
 * @version 0.1
 *******************************************************************************/

public enum EventType
{
	INCIDENT (1), CONSTRUCTION (2), TTA (3), SPECIAL_EVENT (4);


	/** Current version */
	public static final double VERSION = 0.1; //PLEASE UPDATE MANUALLY

	private final int id;

	private EventType(int id) { this.id = id; }


	/**
	 * Gets the ID # used in the DB for the type this EventType instance represents.
	 *
	 * @return id Integer value of this EventType
	 **/
	public int id() { return id; }
	public static EventType getEvent(int eventValue){
		if(eventValue==1){
			return EventType.INCIDENT;
		}
		if(eventValue==2){
			return EventType.CONSTRUCTION;
		}
		if(eventValue==3){
			return EventType.TTA;
		}
		else return EventType.SPECIAL_EVENT;
	}
}