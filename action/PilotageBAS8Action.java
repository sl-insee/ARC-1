package fr.insee.arc_composite.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fr.insee.arc_composite.core.factory.ApiServiceFactory;
import fr.insee.arc_composite.core.model.TraitementEtat;
import fr.insee.arc_composite.core.model.TraitementPhase;
import fr.insee.arc_composite.core.service.ApiInitialisationService;
import fr.insee.arc_composite.core.service.ApiService;
import fr.insee.arc_composite.web.util.VObjectUtil;
import fr.insee.config.InseeConfig;
import fr.insee.siera.core.dao.UtilitaireDao;
import fr.insee.siera.core.format.Format;
import fr.insee.siera.core.structure.GenericBean;
import fr.insee.siera.core.util.FormatSQL;
import fr.insee.siera.core.util.ManipString;
import fr.insee.siera.webutils.LoggerDispatcher;
import fr.insee.siera.webutils.VObject;

@Component
@Results({ @Result(name = "success", location = "/jsp/gererPilotageBAS8.jsp"), @Result(name = "index", location = "/jsp/index.jsp") })
public class PilotageBAS8Action implements SessionAware {

	private String envExecution="arc_BAS8";
	
    @Override
    public void setSession(Map<String, Object> session) {
        this.viewEntrepotBAS8.setMessage("");
        this.viewPilotageBAS8.setMessage("");
        this.viewRapportBAS8.setMessage("");
        this.viewFichierBAS8.setMessage("");
        this.viewArchiveBAS8.setMessage("");
    }

    private static final Logger logger = Logger.getLogger(PilotageBAS8Action.class);

    @Autowired
    @Qualifier("viewPilotageBAS8")
    VObject viewPilotageBAS8;

    @Autowired
    @Qualifier("viewRapportBAS8")
    VObject viewRapportBAS8;

    @Autowired
    @Qualifier("viewFichierBAS8")
    VObject viewFichierBAS8;

    @Autowired
    @Qualifier("viewEntrepotBAS8")
    VObject viewEntrepotBAS8;

    @Autowired
    @Qualifier("viewArchiveBAS8")
    VObject viewArchiveBAS8;

    private String scope;

    private String repertoire = InseeConfig.getConfig().getString("fr.insee.arc.batch.parametre.repertoire");

    public String sessionSyncronize() {

        this.viewPilotageBAS8.setActivation(this.scope);
        this.viewRapportBAS8.setActivation(this.scope);
        this.viewFichierBAS8.setActivation(this.scope);
        this.viewEntrepotBAS8.setActivation(this.scope);
        this.viewArchiveBAS8.setActivation(this.scope);

        Boolean defaultWhenNoScope = true;

        if (this.viewPilotageBAS8.getIsScoped()) {
            initializePilotageBAS8();
            defaultWhenNoScope = false;
        }

        if (this.viewRapportBAS8.getIsScoped()) {
            initializeRapportBAS8();
            defaultWhenNoScope = false;
        }

        if (this.viewFichierBAS8.getIsScoped()) {
            initializeFichierBAS8();
            defaultWhenNoScope = false;
        }

        if (this.viewEntrepotBAS8.getIsScoped()) {
            initializeEntrepotBAS8();
            defaultWhenNoScope = false;
        }

        if (this.viewArchiveBAS8.getIsScoped()) {
            initializeArchiveBAS8();
            defaultWhenNoScope = false;
        }

        if (defaultWhenNoScope) {
            System.out.println("default");
            this.viewPilotageBAS8.setIsActive(true);
            this.viewPilotageBAS8.setIsScoped(true);

            this.viewRapportBAS8.setIsActive(true);
            this.viewRapportBAS8.setIsScoped(true);

            this.viewEntrepotBAS8.setIsActive(true);
            this.viewEntrepotBAS8.setIsScoped(true);

            initializePilotageBAS8();
            initializeRapportBAS8();
            initializeEntrepotBAS8();
        }

        return "success";

    }

