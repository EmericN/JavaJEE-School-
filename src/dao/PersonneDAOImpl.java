package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Personne;

public class PersonneDAOImpl implements PersonneDAO {
    private DAOFactory daoFactory;
	private static final String SQL_SELECT_LOGIN_UNIQUE = "SELECT login FROM Personne WHERE login = ?";
	private static final String SQL_SELECT_PAR_LOGIN = "SELECT id, login, password, inscription_date FROM Personne WHERE login = ?";
    private static final String SQL_SELECT_ID_VILLE = "SELECT id FROM ville WHERE ville = ?";
    private static final String SQL_SELECT_VILLE = "SELECT ville FROM ville WHERE 1";
	private static final String SQL_INSERT = "INSERT INTO Personne (login, password, inscription_date) VALUES (?, ?, NOW())";
    private static final String SQL_INSERT_RELATION_VILLES_PERSONNE = "INSERT INTO relation_ville_personne (id_personne, id_ville) VALUES (?, ?)";
	private static final String SQL_CONNEXION_USER = "SELECT id, login, password FROM Personne WHERE login = ? && password = ?";
    private static final String SQL_CONNEXION_VILLE = "SELECT id FROM relation_ville_personne WHERE id_personne = ? && id_ville = ?";

	PersonneDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }
	
	public void create(Personne personne, String[] villes) throws DAOException {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet valeursAutoGenerees = null;

	    try {

	        connexion = daoFactory.getConnection();
	        preparedStatement = DAOutils.initialisationRequetePreparee( connexion, SQL_INSERT, true, personne.getLogin(), personne.getPassword() );

	        int statut = preparedStatement.executeUpdate();
	        // Analyse du statut retourné par la requête d'insertion 
	        if ( statut == 0 ) {
	            throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
	        }

	        // Récupération de l'id auto-généré par la requête d'insertion
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
	        if (valeursAutoGenerees.next()) {
	            personne.setId(valeursAutoGenerees.getLong(1));
	        } else {
	            throw new DAOException("Échec de la création de l'utilisateur en base, pas d'ID auto-généré retourné.");
	        }

            String[] idVille = getVilleId(villes);
            for (String villeId : idVille) {
                preparedStatement = DAOutils.initialisationRequetePreparee(connexion, SQL_INSERT_RELATION_VILLES_PERSONNE, false,valeursAutoGenerees.getLong(1) , villeId);
                int statut2 = preparedStatement.executeUpdate();

                if ( statut2 == 0 ) {
                    throw new DAOException( "Échec de la création de la relation ville / personne, aucune ligne ajoutée dans la table." );
                }
            }

	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        DAOutils.close( valeursAutoGenerees, preparedStatement, connexion );
	    }

	}

	@Override
	public List<String> loadVille() throws DAOException{
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
        List<String> ville = new ArrayList<>();

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = DAOutils.initialisationRequetePreparee(connexion, SQL_SELECT_VILLE, true );
            resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {

                ville.add(resultSet.getString("ville"));
                System.out.println("ville load : "+resultSet.getString("ville"));
            }

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ville;
	}

	public Personne getPersonne(String login) throws DAOException {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Personne personne = null;

	    try {
	        connexion = daoFactory.getConnection();
	        preparedStatement = DAOutils.initialisationRequetePreparee( connexion, SQL_SELECT_PAR_LOGIN, false, login);
	        resultSet = preparedStatement.executeQuery();
	        if ( resultSet.next() ) {
	        	personne = loadPersonneFromDb( resultSet );
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        DAOutils.close( resultSet, preparedStatement, connexion );
	    }

	    return personne;
	}

    private String[] getVilleId(String[] villes ) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String[] idVille = new String[villes.length];

        try {
            connexion = daoFactory.getConnection();
            for(int i=0; i<villes.length; i++) {
                preparedStatement = DAOutils.initialisationRequetePreparee(connexion, SQL_SELECT_ID_VILLE, false, villes[i]);

                resultSet = preparedStatement.executeQuery();
                    if ( resultSet.next() ) {
                        idVille[i] = resultSet.getString("id");
                    }

            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOutils.close( resultSet, preparedStatement, connexion );
        }

        return idVille;
    }

	private static Personne loadPersonneFromDb( ResultSet resultSet ) throws SQLException {
		Personne personne = new Personne();
		personne.setId( resultSet.getLong( "id" ) );
		personne.setLogin( resultSet.getString( "login" ) );
		personne.setPassword( resultSet.getString( "password" ) );
		personne.setInscriptionDate( resultSet.getTimestamp( "inscription_date" ) );
	    return personne;
	}
	
	public boolean isRegistered(String login, String password, String ville) throws DAOException {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet statut = null;
        ResultSet statut2 = null;
	    
	    try {
	        
	        connexion = daoFactory.getConnection();
	        System.out.println(connexion);

	        preparedStatement = DAOutils.initialisationRequetePreparee( connexion, SQL_CONNEXION_USER, false, login, password );
	        statut = preparedStatement.executeQuery();
	        // Analyse du statut retourné par la requête d'insertion

	        if ( statut.next() ) {
                String loginId = statut.getString(1);

                String[] villeToTab = new String[1];
                villeToTab[0]=ville;
                String[] villeId = getVilleId(villeToTab);

				preparedStatement = DAOutils.initialisationRequetePreparee( connexion, SQL_CONNEXION_VILLE, false, loginId, villeId[0] );
				statut2 = preparedStatement.executeQuery();

				if(statut2.next()){
				    return true;
                }
	        }
	       
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        DAOutils.close( statut, preparedStatement, connexion );
	    }
	    
	    return false;

	}

	@Override
	public boolean isLoginUnique(String login) {

		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Personne personne = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = DAOutils.initialisationRequetePreparee( connexion, SQL_SELECT_LOGIN_UNIQUE, false, login);
			resultSet = preparedStatement.executeQuery();
			if ( resultSet.next() ) {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}
}
