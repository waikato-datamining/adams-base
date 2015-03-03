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
 * WekaExtractArrayTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.base.BaseString;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.ArrayStandardScores;
import adams.data.statistics.MultiArrayStatistic;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaExtractArray.ExtractionType;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.flow.transformer.WekaInstancesStatistic.DataType;
import adams.test.TmpFile;

/**
 * Tests the WekaExtractArray actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExtractArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaExtractArrayTest(String name) {
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
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    WekaClassifierSetup cl = new WekaClassifierSetup();
    cl.setName("cl");
    cl.setClassifier(new weka.classifiers.functions.LinearRegression());

    CallableActors ga = new CallableActors();
    ga.setActors(new AbstractActor[]{
	cl
    });

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.DATASET);

    WekaClassSelector cs = new WekaClassSelector();

    WekaCrossValidationEvaluator cv = new WekaCrossValidationEvaluator();
    cv.setClassifier(new CallableActorReference("cl"));

    WekaPredictionsToInstances p2i = new WekaPredictionsToInstances();

    MultiArrayStatistic mas = new MultiArrayStatistic();
    mas.setSubStatistics(new AbstractArrayStatistic[]{
	new ArrayStandardScores()
    });
    WekaInstancesStatistic is = new WekaInstancesStatistic();
    is.setDataType(DataType.COLUMN_BY_REGEXP);
    is.setLocations(new BaseString[]{
	new BaseString("Predicted")
    });
    is.setStatistic(mas);

    WekaExtractArray ea = new WekaExtractArray();
    ea.setType(ExtractionType.COLUMN);
    ea.setIndex(new Index("1"));

    ArrayToSequence a2s = new ArrayToSequence();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sfs, fr, cs, cv, p2i, is, ea, a2s, df});

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
    return new TestSuite(WekaExtractArrayTest.class);
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
