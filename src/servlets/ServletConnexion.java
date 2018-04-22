package servlets;

import dao.DAOFactory;
import dao.PersonneDAO;
import forms.ConnexionForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletConnexion")
public class ServletConnexion extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public static final String ATT_DAO_FACTORY = "daofactory";
    public static final String ATT_PERSONNE = "personne";
    public static final String ATT_FORM = "form";
    public static final String VUE_CONNEXION = "/connexion.jsp";
    public static final String VUE_CONNECTED = "/connected.jsp";



    private PersonneDAO personneDao;

    public ServletConnexion() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConnexionForm form = new ConnexionForm( personneDao );
        boolean connected = form.connecterPersonne( request );

        if (connected){
            request.setAttribute( ATT_FORM, form );
            this.getServletContext().getRequestDispatcher( VUE_CONNECTED ).forward( request, response );
        }else{
            request.setAttribute( ATT_FORM, form );
            this.getServletContext().getRequestDispatcher( VUE_CONNEXION ).forward( request, response );
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher(VUE_CONNEXION).forward(request, response);
    }

    public void init() throws ServletException {
        // TODO Auto-generated method stub
        this.personneDao = ( (DAOFactory) getServletContext().getAttribute( ATT_DAO_FACTORY ) ).getPersonneDAO();
    }
}
