#!/bin/bash
#
# script that looks into the local repository and generates a bash script
# for uploading the artifacts to Maven Central for a specific version.
#
# Author: fracpete (fracpete at waikato dot ac dot nz)

# the usage of this script
function usage()
{
   echo
   echo "${0##*/} -o <outdir> -v <version> [-r <repo>] [-h]"
   echo
   echo "Creates a bash script that uploads artifacts to Maven central,"
   echo "using the local Maven repository."
   echo
   echo " -h   this help"
   echo " -o   <outdir>"
   echo "      the output directory for artifacts and upload script"
   echo "      default: $OUTDIR_DEF"
   echo " -v   <version>"
   echo "      the version of the artifacts"
   echo "      default: $VERSION_DEF"
   echo " -r   <repo>"
   echo "      the local Maven repository directory"
   echo "      default: $REPO_DEF"
   echo
}

URL="https://oss.sonatype.org/service/local/staging/deploy/maven2/"
GROUP="nz/ac/waikato/cms/adams"
REPO_DEF="$HOME/.m2/repository"
REPO=$REPO_DEF
VERSION_DEF="0.4.13-SNAPSHOT"
VERSION=$VERSION_DEF
OUTDIR_DEF="/tmp/central"
OUTDIR=$OUTDIR_DEF

# interpret parameters
while getopts ":ho:v:r:" flag
do
  case $flag in
    o) OUTDIR=$OPTARG
       ;;
    v) VERSION=$OPTARG
       ;;
    r) REPO=$OPTARG
       ;;
    h) usage
       exit 0
       ;;
    *) usage
       exit 1
       ;;
  esac
done

# start?
echo
echo "Creating upload for artifacts version: $VERSION"
echo "Using repository: $REPO"
echo "Creating output in directory: $OUTDIR"
echo ""
echo "Proceed with <Enter> or cancel with <Ctrl+C>?"
read

# init output dir
mkdir -p $OUTDIR
rm -f $OUTDIR/*

# init script
SCRIPT="$OUTDIR/upload.sh"
echo "#!/bin/bash" > $SCRIPT
echo "#" >> $SCRIPT
echo "# Uploads version $VERSION to Maven Central" >> $SCRIPT
echo "" >> $SCRIPT
chmod a+x $SCRIPT

# copy artifacts and fill script
LIST=`find $REPO/$GROUP -name "adams-*$VERSION.pom" | sort`
for i in $LIST
do
  COUNT=`cat $i | grep "incubator\|archetype" | wc -l`
  MODULES=`cat $i | grep "<modules>" | wc -l`
  if [ $COUNT -eq 0 ] || [ $MODULES -gt 0 ]
  then
    POM=$i
    POM_SHORT=`echo $POM | sed s/".*\/"//g`
    JAR=`echo $i | sed s/"pom$"/"jar"/g`
    JAR_SHORT=`echo $JAR | sed s/".*\/"//g`
    SRC=`echo $i | sed s/"\.pom$"/"-sources.jar"/g`
    SRC_SHORT=`echo $SRC | sed s/".*\/"//g`

    echo $POM_SHORT

    # pom
    cp $POM $OUTDIR
    if [ $MODULES -gt 0 ]
    then
      echo "mvn gpg:sign-and-deploy-file -Durl=$URL -DrepositoryId=sonatype-nexus-staging -Dfile=$POM_SHORT -Dpackaging=pom" >> $SCRIPT
    fi

    # jar
    if [ -f "$JAR" ]
    then
      echo "--> $JAR_SHORT"
      cp $JAR $OUTDIR
      echo "mvn gpg:sign-and-deploy-file -Durl=$URL -DrepositoryId=sonatype-nexus-staging -DpomFile=$POM_SHORT -Dfile=$JAR_SHORT -Dclassifier=sources" >> $SCRIPT
    fi

    # sources
    if [ -f "$SRC" ]
    then
      echo "--> $SRC_SHORT"
      cp $SRC $OUTDIR
      echo "mvn gpg:sign-and-deploy-file -Durl=$URL -DrepositoryId=sonatype-nexus-staging -DpomFile=$POM_SHORT -Dfile=$SRC_SHORT -Dclassifier=sources" >> $SCRIPT
    fi
  fi
done

