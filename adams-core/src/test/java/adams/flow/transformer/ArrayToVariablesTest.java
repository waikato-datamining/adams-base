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
 * ArrayToVariablesTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.TextRenderer;
import adams.data.textrenderer.DefaultTextRenderer;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.DumpVariables;
import adams.flow.source.ForLoop;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ArrayToVariables actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class ArrayToVariablesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayToVariablesTest(String name) {
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
    return new TestSuite(ArrayToVariablesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopUpper");
      forloop.setLoopUpper((Integer) argOption.valueOf("3"));
      forloop.setOutputArray(true);

      actors.add(forloop);

      // Flow.ArrayToVariables
      ArrayToVariables arraytovariables = new ArrayToVariables();
      argOption = (AbstractArgumentOption) arraytovariables.getOptionManager().findByProperty("variableNames");
      List<VariableName> variablenames = new ArrayList<>();
      variablenames.add((VariableName) argOption.valueOf("var_a"));
      variablenames.add((VariableName) argOption.valueOf("var_b"));
      variablenames.add((VariableName) argOption.valueOf("var_c"));
      arraytovariables.setVariableNames(variablenames.toArray(new VariableName[0]));
      TextRenderer textrenderer = new TextRenderer();
      DefaultTextRenderer defaulttextrenderer = new DefaultTextRenderer();
      textrenderer.setCustomRenderer(defaulttextrenderer);

      arraytovariables.setConversion(textrenderer);

      actors.add(arraytovariables);

      // Flow.Trigger
      Trigger trigger = new Trigger();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.Trigger.DumpVariables
      DumpVariables dumpvariables = new DumpVariables();
      argOption = (AbstractArgumentOption) dumpvariables.getOptionManager().findByProperty("regExp");
      dumpvariables.setRegExp((BaseRegExp) argOption.valueOf("var_.*"));
      actors2.add(dumpvariables);

      // Flow.Trigger.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors2.add(dumpfile);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

