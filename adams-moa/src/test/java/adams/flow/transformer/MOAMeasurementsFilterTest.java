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
 * MOAMeasurementsFilterTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.MOAStream;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;

/**
 * Test for MOAMeasurementsFilter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MOAMeasurementsFilterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAMeasurementsFilterTest(String name) {
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

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile("dumpfile.txt"));
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAMeasurementsFilterTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      Actor[] tmp1 = new Actor[6];
      CallableActors tmp2 = new CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      Actor[] tmp3 = new Actor[1];
      adams.flow.source.MOAClassifierSetup tmp4 = new adams.flow.source.MOAClassifierSetup();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputInterval");
      tmp4.setName("MOAClassifier");

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      MOAStream tmp6 = new MOAStream();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("numExamples");
      tmp6.setNumExamples((Integer) argOption.valueOf("300"));

      tmp1[1] = tmp6;
      MOAClassifierEvaluation tmp8 = new MOAClassifierEvaluation();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("outputInterval");
      tmp8.setOutputInterval((Integer) argOption.valueOf("100"));

      tmp1[2] = tmp8;
      MOAMeasurementsFilter tmp10 = new MOAMeasurementsFilter();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("regExp");
      tmp10.setRegExp((BaseRegExp) argOption.valueOf("(Kappa Statistic.*|.*correct.*)"));

      tmp1[3] = tmp10;
      MOALearningEvaluation tmp12 = new MOALearningEvaluation();
      tmp1[4] = tmp12;
      DumpFile tmp13 = new DumpFile();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("outputFile");
      tmp13.setOutputFile(new TmpFile("dumpfile.txt"));

      tmp13.setAppend(true);

      tmp1[5] = tmp13;
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

