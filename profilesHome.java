/*

 Profiles.java
 *
 * Basead on the code by Peter Dietz:
 * https://gist.github.com/842301#file_dspace_add_about.diff (acessed 11-05-23)
 *
 * Modified to work with internationalization (i18n locales) and breadcrumbs
 * by Andre Nito Assada e Josi Perez Alvarez on 11-05-23
 *
 * Modified again by Miami University capstone team A-3 to allow users to 
 * create and add profiles for scholars in DSpace.
 */

package org.dspace.app.xmlui.aspect.artifactbrowser;
//imports for person
import org.dspace.eperson.EPerson;
//imports for context path
import java.sql.*;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
//imports for servlet
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.*;
//imports for the form
import org.apache.cocoon.util.HashUtil;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.Radio;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.app.xmlui.wing.element.TextArea;
import org.dspace.content.Metadatum;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
//general imports
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Figure;
import org.dspace.app.xmlui.wing.element.Para;
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
import java.util.*;
/**
 * Profiles pages
 *
 * @author Capstone Team A-3
 */
public class ProfilesHome extends AbstractDSpaceTransformer {

	//private form variables

	//breadcrumb information
	public static final Message T_dspace_home = message("xmlui.general.dspace_home");
	public static final Message T_title = message("Profiles");
	public static final Message T_trail = message("Profiles");
	//database information
	public static final String databaseConnection = "jdbc:postgresql://localhost:5432/profiles";
	public static final String databaseUsername = "postgres";
	public static final String databasePassword = "capstone";

	private static Logger log = Logger.getLogger(ProfilesHome.class);

	//page data retrieved from database
	private String uniqueId = "", name = "", pictureURL = "";
	private String jobTitle = "", researchArea = "Research: ", address = "Address: ";
	private String phone = "Phone: ", email = "Email: ", website = "Personal Website: ";
	private String school = "", degreeAndAttended = "";
	private String grantTitle = "Grant Title: ", grantLength = "Grant Length: ", grantNumber = "Grant Number: ";
	private String orcid = "", academia = "", googlePlus = "", linkedin = "", researchGate = "", twitter = "";
	private String organization = "", orgJobTitle = "", dateRange = "";
	private Request request;
	/**
	 * Add a page title and trail links.
	 */	
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException {
		// Set the page title

		// breadcrumbs
		pageMeta.addMetadata("title").addContent("Scholar Profiles Homepage");
		// add trail
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}


