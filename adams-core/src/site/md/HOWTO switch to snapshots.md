HOWTO switch to snapshots
=========================

* Checking for updates

  ```
  mvn versions:display-dependency-updates -DprocessDependencies=true -DprocessDependencyManagement=false
  ```

* Upgrading to latest snapshots

  ```
  mvn versions:update-parent -DgenerateBackupPoms=false
  mvn versions:use-latest-snapshots -Dincludes=nz.ac.waikato.cms.adams:* -DgenerateBackupPoms=false
  ```

**Note:** The projects referenced in "-Dincludes" need to be deployed to the maven 
repository, otherwise they won't be found.


$Revision$
