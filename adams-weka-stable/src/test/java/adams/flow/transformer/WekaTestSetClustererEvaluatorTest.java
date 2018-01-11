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
 * WekaTestSetClustererEvaluatorTest.java
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
 * Test for WekaTestSetClustererEvaluator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaTestSetClustererEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaTestSetClustererEvaluatorTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("anneal_train.arff");
    m_TestHelper.copyResourceToTmp("anneal_test.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal_train.arff");
    m_TestHelper.deleteFileFromTmp("anneal_test.arff");
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
    return new TestSuite(WekaTestSetClustererEvaluatorTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[8];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor3 = new adams.flow.core.Actor[2];

      // Flow.CallableActors.WekaClustererSetup
      adams.flow.source.WekaClustererSetup wekaclusterersetup4 = new adams.flow.source.WekaClustererSetup();
      argOption = (AbstractArgumentOption) wekaclusterersetup4.getOptionManager().findByProperty("clusterer");
      weka.clusterers.SimpleKMeans simplekmeans6 = new weka.clusterers.SimpleKMeans();
      simplekmeans6.setOptions(OptionUtils.splitOptions("-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
      wekaclusterersetup4.setClusterer(simplekmeans6);

      abstractactor3[0] = wekaclusterersetup4;

      // Flow.CallableActors.Testset
      adams.flow.source.SequenceSource sequencesource7 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource7.getOptionManager().findByProperty("name");
      sequencesource7.setName((java.lang.String) argOption.valueOf("Testset"));

      argOption = (AbstractArgumentOption) sequencesource7.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor9 = new adams.flow.core.Actor[3];

      // Flow.CallableActors.Testset.FileSupplier
      adams.flow.source.FileSupplier filesupplier10 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier10.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile11 = new adams.core.io.PlaceholderFile[1];
      placeholderfile11[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_test.arff");
      filesupplier10.setFiles(placeholderfile11);

      abstractactor9[0] = filesupplier10;

      // Flow.CallableActors.Testset.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader12 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader12.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader14 = new weka.core.converters.ArffLoader();
      wekafilereader12.setCustomLoader(arffloader14);

      abstractactor9[1] = wekafilereader12;

      // Flow.CallableActors.Testset.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter15 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter15.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove17 = new weka.filters.unsupervised.attribute.Remove();
      remove17.setOptions(OptionUtils.splitOptions("-R last \"\""));
      wekafilter15.setFilter(remove17);

      abstractactor9[2] = wekafilter15;
      sequencesource7.setActors(abstractactor9);

      abstractactor3[1] = sequencesource7;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier18 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier18.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile19 = new adams.core.io.PlaceholderFile[1];
      placeholderfile19[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_train.arff");
      filesupplier18.setFiles(placeholderfile19);

      abstractactor1[1] = filesupplier18;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader20 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader20.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader22 = new weka.core.converters.ArffLoader();
      wekafilereader20.setCustomLoader(arffloader22);

      abstractactor1[2] = wekafilereader20;

      // Flow.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter23 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter23.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove25 = new weka.filters.unsupervised.attribute.Remove();
      remove25.setOptions(OptionUtils.splitOptions("-R last \"\""));
      wekafilter23.setFilter(remove25);

      abstractactor1[3] = wekafilter23;

      // Flow.WekaTrainClusterer
      adams.flow.transformer.WekaTrainClusterer wekatrainclusterer26 = new adams.flow.transformer.WekaTrainClusterer();
      argOption = (AbstractArgumentOption) wekatrainclusterer26.getOptionManager().findByProperty("postProcessor");
      adams.flow.transformer.wekaclusterer.PassThrough passthrough28 = new adams.flow.transformer.wekaclusterer.PassThrough();
      wekatrainclusterer26.setPostProcessor(passthrough28);

      abstractactor1[4] = wekatrainclusterer26;

      // Flow.WekaTestSetClustererEvaluator
      adams.flow.transformer.WekaTestSetClustererEvaluator wekatestsetclustererevaluator29 = new adams.flow.transformer.WekaTestSetClustererEvaluator();
      abstractactor1[5] = wekatestsetclustererevaluator29;

      // Flow.WekaClusterEvaluationSummary
      adams.flow.transformer.WekaClusterEvaluationSummary wekaclusterevaluationsummary30 = new adams.flow.transformer.WekaClusterEvaluationSummary();
      abstractactor1[6] = wekaclusterevaluationsummary30;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile31 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile31.getOptionManager().findByProperty("outputFile");
      dumpfile31.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[7] = dumpfile31;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener34 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener34);

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

