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
 * WekaCrossValidationClustererEvaluatorTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for WekaCrossValidationClustererEvaluator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaCrossValidationClustererEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaCrossValidationClustererEvaluatorTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("anneal.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal.arff");
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
    return new TestSuite(WekaCrossValidationClustererEvaluatorTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[7];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.WekaClustererSetup
      adams.flow.source.WekaClustererSetup wekaclusterersetup4 = new adams.flow.source.WekaClustererSetup();
      argOption = (AbstractArgumentOption) wekaclusterersetup4.getOptionManager().findByProperty("clusterer");
      weka.clusterers.SimpleKMeans simplekmeans6 = new weka.clusterers.SimpleKMeans();
      simplekmeans6.setOptions(OptionUtils.splitOptions("-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
      wekaclusterersetup4.setClusterer(simplekmeans6);

      abstractactor3[0] = wekaclusterersetup4;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile8 = new adams.core.io.PlaceholderFile[1];
      placeholderfile8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff");
      filesupplier7.setFiles(placeholderfile8);

      abstractactor1[1] = filesupplier7;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader11 = new weka.core.converters.ArffLoader();
      wekafilereader9.setCustomLoader(arffloader11);

      abstractactor1[2] = wekafilereader9;

      // Flow.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter12 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter12.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove14 = new weka.filters.unsupervised.attribute.Remove();
      remove14.setOptions(OptionUtils.splitOptions("-R last \"\""));
      wekafilter12.setFilter(remove14);

      abstractactor1[3] = wekafilter12;

      // Flow.WekaCrossValidationClustererEvaluator
      adams.flow.transformer.WekaCrossValidationClustererEvaluator wekacrossvalidationclustererevaluator15 = new adams.flow.transformer.WekaCrossValidationClustererEvaluator();
      abstractactor1[4] = wekacrossvalidationclustererevaluator15;

      // Flow.WekaClusterEvaluationSummary
      adams.flow.transformer.WekaClusterEvaluationSummary wekaclusterevaluationsummary16 = new adams.flow.transformer.WekaClusterEvaluationSummary();
      abstractactor1[5] = wekaclusterevaluationsummary16;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile17 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile17.getOptionManager().findByProperty("outputFile");
      dumpfile17.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[6] = dumpfile17;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener20 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener20);

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

