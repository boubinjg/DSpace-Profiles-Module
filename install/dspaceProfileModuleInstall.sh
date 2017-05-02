clear
echo "===================================="
echo "DSpace Profile Module Install Script"
echo "===================================="
echo "Enter [dspace-source] directory path:" 
#read in [dspace-source] path
read value
#install java files
echo "installing Profile page"
cp lib/Profiles.java $value/dspace-xmlui/src/main/java/org/dspace/app/xmlui/aspect/artifactbrowser
echo "installing profile manager"
cp lib/ProfileManager.java $value/dspace-xmlui/src/main/java/org/dspace/app/xmlui/aspect/artifactbrowser
echo "installing profile homepage"
cp lib/ProfilesHome.java $value/dspace-xmlui/src/main/java/org/dspace/app/xmlui/aspect/artifactbrowser
echo "updating wing Division class"
cp lib/Division.java $value/dspace-xmlui/src/main/java/org/dspace/app/xmlui/wing/element
#install CSS
echo "installing CSS"
cp lib/moduleCSS.css $value/dspace-xmlui/src/main/webapp/themes/Mirage/lib/css
#install javascript
echo "installing Javascript"
cp lib/moduleJS.js $value/dspace-xmlui/src/main/webapp/themes/Mirage/lib/js
#install images
echo "installing images"
cp lib/academia-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
cp lib/orcid-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
cp lib/researchgate-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
cp lib/linkedin-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
cp lib/google-plus-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
cp lib/twitter-logo.png $value/dspace-xmlui/src/main/webapp/themes/Mirage/images
echo "===================================="
echo "Done Installing Changes"
echo "Please Recompile and Restart DSpace"
echo "===================================="
