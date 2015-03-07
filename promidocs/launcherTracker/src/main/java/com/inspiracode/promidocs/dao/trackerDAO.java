package com.inspiracode.promidocs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.inspiracode.promidocs.dto.Tracker;

public class trackerDAO {
	public boolean ErrorThrown = false;
	public String ErrorMessage = "";

	public List<Tracker> readAll(Connection conn) {
		ErrorThrown = false;
		List<Tracker> result = new ArrayList<Tracker>();

		String sQuery = "SELECT id, file_name, status, uploaded_on, source_location FROM TRACKER";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery);

			while (rs.next()) {
				Tracker tracker = new Tracker();
				tracker.setId(rs.getLong(1));
				tracker.setFileName(rs.getString(2));
				tracker.setStatus(rs.getString(3));
				tracker.setUploadedOn(rs.getDate(4));
				tracker.setSourceLocation(rs.getString(5));
				result.add(tracker);
			}
		} catch (SQLException e) {
			ErrorThrown = true;
			ErrorMessage = e.getMessage();
		} finally {
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				// If too many statments or resultsets stay opened, the app
				// will have to be restarted.
			}
		}
		return result;
	}

	public boolean update(long id, String status, Connection conn) {
		ErrorThrown = false;
		PreparedStatement st = null;
		try {
			st = conn
					.prepareStatement("UPDATE TRACKER SET status=? WHERE id = ?");
			st.setString(1, status);
			st.setLong(2, id);
			st.executeUpdate();
		} catch (SQLException e) {
			ErrorThrown = true;
			ErrorMessage = e.getMessage();
			return false;
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				// If too many statments or resultsets stay opened, the app
				// will have to be restarted.
			}
		}
		return true;
	}

	public boolean delete(long id, Connection conn) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM tracker WHERE id = ?");
			st.setLong(1, id);
			st.executeUpdate();
		} catch (SQLException e) {
			ErrorThrown = true;
			ErrorMessage = e.getMessage();
			return false;
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				// If too many statments or resultsets stay opened, the app
				// will have to be restarted.
			}
		}
		return true;
	}

	public Tracker create(Tracker tracker, Connection conn) {
		ErrorThrown = false;
		PreparedStatement st = null;
		String strSQL = "INSERT INTO TRACKER( "
				+ "file_name, status, uploaded_on, source_location) "
				+ "VALUES (?, ?, ?, ?)";
		try {
			st = conn.prepareStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, tracker.getFileName());
			st.setString(2, tracker.getStatus());
			st.setDate(3, tracker.getUploadedOn());
			st.setString(4, tracker.getSourceLocation());
			int affectedRows = st.executeUpdate();
			if (affectedRows == 0) {
				ErrorThrown = true;
				ErrorMessage = "Error when attempting to create Tracker's record.";
				return null;
			}
			st.getGeneratedKeys().next();
			tracker.setId(st.getGeneratedKeys().getLong(1));
		} catch (SQLException e) {
			ErrorThrown = true;
			ErrorMessage = e.getMessage();
			return null;
		}
		try {
			st.close();
		} catch (SQLException e) {
			// If too many statments or resultsets stay opened, the app will
			// have to be restarted.
		}
		return tracker;
	}
}
