package dao;

import beans.Personne;

import java.util.List;

public interface PersonneDAO {
	void create(Personne personne, String[] villes) throws DAOException;
    List<String> loadVille();
	boolean isRegistered(String login, String password, String ville) throws DAOException;
    boolean isLoginUnique(String login);
    Personne getPersonne(String login) throws DAOException;


}
