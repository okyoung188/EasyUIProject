package com.dao.Impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dbUtil.DBConnector;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.PreparedStatement;
import com.util.IncConRecord;

public class IncidentDaoImpl implements IncidentDao {

	@Override
	public List<IncConRecord> loadIncident(String readerId) {
		if (readerId == null || "".equals(readerId.trim())) {
			LOGGER.info("ReaderId is null, cannot query database.");
			return null;
		}
		DBConnector connector = DBConnector.getInstance();
		String dbName = "incident_db";
		String sql = null;
		Connection conn = null;
		PreparedStatement preStat = null;
		ResultSet results = null;
		IncConRecord incRecord = null;
		List<IncConRecord> incList = new ArrayList<IncConRecord>();
		try {
			conn = (Connection) connector.connect();
			if (conn == null) {
				LOGGER.info("Connection to db isn't established.");
				return null;
			}
			sql = "select id, checked, link_id, link_dir, end_link_id, end_link_dir, "
					+ "main_st, main_dir, cross_from, from_dir, cross_to, to_dir, slat, slong, elat, elong, "
					+ "state, city, county, type, status, start_time, end_time, updated_time, creation_time, "
					+ "mapurl, itis_code, description from " + dbName +".incidents where reader_id=?";
			preStat = (PreparedStatement) conn.prepareStatement(sql);
			preStat.setString(1, readerId);
			results = preStat.executeQuery();
			while (results.next()) {
				incRecord = new IncConRecord();
				incRecord.setId(results.getInt(1));
				incRecord.setChecked(results.getInt(2));
				incRecord.setLink_id(results.getInt(3));
				incRecord.setLink_dir(results.getString(4));
				incRecord.setEnd_link_id(results.getInt(5));
				incRecord.setEnd_link_dir(results.getString(6));
				incRecord.setMainSt(results.getString(7));
				incRecord.setMainDir(results.getString(8));
				incRecord.setFromSt(results.getString(9));
				incRecord.setFromDir(results.getString(10));
				incRecord.setToSt(results.getString(11));
				incRecord.setToDir(results.getString(12));
				incRecord.setS_lat(results.getDouble(13));
				incRecord.setS_long(results.getDouble(14));
				incRecord.setE_lat(results.getDouble(15));
				incRecord.setE_long(results.getDouble(16));
				incRecord.setState(results.getString(17));
				incRecord.setCity(results.getString(18));
				incRecord.setCounty(results.getString(19));
				incRecord.setType(results.getString(20));
				incRecord.setStatus(results.getString(21));
				incRecord.setStart_time(results.getString(22));
				incRecord.setEnd_time(results.getString(23));
				incRecord.setUpdated_time(results.getString(24));
				incRecord.setCreation_time(results.getString(25));
				incRecord.setMapUrl(results.getString(26));
				incRecord.setITIS_code(results.getInt(27));
				incRecord.setDescription(results.getString(28));
				incList.add(incRecord);
			}
			if (incList != null && incList.size() > 0) {
				LOGGER.debug("Incident size: " + incList.size());
				return incList;
			} else {
				LOGGER.debug("Query data size is 0.");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occur when accessing db: " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
			if (preStat != null) {
				try {
					preStat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				preStat = null;
			}			
			if(conn != null) {
				connector.disconnect();
				conn = null;
			}
		}
		return null;
	}

	@Override
	public IncConRecord queryIncident(String incId) {
		if (incId == null || "".equals(incId.trim())) {
			LOGGER.info("Incident id is null.");
			return null;
		}
		DBConnector connector = DBConnector.getInstance();
		String dbName = "incident_db";
		String sql = null;
		Connection conn = null;
		PreparedStatement preStat = null;
		ResultSet results = null;
		IncConRecord incRecord = null;
		try {
			conn = (Connection) connector.connect();
			if (conn == null) {
				LOGGER.info("Connection to db isn't established.");
				return null;
			}
			sql = "select id, checked, link_id, link_dir, end_link_id, end_link_dir, "
					+ "main_st, main_dir, cross_from, from_dir, cross_to, to_dir, slat, slong, elat, elong, "
					+ "state, city, county, type, status, start_time, end_time, updated_time, creation_time, "
					+ "mapurl, itis_code, description from " + dbName +".incidents where id=?";
			preStat = (PreparedStatement) conn.prepareStatement(sql);
			preStat.setString(1, incId);
			results = preStat.executeQuery();
			if (results.next()) {
				incRecord = new IncConRecord();
				incRecord.setId(results.getInt(1));
				incRecord.setChecked(results.getInt(2));
				incRecord.setLink_id(results.getInt(3));
				incRecord.setLink_dir(results.getString(4));
				incRecord.setEnd_link_id(results.getInt(5));
				incRecord.setEnd_link_dir(results.getString(6));
				incRecord.setMainSt(results.getString(7));
				incRecord.setMainDir(results.getString(8));
				incRecord.setFromSt(results.getString(9));
				incRecord.setFromDir(results.getString(10));
				incRecord.setToSt(results.getString(11));
				incRecord.setToDir(results.getString(12));
				incRecord.setS_lat(results.getDouble(13));
				incRecord.setS_long(results.getDouble(14));
				incRecord.setE_lat(results.getDouble(15));
				incRecord.setE_long(results.getDouble(16));
				incRecord.setState(results.getString(17));
				incRecord.setCity(results.getString(18));
				incRecord.setCounty(results.getString(19));
				incRecord.setType(results.getString(20));
				incRecord.setStatus(results.getString(21));
				incRecord.setStart_time(results.getString(22));
				incRecord.setEnd_time(results.getString(23));
				incRecord.setUpdated_time(results.getString(24));
				incRecord.setCreation_time(results.getString(25));
				incRecord.setMapUrl(results.getString(26));
				incRecord.setITIS_code(results.getInt(27));
				incRecord.setDescription(results.getString(28));
			}
			if (incRecord != null) {
				LOGGER.debug("Incident: " + incRecord.toString());
				return incRecord;
			} else {
				LOGGER.debug("Query data size is 0. incId: " + incId);
			}			
		} catch (Exception e) {
			LOGGER.error("Exception occur when accessing db: " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
			if (preStat != null) {
				try {
					preStat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				preStat = null;
			}			
			if(conn != null) {
				connector.disconnect();
				conn = null;
			}
		}
		return null;
	}

}
