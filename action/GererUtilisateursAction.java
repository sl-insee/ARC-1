package fr.insee.arc_composite.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.SessionAware;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fr.insee.arc_composite.web.model.ViewListProfils;
import fr.insee.config.InseeConfig;
import fr.insee.igesa.connection.ConnectionIgesa;
import fr.insee.igesa.exception.IgesaException;
import fr.insee.igesa.model.Application;
import fr.insee.igesa.model.Groupe;
import fr.insee.igesa.model.Personne;
import fr.insee.igesa.services.impl.IgesaSearch;
import fr.insee.igesa.services.impl.IgesaUpdate;
import fr.insee.siera.webutils.VObject;


@Component
@Results({ @Result(name = "success", location = "/jsp/gererUtilisateurs.jsp"), @Result(name = "index", location = "/jsp/index.jsp") })
public class GererUtilisateursAction implements SessionAware {

	private static final Logger logger = Logger.getLogger(GererUtilisateursAction.class);
	@Autowired
	@Qualifier("viewListProfils")
	private VObject viewListProfils;
	@Autowired
	@Qualifier("viewListUtilisateursDuProfil")
	private VObject viewListUtilisateursDuProfil;

	private String scope;
	private String f;
	private String g;

	protected String uri = InseeConfig.getConfig().getString("fr.insee.annuaire.arc.uri");
	protected String ident = InseeConfig.getConfig().getString("fr.insee.annuaire.arc.ident");
	protected String password = InseeConfig.getConfig().getString("fr.insee.annuaire.arc.password");
	protected ConnectionIgesa connectionLDAP = new ConnectionIgesa(uri, ident, password);
	// appli est le nom de la branche applicative dans l'annuaire. ce nom est
	// repris pour suffixer le nom des groupes applicatifs créés
	protected String appli = InseeConfig.getConfig().getString("fr.insee.annuaire.arc.ident").substring(ident.lastIndexOf("_") + 1, ident.length());

	@Override
	public void setSession(Map<String, Object> session) {
		this.viewListProfils.setMessage("");
		this.viewListUtilisateursDuProfil.setMessage("");

	}

	public String sessionSyncronize() {

		if (logger.isDebugEnabled()) {
			logger.debug("début sessionSyncronize()");
			logger.debug(ServletActionContext.getRequest().getUserPrincipal().toString().toUpperCase());
		}

		this.viewListProfils.setActivation(this.scope);
		this.viewListUtilisateursDuProfil.setActivation(this.scope);
		Boolean defaultWhenNoScope = true;

		if (this.viewListProfils.getIsScoped()) {
			initializeListProfils();
			defaultWhenNoScope = false;
		}
		if (this.viewListUtilisateursDuProfil.getIsScoped()) {
			initializeListUtilisateursDuProfil();
			defaultWhenNoScope = false;
		}

		if (defaultWhenNoScope) {
			System.out.println("default");
			initializeListProfils();
			this.viewListProfils.setIsActive(true);
			this.viewListProfils.setIsScoped(true);
		}

		if (f != null) {
			list(new File(f), 0);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("fin sessionSyncronize()");
			logger.debug(ServletActionContext.getRequest().getUserPrincipal().toString().toUpperCase());
		}

		return "success";
	}

