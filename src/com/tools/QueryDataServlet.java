package com.tools;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.service.Impl.IncidentService;
import com.service.Impl.IncidentServiceImpl;
import com.service.Impl.ReaderIdService;
import com.service.Impl.ReaderIdServiceImpl;
import com.util.IncConRecord;

public class QueryDataServlet extends HttpServlet {
	private Logger LOGGER = Logger.getLogger(this.getClass());
	private ReaderIdService readerIdService = new ReaderIdServiceImpl();
	private IncidentService incidentSerivce = new IncidentServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		String queryStr = request.getQueryString();
		if (queryStr == null || "".equals(queryStr.trim())) {
			LOGGER.info("No query string.");
			return;			
		}
		// determine whether it is incident detail request
		String queryStrDecoded = URLDecoder.decode(queryStr);
		if (queryStrDecoded.matches("dataType=incidentDetail&id=\\d+")) {
			String incId = queryStr.replaceAll("dataType=incidentDetail&id=(\\d+)", "$1");
			if (incId.matches("\\d+")) {
				IncConRecord incident = incidentSerivce.queryIncident(incId);
				request.setAttribute("IncidentDetail", incident);
				request.getRequestDispatcher("/IncidentDetail.jsp").forward(request, response);
			} else {
				LOGGER.info("Querystr: " + queryStrDecoded);;
			}			
			return;
		} else {
			LOGGER.info("Query string: " + queryStrDecoded);
		}
		
		String[] queryStrs = queryStr.split("&");
		for (String query : queryStrs) {
			if (query != null && !query.trim().equals("")) {
				if (query.matches("dataType=readerId")) {
					JSONArray readerIds = loadReaderId(request, response);
					if (readerIds != null) {
						response.getWriter().print(readerIds.toString());
					} else {
						response.sendError(400);
					}
					return;
				}
			} else {
				LOGGER.info("Invalid param: " + query);
			}
		}
	}

	/**
	 * Query readerId
	 * @throws IOException 
	 */
	private JSONArray loadReaderId(HttpServletRequest requset, HttpServletResponse response) throws IOException {
		JSONArray readerIds = readerIdService.loadReaderId();
		if (readerIds != null) {
			return readerIds;			
		} else {
			LOGGER.info("ReaderIds is null.");
			return null;
		}	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		String queryStr = request.getQueryString();
		LOGGER.info("Query string: " + queryStr);
		if (queryStr == null || "".equals(queryStr.trim())) {
			LOGGER.info("No query string.");
			return;			
		}
		String[] queryStrs = queryStr.split("&");
		for (String query : queryStrs) {
			if (query != null && !query.trim().equals("")) {
				if (query.matches("dataType=incidents")) {
					JSONArray incidents = loadIncidents(request, response);
					if (incidents != null) {
						response.getWriter().print(incidents.toString());
					} else {
						response.sendError(400);
					}
					return;
				}
			} else {
				LOGGER.info("Invalid param: " + query);
			}
		}
	}

	/**
	 * Load incidents data
	 * @param request
	 * @param response
	 */
	private JSONArray loadIncidents(HttpServletRequest request, HttpServletResponse response) {
		String readerId = request.getParameter("readerId");
		if (readerId != null && !"".equals(readerId.trim())) {
			LOGGER.info("ReaderId : " + readerId);
			return incidentSerivce.loadIncidents(readerId);
		} else {
			LOGGER.info("ReaderIds is null.");
			return null;
		}
	}

}
