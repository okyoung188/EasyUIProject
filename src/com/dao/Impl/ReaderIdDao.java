package com.dao.Impl;

import java.util.List;

import org.apache.log4j.Logger;

public interface ReaderIdDao {
   public Logger LOGGER = Logger.getLogger(ReaderIdDao.class);
   public List<String> loadReaderId();
}
