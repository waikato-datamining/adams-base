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
 * WekaPrincipalComponentsTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for WekaPrincipalComponents actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaPrincipalComponentsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaPrincipalComponentsTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaPrincipalComponentsTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[6];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.arff");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader6 = new weka.core.converters.ArffLoader();
      wekafilereader4.setCustomLoader(arffloader6);

      actors1[1] = wekafilereader4;

      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector wekaclassselector7 = new adams.flow.transformer.WekaClassSelector();
      actors1[2] = wekaclassselector7;

      // Flow.WekaPrincipalComponents
      adams.flow.transformer.WekaPrincipalComponents wekaprincipalcomponents8 = new adams.flow.transformer.WekaPrincipalComponents();
      actors1[3] = wekaprincipalcomponents8;

      // retrieve loadings
      GetArrayElement gae = new GetArrayElement();
      gae.setIndex(new Index("1"));
      actors1[4] = gae;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile9 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile9.getOptionManager().findByProperty("outputFile");
      dumpfile9.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors1[5] = dumpfile9;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener12 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener12);

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

