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

/**
 * Profiles pages
 *
 * @author Capstone Team A-3
 */
public class Profiles extends AbstractDSpaceTransformer {

	//private form variables
	private static final Message F_head =
        	message("Create Your Profile");
        private static final Message F_para1 =
        	message("Test2");
        private static final Message F_jobTitle =
        	message("Job Title");
	private static final Message F_name =
	        message("Name");
	private static final Message F_research =
	        message("Research");
	private static final Message F_address =
	        message("Office");
	private static final Message F_phone =
	        message("Phone");
	private static final Message F_email =
	        message("Email");
	private static final Message F_website =
	        message("Website");
	private static final Message F_picurl =
	        message("Picture URL");
	private static final Message F_degree =
	        message("Degree");
	private static final Message F_earnedFrom =
	        message("Degree Earned From");
	private static final Message F_datesAttended =
	        message("Dates Attended");
	private static final Message F_grantTitle =
	        message("Grant Title");
	private static final Message F_grantLength =
	        message("Grant Length");
	private static final Message F_grantNumber =
	        message("Grant Number");
	private static final Message F_organization =
	        message("Organization");
	private static final Message F_orgJobTitle =
	        message("Job Title");
	private static final Message F_dateWorked =
	        message("Dates Worked");
	private static final Message F_orcidURL =
	        message("Orcid URL");
	private static final Message F_academiaURL =
	        message("Academia URL");
	private static final Message F_googleplusURL =
	        message("Google Plus URL");
	private static final Message F_linkedinURL =
	        message("LinkedIn URL");
	private static final Message F_researchgateURL =
	        message("ResearchGate URL");
	private static final Message F_twitterURL =
	        message("Twitter URL");
	private static final Message T_submit =
         	message("xmlui.ArtifactBrowser.ItemRequestForm.submit");
	//end formvariables

	private static final Message T_link = 
		message("Back to Home Page");
	private static final Message T_edit_link =
		message("Edit Profile");

	//breadcrumb information
	public static final Message T_dspace_home = message("xmlui.general.dspace_home");
	public static final Message T_title = message("Profiles");
	public static final Message T_trail = message("Profiles");
	//database information
	public static final String databaseConnection = "jdbc:postgresql://localhost:5432/profiles";
	public static final String databaseUsername = "postgres";
	public static final String databasePassword = "capstone";
	//image location information
	public static final String orcidLoc = "/xmlui/themes/Mirage/images/orcid-logo.png";
	public static final String academiaLoc = "/xmlui/themes/Mirage/images/academia-logo.png";
	public static final String googlePlusLoc = "/xmlui/themes/Mirage/images/google-plus-logo.png";
	public static final String linkedinLoc = "/xmlui/themes/Mirage/images/linkedin-logo.png";
	public static final String researchGateLoc = "/xmlui/themes/Mirage/images/researchgate-logo.png";
	public static final String twitterLoc = "/xmlui/themes/Mirage/images/twitter-logo.png";

	private static Logger log = Logger.getLogger(Profiles.class);

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
		pageMeta.addMetadata("title").addContent("Scholar Profiles");
		// add trail
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	/**
	 * creates Profile/Form page
	 */

	public boolean checkDB(String pageUID)
	{
		uniqueId = ""; name = ""; pictureURL = ""; jobTitle = ""; researchArea = "Research: "; 
		address = "Address: "; phone = "Phone: "; email = "Email: "; website = "Personal Website: ";
        	school = ""; degreeAndAttended = ""; grantTitle = "Grant Title: "; grantLength = "Grant Length: "; 
		grantNumber = "Grant Number: "; orcid = ""; academia = ""; googlePlus = ""; linkedin = ""; 
		researchGate = ""; twitter = ""; organization = ""; orgJobTitle = ""; dateRange = "";

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
					degreeAndAttended += ", ";
					degreeAndAttended += rs1.getString("dateEarned");
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

	public void createProfile(Division page) throws WingException
	{
		Division infoBar = page.addDivision("infoBar");
		
		Division picture = infoBar.addDivision("picture");
		picture.addParaFigure("", "", pictureURL, "", "");

		Division infoWithName = infoBar.addDivision("infoWithName");
		
		Division nameHeader = infoWithName.addDivision("nameHeader");
		nameHeader.addPara(name);
		
		Division personalInfo = infoWithName.addDivision("personalInfo");
		
		Division infoLeftContainer = personalInfo.addDivision("infoLeftContainer");
		infoLeftContainer.addPara(jobTitle);
		infoLeftContainer.addPara(researchArea);

		Division infoRightContainer = personalInfo.addDivision("infoRightContainer");
		infoRightContainer.addPara(address);
		infoRightContainer.addPara(phone);
		infoRightContainer.addPara(email);
		infoRightContainer.addPara(website);
		
		Division links = infoRightContainer.addDivision("links");
		// orcid/
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
		
		// Biography part of the profile module
		Division bios = page.addDivision("bios");
		
		Division academicContainer = bios.addDivision("academicContainer");
		Division employmentContainer = bios.addDivision("employmentContainer");
		Division grantsContainer = bios.addDivision("grantsContainer");
		
		// Set the headers of the bio containers
		Division academicHeader = academicContainer.addDivision("academicHeader");
		academicHeader.addPara("Education");
		Division employmentHeader = employmentContainer.addDivision("employmentHeader");
		employmentHeader.addPara("Employment");
		Division grantsHeader = grantsContainer.addDivision("grantsHeader");
		grantsHeader.addPara("Funding");
		
		Division educationContent = academicContainer.addDivision("educationContent");
		educationContent.addPara(school);
		educationContent.addPara(degreeAndAttended);
		
		Division employmentContent = employmentContainer.addDivision("employmentContent");
		employmentContent.addPara(organization);
		employmentContent.addPara(orgJobTitle);
		employmentContent.addPara(dateRange);
		
		Division fundingContent = grantsContainer.addDivision("fundingContent");
		fundingContent.addPara(grantTitle);
		fundingContent.addPara(grantLength);
		fundingContent.addPara(grantNumber);	
	}
               	
	public void addBody(Body body) throws SAXException, WingException {

		//parses the request to obtain user information
		request = ObjectModelHelper.getRequest(objectModel);
		String req = request.getPathInfo();
		String[] tok = req.split("/");
		String pageUID = tok[2];
	
		//create eperson
		EPerson loggedin = context.getCurrentUser();
		String eperson = null;
		if(loggedin != null)
			eperson = loggedin.getNetid();
		else
			eperson = "Not Logged In";

		Division division = body.addDivision("profile", "primary");

		// the divisions for the page
		Division page = division.addDivision("page");

		boolean containsUser = checkDB(pageUID);

		//if user is in database, build profile
		if (containsUser) {
			createProfile(page);
			if(eperson.equals(pageUID)) {
				String link = "/xmlui/scholarprofiles/profilemanager";
				Para editLink = page.addPara(null, "Edit Link");
				editLink.addXref(link).addContent(T_edit_link);
			}
		} else {
			page.addPara("This Profile Does Not Exist");
		}
		Division returnHomeLink = page.addDivision("returnHomeLink");	
		String homeLink = "/xmlui/scholarprofiles";
		Para editLink = returnHomeLink.addPara(null, "Home Link");
		editLink.addXref(homeLink).addContent("Return to Scholar Profiles Home");
	}
}
