package servlets;

import beans.Personne;
import dao.DAOFactory;
import dao.PersonneDAO;
import forms.InscriptionForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ServletInscription")
public class ServletInscription extends HttpServlet {

    static final long serialVersionUID = 1L;

    public static final String ATT_DAO_FACTORY = "daofactory";
    public static final String ATT_PERSONNE = "personne";
    public static final String ATT_FORM = "form";
    public static final String VUE = "/inscription.jsp";
    private List<String> ville;

    private PersonneDAO personneDao;

    public ServletInscription() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InscriptionForm form = new InscriptionForm( personneDao );
        Personne personne = form.inscrirePersonne( request );

        request.setAttribute("ville",ville);
        request.setAttribute( ATT_FORM, form );
        request.setAttribute( ATT_PERSONNE, personne);

        this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("ville",ville);
        this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
    }

    public void init() throws ServletException {
        // TODO Auto-generated method stub
        this.personneDao = ( (DAOFactory) getServletContext().getAttribute( ATT_DAO_FACTORY ) ).getPersonneDAO();
        ville = personneDao.loadVille();
    }
}
