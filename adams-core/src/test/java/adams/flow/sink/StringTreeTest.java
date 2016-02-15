/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * StringTreeTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for StringTree actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StringTreeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringTreeTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringTreeTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[2];

      // Flow.DirectoryLister
      adams.flow.source.DirectoryLister directorylister2 = new adams.flow.source.DirectoryLister();
      directorylister2.setOutputArray(true);

      argOption = (AbstractArgumentOption) directorylister2.getOptionManager().findByProperty("watchDir");
      directorylister2.setWatchDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));
      directorylister2.setListFiles(true);

      argOption = (AbstractArgumentOption) directorylister2.getOptionManager().findByProperty("sorting");
      directorylister2.setSorting((adams.core.io.DirectoryLister.Sorting) argOption.valueOf("SORT_BY_NAME"));
      directorylister2.setRecursive(true);

      argOption = (AbstractArgumentOption) directorylister2.getOptionManager().findByProperty("maxDepth");
      directorylister2.setMaxDepth((Integer) argOption.valueOf("3"));
      actors1[0] = directorylister2;

      // Flow.StringTree
      adams.flow.sink.StringTree stringtree6 = new adams.flow.sink.StringTree();
      argOption = (AbstractArgumentOption) stringtree6.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter8 = new adams.gui.print.NullWriter();
      stringtree6.setWriter(nullwriter8);

      argOption = (AbstractArgumentOption) stringtree6.getOptionManager().findByProperty("separator");
      stringtree6.setSeparator((java.lang.String) argOption.valueOf("/"));
      actors1[1] = stringtree6;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener11 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener11);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

