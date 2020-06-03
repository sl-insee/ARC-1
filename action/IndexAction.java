package fr.insee.arc_composite.web.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import fr.insee.siera.core.dao.UtilitaireDao;



@Component
@Results({
	   @Result(name="success", location="/jsp/index.jsp"),
	   @Result(name="status", type="stream", params= {"contentType","text/plain"})
	})
public class IndexAction extends ActionSupport{

	/**
	 * 
	 */
    private static final long serialVersionUID = -7090380894906158840L;
	private static final Logger logger = Logger.getLogger(IndexAction.class);
	/**
	 * Pour récupérer le choix de la norme
	**/
	

	@Action(value="/index")
	public String index(){
		return "success";
	}

	@Action(value="/accueil")
	public String accueil(){
		return "success";
	}

	private InputStream inputStream;
	
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Action(value="/status")
	public String status(){

		StringBuilder response=new StringBuilder();
		
		// test the database connection
		try {
			UtilitaireDao.get("arc", 1).executeRequest(null, "select true");
			
			response.append("0");
			response.append("\n");
			response.append("Configuration is OK");

		} catch (Exception e) {
			response.append("201");
			response.append("\n");
			response.append("Database connection failed");
		}

		
		this.inputStream = new ByteArrayInputStream(
				response.toString().getBytes(StandardCharsets.UTF_8));
		return "status";
	}
	
	
}
