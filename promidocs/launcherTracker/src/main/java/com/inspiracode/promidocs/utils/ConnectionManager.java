package com.inspiracode.promidocs.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private String databaseName = "launcherTrackerDB";
	private String user = "admin";
	private String password = "password";

	private Connection conn;

	public Connection getConnection() throws SQLException,
			ClassNotFoundException {
		if (conn == null)
			Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:" + databaseName, user, password);
		return conn;
	}
}
