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
 * DeleteJsonValueTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseText;
import adams.core.base.JsonPathExpression;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.JsonToString;
import adams.data.conversion.StringToJson;
import adams.data.conversion.StringToString;
import adams.data.json.JsonObjectType;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.CombineVariables;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DeleteJsonValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class DeleteJsonValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeleteJsonValueTest(String name) {
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
    return new TestSuite(DeleteJsonValueTest.class);
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

      // Flow.CombineVariables
      CombineVariables combinevariables = new CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
      combinevariables.setExpression((BaseText) argOption.valueOf("{\n  \\\"a\\\": 123,\n  \\\"b\\\": 456,\n  \\\"c\\\": 789\n}"));
      StringToString stringtostring = new StringToString();
      combinevariables.setConversion(stringtostring);

      actors.add(combinevariables);

      // Flow.Convert
      Convert convert = new Convert();
      StringToJson stringtojson = new StringToJson();
      argOption = (AbstractArgumentOption) stringtojson.getOptionManager().findByProperty("type");
      stringtojson.setType((JsonObjectType) argOption.valueOf("OBJECT"));
      convert.setConversion(stringtojson);

      actors.add(convert);

      // Flow.DeleteJsonValue
      DeleteJsonValue deletejsonvalue = new DeleteJsonValue();
      argOption = (AbstractArgumentOption) deletejsonvalue.getOptionManager().findByProperty("path");
      deletejsonvalue.setPath((JsonPathExpression) argOption.valueOf("b"));
      actors.add(deletejsonvalue);

      // Flow.Convert (2)
      Convert convert2 = new Convert();
      argOption = (AbstractArgumentOption) convert2.getOptionManager().findByProperty("name");
      convert2.setName((String) argOption.valueOf("Convert (2)"));
      JsonToString jsontostring = new JsonToString();
      jsontostring.setPrettyPrinting(true);

      convert2.setConversion(jsontostring);

      actors.add(convert2);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);
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

