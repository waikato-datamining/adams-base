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
 * ConditionalTransformerTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.test.FileExists;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;

/**
 * Tests the ConditionalTransformer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConditionalTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ConditionalTransformerTest(String name) {
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

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.copyResourceToTmp("labor.arff");
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
    m_TestHelper.deleteFileFromTmp("labor.arff");
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
    WekaClassifierSetup cls = new WekaClassifierSetup();
    cls.setName("cls");
    cls.setClassifier(new weka.classifiers.trees.J48());

    CallableActors ga = new CallableActors();
    ga.setActors(new AbstractActor[]{cls});

    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("${TMP}/vote.arff"),
	new BaseString("${TMP}/labor.arff")
    });

    SetVariable sv = new SetVariable();
    sv.setVariableName(new VariableName("filename"));

    WekaFileReader fr = new WekaFileReader();

    FileExists fe = new FileExists();
    fe.getOptionManager().setVariableForProperty("file", "filename");

    ConditionalTransformer ctrans = new ConditionalTransformer();
    ctrans.setCondition(fe);
    ctrans.setActor(fr);
    ctrans.setCheckAtExecutionTime(true);

    WekaClassSelector cs = new WekaClassSelector();

    WekaCrossValidationEvaluator cv = new WekaCrossValidationEvaluator();
    cv.setClassifier(new CallableActorReference("cls"));

    WekaEvaluationSummary eval = new WekaEvaluationSummary();
    eval.setOutputRelationName(true);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sc, sv, ctrans, cs, cv, eval, df});

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
    return new TestSuite(ConditionalTransformerTest.class);
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
