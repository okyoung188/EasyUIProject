package com.service.Impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dao.Impl.ReaderIdDaoImpl;

public class ReaderIdServiceImplTest {
	ReaderIdService test = new ReaderIdServiceImpl();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadReaderId() {
		if (test.loadReaderId() != null) {
			System.out.println("Success");
		} else {
			fail("Not yet implemented");
		}
	}

}
