package com.service.Impl;

import java.util.List;

import com.dao.Impl.ReaderIdDao;
import com.dao.Impl.ReaderIdDaoImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ReaderIdServiceImpl implements ReaderIdService {
	ReaderIdDao readerIdLoader = null;

	@Override
	public JSONArray loadReaderId() {
		List<String> readerIdList = null;
		readerIdLoader = new ReaderIdDaoImpl();
		readerIdList = readerIdLoader.loadReaderId();
		if (readerIdList != null && readerIdList.size() > 0) {
			JSONObject object = null;
			JSONArray array = new JSONArray();
			for (String readerId : readerIdList) {
				if (readerId != null && !readerId.trim().equals("")) {
					object = new JSONObject();
					object.put("text", readerId);
					object.put("value", readerId);
					array.add(object);
				}
			}
			if (array != null && array.size() > 0) {
				LOGGER.debug("ReaderIds json array size is: " + array.size());
				return array;
			} else {
				LOGGER.info("ReaderIds is null or size is 0.");
			}
		} else {
			LOGGER.info("ReaderIdList is null.");
		}
		return null;
	}
	
	

}
