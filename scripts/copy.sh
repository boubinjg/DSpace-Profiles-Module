#copies the profiels module to this directory and pushes it to github
cp ../DSpace/dspace-xmlui/src/main/java/org/dspace/app/xmlui/aspect/artifactbrowser/Profiles.java .
git add *
git commit -m "copy.sh commit"
git push -u origin master
