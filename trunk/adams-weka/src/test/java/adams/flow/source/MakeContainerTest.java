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
 * MakeContainerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

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
 * Test for MakeContainer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MakeContainerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MakeContainerTest(String name) {
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
    return new TestSuite(MakeContainerTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[5];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.SequenceSource tmp4 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("train"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp6 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("files");
      tmp7.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_train.arff")});

      tmp6[0] = tmp7;
      adams.flow.transformer.WekaFileReader tmp9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp11 = new weka.core.converters.ArffLoader();
      tmp9.setCustomLoader(tmp11);

      tmp6[1] = tmp9;
      adams.flow.transformer.WekaClassSelector tmp12 = new adams.flow.transformer.WekaClassSelector();
      tmp6[2] = tmp12;
      tmp4.setActors(tmp6);

      tmp3[0] = tmp4;
      adams.flow.source.SequenceSource tmp13 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("name");
      tmp13.setName((java.lang.String) argOption.valueOf("test"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp15 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp16 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("files");
      tmp16.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_test.arff")});

      tmp15[0] = tmp16;
      adams.flow.transformer.WekaFileReader tmp18 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp20 = new weka.core.converters.ArffLoader();
      tmp18.setCustomLoader(tmp20);

      tmp15[1] = tmp18;
      adams.flow.transformer.WekaClassSelector tmp21 = new adams.flow.transformer.WekaClassSelector();
      tmp15[2] = tmp21;
      tmp13.setActors(tmp15);

      tmp3[1] = tmp13;
      adams.flow.source.WekaClassifierSetup tmp22 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("classifier");
      weka.classifiers.trees.J48 tmp24 = new weka.classifiers.trees.J48();
      tmp24.setOptions(OptionUtils.splitOptions("-C 0.25 -M 2"));
      tmp22.setClassifier(tmp24);

      tmp3[2] = tmp22;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.MakeContainer tmp25 = new adams.flow.source.MakeContainer();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("callableActors");
      adams.flow.core.CallableActorReference[] tmp26 = new adams.flow.core.CallableActorReference[2];
      tmp26[0] = (adams.flow.core.CallableActorReference) argOption.valueOf("train");
      tmp26[1] = (adams.flow.core.CallableActorReference) argOption.valueOf("test");
      tmp25.setCallableActors(tmp26);

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("valueNames");
      adams.core.base.BaseString[] tmp27 = new adams.core.base.BaseString[2];
      tmp27[0] = (adams.core.base.BaseString) argOption.valueOf("Train");
      tmp27[1] = (adams.core.base.BaseString) argOption.valueOf("Test");
      tmp25.setValueNames(tmp27);

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("containerClass");
      tmp25.setContainerClass((adams.flow.container.AbstractContainer) argOption.valueOf("adams.flow.container.WekaTrainTestSetContainer"));

      tmp1[1] = tmp25;
      adams.flow.transformer.WekaTrainTestSetEvaluator tmp29 = new adams.flow.transformer.WekaTrainTestSetEvaluator();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("output");
      weka.classifiers.evaluation.output.prediction.Null tmp31 = new weka.classifiers.evaluation.output.prediction.Null();
      tmp29.setOutput(tmp31);

      tmp1[2] = tmp29;
      adams.flow.transformer.WekaEvaluationSummary tmp32 = new adams.flow.transformer.WekaEvaluationSummary();
      tmp1[3] = tmp32;
      adams.flow.sink.DumpFile tmp33 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp33.getOptionManager().findByProperty("outputFile");
      tmp33.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp1[4] = tmp33;
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

