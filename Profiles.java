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
public class Profiles extends AbstractDSpaceTransformer {

	/**
	 * Internationalization 110523
	 */

	// AbstractDSpaceTransformer d = new AbstractDSpaceTransformer();
	// static String curUrl = d.url;

	public String s = url;
	public static final Message T_dspace_home = message("xmlui.general.dspace_home");
	public static final Message T_title = message("Profiles");
	public static final Message T_trail = message("Profiles");
	public static final Message T_head = message("Bo Brinkman");
	public static final Message T_para = message("This page displays profiles");
	public static final String databaseConnection = "jdbc:postgresql://localhost:5432/profiles";
	public static final String databaseUsername = "postgres";
	public static final String databasePassword = "capstone";
	public static final String orcidLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/orcid-logo.png";
	public static final String academiaLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/academia-logo.png";
	public static final String googlePlusLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/google-plus-logo.png";
	public static final String linkedinLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/linkedin-logo.png";
	public static final String researchGateLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/researchgate-logo.png";
	public static final String twitterLoc = "http://172.17.31.180:8080/xmlui/themes/Mirage/images/twitter-logo.png";

	public Message test = message(s);
	public Message test2 = message("test2");
	public String str = "variable";
	public Message var = message(str);
	private static Logger log = Logger.getLogger(Profiles.class);

	/**
	 * Add a page title and trail links.
	 */	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException {
		// Set the page title

		// pageMeta.addMetadata("title").addContent("About Us");
		// 110523 modified page title with internationalization and added
		// breadcrumbs
		pageMeta.addMetadata("title").addContent(T_title);
		// add trail
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	/**
	 * Add some basic contents
	 */
	public void addBody(Body body) throws SAXException, WingException {

		// 110523 modified with internationalization
		Request request = ObjectModelHelper.getRequest(objectModel);
		String req = request.getPathInfo();
		String[] tok = req.split("/");
		Message newM = message(tok[2]);

		// sql test / Variables
		String test = "Test: ";
		String error = "No Errors!";
		String uniqueId = "", name = "", pictureURL = "", jobTitle = "", researchArea = "Research: ", address = "Address: ";
		String phone = "Phone: ", email = "Email: ", website = "Personal Website: ";
		String school = "", degreeAndAttended = "";
		String grantTitle = "Grant Title: ", grantLength = "Grant Length: ", grantNumber = "Grant Number: ";
		String orcid = "", academia = "", googlePlus = "", linkedin = "", researchGate = "", twitter = "";
		String organization = "", orgJobTitle = "", dateRange = "";
		
		try {
			Connection conn = null;
			
			Statement stmt = null;
			// might be just localhost:5432/dspace
			test += "attempting to connect ";
			conn = DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword);
			test += "connected ";
			stmt = conn.createStatement();
			test += "got statement ";
			String sql, sql1, sql2, sql3, sql4;
			sql = "SELECT * FROM faculty WHERE uniqueid = '" + newM + "'";
			ResultSet rs = stmt.executeQuery(sql);
			test += "got query ";

			while (rs.next()) {
				uniqueId += rs.getString("uniqueid");
				name += rs.getString("name");
				pictureURL += rs.getString("pictureurl");
				jobTitle += rs.getString("jobtitle");
				researchArea += rs.getString("research");
				address += rs.getString("address");
				phone += rs.getString("phone");
				email += rs.getString("email");
				website += rs.getString("website");
			}
			rs.close();
			
			sql1 = "SELECT * FROM bio WHERE uid = '" + newM + "'";
			ResultSet rs1 = stmt.executeQuery(sql1);
			while(rs1.next()) {
				uniqueId += rs.getString("uid");
				school += rs.getString("school");
				degreeAndAttended += rs.getString("degree");
				degreeAndAttended += ", ";
				degreeAndAttended += rs.getString("dateEarned");
			}
			rs1.close();
			
			sql2 = "SELECT * FROM funding WHERE uid = '" + newM + "'";
			ResultSet rs2 = stmt.executeQuery(sql2);
			while(rs2.next()) {
				uniqueId += rs.getString("uid");
				grantTitle += rs.getString("granttitle");
				grantLength += rs.getString("grantlength");
				grantNumber += rs.getString("grantnumber");
			}
			rs2.close();
			
			sql3 = "SELECT * FROM links WHERE uid = '" + newM + "'";
			ResultSet rs3 = stmt.executeQuery(sql3);
			while(rs3.next()) {
				uniqueId += rs.getString("uid");
				orcid += rs.getString("orcid");
				academia += rs.getString("academia");
				googlePlus += rs.getString("googleplus");
				linkedin += rs.getString("linkedin");
				researchGate += rs.getString("researchgate");
				twitter += rs.getString("twitter");
			}
			rs3.close();
			
			sql4 = "SELECT * FROM employment WHERE uid = '" + newM + "'";
			ResultSet rs4 = stmt.executeQuery(sql4);
			while(rs4.next()) {
				uniqueId += rs.getString("uid");
				organization += rs.getString("organization");
				orgJobTitle += rs.getString("jobtitle");
				dateRange += rs.getString("daterange");
			}
			rs4.close();
			
			stmt.close();
			conn.close();
			test += "closed";
		} catch (SQLException se) {
			error = se.getSQLState();
		}
		// end sql test

		Division division = body.addDivision("profile", "primary");
		division.setHead(name);

		// division.addPara(newM);

		// the divisions for the page
		Division page = division.addDivision("page");

		Division picture = division.addDivision("picture");
		picture.addParaFigure("", "", pictureURL, "", "");

		Division pinfo = page.addDivision("personalInformation");
		Division info1 = pinfo.addDivision("info1");
		info1.addPara(jobTitle);
		info1.addPara(researchArea);

		Division info2 = pinfo.addDivision("info2");
		info2.addPara(address);
		info2.addPara(phone);
		info2.addPara(email);
		info2.addPara(website);

		// pictures

		Division bios = page.addDivision("bios");
		Division education = bios.addDivision("education");
		Division employment = bios.addDivision("employment");
		Division funding = bios.addDivision("funding");

		education.setHead("Education");
		employment.setHead("Employment");
		funding.setHead("Funding");

		education.addPara(school);
		education.addPara(degreeAndAttended);

		employment.addPara(organization);
		employment.addPara(orgJobTitle);
		employment.addPara(dateRange);

		funding.addPara(grantTitle);
		funding.addPara(grantLength);
		funding.addPara(grantNumber);

		Division links = page.addDivision("links");
		// orcid
		links.addParaFigure("", "", orcidLoc, orcid, "");
		// adacemia.edu
		links.addParaFigure("", "", academiaLoc, academia, "");
		// google+
		links.addParaFigure("", "", googlePlusLoc, googlePlus, "");
		// linkedin
		links.addParaFigure("", "", linkedinLoc, linkedin, "");
		// researchgate
		links.addParaFigure("", "", researchGateLoc, researchGate, "");
		// twitter
		links.addParaFigure("", "", twitterLoc, twitter, "");

		Division testSql = page.addDivision("testSql");
		testSql.addPara(test);
		testSql.addPara(error);

		//Division testForm = page.addDivision("testForm");
		//List form = page.addList("form", List.TYPE_FORM);
		//form.addItem().addButton("submit?").setValue(T_changeToOpen);
	}
}
