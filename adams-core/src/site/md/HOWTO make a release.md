HOWTO make a release
====================

Requirements
------------
  
* has the java-cup dependency changed? If so, change env.sh and
  *.bat files (search for "java-cup-")

* execute/check all flows

* all files need to be up-to-date and committed

* use same Java version as on build server (1.8.0_191)

* disable all local Weka packages, renaming or deleting following directories:

  * $HOME/wekafiles
  * $HOME/.adams/wekafiles


Commands
--------

* adams-applications

  * build all (`mvn clean install -DskipTests=true`)
  * fix all missing licenses in `src/license/THIRD-PARTY.properties`
  * commit/push changes

* adams-base

  * `mvn --batch-mode release:prepare release:perform`

* adams-addons
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * change parent pom version, version and adams-version of `adams-core` dependency in `adams-maven-plugin` 
    pom.xml to just released one (remove -SNAPSHOT)
  * commit/push changes
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT
  * increment version and add -SNAPSHOT in `adams-maven-plugin` pom.xml of parent pom
    and `adams-core` dependency 
  * commit/push changes

* adams-lts
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * commit/push changes
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT
  * commit/push changes

* adams-libraries
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * commit/push changes
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT
  * commit/push changes

* adams-incubator

  * increment parent pom version (including -SNAPSHOT)
  * increment version (including -SNAPSHOT)
  * increment `adams.version` property (including -SNAPSHOT)
  * commit/push changes

* adams-spectral-base
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * commit/push changes
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT
  * commit/push changes

* adams-applications
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * commit/push changes
  * `mvn --batch-mode release:prepare release:perform`
  * change all -SNAPSHOT versions in the pom.xml files to the just released version 
  * `mvn install deb:package`
  * revert the changes
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT
  * commit/push changes

* increment adams version in all other dependent modules

* copy target/*-bin.zip files and test them

  * adams-addons-all
  * adams-annotator
  * adams-base-all
  * adams-basic-app
  * adams-deeplearning-spectral-app
  * adams-ml-app
  * adams-spectral-app


Deployment failures
-------------------

* rollback via Maven

  ```
  mvn release:rollback
  ```
  
* delete tags

  ```
  git push --delete origin adams-pom-X.Y.Z
  git tag --delete adams-pom-X.Y.Z
  git push
  ```
  
* delete adams-pom artifact (X.Y.Z) from Nexus


Uploads/updates
---------------
  
* generate comparison of actors/conversions between current and previous
  version (best to use "adams-addons-all" as this is the most complete):
    
  ```
  bin/launcher.sh -main adams.core.ClassLister -super adams.flow.core.Actor > actors.txt
  bin/launcher.sh -main adams.core.ClassLister -super adams.data.conversion.Conversion > conversions.txt
  ```

* create a new directory on sf.net and upload the -bin.zip files via SFTP:

  * adams-addons-all
  * adams-annotator
  * adams-base-all
  * adams-basic-app
  * adams-deeplearning-spectral-app
  * adams-dex-app
  * adams-ml-app
  * adams-spectral-app

* create a README.md (markdown style) and upload this to the sf.net directory
  as well
    
* copy same files onto adams.cms:

  ```
  adams.cms.waikato.ac.nz:/var/www/html/releases/adams
  ```

* create new release page (copy an old one and update links)

  ```
  pages/download/X.Y.Z.rst
  ``` 

* update web pages

  ```
  pages/root/index.rst
  pages/download/release.rst
  ```

* publish release on theadamsflow-dev and theadamsflow-user mailing lists

  ```
  https://groups.google.com/forum/#!forum/theadamsflow-user
  https://groups.google.com/forum/#!forum/theadamsflow-dev
  ```

* update entry on mloss.org

  ```
  https://mloss.org/software/view/425/
  ```

* publish release on Twitter, LinkedIn, blog

