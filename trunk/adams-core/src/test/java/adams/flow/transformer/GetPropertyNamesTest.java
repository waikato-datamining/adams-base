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
 * GetPropertyNamesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for GetPropertyNames actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class GetPropertyNamesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GetPropertyNamesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(GetPropertyNamesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.NewProperties
      adams.flow.source.NewProperties newproperties2 = new adams.flow.source.NewProperties();
      actors1[0] = newproperties2;

      // Flow.SetPropertyValue
      adams.flow.transformer.SetPropertyValue setpropertyvalue3 = new adams.flow.transformer.SetPropertyValue();
      argOption = (AbstractArgumentOption) setpropertyvalue3.getOptionManager().findByProperty("key");
      setpropertyvalue3.setKey((java.lang.String) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setpropertyvalue3.getOptionManager().findByProperty("value");
      setpropertyvalue3.setValue((java.lang.String) argOption.valueOf("world"));
      actors1[1] = setpropertyvalue3;

      // Flow.SetPropertyValue-1
      adams.flow.transformer.SetPropertyValue setpropertyvalue6 = new adams.flow.transformer.SetPropertyValue();
      argOption = (AbstractArgumentOption) setpropertyvalue6.getOptionManager().findByProperty("name");
      setpropertyvalue6.setName((java.lang.String) argOption.valueOf("SetPropertyValue-1"));
      argOption = (AbstractArgumentOption) setpropertyvalue6.getOptionManager().findByProperty("key");
      setpropertyvalue6.setKey((java.lang.String) argOption.valueOf("another"));
      argOption = (AbstractArgumentOption) setpropertyvalue6.getOptionManager().findByProperty("value");
      setpropertyvalue6.setValue((java.lang.String) argOption.valueOf("one"));
      actors1[2] = setpropertyvalue6;

      // Flow.GetPropertyNames
      adams.flow.transformer.GetPropertyNames getpropertynames10 = new adams.flow.transformer.GetPropertyNames();
      getpropertynames10.setSortKeys(true);
      actors1[3] = getpropertynames10;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile11 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile11.getOptionManager().findByProperty("outputFile");
      dumpfile11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile11.setAppend(true);

      actors1[4] = dumpfile11;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener14 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener14);

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

