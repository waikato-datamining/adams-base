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
 * MOARegressorEvaluationTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.MOARegressorSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import moa.evaluation.BasicRegressionPerformanceEvaluator;
import moa.options.ClassOption;

/**
 * Tests the MOARegressorEvaluation actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOARegressorEvaluationTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOARegressorEvaluationTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    ClassOption option = new ClassOption(
	"regressor",
	'r',
	"The MOA regressor to use from within ADAMS.",
	moa.classifiers.Regressor.class,
	"trees.FIMTDD",
	"moa.classifiers.trees.FIMTDD");

    MOARegressorSetup cls = new MOARegressorSetup();
    cls.setRegressor(option);
    cls.setName("reg");

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{cls});

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaClassSelector cs = new WekaClassSelector();

    option = new ClassOption(
	"evaluator",
	'e',
	"The MOA classifier performance evaluator to use from within ADAMS.",
	moa.evaluation.RegressionPerformanceEvaluator.class,
	"BasicRegressionPerformanceEvaluator",
	BasicRegressionPerformanceEvaluator.class.getName());

    MOARegressorEvaluation mce = new MOARegressorEvaluation();
    mce.setRegressor(new CallableActorReference("reg"));
    mce.setEvaluator(option);
    mce.setOutputInterval(40);   // 40 instances in the dataset

    MOALearningEvaluation mle = new MOALearningEvaluation();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, sfs, fr, cs, mce, mle, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOARegressorEvaluationTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
