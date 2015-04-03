import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.inspiracode.promidocs.dao.TrackerDAO;
import com.inspiracode.promidocs.dto.Tracker;
import com.inspiracode.promidocs.utils.ConnectionManager;


public class test {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Tracker oTracker = new Tracker();
		TrackerDAO oTrackerDAO = new TrackerDAO();
		
		//Create record***************************************************************************
		oTracker.setSourceLocation("a Sourceh Location");
		oTracker.setStatus("Status A");
		oTracker.setUploadedOn(new Date((new java.util.Date()).getTime()));
		
		Connection conn = ConnectionManager.getConnection();
		for(int i=0;i<10;i++){
			oTracker.setFileName("fileName" + i);
			oTrackerDAO.create(oTracker, conn);	
		}
		
		//Get status*****************************************************************************
		oTracker = oTrackerDAO.read("fileName3", conn);
		System.out.println(oTracker.getStatus());

		//Set status
		oTrackerDAO.update(oTracker.getId(), "StatusEdited", conn);
		
		//Delete
		/*for(int i=0;i<11;i++){
			oTrackerDAO.delete(i, conn);	
		}*/
		
		conn.close();
		System.out.println("End");
	}

}
