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
 * StorageForLoopTest.java
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
 * Test for StorageForLoop actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StorageForLoopTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StorageForLoopTest(String name) {
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

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
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
    return new TestSuite(StorageForLoopTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[4];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[1];
      adams.flow.source.WekaClassifierSetup tmp4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("classifier");
      weka.classifiers.trees.J48 tmp6 = new weka.classifiers.trees.J48();
      tmp6.setOptions(OptionUtils.splitOptions("-C 0.25 -M 2"));
      tmp4.setClassifier(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.Start tmp7 = new adams.flow.source.Start();
      tmp1[1] = tmp7;
      adams.flow.control.Trigger tmp8 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp9 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.FileSupplier tmp10 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("files");
      tmp10.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/vote.arff")});

      tmp9[0] = tmp10;
      adams.flow.transformer.WekaFileReader tmp12 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp14 = new weka.core.converters.ArffLoader();
      tmp12.setCustomLoader(tmp14);

      tmp9[1] = tmp12;
      adams.flow.transformer.WekaClassSelector tmp15 = new adams.flow.transformer.WekaClassSelector();
      tmp9[2] = tmp15;
      adams.flow.transformer.SetStorageValue tmp16 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("storageName");
      tmp16.setStorageName((adams.flow.control.StorageName) argOption.valueOf("dataset"));

      tmp9[3] = tmp16;
      tmp8.setActors(tmp9);

      tmp1[2] = tmp8;
      adams.flow.control.Trigger tmp18 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("name");
      tmp18.setName((java.lang.String) argOption.valueOf("Trigger-1"));

      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp20 = new adams.flow.core.AbstractActor[5];
      adams.flow.source.StorageForLoop tmp21 = new adams.flow.source.StorageForLoop();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("loopUpper");
      tmp21.setLoopUpper((Integer) argOption.valueOf("5"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("variableName");
      tmp21.setVariableName((adams.core.VariableName) argOption.valueOf("index"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("storageName");
      tmp21.setStorageName((adams.flow.control.StorageName) argOption.valueOf("dataset"));

      tmp20[0] = tmp21;
      adams.flow.control.UpdateProperties tmp25 = new adams.flow.control.UpdateProperties();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("properties");
      adams.core.base.BaseString[] tmp26 = new adams.core.base.BaseString[1];
      tmp26[0] = (adams.core.base.BaseString) argOption.valueOf("filter.attributeIndices");
      tmp25.setProperties(tmp26);

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("variableNames");
      adams.core.VariableName[] tmp27 = new adams.core.VariableName[1];
      tmp27[0] = (adams.core.VariableName) argOption.valueOf("index");
      tmp25.setVariableNames(tmp27);

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("subActor");
      adams.flow.transformer.WekaFilter tmp29 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove tmp31 = new weka.filters.unsupervised.attribute.Remove();
      tmp29.setFilter(tmp31);

      tmp25.setSubActor(tmp29);

      tmp20[1] = tmp25;
      adams.flow.transformer.WekaCrossValidationEvaluator tmp32 = new adams.flow.transformer.WekaCrossValidationEvaluator();
      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("output");
      weka.classifiers.evaluation.output.prediction.Null tmp34 = new weka.classifiers.evaluation.output.prediction.Null();
      tmp32.setOutput(tmp34);

      tmp20[2] = tmp32;
      adams.flow.transformer.WekaEvaluationSummary tmp35 = new adams.flow.transformer.WekaEvaluationSummary();
      tmp35.setOutputRelationName(true);

      tmp20[3] = tmp35;
      adams.flow.sink.DumpFile tmp36 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("outputFile");
      tmp36.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp36.setAppend(true);

      tmp20[4] = tmp36;
      tmp18.setActors(tmp20);

      tmp1[3] = tmp18;
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
