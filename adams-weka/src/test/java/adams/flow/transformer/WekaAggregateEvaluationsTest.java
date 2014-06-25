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
 * WekaAggregateEvaluationsTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for WekaAggregateEvaluations actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaAggregateEvaluationsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaAggregateEvaluationsTest(String name) {
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
    return new TestSuite(WekaAggregateEvaluationsTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[9];
      // Flow.CallableActors
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[1];
      // Flow.CallableActors.WekaClassifier
      adams.flow.source.WekaClassifierSetup tmp4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("classifier");
      weka.classifiers.trees.J48 tmp6 = new weka.classifiers.trees.J48();
      tmp6.setOptions(OptionUtils.splitOptions("-C 0.25 -M 2 \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\""));
      tmp4.setClassifier(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      // Flow.SetVariable
      adams.flow.standalone.SetVariable tmp7 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("folds"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableValue");
      tmp7.setVariableValue((BaseText) argOption.valueOf("10"));

      tmp1[1] = tmp7;
      // Flow.FileSupplier
      adams.flow.source.FileSupplier tmp10 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("files");
      tmp10.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff")});

      tmp1[2] = tmp10;
      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader tmp12 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp14 = new weka.core.converters.ArffLoader();
      tmp12.setCustomLoader(tmp14);

      tmp1[3] = tmp12;
      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector tmp15 = new adams.flow.transformer.WekaClassSelector();
      tmp1[4] = tmp15;
      // Flow.WekaCrossValidationSplit
      adams.flow.transformer.WekaCrossValidationSplit tmp16 = new adams.flow.transformer.WekaCrossValidationSplit();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("folds");
      argOption.setVariable("@{folds}");

      tmp1[5] = tmp16;
      // Flow.WekaTrainTestSetEvaluator
      adams.flow.transformer.WekaTrainTestSetEvaluator tmp17 = new adams.flow.transformer.WekaTrainTestSetEvaluator();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("output");
      weka.classifiers.evaluation.output.prediction.Null tmp19 = new weka.classifiers.evaluation.output.prediction.Null();
      tmp17.setOutput(tmp19);

      tmp17.setDiscardPredictions(true);

      tmp1[6] = tmp17;
      // Flow.WekaAggregateEvaluations
      adams.flow.transformer.WekaAggregateEvaluations tmp20 = new adams.flow.transformer.WekaAggregateEvaluations();
      tmp1[7] = tmp20;
      // Flow.ConditionalTee
      adams.flow.control.ConditionalTee tmp21 = new adams.flow.control.ConditionalTee();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp22 = new adams.flow.core.AbstractActor[2];
      // Flow.ConditionalTee.WekaEvaluationSummary
      adams.flow.transformer.WekaEvaluationSummary tmp23 = new adams.flow.transformer.WekaEvaluationSummary();
      tmp23.setOutputRelationName(true);

      tmp23.setClassDetails(true);

      tmp22[0] = tmp23;
      // Flow.ConditionalTee.DumpFile
      adams.flow.sink.DumpFile tmp24 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("outputFile");
      tmp24.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp22[1] = tmp24;
      tmp21.setActors(tmp22);

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting tmp27 = new adams.flow.condition.bool.Counting();
      argOption = (AbstractArgumentOption) tmp27.getOptionManager().findByProperty("minimum");
      argOption.setVariable("@{folds}");

      tmp21.setCondition(tmp27);

      tmp1[8] = tmp21;
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

