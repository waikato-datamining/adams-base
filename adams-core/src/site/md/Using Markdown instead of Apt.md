Using Markdown instead of Apt
=============================

* Add to pom.xml

```
         <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-site-plugin</artifactId>
              <version>3.2</version>
              <dependencies>
                <dependency>
                  <groupId>org.apache.maven.doxia</groupId>
                  <artifactId>doxia-module-markdown</artifactId>
                  <version>1.3</version>
                </dependency>
              </dependencies>
            </plugin>
```

* places files in src/site/markdown/ with extension .md

* Source: http://stackoverflow.com/a/14831412/4698227