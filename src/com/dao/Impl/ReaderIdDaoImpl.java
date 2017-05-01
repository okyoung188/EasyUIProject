package com.dao.Impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dbUtil.DBConnector;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class ReaderIdDaoImpl implements ReaderIdDao {

	@Override
	public List<String> loadReaderId() {
		Connection conn = null;
		Statement statmt = null;
		String sql = null;
		ResultSet result = null;
		List<String> readerList = null;
		DBConnector connector = DBConnector.getInstance();
		try {
			sql = "select distinct reader_id from reader_info";
			readerList = new ArrayList<String>();
			conn = (Connection) connector.connect();
			statmt = (Statement) conn.createStatement();
			result = statmt.executeQuery(sql);
		    while (result.next()) {
		    	readerList.add(result.getString(1));
		    }
			if (readerList.size() > 0) {
				LOGGER.info("Queried size: " + readerList.size());
				return readerList;
			} else {
				LOGGER.info("Didn't query any readerId.");
				return null;
			}
		} catch(Exception e) {
			LOGGER.error("Exception occur when accessing db: " + e.getMessage());
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				result = null;
			}
			if (statmt != null) {
				try {
					statmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				statmt = null;
			}			
			if(conn != null) {
				connector.disconnect();
				conn = null;
			}
		}
		return null;
	}

}
