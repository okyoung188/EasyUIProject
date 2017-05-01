package com.service.Impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public interface ReaderIdService {
	public Logger LOGGER = Logger.getLogger(ReaderIdService.class);

	public JSONArray loadReaderId();
}
