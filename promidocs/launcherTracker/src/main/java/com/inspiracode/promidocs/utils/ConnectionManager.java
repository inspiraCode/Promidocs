package com.inspiracode.promidocs.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static String databaseName = "/launcherTrackerDB/launcherTrackerDB";
	private static String user = "admin";
	private static String password = "password";

	private static Connection conn;

	public static Connection getConnection() throws SQLException,
			ClassNotFoundException {
		if (conn == null)
			Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:" + databaseName, user, password);
		return conn;
	}
}
