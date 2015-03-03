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
 * UpdateVariableNameTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.SingleStringTextReader;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for UpdateVariableName processor.
 *
 * @author fracpete
 * @version $Revision$
 */
public class UpdateVariableNameTest
  extends AbstractActorProcessorTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdateVariableNameTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UpdateVariableNameTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Simple example for using variables:\n1. The ForLoop generates the index for the file to load\n2. The Tee assembles the full path of the file to load\nand sets the variable \"filename\"\n3. The Trigger loads the file, the variable \"filename\"\nis attached to the \"file\" option of the FileSupplier\nactor."));

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp2 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.ForLoop tmp3 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("loopUpper");
      tmp3.setLoopUpper((Integer) argOption.valueOf("2"));

      tmp2[0] = tmp3;
      adams.flow.control.Tee tmp5 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("name");
      tmp5.setName((java.lang.String) argOption.valueOf("Tee (set filename variable)"));

      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp7 = new adams.flow.core.AbstractActor[4];
      adams.flow.transformer.Convert tmp8 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp10 = new adams.data.conversion.AnyToString();
      tmp8.setConversion(tmp10);

      tmp7[0] = tmp8;
      adams.flow.transformer.StringReplace tmp11 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("name");
      tmp11.setName((java.lang.String) argOption.valueOf("StringReplace (path)"));

      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("find");
      tmp11.setFind((adams.core.base.BaseRegExp) argOption.valueOf("^"));

      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("replace");
      tmp11.setReplace((java.lang.String) argOption.valueOf("${EXAMPLE_FLOWS}/data/variable"));

      tmp11.setReplaceContainsPlaceholder(true);

      tmp7[1] = tmp11;
      adams.flow.transformer.StringReplace tmp15 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("StringReplace (extension)"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("find");
      tmp15.setFind((adams.core.base.BaseRegExp) argOption.valueOf("$"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("replace");
      tmp15.setReplace((java.lang.String) argOption.valueOf(".txt"));

      tmp7[2] = tmp15;
      adams.flow.transformer.SetVariable tmp19 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("variableName");
      tmp19.setVariableName((adams.core.VariableName) argOption.valueOf("filename"));

      tmp7[3] = tmp19;
      tmp5.setActors(tmp7);

      tmp2[1] = tmp5;
      adams.flow.control.Trigger tmp21 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("name");
      tmp21.setName((java.lang.String) argOption.valueOf("Trigger (load and display file)"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp23 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp24 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("files");
      argOption.setVariable("@{filename}");

      tmp23[0] = tmp24;
      adams.flow.transformer.TextFileReader tmp25 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("outputType");
      tmp25.setReader(new SingleStringTextReader());

      tmp23[1] = tmp25;
      adams.flow.sink.HistoryDisplay tmp27 = new adams.flow.sink.HistoryDisplay();
      tmp23[2] = tmp27;
      tmp21.setActors(tmp23);

      tmp2[2] = tmp21;
      flow.setActors(tmp2);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractActorProcessor[] getRegressionSetups() {
    UpdateVariableName[]	result;

    result    = new UpdateVariableName[1];
    result[0] = new UpdateVariableName();
    result[0].setOldName("filename");
    result[0].setNewName("funky");

    return result;
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

