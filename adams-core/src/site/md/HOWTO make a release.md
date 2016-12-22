HOWTO make a release
====================

Requirements
------------
  
* update the versions in archetype HOWTO:

  ```
  adams-archetype-module/src/site/apt/HOWTO create a new sub-module.apt
  ```

* update the versions in the pom.xml of the archetype:

  ```
  adams-archetype-module/src/main/resources/archetype-resources/pom.xml
  ```

* update the versions in the manual for making a release:

  ```
  file......: adams-core/src/main/latex/adams-core-manual/developing_with_adams.tex
  section...: Creating a new module or project
  subsection: Module
  ```

* has the java-cup dependency changed? If so, change launcher.sh and
  launcher.bat files

* all files need to be up-to-date and committed


Commands
--------

```
mvn --batch-mode release:prepare release:perform
```

Note: use the following option to avoid tests being run, though this is not encouraged:

```
 -Darguments="-DskipTests=true"
```

See next section for detailed command sequences.


Order
-----

* adams-base

  * `mvn --batch-mode release:prepare release:perform`

* adams-addons
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT

* adams-libraries
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT

* adams-incubator

  * increment parent pom version (including -SNAPSHOT)
  * increment version (including -SNAPSHOT)
  * increment `adams.version` property (including -SNAPSHOT)

* adams-spectral-base
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * `mvn --batch-mode release:prepare release:perform`
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT

* adams-spectral-addons

  * increment parent pom version (including -SNAPSHOT)
  * increment `adams.version` property (including -SNAPSHOT)

* adams-applications
  
  * change parent pom version to just released adams-pom one (remove -SNAPSHOT)
  * change `adams.version` property to just released adams-pom one (remove -SNAPSHOT)
  * comment out adams-incubator-all module
  * `mvn --batch-mode release:prepare release:perform`
  * uncomment adams-incubator-all module
  * increment parent pom version in adams-incubator-all module
  * increment version of parent pom and add -SNAPSHOT
  * increment version of `adams.version` property and add -SNAPSHOT

* increment adams version in all other dependent modules

* copy target/*-bin.zip files and test them

  * adams-base-all
  * adams-addons-all
  * adams-annotator
  * adams-ml-app
  * adams-spectral-app


Uploads/updates
---------------
  
* generate comparison of actors/conversions between current and previous
  version (best to use "adams-addons-all" as this is the most complete):
    
  ```
  bin/launcher.sh -main adams.core.ClassLister -super adams.flow.core.Actor > actors.txt
  bin/launcher.sh -main adams.core.ClassLister -super adams.data.conversion.Conversion > conversions.txt
  ```

* create a new directory on sf.net and upload the -bin.zip files:

  * adams-base-all
  * adams-addons-all
  * adams-annotator
  * adams-ml-app
  * adams-spectral-app

* create a README.md (markdown style) and upload this to the sf.net directory
  as well
    
* copy same files onto adams.cms:

  ```
  adams.cms.waikato.ac.nz:/var/www/html/releases/adams
  ```

* deploy artifacts on Maven Central

  ```
  adams-core/src/main/scripts/central.sh  
  ```

* update adams webpage

  ```
  https://adams.cms.waikato.ac.nz/index.html
  https://adams.cms.waikato.ac.nz/release.html
  https://adams.cms.waikato.ac.nz/rollyourown.html
  https://adams.cms.waikato.ac.nz/resources/rollyourown/modules/*
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


$Revision$
