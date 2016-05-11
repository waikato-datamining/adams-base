#!/bin/bash
#
# script that looks into the local repository and generates a bash script
# for uploading the artifacts to Maven Central for a specific version.
#
# Author: fracpete (fracpete at waikato dot ac dot nz)

REPO="$HOME/.m2/repository"
GROUP="nz/ac/waikato/cms/adams"
VERSION="0.4.13-SNAPSHOT"
OUTDIR="$HOME/temp/central/out"
SCRIPT="$OUTDIR/upload.sh"

LIST=`find $REPO/$GROUP -name "adams-*$VERSION.pom" | sort`

# init output dir
mkdir -p $OUTDIR
rm -f $OUTDIR/*

# init script
echo "#!/bin/bash" > $SCRIPT
echo "#" >> $SCRIPT
echo "# Uploads version $VERSION to Maven Central" >> $SCRIPT
echo "" >> $SCRIPT
chmod a+x $SCRIPT

for i in $LIST
do
  COUNT=`cat $i | grep "incubator\|-all\|archetype" | wc -l`
  if [ $COUNT -eq 0 ]
  then
    POM=$i
    POM_SHORT=`echo $POM | sed s/".*\/"//g`
    JAR=`echo $i | sed s/"pom$"/"jar"/g`
    JAR_SHORT=`echo $JAR | sed s/".*\/"//g`
    SRC=`echo $i | sed s/"\.pom$"/"-sources.jar"/g`
    SRC_SHORT=`echo $SRC | sed s/".*\/"//g`
    echo $POM
    cp $POM $OUTDIR
    cp $JAR $OUTDIR
    cp $SRC $OUTDIR
    echo "mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=$POM_SHORT -Dfile=$JAR_SHORT" >> $SCRIPT
    echo "mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=$POM_SHORT -Dfile=$SRC_SHORT -Dclassifier=sources" >> $SCRIPT
  fi
done

