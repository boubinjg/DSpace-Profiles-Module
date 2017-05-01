//Function to create the links to filter by last name
//on the Scholarprofiles main page
function letterLinks() {
  var letterDiv = document.getElementById('aspect_artifactbrowser_ProfilesHome_div_links');

  var letter;
  var html = "";
  //Make HTML for links using ASCII codes, adding a space between each
  for(i = 0; i < 26; i++) {
    letter = String.fromCharCode(65 + i);
    html += "<a href='scholarprofiles?letter=" + letter + "'>" + letter + "</a>"; 
    html += "&nbsp";
  }
  //Add the HTML to the div, if it exists on the page
  if(letterDiv != null){
    letterDiv.innerHTML = html;
  }
}
//Function to get the publications an individual has, if any, and 
//display it on their profile page.
//DSpace makes ID's on divs rather than elements within them,
//so there was some workarounds necesary to grab the P element
function getPublications() {
  var nameDiv= document.getElementById("aspect_artifactbrowser_Profiles_div_nameHeader");
  //Get the paragraph with name of profile
  var namePara = nameDiv.getElementsByTagName("p");
  var name = namePara[0].innerHTML;
  //Separate name into first and last name
  var splitName = name.split(' ');
  var fn = splitName[0];
  var ln = splitName[1];
  var getHTML = function ( url, callback ) {

    // Create new request
    var xhr = new XMLHttpRequest();

    // Setup callback
    xhr.onload = function() {
        if ( callback && typeof( callback ) === 'function' ) {
            callback( this.responseXML );
        }
    };

    // Send request
    xhr.open( 'GET', url );
    xhr.responseType = 'document';
    xhr.send();
  };
  //Request searches by first and last name fonud on profile
  getHTML( '/xmlui/browse?value='+ln+',+'+fn+'&type=author', function (response) { 
    //Get div with publications list for requested author
    var pubs = response.getElementById('aspect_artifactbrowser_ConfigurableBrowse_div_browse-by-author-results');
    //If the author doesn't have any publications, display nothing
    if(pubs.innerHTML.includes('Sorry, there are no results for this browse.')){
        pubs.innerHTML = "";
    }
    //If they do have publications, display them on profile page
document.getElementById('aspect_artifactbrowser_Profiles_div_publicationsContent').innerHTML = pubs.innerHTML;
  });
}
//Function to redirect users to their profile after creating/editing it. 
//There is already a link there, this just makes it so users don't have to manually click it. 
function redirect() {
  //Another workaround because only divs have ID's. 
  //Gets the link to profile
  var linksOnPage = document.getElementsByTagName('a');
  for (var x in linksOnPage){
    var redirectLink = linksOnPage[x];
    if(redirectLink.innerHTML === "Click Here To See Your Profile"){
      //Once the link to profile is found, redirect there. 
      window.location.replace(redirectLink.href);
    }
  }
}
//Wait until page is completely loaded before executing functions
window.onload = function() {
	 	  letterLinks();
		  getPublications();
                  redirect();
		}

 
