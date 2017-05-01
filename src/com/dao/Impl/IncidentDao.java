package com.dao.Impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.util.IncConRecord;

public interface IncidentDao {
    Logger LOGGER = Logger.getLogger(IncidentDao.class);
	public List<IncConRecord> loadIncident(String readerId);
	
	public IncConRecord queryIncident(String incId);
}