	/**
	 * Liste des profils
	 */
	private void initializeListProfils() {
		if (logger.isDebugEnabled()) {
			logger.debug("/* initializeListProfils      début recherche de la liste des groupes de :" + appli+" */");
		}

		HashMap<String, String> defaultInputFields = new HashMap<String, String>();
		ArrayList<ArrayList<String>> listeDesGroupes = findGroupes();
		this.viewListProfils.initializeByList(listeDesGroupes, defaultInputFields);
		
		if (logger.isDebugEnabled()) {
			logger.debug("/* initializeListProfils        fin recherche de la liste des groupes de :" + appli+" */");
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		Set<String> roles = securityContext.getToken().getRealmAccess().getRoles();
		
		String message = "Keycloak [";
		message += "name="+ request.getUserPrincipal().toString().toUpperCase();
		message += ", roles="+ roles.toString();
		message += "]";
		this.viewListProfils.setMessage(message);
	}

	/**
	 * liste des utilisateurs pour un profil sélectionné
	 */

	public void initializeListUtilisateursDuProfil() {

		if (logger.isDebugEnabled()) {
			logger.debug("/* initializeListUtilisateursDuProfil  début recherche de la liste des utilisateurs du groupe sélectionné */");
		}

		HashMap<String, ArrayList<String>> selection = this.viewListProfils.mapContentSelected();

		if (!selection.isEmpty()) {
			String groupeSelected = selection.get("groupe").get(0);
			ArrayList<ArrayList<String>> listeDesUtilisateurs = findGroupe(groupeSelected);

			HashMap<String, String> defaultInputFields = new HashMap<String, String>();
			defaultInputFields.put("groupe", selection.get("groupe").get(0));

			this.viewListUtilisateursDuProfil.initializeByList(listeDesUtilisateurs, defaultInputFields);
		} else {
			this.viewListUtilisateursDuProfil.destroy();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("/* initializeListUtilisateursDuProfil  fin recherche de la liste des utilisateurs du groupe sélectionné */");
		}

	}

	/*
	 * @Action(value = "/selectListProfils") public String selectListProfils() {
	 * return sessionSyncronize(); }
	 */

	/**
	 * initialisation des vues "profils" et "utilisateurs"
	 *
	 * @return "success"
	 */

	@Action(value = "/selectGererUtilisateurs")
	public String selectGererUtilisateurs()
	{
		return sessionSyncronize();
	}

	/**
	 * ajout d'un profil
	 *
	 * @return "success"
	 */

	@Action(value = "/addProfil")
	public String addProfil() {

		if (logger.isDebugEnabled()) {
			logger.debug("/* addProfil " + this.viewListProfils.mapInputFields().get("groupe").get(0)+" */");
		}
		HashMap<String, ArrayList<String>> input = viewListProfils.mapInputFields();
		addGroupe(connectionLDAP, appli, input.get("groupe").get(0));

		return sessionSyncronize();
	}

	/**
	 * suppression d'un profil
	 *
	 * @return "success"
	 */

	@Action(value = "/deleteProfil")
	public String deleteProfil() {

		if (logger.isDebugEnabled()) {
			logger.debug("/* deleteProfil " + this.viewListProfils.mapContentSelected().get("groupe").get(0)+" */");
		}
		deleteGroupe(connectionLDAP, appli, this.viewListProfils.mapContentSelected().get("groupe").get(0));

		return sessionSyncronize();
	}

	/**
	 * tri de la vue "profils"
	 *
	 * @return "success"
	 */

	@Action(value = "/sortListProfils")
	public String sortListProfils() {
		this.viewListProfils.sort();
		return sessionSyncronize();
	}

	/**
	 * lister les utilisateurs d'un profil
	 *
	 * @return "success"
	 */

	@Action(value = "/selectTableUtilisateur")
	public String selectTableUtilisateur() {
		return sessionSyncronize();
	}

	/**
	 * ajout d'un utilisateur dans un profil
	 *
	 * @return "success"
	 */

	@Action(value = "/addTableUtilisateur")
	public String addTableUtilisateur() {
		HashMap<String, ArrayList<String>> input = viewListUtilisateursDuProfil.mapInputFields();

		int i = input.get("idep").size() - 1;
		if (logger.isDebugEnabled()) {
			logger.debug("addTableUtilisateur : " + input.get("groupe").get(i) + " " + input.get("idep").get(i).toUpperCase());

		}
		addUtilisateur(connectionLDAP, appli, input.get("groupe").get(i), input.get("idep").get(i).toUpperCase());
		return sessionSyncronize();
	}

	/**
	 * suppression d'un utilisateur dans un profil
	 *
	 * @return "success"
	 */

	@Action(value = "/deleteTableUtilisateur")
	public String deleteTableUtilisateur() {
		HashMap<String, ArrayList<String>> input = viewListUtilisateursDuProfil.mapContentSelected();

		for (int i = 0; i < input.get("groupe").size(); i++) {
			if (logger.isDebugEnabled()) {
				logger.debug("deleteTableUtilisateur : " + input.get("groupe").get(i) + " " + input.get("idep").get(i));
			}
			deleteUtilisateur(connectionLDAP, appli, input.get("groupe").get(i), input.get("idep").get(i));
		}
		return sessionSyncronize();
	}

	/**
	 * tri de la vue "profils"
	 *
	 * @return "success"
	 */

	@Action(value = "/sortTableUtilisateur")
	public String sortTableUtilisateur() {
		this.viewListUtilisateursDuProfil.sort();
		return sessionSyncronize();
	}

	/**
	 * sélectionner les utilisateurs d'une application(filtre) avec leurs
	 * attributs
	 *
	 * @filtre par ex.nom de l'application
	 */

	public void findListeUtilisateurs(ConnectionIgesa connection, String filtre) {
		if (logger.isDebugEnabled()) {
			logger.debug("findListeUtilisateurs d'une application :" + filtre);
		}
		IgesaSearch ldapSearch = new IgesaSearch(connection);
		try {
			List<Personne> reponses = ldapSearch.getUtilisateurByFiltre(filtre);
			Iterator<Personne> it = reponses.iterator();
			while (it.hasNext()) {
				Personne personne = (Personne) it.next();
				if (logger.isDebugEnabled()) {
					logger.debug("personne.getCn() : " + personne.getCn());
				}
				List<String> groupes = (List<String>) personne.getGroupes();
				Iterator<String> itg = groupes.iterator();
				while (itg.hasNext()) {
					String nomGroupe = (String) itg.next();
					if (logger.isDebugEnabled()) {
						logger.debug("nomGroupe.getDescription() : " + nomGroupe);
					}
				}
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}

	}

	/**
	 * sélectionner un utilisateur et ses attributs
	 *
	 * @ident idep de l'utilisateur recherché
	 */

	public void findUtilisateur(ConnectionIgesa connection, String ident) {
		if (logger.isDebugEnabled()) {
			logger.debug("findUtilisateur " + ident);
		}
		IgesaSearch ldapSearch = new IgesaSearch(connection);
		try {
			List<Personne> reponses = ldapSearch.getUtilisateurByIdep(ident);
			Iterator<Personne> it = reponses.iterator();
			while (it.hasNext()) {
				Personne personne = (Personne) it.next();
				if (logger.isDebugEnabled()) {
					logger.debug("findUtilisateur personne.getCn() : " + personne.getCn());
				}
				List<String> groupes = (List<String>) personne.getGroupes();
				Iterator<String> itg = groupes.iterator();
				while (itg.hasNext()) {
					String nomGroupe = (String) itg.next();
					if (logger.isDebugEnabled()) {
						logger.debug("findUtilisateur nomGroupe.getDescription() : " + nomGroupe);
					}
				}
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ajouter un utilisateur dans un groupe
	 *
	 * @appli nom de l'appli (sans préfixe appli_)
	 *
	 * @groupe nom réèl du groupe
	 *
	 * @ident idep de l'utilisateur à supprimer
	 */

	public void addUtilisateur(ConnectionIgesa connection, String appli, String groupe, String ident) {
		try {
			IgesaUpdate ldapUpdate = new IgesaUpdate(connection);
			String res = ldapUpdate.addPersonne(appli, groupe, ident);
			if (logger.isDebugEnabled()) {
				logger.debug("addUtilisateur : " + ident + "dans groupe " + groupe + " res : " + res);
			}

		} catch (IgesaException e) {
			e.printStackTrace();
		}
	}

	/**
	 * supprimer un utilisateur dans un groupe
	 *
	 * @appli nom de l'appli (sans préfixe appli_)
	 *
	 * @groupe nom du groupe (attention sera créé dans annuaire suffixé avec
	 * _<appli>)
	 *
	 * @ident idep de l'utilisateur à supprimer
	 */

	public void deleteUtilisateur(ConnectionIgesa connection, String appli, String groupe, String ident) {
		try {
			IgesaUpdate ldapUpdate = new IgesaUpdate(connection);
			String res = ldapUpdate.removePersonne(appli, groupe, ident);
			if (logger.isDebugEnabled()) {
				logger.debug("deleteUtilisateur : " + ident + " dans groupe " + groupe + " res : " + res);
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ajouter un groupe
	 *
	 * @appli nom de l'appli (sans préfixe appli_)
	 *
	 * @groupe nom du groupe (attention sera créé dans annuaire suffixé avec
	 * _<appli>)
	 */
	public void addGroupe(ConnectionIgesa connection, String appli, String groupe) {
		try {
			IgesaUpdate ldapUpdate = new IgesaUpdate(connection);
			String res = ldapUpdate.addGroupe(appli, groupe);
			if (logger.isDebugEnabled()) {
				logger.debug("addGroupe : " + groupe + " res : " + res);
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}
	}

	/**
	 * supprimer un groupe
	 *
	 * @appli nom de l'appli (sans préfixe appli_)
	 *
	 * @groupe nom réèl du groupe dans l'annuaire ex Maintenance_ARC
	 */
	public void deleteGroupe(ConnectionIgesa connection, String appli, String groupe) {
		try {
			IgesaUpdate ldapUpdate = new IgesaUpdate(connection);
			String res = ldapUpdate.removeGroupe(appli, groupe);
			if (logger.isDebugEnabled()) {
				logger.debug("deleteGroupe " + groupe + " res : " + res);
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Trouver tous les groupes pour l'appli
	 */
	public ArrayList<ArrayList<String>> findGroupes() {
		if (logger.isDebugEnabled()) {
			logger.debug("findGroupes");
		}
		ArrayList<ArrayList<String>> listeProfils = new ArrayList<ArrayList<String>>();
		// insert des entêtes
		ArrayList<String> user1 = new ArrayList<String>();
		user1.add("groupe");
		user1.add("lib_groupe");
		ArrayList<String> user2 = new ArrayList<String>();
		user2.add("text");
		user2.add("text");
		listeProfils.add(user1);
		listeProfils.add(user2);

		try {
			Iterator<Groupe> it = getApplication().getGroupes().iterator();
			while (it.hasNext()) {
				Groupe groupeTrouve = (Groupe) it.next();
				ArrayList<String> lGroupeTrouve = new ArrayList<String>();
				if (logger.isDebugEnabled()) {
					logger.debug("groupeTrouve.getDescription() : " + getDescription(groupeTrouve));
					logger.debug("groupeTrouve.getCn() : " + groupeTrouve.getCn());
				}
				if(groupeTrouve.getCn().substring(groupeTrouve.getCn().lastIndexOf("_") + 1, groupeTrouve.getCn().length()).compareTo(appli)==0) {
					lGroupeTrouve.add(groupeTrouve.getCn());
					lGroupeTrouve.add(getDescription(groupeTrouve));
					listeProfils.add(lGroupeTrouve);
				}
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}
		return listeProfils;
	}

	/**
	 * Trouver un groupe et les utilisateurs qui y sont déclarés
	 *
	 * @groupe nom réèl d'un groupe dans l'annuaire ex Maintenance_ARC
	 */
	public ArrayList<ArrayList<String>> findGroupe(String groupe) {
		if (logger.isDebugEnabled()) {
			logger.debug("findGroupe : " + groupe);
		}
		
		ArrayList<ArrayList<String>> listeProfils = new ArrayList<ArrayList<String>>();
		// insert des entêtes
		ArrayList<String> entetes = new ArrayList<String>();
		entetes.add("idep");
		entetes.add("nom_prenom");
		entetes.add("groupe");
		entetes.add("lib_groupe");
		ArrayList<String> formats = new ArrayList<String>();
		formats.add("text");
		formats.add("text");
		formats.add("text");
		formats.add("text");
		listeProfils.add(entetes);
		listeProfils.add(formats);

		try {
			List<Personne> listePersonnes = null;
			Iterator<Groupe> it = getApplication().getGroupes().iterator();
			while (it.hasNext()) {
				Groupe groupeTrouve = (Groupe) it.next();
				if(groupeTrouve.getCn().equals(groupe)) {
					listePersonnes = (List<Personne>) groupeTrouve.getPersonnes();
					Iterator<Personne> itp = listePersonnes.iterator();
					while (itp.hasNext()) {
						ArrayList<String> user = new ArrayList<String>();
						Personne personne = (Personne) itp.next();
						if (logger.isDebugEnabled()) {
							logger.debug("personne.getUid() : " + personne.getUid());
							logger.debug("personne.getCn() : " + personne.getCn());
						}
						user.add(personne.getUid());
						user.add(personne.getCn());
						user.add(groupeTrouve.getCn());
						user.add(getDescription(groupeTrouve));
						listeProfils.add(user);
					}
				}
			}
		} catch (IgesaException e) {
			e.printStackTrace();
		}
		return listeProfils;
	}

	/** Retourne la description du groupe en gérant le cas null.*/
	private String getDescription(Groupe groupe) {
		return groupe.getDescription() == null ? "" : groupe.getDescription();
	}

	private Application getApplication() throws IgesaException {
		IgesaSearch ldapSearch = new IgesaSearch(connectionLDAP);
		return ldapSearch.getApplicationByNom(appli).get(0);
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the viewListProfils
	 */
	public final VObject getViewListProfils() {
		return this.viewListProfils;
	}

	/**
	 * @param viewListProfils
	 *            the viewListProfils to set
	 */
	public final void setViewListProfils(ViewListProfils viewListProfils) {
		this.viewListProfils = viewListProfils;
	}

	/**
	 * @return the viewListUtilisateursDuProfil
	 */
	public VObject getViewListUtilisateursDuProfil() {
		return this.viewListUtilisateursDuProfil;
	}

	/**
	 * @param viewListUtilisateursDuProfil
	 *            the viewListUtilisateursDuProfil to set
	 */
	public void setViewListUtilisateursDuProfil(VObject viewListUtilisateursDuProfil) {
		this.viewListUtilisateursDuProfil = viewListUtilisateursDuProfil;
	}

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public void list(File file, int depth) {

		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				if (depth < 5) {
					list(child, depth + 1);
				}
			}
		}

		if ((file.isDirectory() && file.getAbsolutePath().contains(g)) || depth == 0) {
			System.out.println(file.getAbsolutePath());
		}

		if (depth == 0) {
			System.out.println(children);
		}

	}

}