#!/bin/bash
#
# Imports adams 3rd party libraries into Nexus
#
# Author: FracPete (fracpete at waikato dot ac dot nz)

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

GROUP=com.github.grumlimited
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=geocalc \
  -Dversion=0.5.7 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/geocalc-0.5.7.jar \
  -DpomFile=$LIB_DIR/geocalc-0.5.7.pom \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL
