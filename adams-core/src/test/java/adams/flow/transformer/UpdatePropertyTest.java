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
 * UpdatePropertyTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for UpdateProperty actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class UpdatePropertyTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdatePropertyTest(String name) {
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
    return new TestSuite(UpdatePropertyTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.DumpFile
      adams.flow.sink.DumpFile dumpfile4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile4.getOptionManager().findByProperty("outputFile");
      dumpfile4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile4.setAppend(true);

      actors3[0] = dumpfile4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.SelectObjects
      adams.flow.source.SelectObjects selectobjects6 = new adams.flow.source.SelectObjects();
      argOption = (AbstractArgumentOption) selectobjects6.getOptionManager().findByProperty("superClass");
      selectobjects6.setSuperClass((java.lang.String) argOption.valueOf("adams.flow.core.AbstractActor"));
      argOption = (AbstractArgumentOption) selectobjects6.getOptionManager().findByProperty("initialObjects");
      adams.core.base.BaseString[] initialobjects8 = new adams.core.base.BaseString[1];
      initialobjects8[0] = (adams.core.base.BaseString) argOption.valueOf("adams.flow.sink.Display");
      selectobjects6.setInitialObjects(initialobjects8);
      selectobjects6.setNonInteractive(true);
      actors1[1] = selectobjects6;

      // Flow.get
      adams.flow.control.Tee tee9 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("name");
      tee9.setName((java.lang.String) argOption.valueOf("get"));
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors11 = new adams.flow.core.AbstractActor[2];

      // Flow.get.GetProperty
      adams.flow.transformer.GetProperty getproperty12 = new adams.flow.transformer.GetProperty();
      argOption = (AbstractArgumentOption) getproperty12.getOptionManager().findByProperty("property");
      getproperty12.setProperty((java.lang.String) argOption.valueOf("writer.enabled"));
      actors11[0] = getproperty12;

      // Flow.get.CallableSink
      adams.flow.sink.CallableSink callablesink14 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink14.getOptionManager().findByProperty("callableName");
      callablesink14.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));
      actors11[1] = callablesink14;
      tee9.setActors(actors11);

      actors1[2] = tee9;

      // Flow.UpdateProperty
      adams.flow.transformer.UpdateProperty updateproperty16 = new adams.flow.transformer.UpdateProperty();
      argOption = (AbstractArgumentOption) updateproperty16.getOptionManager().findByProperty("property");
      updateproperty16.setProperty((java.lang.String) argOption.valueOf("writer.enabled"));
      argOption = (AbstractArgumentOption) updateproperty16.getOptionManager().findByProperty("value");
      updateproperty16.setValue((java.lang.String) argOption.valueOf("false"));
      actors1[3] = updateproperty16;

      // Flow.get-1
      adams.flow.control.Tee tee19 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee19.getOptionManager().findByProperty("name");
      tee19.setName((java.lang.String) argOption.valueOf("get-1"));
      argOption = (AbstractArgumentOption) tee19.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors21 = new adams.flow.core.AbstractActor[2];

      // Flow.get-1.GetProperty
      adams.flow.transformer.GetProperty getproperty22 = new adams.flow.transformer.GetProperty();
      argOption = (AbstractArgumentOption) getproperty22.getOptionManager().findByProperty("property");
      getproperty22.setProperty((java.lang.String) argOption.valueOf("writer.enabled"));
      actors21[0] = getproperty22;

      // Flow.get-1.CallableSink
      adams.flow.sink.CallableSink callablesink24 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink24.getOptionManager().findByProperty("callableName");
      callablesink24.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));
      actors21[1] = callablesink24;
      tee19.setActors(actors21);

      actors1[4] = tee19;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener27 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener27);

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

