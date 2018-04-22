package forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import beans.Personne;
import dao.DAOException;
import dao.PersonneDAO;

public class InscriptionForm {
	private static final String CHAMP_LOGIN               = "login";
    private static final String CHAMP_PASSWORD            = "password";
    private static final String CHAMP_CONFIRM_PASSWORD    = "confirm_password";
    private static final String CHAMP_VILLES              = "villes";


    private String resultat;
    private Map<String, String> erreurs = new HashMap<String, String>();
	private PersonneDAO personneDao;
	
	public InscriptionForm( PersonneDAO personneDao ) {
	    this.personneDao = personneDao;
	}
	
	public Map<String, String> getErreurs() {
        return erreurs;
    }

    public String getResultat() {
        return resultat;
    }
	
	public Personne inscrirePersonne( HttpServletRequest request ) {
	    String login = getValeurChamp( request, CHAMP_LOGIN );
	    String password = getValeurChamp( request, CHAMP_PASSWORD );
	    String confirm_password = getValeurChamp( request, CHAMP_CONFIRM_PASSWORD );
        String[] villes = getParamterValue(request, CHAMP_VILLES);

	    
	    Personne personne = new Personne();
	    try {
	        validerLogin( login, personne );
	        validerPassword( password, personne, confirm_password );

	        if ( erreurs.isEmpty() ) {
	            personneDao.create( personne, villes );
	            resultat = "Succès de l'inscription.";
	        } else {
	            resultat = "Echec de l'inscription.";
	        }
	    } catch ( DAOException e ) {
	        resultat = "Echec de l'inscription : une erreur imprévue est survenue, merci de réessayer dans quelques instants.";
	        e.printStackTrace();
	    }

	    return personne;
	}
	
	private void validerLogin( String login, Personne personne ) {
        try {
            validationLogin( login );
        } catch ( FormValidationException e ) {
            setErreur( CHAMP_LOGIN, e.getMessage() );
        }
        personne.setLogin( login );
    }
	
	private void validerPassword( String password, Personne personne, String confirm_password ) {
        try {
            validationPassword( password, confirm_password );
        } catch ( FormValidationException e ) {
            setErreur( CHAMP_PASSWORD, e.getMessage() );
        }
        personne.setPassword(password);
    }
	
	
	private void validationLogin( String login ) throws FormValidationException {
        if ( login != null && login.length() < 4 ) {
            throw new FormValidationException( "Le login doit contenir au moins 4 caractères." );
        }else{
           boolean isLoginUnique = personneDao.isLoginUnique(login);
           if(isLoginUnique){
               throw new FormValidationException( "L'utilisateur existe déjà." );
           }
        }
    }
	
	private void validationPassword( String password, String confirm_password ) throws FormValidationException {
        if ( password != null && password.length() < 6 ) {
            throw new FormValidationException( "Le mot de passe doit contenir au moins 6 caractères." );
        }
        else if (! password.equals(confirm_password) ) {
        	System.out.println(password + "\n" +confirm_password+ "\n");
        	throw new FormValidationException( "Le mot de passe est différent de sa confirmation." );
        	
        }
    }
	
	private void setErreur( String champ, String message ) {
        erreurs.put( champ, message );
        System.out.println("erreur champ : " + champ + " --> " + message);
    }
	
	private static String getValeurChamp( HttpServletRequest request, String nomChamp ) {
        String valeur = request.getParameter( nomChamp );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur;
        }
    }

    private static String[] getParamterValue( HttpServletRequest request, String nomChamp ) {
        String[] valeur = request.getParameterValues( nomChamp );
        if ( valeur == null || valeur.length == 0 ) {
            return null;
        } else {
            return valeur;
        }
    }
}
