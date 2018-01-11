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
 * WekaStreamFilterTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for WekaStreamFilter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaStreamFilterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaStreamFilterTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.arff")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaStreamFilterTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      // Flow.FileSupplier
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.arff")});

      tmp1[0] = tmp2;
      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader tmp4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp6 = new weka.core.converters.ArffLoader();
      tmp4.setCustomLoader(tmp6);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputType");
      tmp4.setOutputType((adams.flow.transformer.WekaFileReader.OutputType) argOption.valueOf("INCREMENTAL"));

      tmp1[1] = tmp4;
      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector tmp8 = new adams.flow.transformer.WekaClassSelector();
      tmp1[2] = tmp8;
      // Flow.WekaStreamFilter
      adams.flow.transformer.WekaStreamFilter tmp9 = new adams.flow.transformer.WekaStreamFilter();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.instance.RowNorm tmp11 = new weka.filters.unsupervised.instance.RowNorm();
      tmp9.setFilter(tmp11);

      tmp9.setKeepRelationName(true);

      tmp1[3] = tmp9;
      // Flow.WekaInstanceDumper
      adams.flow.transformer.WekaInstanceDumper tmp12 = new adams.flow.transformer.WekaInstanceDumper();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("outputPrefix");
      tmp12.setOutputPrefix((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile"));

      tmp1[4] = tmp12;
      flow.setActors(tmp1);

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
