package com.service.Impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.util.IncConRecord;

public interface IncidentService {
	Logger LOGGER = Logger.getLogger(IncidentService.class);
	public JSONArray loadIncidents(String readerId);
	
	public IncConRecord queryIncident(String incId);

}
