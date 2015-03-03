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
 * IncludeExternalTransformerTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for IncludeExternalTransformer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class IncludeExternalTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IncludeExternalTransformerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("external_transformer.flow");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("external_transformer.flow");
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
    return new TestSuite(IncludeExternalTransformerTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop2 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopLower");
      forloop2.setLoopLower((Integer) argOption.valueOf("10"));
      argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopUpper");
      forloop2.setLoopUpper((Integer) argOption.valueOf("20"));
      actors1[0] = forloop2;

      // Flow.IncludeExternalTransformer
      adams.flow.transformer.IncludeExternalTransformer includeexternaltransformer5 = new adams.flow.transformer.IncludeExternalTransformer();
      argOption = (AbstractArgumentOption) includeexternaltransformer5.getOptionManager().findByProperty("actorFile");
      includeexternaltransformer5.setActorFile((adams.core.io.FlowFile) argOption.valueOf("${TMP}/external_transformer.flow"));
      actors1[1] = includeexternaltransformer5;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile7 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile7.getOptionManager().findByProperty("outputFile");
      dumpfile7.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile7.setAppend(true);

      actors1[2] = dumpfile7;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener10 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener10);

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

