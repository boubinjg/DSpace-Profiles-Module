function letterLinks() {
  var letterDiv = document.getElementById('aspect_artifactbrowser_ProfilesHome_div_links');

  var letter;
  var html = "";

  for(i = 0; i < 26; i++) {
  letter = String.fromCharCode(65 + i);
  html += "<a href='scholarprofiles?letter=" + letter + "'>" + letter + "</a>"; 
  html += "&nbsp";
  }
  if(letterDiv != null){
    letterDiv.innerHTML = html;
  }
}
function getPublications() {
  var nameDiv= document.getElementById("aspect_artifactbrowser_Profiles_div_nameHeader");
  var namePara = nameDiv.getElementsByTagName("p");
  var name = namePara[0].innerHTML;
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

    // Get the HTML
    xhr.open( 'GET', url );
    xhr.responseType = 'document';
    xhr.send();
  };

  getHTML( '/xmlui/browse?value='+ln+',+'+fn+'&type=author', function (response) { 
    var pubs = response.getElementById('aspect_artifactbrowser_ConfigurableBrowse_div_browse-by-author-results');
    if(pubs.innerHTML.includes('Sorry, there are no results for this browse.')){
        pubs.innerHTML = "";
    }
document.getElementById('aspect_artifactbrowser_Profiles_div_publicationsContent').innerHTML = pubs.innerHTML;
  });
}

function redirect() {
  var linksOnPage = document.getElementsByTagName('a');
  for (var x in linksOnPage){
    var redirectLink = linksOnPage[x];
    if(redirectLink.innerHTML === "Click Here To See Your Profile"){
      window.location.replace(redirectLink.href);
    }
  }
}

window.onload = function() {
	 	  letterLinks();
		  getPublications();
                  redirect();
		}

 
