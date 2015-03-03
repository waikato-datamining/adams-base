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
 * WekaClassifierOptimizerTest.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;

/**
 * Tests the WekaClassifier actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierOptimizerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaClassifierOptimizerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.copyResourceToTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    FileSupplier mfs = new FileSupplier();
    mfs.setFiles(
	new TmpFile[]{
	    new TmpFile("vote.arff"),
	    new TmpFile("labor.arff")
	}
    );

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.DATASET);

    WekaClassSelector cs = new WekaClassSelector();

    WekaClassifierOptimizer co = new WekaClassifierOptimizer();
    weka.classifiers.meta.GridSearch gs = new weka.classifiers.meta.GridSearch();
    try {
      gs.setOptions(weka.core.Utils.splitOptions("-E ACC -y-property classifier.minNumObj -y-min 2.0 -y-max 5.0 -y-step 1.0 -y-base 10.0 -y-expression I -filter weka.filters.AllFilter -x-property classifier.confidenceFactor -x-min 0.1 -x-max 0.5 -x-step 0.05 -x-base 10.0 -x-expression I -sample-size 100.0 -traversal COLUMN-WISE -log-file /home/fracpete/development/projects/adams -num-slots 1 -S 1 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2"));
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to set options for optimizer: " + e);
    }
    co.setOptimizer(gs);

    AnyToCommandline a2c = new AnyToCommandline();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{mfs, fr, cs, co, a2c, df});

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
    return new TestSuite(WekaClassifierOptimizerTest.class);
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
