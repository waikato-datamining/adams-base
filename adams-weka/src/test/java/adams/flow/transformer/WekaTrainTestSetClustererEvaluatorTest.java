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
 * WekaTrainTestSetClustererEvaluatorTest.java
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
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for WekaTrainTestSetClustererEvaluator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaTrainTestSetClustererEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaTrainTestSetClustererEvaluatorTest(String name) {
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
    return new TestSuite(WekaTrainTestSetClustererEvaluatorTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[5];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor3 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.train
      adams.flow.source.SequenceSource sequencesource4 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource4.getOptionManager().findByProperty("name");
      sequencesource4.setName((java.lang.String) argOption.valueOf("train"));

      argOption = (AbstractArgumentOption) sequencesource4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor6 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.train.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile8 = new adams.core.io.PlaceholderFile[1];
      placeholderfile8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_train.arff");
      filesupplier7.setFiles(placeholderfile8);

      abstractactor6[0] = filesupplier7;

      // Flow.CallableActors.train.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader11 = new weka.core.converters.ArffLoader();
      wekafilereader9.setCustomLoader(arffloader11);

      abstractactor6[1] = wekafilereader9;

      // Flow.CallableActors.train.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter12 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter12.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove14 = new weka.filters.unsupervised.attribute.Remove();
      remove14.setOptions(OptionUtils.splitOptions("-R last \"\""));
      wekafilter12.setFilter(remove14);

      abstractactor6[2] = wekafilter12;
      sequencesource4.setActors(abstractactor6);

      abstractactor3[0] = sequencesource4;

      // Flow.CallableActors.test
      adams.flow.source.SequenceSource sequencesource15 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource15.getOptionManager().findByProperty("name");
      sequencesource15.setName((java.lang.String) argOption.valueOf("test"));

      argOption = (AbstractArgumentOption) sequencesource15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor17 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.test.FileSupplier
      adams.flow.source.FileSupplier filesupplier18 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier18.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile19 = new adams.core.io.PlaceholderFile[1];
      placeholderfile19[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_test.arff");
      filesupplier18.setFiles(placeholderfile19);

      abstractactor17[0] = filesupplier18;

      // Flow.CallableActors.test.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader20 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader20.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader22 = new weka.core.converters.ArffLoader();
      wekafilereader20.setCustomLoader(arffloader22);

      abstractactor17[1] = wekafilereader20;

      // Flow.CallableActors.test.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter23 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter23.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove25 = new weka.filters.unsupervised.attribute.Remove();
      remove25.setOptions(OptionUtils.splitOptions("-R last \"\""));
      wekafilter23.setFilter(remove25);

      abstractactor17[2] = wekafilter23;
      sequencesource15.setActors(abstractactor17);

      abstractactor3[1] = sequencesource15;

      // Flow.CallableActors.WekaClustererSetup
      adams.flow.source.WekaClustererSetup wekaclusterersetup26 = new adams.flow.source.WekaClustererSetup();
      argOption = (AbstractArgumentOption) wekaclusterersetup26.getOptionManager().findByProperty("clusterer");
      weka.clusterers.SimpleKMeans simplekmeans28 = new weka.clusterers.SimpleKMeans();
      simplekmeans28.setOptions(OptionUtils.splitOptions("-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
      wekaclusterersetup26.setClusterer(simplekmeans28);

      abstractactor3[2] = wekaclusterersetup26;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.MakeContainer
      adams.flow.source.MakeContainer makecontainer29 = new adams.flow.source.MakeContainer();
      argOption = (AbstractArgumentOption) makecontainer29.getOptionManager().findByProperty("callableActors");
      adams.flow.core.CallableActorReference[] globalactorreference30 = new adams.flow.core.CallableActorReference[2];
      globalactorreference30[0] = (adams.flow.core.CallableActorReference) argOption.valueOf("train");
      globalactorreference30[1] = (adams.flow.core.CallableActorReference) argOption.valueOf("test");
      makecontainer29.setCallableActors(globalactorreference30);

      argOption = (AbstractArgumentOption) makecontainer29.getOptionManager().findByProperty("valueNames");
      adams.core.base.BaseString[] basestring31 = new adams.core.base.BaseString[2];
      basestring31[0] = (adams.core.base.BaseString) argOption.valueOf("Train");
      basestring31[1] = (adams.core.base.BaseString) argOption.valueOf("Test");
      makecontainer29.setValueNames(basestring31);

      argOption = (AbstractArgumentOption) makecontainer29.getOptionManager().findByProperty("containerClass");
      adams.flow.container.WekaTrainTestSetContainer wekatraintestsetcontainer33 = new adams.flow.container.WekaTrainTestSetContainer();
      makecontainer29.setContainerClass(wekatraintestsetcontainer33);

      abstractactor1[1] = makecontainer29;

      // Flow.WekaTrainTestSetClustererEvaluator
      adams.flow.transformer.WekaTrainTestSetClustererEvaluator wekatraintestsetclustererevaluator34 = new adams.flow.transformer.WekaTrainTestSetClustererEvaluator();
      abstractactor1[2] = wekatraintestsetclustererevaluator34;

      // Flow.WekaClusterEvaluationSummary
      adams.flow.transformer.WekaClusterEvaluationSummary wekaclusterevaluationsummary35 = new adams.flow.transformer.WekaClusterEvaluationSummary();
      abstractactor1[3] = wekaclusterevaluationsummary35;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile36 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile36.getOptionManager().findByProperty("outputFile");
      dumpfile36.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[4] = dumpfile36;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener39 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener39);

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

