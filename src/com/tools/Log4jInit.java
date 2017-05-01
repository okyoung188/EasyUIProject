package com.tools;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jInit extends HttpServlet {
    private Logger LOGGER = Logger.getLogger(this.getClass());
	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("Log4jInit init.");
		String realPath = config.getServletContext().getRealPath("/");// absolute path of context path
		String log4jPath = config.getInitParameter("log4j-init-file");
		System.out.println("File path: "+log4jPath);
		if (log4jPath != null) {
			System.out.println("----------start init------------------");  
            PropertyConfigurator.configure(realPath + log4jPath);  
            LOGGER.info("Log4jInit init success.");  
        } else {
            System.out.println("===========No init file path£¡==============");  
        }
		
	}



	
	
}
