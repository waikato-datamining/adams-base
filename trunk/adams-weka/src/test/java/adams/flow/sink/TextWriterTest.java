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
 * TextWriterTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.SimpleFilenameGenerator;
import adams.data.io.output.TextFileWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.source.StringConstants;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.SetVariable;
import adams.flow.transformer.StringReplace;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaCrossValidationEvaluator;
import adams.flow.transformer.WekaEvaluationSummary;
import adams.flow.transformer.WekaFileReader;
import adams.test.TmpDirectory;
import adams.test.TmpFile;

/**
 * Tests the TextWriter actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TextWriterTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile-vote.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-labor.txt");
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
    m_TestHelper.deleteFileFromTmp("dumpfile-vote.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-labor.txt");

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
	new BaseString("vote"),
	new BaseString("labor")
    });

    SetVariable sv = new SetVariable();
    sv.setVariableName(new VariableName("dataset"));

    StringReplace sr1 = new StringReplace();
    sr1.setFind(new BaseRegExp("^"));
    sr1.setReplace(m_TestHelper.getTmpDirectory().replace("\\", "/") + "/");

    StringReplace sr2 = new StringReplace();
    sr2.setFind(new BaseRegExp("$"));
    sr2.setReplace(".arff");

    WekaFileReader fr = new WekaFileReader();

    WekaClassSelector cs = new WekaClassSelector();

    WekaCrossValidationEvaluator cv = new WekaCrossValidationEvaluator();
    cv.setClassifier(new CallableActorReference("cls"));

    WekaEvaluationSummary eval = new WekaEvaluationSummary();

    TextFileWriter tfw = new TextFileWriter();
    SimpleFilenameGenerator fgen = new SimpleFilenameGenerator();
    fgen.setDirectory(new TmpDirectory());
    fgen.setPrefix("dumpfile-");
    fgen.setSuffix(".txt");
    tfw.setFilenameGenerator(fgen);
    TextWriter tw = new TextWriter();
    tw.setWriter(tfw);
    tw.getOptionManager().setVariableForProperty("contentName", "dataset");

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sc, sv, sr1, sr2, fr, cs, cv, eval, tw});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile[]{
	    new TmpFile("dumpfile-vote.txt"),
	    new TmpFile("dumpfile-labor.txt")
	});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TextWriterTest.class);
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
