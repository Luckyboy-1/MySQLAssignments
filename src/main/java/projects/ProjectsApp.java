package projects;

import java.sql.Connection;

import projects.dao.DbConnection;

public class ProjectsApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 DbConnection.getConnection();
	}

}
