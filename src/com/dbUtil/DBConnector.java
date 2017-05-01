 package com.dbUtil;

import java.sql.*;
import org.apache.log4j.*;

/*******************************************************************************
 * Singleton used to manage connections to TC's DB.  The connection parameters
 * are meant to be set by the main class, and used by other base classes.  The
 * disconnect method is also meant to be called by the main class.  The base
 * classes will NOT close a connection.  The decision to close a connection to
 * the DB is left up to the main class.
 *
 * @author Ray Nicholus (12/16/2005)
 * @version 1.0
 *******************************************************************************/
public class DBConnector 
{
	/** Current version */
	public static final double VERSION = 1.0; //Please update manually

	/** Instance of this class */
	private static DBConnector instance;

	/** log4j instance */
	private final static Logger LOGGER = Logger.getLogger(DBConnector.class);

	/** Default JDBC DB driver */
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    /** Default DB driver */
    private static final String DB_DRIVER = "jdbc:mysql";

    /** DB hostname */
    private String dbHost;
    /** DB port # */
    private short dbPort;
    /** Reader ID of associated reader program */
    private String readerID;
    /** DB username */
    private String user;
    /** DB password */
    private String pwd;
    /** Connection to DB */
    private Connection con;


	/**
	 * Called only by getInstance(), and only if needed.
	 *
	 * @since 1.0
	 **/
	private DBConnector()
	{
		//TCIDB01 is the db server
		dbHost = "192.168.200.15";
		dbPort = 3306;
		user = "trafficcast";
		pwd = "naitou";
	}


	/**
	 * @since 1.0
	 * @return 1, and only 1 instance of this class, since this is a Singleton.
	 **/
	public static synchronized DBConnector getInstance()
	{
		if (instance == null)
			instance = new DBConnector();
		return instance;
	}


	/**
	 * Sets the hostname used when connecting to the DB.
	 *
	 * @since 1.0
	 * @param dbHost Hostname or IP address of the DB
	 **/
	public void setHost(String dbHost) { instance.dbHost = dbHost; }


	/**
	 * @since 1.0
	 * @return hostname or IP address of the DB
	 **/
	public String getHost() { return instance.dbHost; }


	/**
	 * Sets the port number used when connecting to the DB.
	 *
	 * @since 1.0
	 * @param dbPort Port number the DB listens on
	 **/

	public void setPort(short dbPort) { instance.dbPort = dbPort; }

	/**
	 * @since 1.0
	 * @return port # the DB is assumed to listen on
	 **/
	public short getPort() { return instance.dbPort; }


	/**
	 * Sets the reader ID of the associated reader program.
	 *
	 * @since 1.0
	 * @param readerID Reader ID of the associated reader program
	 **/
	public void setReaderID(String readerID) { instance.readerID = readerID; }

	/**
	 * @since 1.0
	 * @return reader ID of the associated reader
	 **/
	public String getReaderID() { return instance.readerID; }


	/**
	 * Sets the username used to connect to the DB.
	 *
	 * @since 1.0
	 * @param user Username used to connect to DB
	 **/
	public void setUser(String user) { instance.user = user; }

	/**
	 * @since 1.0
	 * @return username used to connect to the DB
	 **/
	public String getUser() { return instance.user; }


	/**
	 * Sets the password used to connect to the DB.
	 *
	 * @since 1.0
	 * @param pwd Password used to connect to the DB
	 **/
	public void setPwd(String pwd) { instance.pwd = pwd; }

	/**
	 * @return password used to connect to the DB
	 **/
	public String getPwd() { return instance.pwd; }



	/**
	 * Establishes a connection to the DB.  If a connection is already live, a
	 * new connection is NOT established.
	 *
	 * @since 1.0

	 * @return live connection to the DB
	 **/
	public Connection connect() throws Exception
	{
		if (con == null || con.isClosed())
		{
			//format path for connection to DB
			String path = DB_DRIVER + "://" + dbHost + ":" + dbPort + "/startdb?dontTrackOpenResources=true";

			Class.forName(JDBC_DRIVER).newInstance();

			instance.con = DriverManager.getConnection(path, user, pwd);
			LOGGER.debug("Established a connection to the DB.");
		}
		try { ((com.mysql.jdbc.Connection)con).ping(); }
		catch (Exception ex)
		{
			LOGGER.warn("Bad DB connection detected.  Reconnecting...");
			String path = DB_DRIVER + "://" + dbHost + ":" + dbPort + "/startdb?dontTrackOpenResources=true";
			//System.out.println(path);
			Class.forName(JDBC_DRIVER).newInstance();

			instance.con = DriverManager.getConnection(path, user, pwd);
			LOGGER.debug("Established a connection to the DB.");
		}

		return instance.con;
	}


	/**
	 * Terminates connection to the DB.
	 *
	 * @since 1.0
	 * @return <code>true</code> if the connection was closed, <code>false</code> otherwise
	 **/
	public boolean disconnect()
	{
		try
		{
			con.close();
			LOGGER.debug("Connection to DB has been closed.");
			return true;
		}
		catch (SQLException ex)
		{
			LOGGER.warn("Could not close connection to the DB (" + ex.getMessage() + ").", ex);
			return false;
		}
	}


	/**
	 * Determines whether a live connection to the DB exists.
	 *
	 * @since 1.0
	 * @return <code>true</code> if a live connection exists, <code>false</code> otherwise
	 **/
	public boolean isConnected()
	{
		try { if (con != null && !con.isClosed()) return true; }
		catch (Exception ex) {}
		return false;
	}


	/**
	 * @since 1.0
	 * @return all properties that make up this connection, as well as its status
	 **/
	public String toString()
	{
		String status;

		try { status = con != null && !con.isClosed() ? "connected" : "not connected"; }
		catch (SQLException ex) { status = "not connected"; }

		return "PATH: '" + DB_DRIVER + ":@" + dbHost + ":" + dbPort
		       + "'; USER: '" + user + "'; PWD: '" + pwd
		       + "'; STATUS: '" + status + "'.";
	}
}
