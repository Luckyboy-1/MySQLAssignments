package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	
	public Project addProject(Project project) {
		// TODO Auto-generated method stub
		// Add a project to the database
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		// TODO Auto-generated method stub
		// Retrieve all projects from the database
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		// TODO Auto-generated method stub
		// Retrieve a project by its ID from the database
	 return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
		
	}

	public void modifyProjectDetails(Project project) {
		// TODO Auto-generated method stub
		// Modify the details of a project in the database
		if (!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}
	}

	public void deleteProject(Integer projectId) {
		// TODO Auto-generated method stub
		// Delete a project from the database
		if (!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	}

}
