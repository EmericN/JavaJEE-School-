package config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import dao.DAOConfigurationException;
import dao.DAOFactory;

public class DAOFactoryStarter implements ServletContextListener {
	private static final String ATT_DAO_FACTORY = "daofactory";
    private DAOFactory daoFactory;

	public void contextDestroyed(ServletContextEvent event) {		
	}

	public void contextInitialized(ServletContextEvent event) {
		/* Récupération du ServletContext lors du chargement de l'application */
        ServletContext servletContext = event.getServletContext();
        try {
			this.daoFactory = DAOFactory.getInstance();
		} 
        catch (DAOConfigurationException e) {
			e.printStackTrace();
		}
        servletContext.setAttribute( ATT_DAO_FACTORY, this.daoFactory );
        System.out.println("Factory : " + daoFactory) ;
	}

}