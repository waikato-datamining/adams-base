HOWTO create a dependency graph
===============================

All dependencies:
  
```
mvn dependency:tree -DoutputType=graphml -DoutputFile=/tmp/adams-dependencies.graphml
```

Only adams modules:

```
mvn dependency:tree -DoutputType=graphml -Dincludes=nz.ac.waikato.cms.adams:* -DoutputFile=/tmp/adams-dependencies.graphml
```

Instead of graphml, graphviz can be generated as well:

```
-DoutputType=dot
```

Removing unwanted node label content (e.g., "active project artifact:"):

```
cat file.graphml | \
     sed s/"artifact = active project artifact:"//g | \
     sed s/"active project artifact:"//g | \
     sed s/"artifact = "//g | \
     sed s/"project:.*;\|project:.*xml"//g | \
     sed s/"compile\|:compile\|test\|;"//g > file-new.graphml
```

Viewing graphml files with yEd 3.9:
  
* open generated file
* Tools -> Fit Node to Label -> OK
* Layout -> Tree: select "Compact" style -> goto "Compact" tab -> Horiz. = 20, Vert. = 20, Bend = 20
* Export to PDF, all margins 0.2

Source: http://www.summa-tech.com/blog/2011/04/12/a-visual-maven-dependency-tree-view/


$Revision$
