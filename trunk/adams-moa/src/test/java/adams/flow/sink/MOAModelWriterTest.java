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
 * MOAModelWriterTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import moa.options.ClassOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.MOAClassifier;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;

/**
 * Tests the FileReader, ClassSelector, Classifier and ModelWriter actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAModelWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAModelWriterTest(String name) {
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

    m_TestHelper.copyResourceToTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.model");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.model");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("iris.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaClassSelector cs = new WekaClassSelector();

    ClassOption option = new ClassOption(
	"classifier",
	'c',
	"The MOA classifier to use from within ADAMS.",
	moa.classifiers.Classifier.class,
	"bayes.NaiveBayes",
	"moa.classifiers.bayes.NaiveBayes");

    MOAClassifier cls = new MOAClassifier();
    cls.setClassifier(option);
    cls.setOutputInterval(150);  // number of instances in dataset

    MOAModelWriter mw = new MOAModelWriter();
    mw.setOutputFile(new TmpFile("dumpfile.model"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{sfs, fr, cs, cls, mw});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.model"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAModelWriterTest.class);
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
