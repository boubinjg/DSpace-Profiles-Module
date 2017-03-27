cd ~/DSpace
mvn package
cd ~/DSpace/dspace/target/dspace-installer
ant fresh_install
sudo /bin/systemctl restart tomcat7
