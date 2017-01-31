/*
 AboutPage.java
 *
 * Basead on the code by Peter Dietz:
 * https://gist.github.com/842301#file_dspace_add_about.diff (acessed 11-05-23)
 *
 * Modified to work with internationalization (i18n locales) and breadcrumbs
 * by Andre Nito Assada e Josi Perez Alvarez on 11-05-23
 */
 
package org.dspace.app.xmlui.aspect.artifactbrowser;
 
//JB 
import java.sql.*;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
//end
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Figure;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.authorize.AuthorizeException;
 
 
/**
 * Display about us page.
 *
 * @author Peter Dietz
 */
public class Profiles extends AbstractDSpaceTransformer
{
 
/**
 * Internationalization
 * 110523
 */
    
    //AbstractDSpaceTransformer d = new AbstractDSpaceTransformer();
    //static String curUrl = d.url;
    
    public String s = url;
    public static final Message T_dspace_home =
        message("xmlui.general.dspace_home");
    public static final Message T_title =
        message("Profiles");
    public static final Message T_trail =
        message("Profiles");
    public static final Message T_head =
        message("BO BRINKMAN");
    public static final Message T_para =
        message("This page displays profiles");
    
    public Message test = message(s);
    public Message test2 = message("test2");
    public String str = "variable";
    public Message var = message(str);
    private static Logger log = Logger.getLogger(Profiles.class);
 
        /**
        * Add a page title and trail links.
        */
        public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException {
            // Set the page title
 
            // pageMeta.addMetadata("title").addContent("About Us");
            // 110523 modified page title with internationalization and added breadcrumbs
            pageMeta.addMetadata("title").addContent(T_title);
            // add trail
            pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
            pageMeta.addTrail().addContent(T_trail);
        }
 
        /**
        * Add some basic contents
        */
        public void addBody(Body body) throws SAXException, WingException {
            
	    //sql test
	String test = "Test: ";
	String error = "No Errors!";
	try{
	    Connection conn = null;
	    
	    Statement stmt = null;
	    //might be just localhost:5432/dspace
	    test += "attempting to connect ";
	    conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/profiles", "postgres", "capstone");
	    test += "connected ";
            stmt = conn.createStatement();
	    test += "got statement ";
	    String sql;
	    sql = "SELECT * FROM faculty";
	    ResultSet rs = stmt.executeQuery(sql);
	    test += "got query ";

	    while(rs.next()){
	    	test += rs.getString("uniqueid");
	    }
	    rs.close();
	    stmt.close();
	    conn.close();
	    test += "closed";
	}catch(SQLException se) {	
		error = se.getSQLState();
	}
            //end sql test

	     //110523 modified with internationalization
	    Request request = ObjectModelHelper.getRequest(objectModel);
	    String req = request.getPathInfo();
	    String[] tok = req.split("/");
	    Message newM = message(tok[2]);

            Division division = body.addDivision("profile", "primary");
            division.setHead("Bo Brinkman");

	    //division.addPara(newM);

	    //the divisions for the page
	    Division page = division.addDivision("page");
	    
	    Division picture = division.addDivision("picture");
	    picture.addParaFigure("","","http://miamioh.edu/cec/_files/images/cse/faculty/brinkman-bo.jpg","","");

	    Division pinfo = page.addDivision("personalInformation");
	    Division info1 = pinfo.addDivision("info1");
	    info1.addPara("Associate Professor of Computer Science and Software Engineering, Miami University");
	    info1.addPara("Research: Augmented Reality, Computer Ethics, CS Education");
	    
	    Division info2 = pinfo.addDivision("info2");	
	    info2.addPara("Address: 205G Benton Hall, Oxford OH 45069");
	    info2.addPara("Phone: (513) 529-0354");
            info2.addPara("E-mail: bo.brinkman@miamioh.edu");
	    info2.addPara("Personal Website: bobrinkman.com");
		    
	    //pictures 

	    Division bios = page.addDivision("bios");
	    Division education = bios.addDivision("education");
	    Division employment = bios.addDivision("employment");
	    Division funding = bios.addDivision("funding");
	
	    education.setHead("Education");
	    employment.setHead("Employment");
	    funding.setHead("Funding");

	    education.addPara("Priceton University: Princeton, NJ, United States");
	    education.addPara("PhD - Computer Science\nAugust, 1999 - May, 2004");
	    
	    employment.addPara("Miami University: Oxford, OH, United States");
 	    employment.addPara("Associate Professor - Computer Science and Software Engineering");
	    employment.addPara("July, 2010 - Present");
	
	    funding.addPara("Electronics and Computing Service Scholars");
	    funding.addPara("Grant Length: June, 2014 - May, 2019");
	    funding.addPara("Grant Number: 1355513");

	    Division links = page.addDivision("links");
	    //orcid
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/orcid-logo.png","","");
	    //adacemia.edu
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/academia-logo.png","","");
	    //google+
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/google-plus-logo.png","https://plus.google.com/+BoBrinkman","");
	    //linkedin
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/linkedin-logo.png","https://www.linkedin.com/in/drbobrinkman","");
	    //researchgate
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/researchgate-logo.png","https://www.researchgate.net/profile/Bo_Brinkman","");
	    //twitter
	    links.addParaFigure("","","http://172.17.31.180:8080/xmlui/themes/Mirage/images/twitter-logo.png","https://twitter.com/drbobrinkman","");
	    
	    Division testSql = page.addDivision("testSql");
	    testSql.addPara(test);
	    testSql.addPara(error);
	
	    //Division testForm = page.addDivision("testForm");
	    //List form = page.addList("form", List.TYPE_FORM);
	    //form.addItem().addButton("submit?").setValue(T_changeToOpen);
	}
}