	public boolean checkDB(String pageUID)
	{
		boolean containsUser = false;
		try {
			Connection conn = null;
			
			Statement stmt = null;
			conn = DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword);
			stmt = conn.createStatement();
			String sql, sql1, sql2, sql3, sql4, sql5;
			
			sql5 = "SELECT * FROM faculty WHERE uniqueid = '" + pageUID + "'";
			ResultSet rs5 = stmt.executeQuery(sql5);		
			String uniqueIdCheck = "";
			while(rs5.next()) {
				uniqueIdCheck += rs5.getString(1);
			}
			rs5.close();
			if(!uniqueIdCheck.isEmpty())
				containsUser = true;
			
			if(containsUser) {
				sql = "SELECT * FROM faculty WHERE uniqueid = '" + pageUID + "'";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					uniqueId = rs.getString("uniqueid");
					name = rs.getString("name");
					pictureURL = rs.getString("pictureurl");
					jobTitle = rs.getString("jobtitle");
					researchArea = "Research: " + rs.getString("research");
					address = "Address: "+rs.getString("address");
					phone = "Phone: "+rs.getString("phone");
					email = "Email: "+rs.getString("email");
					website = "Website: "+rs.getString("website");
				}
				rs.close();
			
				sql1 = "SELECT * FROM bio WHERE uid = '" + pageUID + "'";
				ResultSet rs1 = stmt.executeQuery(sql1);
				while(rs1.next()) {
					uniqueId = rs1.getString("uid");
					school = rs1.getString("school");
					degreeAndAttended = rs1.getString("degree");
					degreeAndAttended = ", ";
					degreeAndAttended = rs1.getString("dateEarned");
				}
				rs1.close();
			
				sql2 = "SELECT * FROM funding WHERE uid = '" + pageUID + "'";
				ResultSet rs2 = stmt.executeQuery(sql2);
				while(rs2.next()) {
					uniqueId = rs2.getString("uid");
					grantTitle = "Grant Title: "+rs2.getString("granttitle");
					grantLength = "Grant Length: "+rs2.getString("grantlength");
					grantNumber = "Grant Number: "+rs2.getString("grantnumber");
				}
				rs2.close();
			
				sql3 = "SELECT * FROM links WHERE uid = '" + pageUID + "'";
				ResultSet rs3 = stmt.executeQuery(sql3);
				while(rs3.next()) {
					uniqueId = rs3.getString("uid");
					orcid = rs3.getString("orcid");
					academia = rs3.getString("academia");
					googlePlus = rs3.getString("googleplus");
					linkedin = rs3.getString("linkedin");
					researchGate = rs3.getString("researchgate");
					twitter = rs3.getString("twitter");
				}
				rs3.close();
			
				sql4 = "SELECT * FROM employment WHERE uid = '" + pageUID + "'";
				ResultSet rs4 = stmt.executeQuery(sql4);
				while(rs4.next()) {
					uniqueId = rs4.getString("uid");
					organization = rs4.getString("organization");
					orgJobTitle = rs4.getString("jobtitle");
					dateRange = rs4.getString("daterange");
				}
				rs4.close();
			
				stmt.close();
				conn.close();
			}
		} catch (SQLException se) {
			return false;		
		}
		return containsUser;
	}
	public class LinkData{
		public String uid, name;
		public LinkData(String u, String n) {
			uid = u;
			name = n;
		}
	}
	public ArrayList<LinkData> generateTable(String linkLetter)
	{
		
		ArrayList<LinkData> ret = new ArrayList<LinkData>();
		
		try{
			Connection conn = null;
			Statement stmt = null;
			
			conn = DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword);
			stmt = conn.createStatement();
			
			String sql = "SELECT uniqueid, name FROM faculty";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				//put things into arrayList by uniqueid
				String uid = rs.getString("uniqueid");
				if(uid.toUpperCase().substring(0,1).equals(linkLetter)) {
					LinkData l = new LinkData(uid, rs.getString("name"));
					ret.add(l);
				}
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			ret = new ArrayList<LinkData>();
		}
		return ret;
	}
	public void addBody(Body body) throws SAXException, WingException {

		request = ObjectModelHelper.getRequest(objectModel);
		String req = null;
		String linkLetter = "A";
		try{
			req = request.getParameter("letter").toUpperCase();
			if(req.length() == 1)
				linkLetter = req;
		}catch(Exception e) {}
	
		Division division = body.addDivision("profileHome", "primary");

		// the divisions for the page
		Division page = division.addDivision("page");
		
		//page header container
		Division header = page.addDivision("header");
		header.addPara("Scholarly Commons Profiles Home Page");

		// create or edit profile container
		Division createEditLink = page.addDivision("createEditLink");
		EPerson loggedin = context.getCurrentUser();
		if(loggedin != null) {
			Para edit = createEditLink.addPara(null, "edit link");
			edit.addXref("/xmlui/scholarprofiles/profilemanager").addContent("Edit or Create Profile");
		}
		
		// scholar's container
		Division scholarsContainer = page.addDivision("scholarsContainer");
		Division scholarsHeader = scholarsContainer.addDivision("scholarsHeader");
		scholarsHeader.addPara("Use the links below to navigate to a scholar's profile");

		// the division that holds the links by last name (A, B, C...Z)
		Division scholarListLinks = scholarsContainer.addDivision("scholarListLinks");
		
		//Hash Table 
		ArrayList<LinkData> users = new ArrayList<LinkData>();
		users = generateTable(linkLetter);
	
		Division scholarList = scholarsContainer.addDivision("scholarList");
		//add links to page
		String link = "/xmlui/scholarprofiles/";
		for(LinkData usr : users) {
			Para scholarLink = scholarList.addPara(null, "page link");
			scholarLink.addXref(link + usr.uid).addContent(usr.name);
		}
	}
}
