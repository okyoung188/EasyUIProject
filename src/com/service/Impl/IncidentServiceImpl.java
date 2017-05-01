package com.service.Impl;

import java.util.List;

import com.dao.Impl.IncidentDao;
import com.dao.Impl.IncidentDaoImpl;
import com.util.IncConRecord;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IncidentServiceImpl implements IncidentService {
    IncidentDao incidentLoader = null;
	@Override
	public JSONArray loadIncidents(String readerId) {
		if (readerId == null || "".equals(readerId.trim())) {
			LOGGER.info("ReaderId is null: " + readerId);
			return null;
		}
		List<IncConRecord> incidentList = null;
		incidentLoader = new IncidentDaoImpl();
		incidentList = incidentLoader.loadIncident(readerId);
		if (incidentList != null && incidentList.size() > 0) {
			JSONArray array = JSONArray.fromObject(incidentList);
			if (array != null && array.size() > 0) {
				LOGGER.debug("Incidents json array size is: " + array.size());
				return array;
			} else {
				LOGGER.info("Incidents is null or size is 0.");
			}
		} else {
			LOGGER.info("IncidentList is null.");
		}
		return null;
	}
	
	@Override
	public IncConRecord queryIncident(String incId) {
		if (incId == null || "".equals(incId.trim())) {
			LOGGER.info("Incident id is null.");
			return null;
		}
		try {
			incidentLoader = new IncidentDaoImpl();
			IncConRecord incident = incidentLoader.queryIncident(incId);
			if (incident != null) {
				return incident;
//	             JSONObject incObject = JSONObject.fromObject(incident);
//	             if (incObject != null && incObject.isNullObject()) {
//	            	 return incObject;
//	             } else {
//	            	 LOGGER.info("JSONObject of incident is null.");
//	             }
			} else {
			    LOGGER.info("Queried incident is null, id:" + incId);	
			}		
		} catch(Exception e) {
			LOGGER.error("Error occur when querying incident data: " + e.getMessage());
		}		
		return null;
	}

}
