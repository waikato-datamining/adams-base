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
 * WekaSelectObjectsTest.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.base.BaseClassname;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for WekaSelectObjects actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaSelectObjectsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaSelectObjectsTest(String name) {
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
    return new TestSuite(WekaSelectObjectsTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.WekaSelectObjects
      adams.flow.source.WekaSelectObjects wekaselectobjects2 = new adams.flow.source.WekaSelectObjects();
      argOption = (AbstractArgumentOption) wekaselectobjects2.getOptionManager().findByProperty("superClass");
      wekaselectobjects2.setSuperClass((BaseClassname) argOption.valueOf("weka.classifiers.Classifier"));
      argOption = (AbstractArgumentOption) wekaselectobjects2.getOptionManager().findByProperty("initialObjects");
      adams.core.base.BaseString[] initialobjects4 = new adams.core.base.BaseString[3];
      initialobjects4[0] = (adams.core.base.BaseString) argOption.valueOf("weka.classifiers.rules.ZeroR");
      initialobjects4[1] = (adams.core.base.BaseString) argOption.valueOf("weka.classifiers.trees.J48 -C 0.25 -M 2");
      initialobjects4[2] = (adams.core.base.BaseString) argOption.valueOf("weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8");
      wekaselectobjects2.setInitialObjects(initialobjects4);
      wekaselectobjects2.setNonInteractive(true);

      actors1[0] = wekaselectobjects2;

      // Flow.Convert
      adams.flow.transformer.Convert convert5 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert5.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToCommandline anytocommandline7 = new adams.data.conversion.AnyToCommandline();
      convert5.setConversion(anytocommandline7);

      actors1[1] = convert5;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile8 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile8.getOptionManager().findByProperty("outputFile");
      dumpfile8.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile8.setAppend(true);

      actors1[2] = dumpfile8;
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

