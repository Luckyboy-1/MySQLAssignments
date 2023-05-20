package projects;



import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner (System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	// Operations menu options
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project" 
			);
	// @formatter:on

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ProjectsApp().processUserSelections(); // Create an instance of ProjectsApp and start processing user selections
	}
	private void processUserSelections() {
		// TODO Auto-generated method stub
		boolean done = false;
		while (!done) {
	        try {
	        	int selection = getUserSelection();  // Get the user's menu selection
	        	 switch (selection) {
	             case -1:
	                 done = exitMenu();
	                 break;
	             case 1:
	                 createProject(); //creates project
	                 break;
	             case 2:
	                 ListProjects(); //lists all projects
	                 break;
	             case 3:
	                 selectProject(); //choose project
	                 break;
	             case 4:
	            	 updateProjectDetails(); //update details
	                 break;
	             case 5:
	            	 deleteProject(); //delete a project
	            	 break;
	             default:
	                 System.out.println("\n" + selection + " is not a valid selection. Try again.");
	         }
	        }
	        catch (Exception e) {
	            System.out.println("Error: " + e.toString());
	        }
		}
	}
	private void deleteProject() {
		// TODO Auto-generated method stub
		listProjects(); 
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		projectService.deleteProject(projectId);
		 System.out.println("Project" + projectId + " was deleted successfully.");
		 if (curProject != null && curProject.getProjectId() == projectId) {
		        curProject = null; 
		    }
	}
	private void listProjects() {
		// TODO Auto-generated method stub
		
	}
	private void updateProjectDetails() {
		// TODO Auto-generated method stub
		 if (curProject == null) {
		        System.out.println("\nPlease select a project.");
		        return;
		    }
		 
		 	String projectName = getStringInput("Enter the project name [" + curProject.getProjectName()+ "]");
		    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours()+ "]");
		    BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours()+ "]");
		    Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty()+ "]");
		    String notes = getStringInput("Enter the project notes [" + curProject.getNotes()+ "]");
		    
		    Project project = new Project();
		    
		    //  set the updated values for the Project object
		    project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName(): projectName);
		    project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours(): estimatedHours);
		    project.setActualHours(Objects.isNull(actualHours)? curProject.getActualHours(): actualHours);
		    project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty(): difficulty);
		    project.setNotes(Objects.isNull(notes)? curProject.getNotes(): notes);
		    project.setProjectId(curProject.getProjectId());
		    
		    projectService.modifyProjectDetails(project);
		    curProject = projectService.fetchProjectById(curProject.getProjectId());
	}
	private void selectProject() {
		// TODO Auto-generated method stub
		ListProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		curProject = null;
		curProject = projectService.fetchProjectById(projectId);
	}
	private void ListProjects() {
		// TODO Auto-generated method stub
		 List<Project> projects = projectService.fetchAllProjects();
		    System.out.println("\nProjects:");
		    for (Project project : projects) {
		        System.out.println("  " + project.getProjectId() + ": " + project.getProjectName());
		    }
	}
	private void createProject() {
		// TODO Auto-generated method stub
		String projectName = getStringInput("Enter the project name");
	    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
	    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
	    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
	    String notes = getStringInput("Enter the project notes");

	    
	    //create a new Project object and set its properties based on the input values
	    Project project = new Project();
	    project.setProjectName(projectName);
	    project.setEstimatedHours(estimatedHours);
	    project.setActualHours(actualHours);
	    project.setDifficulty(difficulty);
	    project.setNotes(notes);

	    Project dbProject = projectService.addProject(project);
	    System.out.println("You have successfully created project: " + dbProject);
	}
	private BigDecimal getDecimalInput(String prompt) {
		// TODO Auto-generated method stub
		String input = getStringInput(prompt);
		 if (Objects.isNull(input)) {
		        return null;
		    }
		    try {
		        return new BigDecimal(input).setScale(2);
		    } catch (NumberFormatException ex) {
		        throw new DbException(input + " is not a valid decimal number.");
		    }
	}
	private boolean exitMenu() {
		// TODO Auto-generated method stub
		return false;
	}
	private int getUserSelection() {
		// TODO Auto-generated method stub
		printOperations();
	    Integer input = getIntInput("Enter a menu selection");
	    if (input == null) {
	        return -1;
	    }
	    return input;
	
	}
	
	//It displays a list of operations stored in the operations list and also provides information about the current project
	private void printOperations() {
		// TODO Auto-generated method stub
		 System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		    operations.forEach(line -> System.out.println("  " + line));
		    
		    if (curProject == null) {
		        System.out.println("\nYou are not working with a project.");
		    } else {
		        System.out.println("\nYou are working with project: " + curProject);
		    }
	}

	
	//This method prompts the user with the provided message (prompt) and reads an integer input from the user
	private Integer getIntInput(String prompt) {
		// TODO Auto-generated method stub
		 String input = getStringInput(prompt);
		 if (Objects.isNull(input)) {
		        return null;
		    }
		    try {
		        return Integer.valueOf(input);
		    } catch (NumberFormatException ex) {
		        throw new DbException(input + " is not a valid number. Try again.");
		    }
	}
	
	//This method prompts the user with the provided message (prompt) and reads a string input from the user
	private String getStringInput(String prompt) {
		// TODO Auto-generated method stub
		 System.out.print(prompt + ": ");
	        String input = scanner.nextLine();

	        if (input.trim().isBlank()) {
	            return null;
	        }
	        return input.trim();
	}
	
	
}
