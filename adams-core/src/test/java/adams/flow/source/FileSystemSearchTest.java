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
 * FileSystemSearchTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.Actor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;

/**
 * Test for FileSystemSearch actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class FileSystemSearchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FileSystemSearchTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FileSystemSearchTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[2];

      // Flow.FileSystemSearch
      adams.flow.source.FileSystemSearch filesystemsearch2 = new adams.flow.source.FileSystemSearch();
      argOption = (AbstractArgumentOption) filesystemsearch2.getOptionManager().findByProperty("search");
      adams.flow.source.filesystemsearch.FileSearch filesearch4 = new adams.flow.source.filesystemsearch.FileSearch();
      argOption = (AbstractArgumentOption) filesearch4.getOptionManager().findByProperty("directory");
      filesearch4.setDirectory((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));
      argOption = (AbstractArgumentOption) filesearch4.getOptionManager().findByProperty("sorting");
      filesearch4.setSorting((adams.core.io.DirectoryLister.Sorting) argOption.valueOf("SORT_BY_NAME"));
      filesystemsearch2.setSearch(filesearch4);

      actors1[0] = filesystemsearch2;

      // Flow.Display
      adams.flow.sink.Display display7 = new adams.flow.sink.Display();
      argOption = (AbstractArgumentOption) display7.getOptionManager().findByProperty("writer");
      adams.data.io.output.NullWriter nullwriter9 = new adams.data.io.output.NullWriter();
      display7.setWriter(nullwriter9);

      actors1[1] = display7;
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

