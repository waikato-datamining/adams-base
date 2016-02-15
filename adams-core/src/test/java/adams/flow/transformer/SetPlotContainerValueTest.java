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
 * SetPlotContainerValueTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.Actor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;

/**
 * Test for SetPlotContainerValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetPlotContainerValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetPlotContainerValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
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
    return new TestSuite(SetPlotContainerValueTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[6];
      adams.flow.source.ForLoop tmp2 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("loopLower");
      tmp2.setLoopLower((Integer) argOption.valueOf("0"));

      tmp1[0] = tmp2;
      adams.flow.control.Tee tmp4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp5 = new adams.flow.core.Actor[2];
      adams.flow.transformer.MathExpression tmp6 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("expression");
      tmp6.setExpression(new MathematicalExpressionText("X^2"));

      tmp5[0] = tmp6;
      adams.flow.transformer.SetVariable tmp8 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("variableName");
      tmp8.setVariableName((adams.core.VariableName) argOption.valueOf("y"));

      tmp5[1] = tmp8;
      tmp4.setActors(tmp5);

      tmp1[1] = tmp4;
      adams.flow.transformer.MakePlotContainer tmp10 = new adams.flow.transformer.MakePlotContainer();
      tmp1[2] = tmp10;
      adams.flow.transformer.SetPlotContainerValue tmp11 = new adams.flow.transformer.SetPlotContainerValue();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("containerValue");
      tmp11.setContainerValue((adams.flow.control.PlotContainerUpdater.PlotContainerValue) argOption.valueOf("X_VALUE"));

      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("value");
      argOption.setVariable("@{y}");

      tmp1[3] = tmp11;
      adams.flow.transformer.Convert tmp13 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp15 = new adams.data.conversion.AnyToString();
      tmp13.setConversion(tmp15);

      tmp1[4] = tmp13;
      adams.flow.sink.DumpFile tmp16 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("outputFile");
      tmp16.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp16.setAppend(true);

      tmp1[5] = tmp16;
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

