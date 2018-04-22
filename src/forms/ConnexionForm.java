package forms;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import beans.Personne;
import dao.DAOException;
import dao.PersonneDAO;

public class ConnexionForm {

	private static final String CHAMP_LOGIN      = "login";
    private static final String CHAMP_PASSWORD   = "password";
    private static final String CHAMP_VILLE      = "villes";
    private String resultat;
    private Map<String, String> erreurs = new HashMap<String, String>();
	private PersonneDAO personneDao;
	
	public ConnexionForm( PersonneDAO personneDao ) {
	    this.personneDao = personneDao;
	}
	
	public Map<String, String> getErreurs() {
        return erreurs;
    }

    public String getResultat() {
        return resultat;
    }
	
	public boolean connecterPersonne( HttpServletRequest request ) {
	    String login = getValeurChamp( request, CHAMP_LOGIN );
	    String password = getValeurChamp( request, CHAMP_PASSWORD );
        String ville = getValeurChamp( request, CHAMP_VILLE );

	    boolean connected = personneDao.isRegistered(login, password, ville);
	    
	    if (!connected){
	    	setErreur(CHAMP_LOGIN, "Echec de l'authentification, vérifier vos identifiants et la ville");
        }
        return connected;
	}

	private void setErreur( String champ, String message ) {
        erreurs.put( champ, message );
    }
	
	public static String getValeurChamp( HttpServletRequest request, String nomChamp ) {
        String valeur = request.getParameter( nomChamp );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur;
        }
    }
}