    public void initializeEntrepotBAS8() {
        System.out.println("/* initializeEntrepotBAS8 */");
        HashMap<String, String> defaultInputFields = new HashMap<String, String>();
        StringBuilder requete = new StringBuilder();

        try {
	        if (UtilitaireDao.get("arc").hasResults(null, FormatSQL.tableExists("arc.ihm_entrepot"))) {
	            requete.append("select id_entrepot from arc.ihm_entrepot");
	        } else {
	            requete.append("select ''::text as id_entrepot");
	        }
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

        // this.viewEntrepotBAS8.setColumnRendering(ArcConstantVObjectGetter.columnRender.get(this.viewEntrepotBAS8.getSessionName()));
        this.viewEntrepotBAS8.initialize(requete.toString(), null, defaultInputFields);
    }

    // private SessionMap session;

    // visual des Pilotages du bac à sable
    public void initializePilotageBAS8() {
        VObjectUtil.initializeVObjectPilotage(viewPilotageBAS8, envExecution);
    }

    @Action(value = "/selectPilotageBAS8")
    public String selectPilotageBAS8() {
        return sessionSyncronize();
    }

    @Action(value = "/sortPilotageBAS8")
    public String sortPilotageBAS8() {
        this.viewPilotageBAS8.sort();
        return sessionSyncronize();

    }

    // visual des Pilotages du bac à sable
    public void initializeRapportBAS8() {
    	VObjectUtil.initializeVObjectRapport(viewRapportBAS8, envExecution);
    }

    @Action(value = "/selectRapportBAS8")
    public String selectRapportBAS8() {
        return sessionSyncronize();
    }

    @Action(value = "/sortRapportBAS8")
    public String sortRapportBAS8() {
        this.viewRapportBAS8.sort();
        return sessionSyncronize();

    }

    // Actions du bac à sable

    @Action(value = "/filesUploadBAS8")
    public String filesUploadBAS8() {

        System.out.println("/* filesUploadBAS8 : " + this.viewEntrepotBAS8.getCustomValues() + " */");

        if (this.viewEntrepotBAS8.getCustomValues() != null && !this.viewEntrepotBAS8.getCustomValues().get("entrepotEcriture").equals("")
                && this.viewPilotageBAS8.getFileUploadFileName() != null) {
            String repertoireUpload = this.repertoire + "ARC_BAS8" + File.separator + TraitementPhase.RECEPTION + "_"
                    + this.viewEntrepotBAS8.getCustomValues().get("entrepotEcriture");
            System.out.println(repertoireUpload);

            this.viewPilotageBAS8.upload(repertoireUpload);
        } else {
            String msg = "";
            if (this.viewPilotageBAS8.getFileUploadFileName() == null) {
                msg = "Erreur : aucun fichier selectionné\n";
                this.viewPilotageBAS8.setMessage("Erreur : aucun fichier selectionné.");
            }

            if (this.viewEntrepotBAS8.getCustomValues() == null || this.viewEntrepotBAS8.getCustomValues().get("entrepotEcriture").equals("")) {
                msg += "Erreur : aucun entrepot selectionné\n";
            }

            this.viewPilotageBAS8.setMessage(msg);
        }
        this.viewEntrepotBAS8.getCustomValues().put("entrepotEcriture", null);
        // Lancement de l'initialisation dans la foulée
        ApiServiceFactory.getService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.INITIALISATION.getNbLigneATraiter())).invokeApi();
        ApiServiceFactory.getService(TraitementPhase.RECEPTION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.RECEPTION.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();

    }

    /**
     * Initialisation de la vue sur la table contenant la liste des fichiers du répertoire d'archive
     */
    public void initializeArchiveBAS8() {
        System.out.println("/* initializeArchiveBAS8 */");
        if (this.viewEntrepotBAS8.getCustomValues().containsKey("entrepotLecture")
                && !this.viewEntrepotBAS8.getCustomValues().get("entrepotLecture").equals("")) {
            HashMap<String, String> defaultInputFields = new HashMap<String, String>();
            StringBuilder requete = new StringBuilder();

            requete.append("select * from "+ApiService.dbEnv(this.envExecution)+"pilotage_archive where entrepot='"
                    + this.viewEntrepotBAS8.getCustomValues().get("entrepotLecture") + "'");
            // this.viewArchiveBAS8.setColumnRendering(ArcConstantVObjectGetter.columnRender.get(this.viewArchiveBAS8.getSessionName()));
            this.viewArchiveBAS8.initialize(requete.toString(), null, defaultInputFields);
        } else {

            this.viewArchiveBAS8.destroy();
        }
    }

    /**
     * Fabrication d'une table temporaire avec comme contenu le nom des archives d'un entrepot donné puis Ouverture d'un VObject sur cette
     * table
     *
     * @return
     */
    @Action(value = "/visualiserEntrepotBAS8")
    public String visualiserEntrepotBAS8() {
        return sessionSyncronize();
   }

