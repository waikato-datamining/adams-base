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
 * DeleteDirTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.DirectoryExists;
import adams.flow.control.Flow;
import adams.flow.control.IfThenElse;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.VariableValueType;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.Start;
import adams.flow.source.Variable;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DeleteDir actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class DeleteDirTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeleteDirTest(String name) {
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
    return new TestSuite(DeleteDirTest.class);
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

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("dir"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("${TMP}/testdir"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("valueType");
      setvariable.setValueType((VariableValueType) argOption.valueOf("FILE_FORWARD_SLASHES"));
      actors.add(setvariable);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.create dir
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("create dir"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.create dir.MakeDir
      MakeDir makedir = new MakeDir();
      argOption = (AbstractArgumentOption) makedir.getOptionManager().findByProperty("directory");
      argOption.setVariable("@{dir}");
      actors2.add(makedir);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.first check
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("first check"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.first check.IfThenElse
      IfThenElse ifthenelse = new IfThenElse();
      DirectoryExists directoryexists = new DirectoryExists();
      argOption = (AbstractArgumentOption) directoryexists.getOptionManager().findByProperty("directory");
      argOption.setVariable("@{dir}");
      ifthenelse.setCondition(directoryexists);


      // Flow.first check.IfThenElse.then
      adams.flow.transformer.SetVariable setvariable2 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
      setvariable2.setName((String) argOption.valueOf("then"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("true"));
      ifthenelse.setThenActor(setvariable2);


      // Flow.first check.IfThenElse.else
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("name");
      setvariable3.setName((String) argOption.valueOf("else"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("false"));
      ifthenelse.setElseActor(setvariable3);

      actors3.add(ifthenelse);
      tee.setActors(actors3.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.output
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("output"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.output.Variable
      Variable variable = new Variable();
      argOption = (AbstractArgumentOption) variable.getOptionManager().findByProperty("variableName");
      variable.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      StringToString stringtostring = new StringToString();
      variable.setConversion(stringtostring);

      actors4.add(variable);

      // Flow.output.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors4.add(dumpfile);
      trigger2.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.delete dir
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("delete dir"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.delete dir.DeleteDir
      DeleteDir deletedir = new DeleteDir();
      argOption = (AbstractArgumentOption) deletedir.getOptionManager().findByProperty("directory");
      argOption.setVariable("@{dir}");
      deletedir.setDeleteItself(true);

      actors5.add(deletedir);
      trigger3.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger3);

      // Flow.second check
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("second check"));
      List<Actor> actors6 = new ArrayList<>();

      // Flow.second check.IfThenElse
      IfThenElse ifthenelse2 = new IfThenElse();
      DirectoryExists directoryexists2 = new DirectoryExists();
      argOption = (AbstractArgumentOption) directoryexists2.getOptionManager().findByProperty("directory");
      argOption.setVariable("@{dir}");
      ifthenelse2.setCondition(directoryexists2);


      // Flow.second check.IfThenElse.then
      adams.flow.transformer.SetVariable setvariable4 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("name");
      setvariable4.setName((String) argOption.valueOf("then"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableValue");
      setvariable4.setVariableValue((BaseText) argOption.valueOf("true"));
      ifthenelse2.setThenActor(setvariable4);


      // Flow.second check.IfThenElse.else
      adams.flow.transformer.SetVariable setvariable5 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("name");
      setvariable5.setName((String) argOption.valueOf("else"));
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("variableName");
      setvariable5.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("variableValue");
      setvariable5.setVariableValue((BaseText) argOption.valueOf("false"));
      ifthenelse2.setElseActor(setvariable5);

      actors6.add(ifthenelse2);
      tee2.setActors(actors6.toArray(new Actor[0]));

      actors.add(tee2);

      // Flow.output (2)
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("output (2)"));
      List<Actor> actors7 = new ArrayList<>();

      // Flow.output (2).Variable
      Variable variable2 = new Variable();
      argOption = (AbstractArgumentOption) variable2.getOptionManager().findByProperty("variableName");
      variable2.setVariableName((VariableName) argOption.valueOf("dir_exists"));
      StringToString stringtostring2 = new StringToString();
      variable2.setConversion(stringtostring2);

      actors7.add(variable2);

      // Flow.output (2).DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);

      actors7.add(dumpfile2);
      trigger4.setActors(actors7.toArray(new Actor[0]));

      actors.add(trigger4);
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

