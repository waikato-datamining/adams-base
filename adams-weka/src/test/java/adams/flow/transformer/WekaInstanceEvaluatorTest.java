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
 * WekaInstanceEvaluatorTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.functions.GaussianProcessesNoWeights;
import weka.classifiers.functions.supportVector.RBFKernel;
import adams.data.weka.evaluator.IntervalEstimatorBased;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.source.FileSupplier;
import adams.flow.source.SequenceSource;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;

/**
 * Tests the WekaInstanceEvaluator actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaInstanceEvaluatorTest(String name) {
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
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier sfsTrain = new FileSupplier();
    sfsTrain.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

    WekaFileReader frTrain = new WekaFileReader();

    WekaClassSelector csTrain = new WekaClassSelector();

    SequenceSource seqs = new SequenceSource();
    seqs.setName("train");
    seqs.setActors(new Actor[]{
	sfsTrain,
	frTrain,
	csTrain
    });

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{
	seqs
    });

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaClassSelector cs = new WekaClassSelector();

    GaussianProcessesNoWeights gp = new GaussianProcessesNoWeights();
    gp.setKernel(new RBFKernel());
    gp.setNoise(0.01);
    IntervalEstimatorBased ieb = new IntervalEstimatorBased();
    ieb.setClassifier(gp);
    WekaInstanceEvaluator eval = new WekaInstanceEvaluator();
    eval.setEvaluator(ieb);
    eval.setInstancesActor(new CallableActorReference("train"));

    WekaInstanceDumper id = new WekaInstanceDumper();
    id.setOutputPrefix(new TmpFile("dumpfile"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, sfs, fr, cs, eval, id});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.arff"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaInstanceEvaluatorTest.class);
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