    /**
     * Téléchargement d'enveloppe contenu dans le dossier d'archive
     *
     * @return
     */
    @Action(value = "/downloadEnveloppeFromArchiveBAS8")
    public String downloadEnveloppeFromArchiveBAS8() {
        LoggerDispatcher.trace("*** Téléchargement des enveloppes à partir de l'archive ***", logger);
        // récupération de la liste des noms d'enloppe
        HashMap<String, ArrayList<String>> selection = this.viewArchiveBAS8.mapContentSelected();

        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct alias_de_table.nom_archive as nom_fichier from (" + this.viewArchiveBAS8.getMainQuery()
                + ") alias_de_table ");
        querySelection.append(this.viewArchiveBAS8.buildFilter(this.viewArchiveBAS8.getFilterFields(), this.viewArchiveBAS8.getHeadersDLabel()));

        if (!selection.isEmpty()) {
            querySelection.append(" AND nom_archive IN " + Format.sqlListe(selection.get("nom_archive")) + " ");
        }

        LoggerDispatcher.info("Ma requete pour récupérer la liste des enveloppes : " + querySelection.toString(), logger);

        ArrayList<String> listRepertoire = new ArrayList<>();
        GenericBean g;
        String entrepot = "";
        try {
            g = new GenericBean(UtilitaireDao.get("arc").executeRequest(null,
                    "SELECT DISTINCT entrepot FROM (" + this.viewArchiveBAS8.getMainQuery() + ") alias_de_table "));
            entrepot = g.mapContent().get("entrepot").get(0);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        listRepertoire.add(TraitementPhase.RECEPTION + "_" + entrepot + "_ARCHIVE");
        String chemin = this.repertoire + File.separator + "ARC_BAS8";
        this.viewArchiveBAS8.downloadEnveloppe(querySelection.toString(), chemin, listRepertoire);
        return "none";
    }

    @Action(value = "/startInitialisationBAS8")
    public String startInitialisationBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.INITIALISATION.getNbLigneATraiter())).invokeApi();
        ApiServiceFactory.getService(TraitementPhase.RECEPTION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.RECEPTION.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    @Action(value = "/startChargementBAS8")
    public String startChargementBAS8() {
        LoggerDispatcher.trace("startChargementBAS8", logger);
        ApiInitialisationService.synchroniserSchemaExecution(null, "arc.ihm", this.envExecution);

        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.CHARGEMENT.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.CHARGEMENT.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    @Action(value = "/startNormageBAS8")
    public String startNormageBAS8() {
        ApiInitialisationService.synchroniserSchemaExecution(null, "arc.ihm", this.envExecution);

        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.NORMAGE.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.NORMAGE.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    @Action(value = "/startControleBAS8")
    public String startControleBAS8() {
        ApiInitialisationService.synchroniserSchemaExecution(null, "arc.ihm", this.envExecution);

        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.CONTROLE.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.CONTROLE.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    @Action(value = "/startFiltrageBAS8")
    public String startFiltrageBAS8() {
        ApiInitialisationService.synchroniserSchemaExecution(null, "arc.ihm", this.envExecution);

        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.FILTRAGE.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.FILTRAGE.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    @Action(value = "/startMappingBAS8")
    public String startMappingBAS8() {
        ApiInitialisationService.synchroniserSchemaExecution(null, "arc.ihm", this.envExecution);

        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiServiceFactory.getService(TraitementPhase.MAPPING.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.MAPPING.getNbLigneATraiter())).invokeApi();
        return sessionSyncronize();
    }

    public String undoFilesSelection() {
    	String selectedSrc=null;
    	
    	HashMap<String,ArrayList<String>> m=viewFichierBAS8.mapContentSelected();
    	
    	if (m!=null && !m.isEmpty() && m.get("id_source")!=null)
    	{
    		for (int i=0;i<m.get("id_source").size();i++)
    		{
    			if (selectedSrc!=null)
    			{
    				selectedSrc+="\n UNION ALL SELECT ";
    			}
    			else
    			{
    				selectedSrc="SELECT ";
    			}
    			selectedSrc+="'"+m.get("id_source").get(i)+"'::text as id_source ";
    		}
    	}
    	return selectedSrc;
    }
    
    // Bouton undo
    @Action(value = "/undoChargementBAS8")
    public String undoChargementBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.CHARGEMENT, undoFilesSelection(),
                    new ArrayList<TraitementEtat>(Arrays.asList(TraitementEtat.OK, TraitementEtat.KO)));
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    @Action(value = "/undoNormageBAS8")
    public String undoNormageBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.NORMAGE, undoFilesSelection(),
                    new ArrayList<TraitementEtat>(Arrays.asList(TraitementEtat.OK, TraitementEtat.KO)));
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    @Action(value = "/undoControleBAS8")
    public String undoControleBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.CONTROLE, undoFilesSelection(),
                    new ArrayList<TraitementEtat>(Arrays.asList(TraitementEtat.OK, TraitementEtat.KO)));
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    @Action(value = "/undoFiltrageBAS8")
    public String undoFiltrageBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.FILTRAGE, undoFilesSelection(),
                    new ArrayList<TraitementEtat>(Arrays.asList(TraitementEtat.OK, TraitementEtat.KO)));
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    @Action(value = "/undoMappingBAS8")
    public String undoMappingBAS8() {
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.MAPPING, undoFilesSelection(),
                    new ArrayList<TraitementEtat>(Arrays.asList(TraitementEtat.OK, TraitementEtat.KO)));
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    @Action(value = "/resetBAS8")
    public String resetBAS8() {
        try {
            ApiInitialisationService.clearPilotageAndDirectories(this.repertoire, this.envExecution);
        } catch (Exception e) {
            e.printStackTrace();
            viewPilotageBAS8.setMessage("Problème : " + e.getMessage());
        }
        ApiInitialisationService service = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            service.resetEnvironnement();
        } finally {
            service.finaliser();
        }
        return sessionSyncronize();
    }

    // visual des Fichiers
    public void initializeFichierBAS8() {
        HashMap<String, ArrayList<String>> selectionLigne = this.viewPilotageBAS8.mapContentSelected();
        ArrayList<String> selectionColonne = this.viewPilotageBAS8.listHeadersSelected();

        HashMap<String, ArrayList<String>> selectionLigneRapport = this.viewRapportBAS8.mapContentSelected();

        if (!selectionLigne.isEmpty() && !selectionColonne.isEmpty()) {
            System.out.println("/* initializeFichierBAS8 */");

            HashMap<String, String> defaultInputFields = new HashMap<String, String>();

            String phase = selectionColonne.get(0).split("_")[0].toUpperCase();
            String etat = selectionColonne.get(0).split("_")[1].toUpperCase();

            StringBuilder requete = new StringBuilder();
            requete.append("select container, id_source,id_norme,validite,periodicite,phase_traitement,array_to_string(etat_traitement,'_') as etat_traitement ,date_traitement, rapport, round(taux_ko*100,2) as taux_ko, nb_enr, to_delete, jointure from "+ApiService.dbEnv(this.envExecution)+"pilotage_fichier ");
            requete.append("where date_entree" + ManipString.sqlEqual(selectionLigne.get("date_entree").get(0), "text"));
            requete.append(" and array_to_string(etat_traitement,'$')" + ManipString.sqlEqual(etat, "text"));
            requete.append(" and phase_traitement" + ManipString.sqlEqual(phase, "text"));

            // this.viewFichierBAS8.setColumnRendering(ArcConstantVObjectGetter.columnRender.get(this.viewFichierBAS8.getSessionName()));
            this.viewFichierBAS8.initialize(requete.toString(), null, defaultInputFields);
        } else if (!selectionLigneRapport.isEmpty()) {
            System.out.println("/* initializeFichierBAS8 */");

            HashMap<String, String> type = this.viewRapportBAS8.mapHeadersType();
            HashMap<String, String> defaultInputFields = new HashMap<String, String>();

            StringBuilder requete = new StringBuilder();
            requete.append("select container, id_source,id_norme,validite,periodicite,phase_traitement,array_to_string(etat_traitement,'_') as etat_traitement ,date_traitement, rapport, round(taux_ko*100,2) as taux_ko, nb_enr, to_delete, jointure from "+ApiService.dbEnv(this.envExecution)+"pilotage_fichier ");
            requete.append("where date_entree" + ManipString.sqlEqual(selectionLigneRapport.get("date_entree").get(0), "text"));
            requete.append(" and array_to_string(etat_traitement,'$')"
                    + ManipString.sqlEqual(selectionLigneRapport.get("etat_traitement").get(0), type.get("etat_traitement")));
            requete.append(" and phase_traitement"
                    + ManipString.sqlEqual(selectionLigneRapport.get("phase_traitement").get(0), type.get("phase_traitement")));
            requete.append(" and rapport" + ManipString.sqlEqual(selectionLigneRapport.get("rapport").get(0), type.get("rapport")));

            // this.viewFichierBAS8.setColumnRendering(ArcConstantVObjectGetter.columnRender.get(this.viewFichierBAS8.getSessionName()));
            this.viewFichierBAS8.initialize(requete.toString(), null, defaultInputFields);
        } else {
            this.viewFichierBAS8.destroy();
        }
    }

    @Action(value = "/selectFichierBAS8")
    public String selectFichierBAS8() {
        return sessionSyncronize();
    }

    @Action(value = "/sortFichierBAS8")
    public String sortFichierBAS8() {
        this.viewFichierBAS8.sort();
        return sessionSyncronize();

    }

    @Action(value = "/downloadFichierBAS8")
    public String downloadFichierBAS8() {
        LoggerDispatcher.trace("*** Téléchargement des fichiers ***", logger);
        // récupération de la liste des id_source

        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        StringBuilder querySelection = this.viewFichierBAS8.queryView();
        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers sélectionner
        //
        if (!selection.isEmpty()) {
            querySelection.append(" AND id_source IN " + Format.sqlListe(selection.get("id_source")) + " ");
        }

        // optimisation pour avoir des bloc successifs sur la même archive
        querySelection.append(" order by container ");

        // String repertoire= ServletActionContext.getServletContext().getRealPath("/");
        this.viewFichierBAS8.downloadXML(querySelection.toString(), this.repertoire, this.envExecution, TraitementPhase.RECEPTION.toString(),
                TraitementEtat.OK.toString(), TraitementEtat.KO.toString());

        LoggerDispatcher.trace("*** Fin du téléchargement des fichiers XML ***", logger);
        sessionSyncronize();
        return "none";
    }

    @Action(value = "/downloadBdBAS8")
    public String downloadBdBAS8() {

        HashMap<String, ArrayList<String>> selectionLigne = this.viewPilotageBAS8.mapContentSelected();
        ArrayList<String> selectionColonne = this.viewPilotageBAS8.listHeadersSelected();

        String phase = selectionColonne.get(0).split("_")[0].toUpperCase();
        String etat = selectionColonne.get(0).split("_")[1].toUpperCase();
        String date = selectionLigne.get("date_entree").get(0);

        String[] etatList = etat.split("\\$");
        String etatBdd = "{" + etat.replace("$", ",") + "}";

        // Sélection des table métiers en fonction de la phase sélectionner (5 pour mapping 1 sinon)
        ApiInitialisationService serv = new ApiInitialisationService();
        ArrayList<String> tableDownload = new ArrayList<>();
        try {
            GenericBean g = new GenericBean(UtilitaireDao.get("arc").executeRequest(null, serv.requeteListAllTablesEnv(this.envExecution)));
            if (!g.mapContent().isEmpty()) {
                ArrayList<String> envTables = g.mapContent().get("table_name");
                System.out.println("Le contenu de ma envTables : " + envTables);
                for (String table : envTables) {
                    // selection des tables qui contiennent la phase dans leur nom
                    if (table.toUpperCase().contains(phase.toUpperCase())) {
                        // ajout uniquement si pas déjà présent (pour éviter les doublons dû à table OK et KO
                        if (!tableDownload.contains(ManipString.substringBeforeLast(table, "_"))) {
                            tableDownload.add(ManipString.substringBeforeLast(table, "_"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Le contenu de ma tableDownload : " + tableDownload + "pour la phase : " + phase);

        String tableauRequete[] = new String[tableDownload.size() + 3];
        for (int k = 0; k < tableDownload.size(); k++) {
            // Début de la requete sur les données de la phase
            StringBuilder requete = new StringBuilder();
            requete.append("with prep as ( ");
            requete.append("select id_source, date_entree from "+ApiService.dbEnv(this.envExecution)+"pilotage_fichier ");
            requete.append("where phase_traitement='" + phase + "' ");
            requete.append("AND etat_traitement='" + etatBdd + "' ");
            requete.append("AND date_entree='" + date + "' ");

            // Si des fichiers ont été selectionnés, on ajoute a la requete la liste des fichiers
            if (!this.viewFichierBAS8.mapContentSelected().isEmpty()) {
                ArrayList<String> filesSelected = this.viewFichierBAS8.mapContentSelected().get("id_source");
                requete.append("AND id_source IN (");
                for (int i = 0; i < filesSelected.size(); i++) {
                    if (i > 0) {
                        requete.append(",");
                    }
                    requete.append("'" + filesSelected.get(i) + "'");
                }
                requete.append(")");
            }
            requete.append(" ) ");
            requete.append("select * from ( ");

            for (int i = 0; i < etatList.length; i++) {
                if (i > 0) {
                    requete.append("UNION ALL ");
                }
                requete.append("select * from " + tableDownload.get(k) + "_" + etatList[i]
                        + " a where exists (select 1 from prep b where a.id_source=b.id_source) ");
            }
            requete.append(") u ");
            System.out.println(requete);
            tableauRequete[k] = requete.toString();
            // fin de la requete pour les données d'une des tables
        }

        // Récupération des règles appliquées sur ces fichiers à télécharger
        StringBuilder requeteRegleC = new StringBuilder();
        requeteRegleC.append(recupRegle("CONTROLE", ApiService.dbEnv(this.envExecution)+"controle_regle"));
        tableauRequete[tableauRequete.length - 3] = requeteRegleC.toString();
        StringBuilder requeteRegleM = new StringBuilder();
        requeteRegleM.append(recupRegle("MAPPING", ApiService.dbEnv(this.envExecution)+"mapping_regle"));
        tableauRequete[tableauRequete.length - 2] = requeteRegleM.toString();
        StringBuilder requeteRegleF = new StringBuilder();
        requeteRegleF.append(recupRegle("FILTRAGE", ApiService.dbEnv(this.envExecution)+"filtrage_regle"));
        tableauRequete[tableauRequete.length - 1] = requeteRegleF.toString();
        // Pour donner des noms aux fichiers csv
        ArrayList<String> fileNames = new ArrayList<>();
        for (int k = 0; k < tableDownload.size(); k++) {
            fileNames.add("Data_" + ManipString.substringAfterFirst(tableDownload.get(k), "_") + "_" + date);
        }
        fileNames.add("Regles_controle");
        fileNames.add("Regles_mapping");
        fileNames.add("Regles_filtrage");
        this.viewFichierBAS8.download(fileNames, tableauRequete);

        return "none";

    }

    @Action(value = "/downloadEnveloppeBAS8")
    public String downloadEnveloppeBAS8() {
        LoggerDispatcher.trace("*** Téléchargement des enveloppes ***", logger);
        // récupération de la liste des noms d'enloppe
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();

        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct alias_de_table.container as nom_fichier from (" + this.viewFichierBAS8.getMainQuery()
                + ") alias_de_table ");
        querySelection.append(this.viewFichierBAS8.buildFilter(this.viewFichierBAS8.getFilterFields(), this.viewFichierBAS8.getHeadersDLabel()));

        if (!selection.isEmpty()) {
            querySelection.append(" AND container IN " + Format.sqlListe(selection.get("container")) + " ");
        }

        LoggerDispatcher.info("Ma requete pour récupérer la liste des enveloppes : " + querySelection.toString(), logger);

        ArrayList<String> listRepertoire = new ArrayList<>();
        listRepertoire.add(TraitementPhase.RECEPTION + "_" + TraitementEtat.OK);
        listRepertoire.add(TraitementPhase.RECEPTION + "_" + TraitementEtat.KO);
        String chemin = this.repertoire + File.separator + "ARC_BAS8";
        this.viewFichierBAS8.downloadEnveloppe(querySelection.toString(), chemin, listRepertoire);
        LoggerDispatcher.trace("*** Fin du téléchargement des enveloppes ***", logger);

        return "none";
    }

    /**
     * Marquage de fichier pour suppression lors de la prochaine initialisation
     *
     * @return
     */
    @Action(value = "/toDeleteBAS8")
    public String toDeleteBAS8() {
        LoggerDispatcher.trace("*** Marquage de fichier à supprimer ***", logger);
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        // System.out.println(selection);

        // Récupération de la sélection de l'utilisateur
        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct container, id_source from (" + this.viewFichierBAS8.getMainQuery() + ") alias_de_table ");
        querySelection.append(this.viewFichierBAS8.buildFilter(this.viewFichierBAS8.getFilterFields(), this.viewFichierBAS8.getHeadersDLabel()));
        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers sélectionné
        if (!selection.isEmpty()) {
            // concaténation des informations
            ArrayList<String> infoConcatenee = new ArrayList<>();
            ArrayList<String> listContainer = selection.get("container");
            ArrayList<String> listIdSource = selection.get("id_source");

            for (int i = 0; i < selection.get("id_source").size(); i++) {
                infoConcatenee.add(listContainer.get(i) + "+" + listIdSource.get(i));
            }
            querySelection.append(" AND container||'+'||id_source IN " + Format.sqlListe(infoConcatenee) + " ");
        }
        // LoggerDispatcher.info("Ma requete de selection : " + querySelection, logger);

        StringBuilder updateToDelete = requeteUpdateToDelete(querySelection, "'1'");
        String message;
        try {
            UtilitaireDao.get("arc").executeImmediate(null, updateToDelete);
            message = "Fichier(s) supprimé(s)";
        } catch (SQLException e) {
            LoggerDispatcher
                    .info("Problème lors de la mise à jour de to_delete dans la table pilotage_fichier, requete :  " + updateToDelete, logger);
            e.printStackTrace();
            message = "Problème lors de la suppression des fichiers";
        }

        // Attention bout de code spécifique aux bacs à sable, ne surtout pas copier en production
        LoggerDispatcher.info("Synchronisation de l'environnement  ", logger);
        ApiServiceFactory.getService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.INITIALISATION.getNbLigneATraiter())).invokeApi();
        
        // Fin du code spécifique aux bacs à sable
        this.viewPilotageBAS8.setMessage(message);

        return sessionSyncronize();
    }

    /**
     * Suppression du marquage de fichier pour suppression lors de la prochaine initialisation
     *
     * @return
     */
    @Action(value = "/undoActionBAS8")
    public String undoActionBAS8() {
        LoggerDispatcher.trace("*** Suppression du marquage de fichier à supprimer ***", logger);
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        // Récupération de la sélection de l'utilisateur
        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct container, id_source from (" + this.viewFichierBAS8.getMainQuery() + ") alias_de_table ");
        querySelection.append(this.viewFichierBAS8.buildFilter(this.viewFichierBAS8.getFilterFields(), this.viewFichierBAS8.getHeadersDLabel()));
        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers sélectionné
        if (!selection.isEmpty()) {
            // concaténation des informations
            ArrayList<String> infoConcatenee = new ArrayList<>();
            ArrayList<String> listContainer = selection.get("container");
            ArrayList<String> listIdSource = selection.get("id_source");

            for (int i = 0; i < selection.get("id_source").size(); i++) {
                infoConcatenee.add(listContainer.get(i) + "+" + listIdSource.get(i));
            }
            querySelection.append(" AND container||'+'||id_source IN " + Format.sqlListe(infoConcatenee) + " ");
        }
        // LoggerDispatcher.info("Ma requete de selection : " + querySelection, logger);

        StringBuilder updateToDelete = requeteUpdateToDelete(querySelection, "null");
        try {

            UtilitaireDao.get("arc").executeImmediate(null, updateToDelete);
        } catch (SQLException e) {
            LoggerDispatcher
                    .info("Problème lors de la mise à jour de to_delete dans la table pilotage_fichier, requete :  " + updateToDelete, logger);
            e.printStackTrace();
        }
        return sessionSyncronize();
    }

   
    /**
     * Marquage de fichier pour le rejouer lors de la prochaine initialisation
     *
     * @return
     */
    @Action(value = "/toRestoreBAS8")
    public String toRestoreBAS8() {
        LoggerDispatcher.trace("*** Marquage de fichier à rejouer ***", logger);
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        // System.out.println(selection);

        // Récupération de la sélection de l'utilisateur
        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct container, id_source from (" + this.viewFichierBAS8.getMainQuery() + ") alias_de_table ");
        querySelection.append(this.viewFichierBAS8.buildFilter(this.viewFichierBAS8.getFilterFields(), this.viewFichierBAS8.getHeadersDLabel()));
        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers sélectionnés
        if (!selection.isEmpty()) {
            // concaténation des informations
            ArrayList<String> infoConcatenee = new ArrayList<>();
            ArrayList<String> listContainer = selection.get("container");
            ArrayList<String> listIdSource = selection.get("id_source");

            for (int i = 0; i < selection.get("id_source").size(); i++) {
                infoConcatenee.add(listContainer.get(i) + "+" + listIdSource.get(i));
            }
            querySelection.append(" AND container||'+'||id_source IN " + Format.sqlListe(infoConcatenee) + " ");
        }
        // LoggerDispatcher.info("Ma requete de selection : " + querySelection, logger);

        StringBuilder updateToDelete = requeteUpdateToDelete(querySelection, "'R'");
        String message;
        try {

            UtilitaireDao.get("arc").executeImmediate(null, updateToDelete);
            message = "Fichier(s) à rejouer";
        } catch (SQLException e) {
            LoggerDispatcher
                    .info("Problème lors de la mise à jour de to_delete dans la table pilotage_fichier, requete :  " + updateToDelete, logger);
            e.printStackTrace();
            message = "Problème lors de la restauration des fichiers";
        }

        // Attention bout de code spécifique aux bacs à sable, ne surtout pas copier en production
        // Lancement de l'initialisation dans la foulée
        LoggerDispatcher.info("Synchronisation de l'environnement  ", logger);
        ApiServiceFactory.getService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.INITIALISATION.getNbLigneATraiter())).invokeApi();
        ApiServiceFactory.getService(TraitementPhase.RECEPTION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.RECEPTION.getNbLigneATraiter())).invokeApi();
        // Fin du code spécifique aux bacs à sable
        this.viewPilotageBAS8.setMessage(message);

        return sessionSyncronize();
    }
    
    /**
     * Marquage des archives à rejouer lors de la prochaine initialisation
     *
     * @return
     */
    @Action(value = "/toRestoreArchiveBAS8")
    public String toRestoreArchiveBAS8() {
        LoggerDispatcher.trace("*** Marquage de fichier à rejouer ***", logger);
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        // System.out.println(selection);

        // Récupération de la sélection de l'utilisateur
        StringBuilder querySelection = new StringBuilder();
        querySelection.append("select distinct container, id_source from (" + this.viewFichierBAS8.getMainQuery() + ") alias_de_table ");
        querySelection.append(this.viewFichierBAS8.buildFilter(this.viewFichierBAS8.getFilterFields(), this.viewFichierBAS8.getHeadersDLabel()));
        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers sélectionnés
        if (!selection.isEmpty()) {
            // concaténation des informations
            ArrayList<String> infoConcatenee = new ArrayList<>();
            ArrayList<String> listContainer = selection.get("container");
            ArrayList<String> listIdSource = selection.get("id_source");

            for (int i = 0; i < selection.get("id_source").size(); i++) {
                infoConcatenee.add(listContainer.get(i) + "+" + listIdSource.get(i));
            }
            querySelection.append(" AND container||'+'||id_source IN " + Format.sqlListe(infoConcatenee) + " ");
        }
        // LoggerDispatcher.info("Ma requete de selection : " + querySelection, logger);

        StringBuilder updateToDelete = requeteUpdateToDelete(querySelection, "'RA'");
        String message;
        try {

            UtilitaireDao.get("arc").executeImmediate(null, updateToDelete);
            message = "Archives(s) à rejouer";
        } catch (SQLException e) {
            LoggerDispatcher
                    .info("Problème lors de la mise à jour de to_delete dans la table pilotage_fichier, requete :  " + updateToDelete, logger);
            e.printStackTrace();
            message = "Problème lors de la restauration des fichiers";
        }

        // Attention bout de code spécifique aux bacs à sable, ne surtout pas copier en production
        // Lancement de l'initialisation dans la foulée
        LoggerDispatcher.info("Synchronisation de l'environnement  ", logger);
        ApiServiceFactory.getService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.INITIALISATION.getNbLigneATraiter())).invokeApi();
        ApiServiceFactory.getService(TraitementPhase.RECEPTION.toString(), "arc.ihm", this.envExecution, this.repertoire,
                String.valueOf(TraitementPhase.RECEPTION.getNbLigneATraiter())).invokeApi();
        // Fin du code spécifique aux bacs à sable
        this.viewPilotageBAS8.setMessage(message);

        return sessionSyncronize();
    }

    private StringBuilder requeteUpdateToDelete(StringBuilder querySelection, String valeur) {
        StringBuilder updateToDelete = new StringBuilder();
        updateToDelete.append("WITH ");
        updateToDelete.append("prep AS ( ");
        updateToDelete.append(querySelection);
        updateToDelete.append("         ) ");
        updateToDelete.append("UPDATE "+ApiService.dbEnv(this.envExecution)+"pilotage_fichier a ");
        updateToDelete.append("SET to_delete=" + valeur + " ");
        updateToDelete.append("WHERE EXISTS (SELECT 1 FROM prep WHERE a.container=prep.container AND a.id_source=prep.id_source); ");
        return updateToDelete;
    }

    /**
     * Méthode qui écrit la requete SQL permettant de récuperer les règles (controle, mapping ou filtrage) appliquées à un fichier
     *
     * @param phase
     *            , CONTROLE ou MAPPING (la phase de filtrage n'existe pas elle est dans MAPPING)
     * @param table
     * @return
     */
    private String recupRegle(String phase, String table) {
        StringBuilder requete = new StringBuilder();
        requete.append("WITH ");
        requete.append("prep as (SELECT DISTINCT id_norme, periodicite, validite_inf, validite_sup, version ");
        requete.append("    FROM "+ApiService.dbEnv(this.envExecution)+"pilotage_fichier ");
        requete.append("    WHERE phase_traitement in('" + phase + "') AND rapport is null ");
        if (!this.viewFichierBAS8.mapContentSelected().isEmpty()) {
            ArrayList<String> filesSelected = this.viewFichierBAS8.mapContentSelected().get("id_source");
            requete.append("AND id_source IN (");
            for (int i = 0; i < filesSelected.size(); i++) {
                if (i > 0) {
                    requete.append(",");
                }
                requete.append("'" + filesSelected.get(i) + "'");
            }
            requete.append(")");
        }
        requete.append("        ) ");// fin du WITH prep
        requete.append("SELECT a.* ");
        requete.append("FROM " + table + " a ");
        requete.append("    INNER JOIN prep  ");
        requete.append("        ON a.id_norme=prep.id_norme ");
        requete.append("        AND a.periodicite=prep.periodicite ");
        requete.append("        AND a.validite_inf=prep.validite_inf ");
        requete.append("        AND a.validite_sup=prep.validite_sup ");
        requete.append("        AND a.version=prep.version ");
        LoggerDispatcher.trace("La requete portant sur : " + table + ", " + requete.toString(), logger);
        return requete.toString();
    }

    /**
     * retour arriere d'une phase
     *
     * @return
     */
    @Action(value = "/resetPhaseBAS8")
    public String resetPhaseBAS8() {
        HashMap<String, ArrayList<String>> selection = this.viewFichierBAS8.mapContentSelected();
        StringBuilder querySelection = this.viewFichierBAS8.queryView();

        // si la selection de fichiers n'est pas vide, on se restreint aux fichiers choisis pour le retour arriere
        //
        if (!selection.isEmpty()) {
            querySelection.append(" AND id_source IN " + Format.sqlListe(selection.get("id_source")) + " ");
        }

        // On recupere la phase
        String phase = this.viewFichierBAS8.mapContent().get("phase_traitement").get(0);

        // Lancement du retour arrière
        // String repertoire=ServletActionContext.getServletContext().getRealPath("/");
        ApiInitialisationService serv = new ApiInitialisationService(TraitementPhase.INITIALISATION.toString(), "arc.ihm", this.envExecution,
                this.repertoire, TraitementPhase.INITIALISATION.getNbLigneATraiter());
        try {
            serv.retourPhasePrecedente(TraitementPhase.valueOf(phase), querySelection.toString(), null);
        } finally {
            serv.finaliser();
        }
        return sessionSyncronize();
    }

    public VObject getViewPilotageBAS8() {
        return this.viewPilotageBAS8;
    }

    public void setViewPilotageBAS8(VObject viewPilotageBAS8) {
        this.viewPilotageBAS8 = viewPilotageBAS8;
    }

    public VObject getViewRapportBAS8() {
        return this.viewRapportBAS8;
    }

    public void setViewRapportBAS8(VObject viewRapportBAS8) {
        this.viewRapportBAS8 = viewRapportBAS8;
    }

    public VObject getViewFichierBAS8() {
        return this.viewFichierBAS8;
    }

    public void setViewFichierBAS8(VObject viewFichierBAS8) {
        this.viewFichierBAS8 = viewFichierBAS8;
    }

    public VObject getViewEntrepotBAS8() {
        return this.viewEntrepotBAS8;
    }

    public void setViewEntrepotBAS8(VObject viewEntrepotBAS8) {
        this.viewEntrepotBAS8 = viewEntrepotBAS8;
    }

    public VObject getViewArchiveBAS8() {
        return this.viewArchiveBAS8;
    }

    public void setViewArchiveBAS8(VObject viewArchiveBAS8) {
        this.viewArchiveBAS8 = viewArchiveBAS8;
    }

    /**
     * Mise à jour de la console dans l'objet struts2
     */
    @Action(value = "/resetConsoleBAS8")
    public String resetConsoleBAS8() {
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        session.setAttribute("console", "");
        return sessionSyncronize();
    }

    @Action(value = "/updateConsoleBAS8")
    public String updateConsoleBAS8() {
        HttpSession session = ServletActionContext.getRequest().getSession(false);

        if (session.getAttribute("console") == null) {
            session.setAttribute("console", "");
        }

        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write((String) session.getAttribute("console"));
            out.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        session.setAttribute("console", "");

        return "none";

    }
    
    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}