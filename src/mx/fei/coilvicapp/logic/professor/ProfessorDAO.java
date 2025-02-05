package mx.fei.coilvicapp.logic.professor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.fei.coilvicapp.dataaccess.DatabaseManager;
import mx.fei.coilvicapp.logic.university.UniversityDAO;
import mx.fei.coilvicapp.logic.implementations.DAOException;
import mx.fei.coilvicapp.logic.implementations.Status;

public class ProfessorDAO implements IProfessor {
    
    private boolean checkEmailDuplication(Professor professor) throws DAOException {
        Professor professorAux;
        int idProfessor = 0;

        try {
            professorAux = getProfessorByEmail(professor.getEmail());
            idProfessor = professorAux.getIdProfessor();
        } catch (DAOException exception) {
            throw new DAOException("No fue posible realizar la validacion, intente registrar mas tarde", Status.ERROR);
        }
        if (idProfessor != professor.getIdProfessor() && idProfessor > 0) {
            throw new DAOException("El correo ya se encuentra registrado", Status.WARNING);
        }
        return false;
    }
    
    public int registerProfessor(Professor professor) throws DAOException {
        int result = 0;

        if (!checkEmailDuplication(professor)) {
            result = insertProfessor(professor);
        }
        return result;        
    }
    
    @Override
    public int insertProfessor(Professor professor) throws DAOException {
        int result = -1;
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "INSERT INTO profesor(nombre, apellidoPaterno, apellidoMaterno, correo, genero, telefono, idUniversidad) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?);";
        DatabaseManager databaseManager = new DatabaseManager();

        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareCall(statement);
            preparedStatement.setString(1, professor.getName());
            preparedStatement.setString(2, professor.getPaternalSurname());
            preparedStatement.setString(3, professor.getMaternalSurname());
            preparedStatement.setString(4, professor.getEmail());
            preparedStatement.setString(5, professor.getGender());
            preparedStatement.setString(6, professor.getPhoneNumber());
            preparedStatement.setInt(7, professor.getIdUniversity());
            preparedStatement.executeUpdate();         
            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible registrar al profesor", Status.ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
        return result;        
    }

    public int updateProfessorVerification(Professor newProfessorInformation) throws DAOException {   
        int result = 0;

        if (!checkEmailDuplication(newProfessorInformation)) {
            result = updateProfessor(newProfessorInformation);
        }
        return result;          
    }
    
    @Override
    public int updateProfessor(Professor newProfessorInformation) throws DAOException {   
        int result = -1;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "UPDATE profesor SET nombre = ?, apellidoPaterno = ?,"
                + " apellidoMaterno = ?, correo = ?, genero = ?, telefono = ? WHERE idProfesor = ?";
        DatabaseManager databaseManager = new DatabaseManager();
        
        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, newProfessorInformation.getName());
            preparedStatement.setString(2, newProfessorInformation.getPaternalSurname());
            preparedStatement.setString(3, newProfessorInformation.getMaternalSurname());
            preparedStatement.setString(4, newProfessorInformation.getEmail());
            preparedStatement.setString(5, newProfessorInformation.getGender());
            preparedStatement.setString(6, newProfessorInformation.getPhoneNumber());
            preparedStatement.setInt(7, newProfessorInformation.getIdProfessor());
            result = preparedStatement.executeUpdate();      
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible actualizar al profesor", Status.ERROR);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }               
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
        }        
        return result;
    }

    @Override
    public int deleteProfessorByID(int idProfessor) throws DAOException {     
        int result = -1;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "DELETE FROM profesor WHERE idProfesor = ?";
        DatabaseManager databaseManager = new DatabaseManager();
        
        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, idProfessor);
            result = preparedStatement.executeUpdate();      
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible eliminar al profesor", Status.ERROR);
        } finally {            
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }               
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
        }       
        return result;
    }

    @Override
    public Professor getProfessorById(int idProfessor) throws DAOException {        
        Professor professor = new Professor();
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String statement = "SELECT * FROM profesor WHERE idProfesor = ?";

        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, idProfessor);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                professor = initializeProfessor(resultSet);
            }
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible obtener al profesor", Status.ERROR);
        } finally {         
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }               
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
        return professor;          
    }
    
    @Override
    public Professor getProfessorByEmail(String professorEmail) throws DAOException {
        Professor professor = new Professor();
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String statement = "SELECT * FROM profesor WHERE correo = ?";

        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, professorEmail);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                professor = initializeProfessor(resultSet);
            }
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible obtener al profesor", Status.ERROR);
        } finally {         
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }               
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
        return professor;          
    }
    
    @Override
    public ArrayList<Professor> getAllProfessors() throws DAOException {
        ArrayList<Professor> professors = new ArrayList<>();
        Professor professor;
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String statement = "SELECT * FROM profesor";

        try {
            connection = databaseManager.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                professor = initializeProfessor(resultSet);
                professors.add(professor);
            }
        } catch (SQLException exception) {
            Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            throw new DAOException("No fue posible recuperar a los profesores", Status.ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException exception) {
                Logger.getLogger(ProfessorDAO.class.getName()).log(Level.SEVERE, null, exception);
            }
            databaseManager.closeConnection();
        }
        return professors;
    }
    
    private Professor initializeProfessor(ResultSet resultSet) throws SQLException {        
        Professor professor = new Professor();
        UniversityDAO universityDAO = new UniversityDAO();
        
        professor.setIdProfessor(resultSet.getInt("idProfesor"));
        professor.setName(resultSet.getString("nombre"));
        professor.setPaternalSurname(resultSet.getString("apellidoPaterno"));
        professor.setMaternalSurname(resultSet.getString("apellidoMaterno"));
        professor.setEmail(resultSet.getString("correo"));
        professor.setGender(resultSet.getString("genero"));
        professor.setPhoneNumber(resultSet.getString("telefono"));
        professor.setState(resultSet.getString("estado"));
        int idUniversity = resultSet.getInt("IdUniversidad");
        try {
            professor.setUniversity(universityDAO.getUniversityById(idUniversity));
        } catch (DAOException exception) {
            Logger.getLogger(UniversityDAO.class.getName()).log(Level.SEVERE, null, exception);
        }
        return professor;  
    }
       
}
