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
 * DeletePropertyValueTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.PropertiesToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.NewProperties;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DeletePropertyValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class DeletePropertyValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeletePropertyValueTest(String name) {
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
    return new TestSuite(DeletePropertyValueTest.class);
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

      // Flow.NewProperties
      NewProperties newproperties = new NewProperties();
      actors.add(newproperties);

      // Flow.SetPropertyValue
      SetPropertyValue setpropertyvalue = new SetPropertyValue();
      argOption = (AbstractArgumentOption) setpropertyvalue.getOptionManager().findByProperty("key");
      setpropertyvalue.setKey((String) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setpropertyvalue.getOptionManager().findByProperty("value");
      setpropertyvalue.setValue((String) argOption.valueOf("123"));
      actors.add(setpropertyvalue);

      // Flow.SetPropertyValue (2)
      SetPropertyValue setpropertyvalue2 = new SetPropertyValue();
      argOption = (AbstractArgumentOption) setpropertyvalue2.getOptionManager().findByProperty("name");
      setpropertyvalue2.setName((String) argOption.valueOf("SetPropertyValue (2)"));
      argOption = (AbstractArgumentOption) setpropertyvalue2.getOptionManager().findByProperty("key");
      setpropertyvalue2.setKey((String) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setpropertyvalue2.getOptionManager().findByProperty("value");
      setpropertyvalue2.setValue((String) argOption.valueOf("456"));
      actors.add(setpropertyvalue2);

      // Flow.DeletePropertyValue
      DeletePropertyValue deletepropertyvalue = new DeletePropertyValue();
      argOption = (AbstractArgumentOption) deletepropertyvalue.getOptionManager().findByProperty("key");
      deletepropertyvalue.setKey((BaseRegExp) argOption.valueOf("b"));
      actors.add(deletepropertyvalue);

      // Flow.DeletePropertyValue (2)
      DeletePropertyValue deletepropertyvalue2 = new DeletePropertyValue();
      argOption = (AbstractArgumentOption) deletepropertyvalue2.getOptionManager().findByProperty("name");
      deletepropertyvalue2.setName((String) argOption.valueOf("DeletePropertyValue (2)"));
      argOption = (AbstractArgumentOption) deletepropertyvalue2.getOptionManager().findByProperty("key");
      deletepropertyvalue2.setKey((BaseRegExp) argOption.valueOf("c"));
      actors.add(deletepropertyvalue2);

      // Flow.Convert
      Convert convert = new Convert();
      PropertiesToString propertiestostring = new PropertiesToString();
      convert.setConversion(propertiestostring);

      actors.add(convert);

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

