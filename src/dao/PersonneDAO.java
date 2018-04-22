package dao;

import beans.Personne;

public interface PersonneDAO {
	void create(Personne personne, String[] villes) throws DAOException;
	boolean isRegistered(String login, String password, String ville) throws DAOException;
    boolean isLoginUnique(String login);
    Personne getPersonne(String login) throws DAOException;
}
