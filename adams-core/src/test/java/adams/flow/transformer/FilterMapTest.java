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
 * FilterMapTest.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.ObjectToObject;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.NewMap;
import adams.flow.transformer.mapfilter.RemoveByName;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for FilterMap actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class FilterMapTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FilterMapTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FilterMapTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.NewMap
      NewMap newmap = new NewMap();
      actors.add(newmap);

      // Flow.SetMapValue
      SetMapValue setmapvalue = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("key");
      setmapvalue.setKey((String) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("value");
      setmapvalue.setValue((String) argOption.valueOf("A"));
      ObjectToObject objecttoobject = new ObjectToObject();
      setmapvalue.setConversion(objecttoobject);

      actors.add(setmapvalue);

      // Flow.SetMapValue (2)
      SetMapValue setmapvalue2 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("name");
      setmapvalue2.setName((String) argOption.valueOf("SetMapValue (2)"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("key");
      setmapvalue2.setKey((String) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("value");
      setmapvalue2.setValue((String) argOption.valueOf("B"));
      ObjectToObject objecttoobject2 = new ObjectToObject();
      setmapvalue2.setConversion(objecttoobject2);

      actors.add(setmapvalue2);

      // Flow.SetMapValue (3)
      SetMapValue setmapvalue3 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("name");
      setmapvalue3.setName((String) argOption.valueOf("SetMapValue (3)"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("key");
      setmapvalue3.setKey((String) argOption.valueOf("c"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("value");
      setmapvalue3.setValue((String) argOption.valueOf("C"));
      ObjectToObject objecttoobject3 = new ObjectToObject();
      setmapvalue3.setConversion(objecttoobject3);

      actors.add(setmapvalue3);

      // Flow.FilterMap
      FilterMap filtermap = new FilterMap();
      RemoveByName removebyname = new RemoveByName();
      argOption = (AbstractArgumentOption) removebyname.getOptionManager().findByProperty("regExp");
      removebyname.setRegExp((BaseRegExp) argOption.valueOf("b"));
      removebyname.setInvert(true);

      filtermap.setFilter(removebyname);

      actors.add(filtermap);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

