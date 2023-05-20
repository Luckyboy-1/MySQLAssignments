package projects.service;

import java.math.BigDecimal;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.dao.DbConnection;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	public Project insertProject(Project project) {
		// TODO Auto-generated method stub
		// Insert a project into the database
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes)"
			+ "VALUES"
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		 try (Connection conn = DbConnection.getConnection()) {
			 startTransaction(conn);
			
			 try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				 setParameter(stmt, 1, project.getProjectName(), String.class);
		         setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
		         setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
		         setParameter(stmt, 4, project.getDifficulty(), Integer.class);
		         setParameter(stmt, 5, project.getNotes(), String.class);
		            
		            stmt.executeUpdate();
		            Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
		            commitTransaction(conn);
		            project.setProjectId(projectId);
		            return project;
				} catch (Exception e) {
				    rollbackTransaction(conn);
				    throw new DbException(e);
				}
			 
		    } catch (SQLException e) {
		        throw new DbException(e);
		    }
		
		
	}

	public List<Project> fetchAllProjects() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            try (ResultSet rs = stmt.executeQuery()) {
	            	 List<Project> projects = new LinkedList<>();
	                while (rs.next()) {
	                    projects.add(extract(rs, Project.class));
	                   
	                }
	                return projects;
	            }
	        } catch (Exception e) {
	        	rollbackTransaction(conn);
	            throw new DbException(e);
	        }
	    } catch (SQLException e) {
	        throw new DbException(e);
	    }
	    
		
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// TODO Auto-generated method stub
		   String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		   try (Connection conn = DbConnection.getConnection()) {
				startTransaction(conn);
				try {
		            Project project = null;
		            
		            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		                setParameter(stmt,1, projectId, Integer.class);
		            
		                try (ResultSet rs = stmt.executeQuery()) {
		                    if (rs.next()) {
		                        project = extract(rs, Project.class);
		                    }
		                }
		            }
		            
		            if (Objects.nonNull(project)) {
		            	project.getMaterials().addAll(fetchMaterialsForProject(projectId, conn));
		               
		            	project.getSteps().addAll(fetchStepsForProject(projectId, conn));
		                
		            	project.getCategories().addAll(fetchCategoriesForProject(projectId, conn));
		                
		            }
		            
		            commitTransaction(conn);
		            return Optional.ofNullable(project);
				} catch (Exception e) {
		        	rollbackTransaction(conn);
		            throw new DbException(e);
		        }
		    } catch (SQLException e) {
		        throw new DbException(e);
		    }
		
	}

	

	

	



	private Collection<? extends Category> fetchCategoriesForProject(Integer projectId, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		// @formatter:off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id)"
				+ "WHERE project_id = ?";
		// @formatter:on	
	    

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, projectId, Integer.class);
	        try (ResultSet rs = stmt.executeQuery()) {
	        	List<Category> categories = new LinkedList<>();
	            while (rs.next()) {
	                categories.add(extract(rs, Category.class));
	            }
	            return categories; 
	        }
	    }
	    
		
	}

	private Collection<? extends Step> fetchStepsForProject(Integer projectId, Connection conn) throws SQLException   {
		// TODO Auto-generated method stub
		 String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		    

		 try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        setParameter(stmt, 1, projectId, Integer.class);
		        try (ResultSet rs = stmt.executeQuery()) {
		        	List<Step> steps = new LinkedList<>();
		            while (rs.next()) {
		            	steps.add(extract(rs, Step.class));   ;
		            }
		            return steps;
		        }
		    }
		    
	}

	private Collection<? extends Material> fetchMaterialsForProject(Integer projectId, Connection conn)throws SQLException {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
	    

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, projectId, Integer.class);
	        try (ResultSet rs = stmt.executeQuery()) {
	        	List<Material> materials = new LinkedList<>();
	            while (rs.next()) {
	            	materials.add(extract(rs, Material.class)); 
	            }
	            return materials;
	        }
	    }
	    
	}

	public boolean modifyProjectDetails(Project project) {
		
		//SQL query updates the PROJECT_TABLE with the new project details based on the project ID.
		//A prepared statement is created with the SQL query, and the project details are set as parameters.
		//If an exception occurs during the process, the transaction is rolled back, and a DbException is thrown.
		// @formatter:off
		String sql = ""
			    + "UPDATE " + PROJECT_TABLE + " SET "
			    + "project_name = ?,"
			    + "estimated_hours = ?,"
			    + "actual_hours = ?,"
			    + "difficulty = ?,"
			    + "notes = ?"
			    + "WHERE project_id = ?";
		// @formatter:on
		 
		 try (Connection conn = DbConnection.getConnection()) {
				startTransaction(conn);   
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					 setParameter(stmt, 1, project.getProjectName(), String.class);
			         setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
			         setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
			         setParameter(stmt, 4, project.getDifficulty(), Integer.class);
			         setParameter(stmt, 5, project.getNotes(), String.class);
			         setParameter(stmt, 6, project.getProjectId(), Integer.class);
			         
			         boolean modified = stmt.executeUpdate() == 1;
			             commitTransaction(conn);
			        return modified;
				} catch (Exception e) {
		        	rollbackTransaction(conn);
		            throw new DbException(e);
		        }
		    } catch (SQLException e) {
		        throw new DbException(e);
		    }
				
	}

	public boolean deleteProject(Integer projectId) {
		// The SQL query deletes a row from the PROJECT_TABLE where the project ID matches the parameter.
		//If the deletion was successful (one row affected), the transaction is committed, and true is returned.
		//If an exception occurs during the process, the transaction is rolled back, and a DbException is thrown.
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        setParameter(stmt, 1, projectId, Integer.class);
		        
		        boolean deleted = stmt.executeUpdate() == 1;
		        commitTransaction(conn);
		        return deleted;
			}  catch (Exception e) {
	        	rollbackTransaction(conn);
	            throw new DbException(e);
	        }
	    } catch (SQLException e) {
	        throw new DbException(e);
	    }
		        
			
		
	}
}