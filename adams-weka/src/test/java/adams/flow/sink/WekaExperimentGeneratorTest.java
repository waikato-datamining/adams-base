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
 * WekaExperimentGeneratorTest.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.WekaExperimentGenerator.EvaluationType;
import adams.flow.sink.WekaExperimentGenerator.ExperimentType;
import adams.flow.sink.WekaExperimentGenerator.ResultFormat;
import adams.flow.source.WekaClassifierGenerator;
import adams.test.TmpFile;
import adams.test.Platform;

/**
 * Tests the WekaExperimentGenerator actor.
 * <p/>
 * Note: regression file needs to be updated whenever the Weka version
 *       changes (the XML stores the Weka version!).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentGeneratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaExperimentGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the platform this test class is for.
   * 
   * @return		the platform.
   */
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.LINUX}));
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile_cv.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile_rsr.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile_rsop.xml");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile_cv.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile_rsr.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile_rsop.xml");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    WekaClassifierGenerator cg = new WekaClassifierGenerator();
    cg.setOutputArray(true);

    WekaExperimentGenerator eg_cv = new WekaExperimentGenerator();
    eg_cv.setExperimentType(ExperimentType.REGRESSION);
    eg_cv.setEvaluationType(EvaluationType.CROSS_VALIDATION);
    eg_cv.setRuns(9);
    eg_cv.setFolds(5);
    eg_cv.setResultFormat(ResultFormat.ARFF);
    eg_cv.setResultFile(new TmpFile("dumpfile_cv.arff"));
    eg_cv.setOutputFile(new TmpFile("dumpfile_cv.xml"));

    WekaExperimentGenerator eg_rsr = new WekaExperimentGenerator();
    eg_rsr.setExperimentType(ExperimentType.REGRESSION);
    eg_rsr.setEvaluationType(EvaluationType.TRAIN_TEST_SPLIT_RANDOMIZED);
    eg_rsr.setRuns(8);
    eg_rsr.setSplitPercentage(50.0);
    eg_rsr.setResultFormat(ResultFormat.CSV);
    eg_rsr.setResultFile(new TmpFile("dumpfile_rsr.csv"));
    eg_rsr.setOutputFile(new TmpFile("dumpfile_rsr.xml"));

    WekaExperimentGenerator eg_rsop = new WekaExperimentGenerator();
    eg_rsop.setExperimentType(ExperimentType.REGRESSION);
    eg_rsop.setEvaluationType(EvaluationType.TRAIN_TEST_SPLIT_ORDER_PRESERVED);
    eg_rsop.setRuns(7);
    eg_rsop.setSplitPercentage(75.0);
    eg_rsop.setResultFormat(ResultFormat.ARFF);
    eg_rsop.setResultFile(new TmpFile("dumpfile_rsop.arff"));
    eg_rsop.setOutputFile(new TmpFile("dumpfile_rsop.xml"));

    Branch br = new Branch();
    br.setNumThreads(0);
    br.setBranches(new AbstractActor[]{
	eg_cv,
	eg_rsr,
	eg_rsop
    });

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{cg, br});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile_cv.xml"),
	    new TmpFile("dumpfile_rsr.xml"),
	    new TmpFile("dumpfile_rsop.xml")
	});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaExperimentGeneratorTest.class);
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
