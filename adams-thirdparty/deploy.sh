#!/bin/bash
#
# Imports adams 3rd party libraries into Nexus
#
# Author: FracPete (fracpete at waikato dot ac dot nz)
# Version: $Revision$

HOST=https://adams.cms.waikato.ac.nz
REPO=adams-thirdparty
REPO_URL=$HOST/nexus/content/repositories/$REPO

LIB_DIR=./

GROUP=pentaho.weka
mvn deploy:deploy-file \
  -DgroupId=$GROUP \
  -DartifactId=pdm-timeseriesforecasting-ce \
  -Dversion=2015.05.19 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/pdm-timeseriesforecasting-ce-2015.05.19.jar \
  -Dsources=$LIB_DIR/pdm-timeseriesforecasting-ce-2015.05.19-sources.jar \
  -DpomFile=$LIB_DIR/pdm-timeseriesforecasting-ce-2015.05.19.pom \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

GROUP=org.openstreetmap
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=jmapviewer \
  -Dversion=1.0.2 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/JMapViewer-1.0.2.jar \
  -Dsources=$LIB_DIR/JMapViewer-1.0.2-sources.jar \
  -DgeneratePom.description="JMapViewer is a java component which allows to easily integrate an OSM map view into your Java application. https://wiki.openstreetmap.org/wiki/JMapViewer" \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

GROUP=org.postgis
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=postgis-jdbc \
  -Dversion=1.5.3 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/postgis-1.5.3.jar \
  -DgeneratePom.description="JDBC driver provided by Ubuntu repositories." \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL
