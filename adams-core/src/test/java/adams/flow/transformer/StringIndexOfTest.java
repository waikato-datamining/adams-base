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
 * StringIndexOfTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
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
 * Test for StringIndexOf actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StringIndexOfTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringIndexOfTest(String name) {
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
    return new TestSuite(StringIndexOfTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.output
      adams.flow.sink.DumpFile dumpfile4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile4.getOptionManager().findByProperty("name");
      dumpfile4.setName((java.lang.String) argOption.valueOf("output"));
      argOption = (AbstractArgumentOption) dumpfile4.getOptionManager().findByProperty("outputFile");
      dumpfile4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile4.setAppend(true);

      actors3[0] = dumpfile4;
      globalactors2.setActors(actors3);

      actors1[0] = globalactors2;

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants7 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants7.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings8 = new adams.core.base.BaseString[1];
      strings8[0] = (adams.core.base.BaseString) argOption.valueOf("1234567890abcd1234567890abcd");
      stringconstants7.setStrings(strings8);
      actors1[1] = stringconstants7;

      // Flow.Tee
      adams.flow.control.Tee tee9 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors10 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee.StringIndexOf
      adams.flow.transformer.StringIndexOf stringindexof11 = new adams.flow.transformer.StringIndexOf();
      argOption = (AbstractArgumentOption) stringindexof11.getOptionManager().findByProperty("find");
      stringindexof11.setFind((java.lang.String) argOption.valueOf("abd"));
      actors10[0] = stringindexof11;

      // Flow.Tee.GlobalSink
      adams.flow.sink.CallableSink globalsink13 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink13.getOptionManager().findByProperty("callableName");
      globalsink13.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors10[1] = globalsink13;
      tee9.setActors(actors10);

      actors1[2] = tee9;

      // Flow.Tee-1
      adams.flow.control.Tee tee15 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee15.getOptionManager().findByProperty("name");
      tee15.setName((java.lang.String) argOption.valueOf("Tee-1"));
      argOption = (AbstractArgumentOption) tee15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-1.StringIndexOf
      adams.flow.transformer.StringIndexOf stringindexof18 = new adams.flow.transformer.StringIndexOf();
      argOption = (AbstractArgumentOption) stringindexof18.getOptionManager().findByProperty("find");
      stringindexof18.setFind((java.lang.String) argOption.valueOf("abc"));
      actors17[0] = stringindexof18;

      // Flow.Tee-1.GlobalSink
      adams.flow.sink.CallableSink globalsink20 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink20.getOptionManager().findByProperty("callableName");
      globalsink20.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors17[1] = globalsink20;
      tee15.setActors(actors17);

      actors1[3] = tee15;

      // Flow.Tee-2
      adams.flow.control.Tee tee22 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee22.getOptionManager().findByProperty("name");
      tee22.setName((java.lang.String) argOption.valueOf("Tee-2"));
      argOption = (AbstractArgumentOption) tee22.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors24 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-2.StringIndexOf
      adams.flow.transformer.StringIndexOf stringindexof25 = new adams.flow.transformer.StringIndexOf();
      argOption = (AbstractArgumentOption) stringindexof25.getOptionManager().findByProperty("find");
      stringindexof25.setFind((java.lang.String) argOption.valueOf("abc"));
      argOption = (AbstractArgumentOption) stringindexof25.getOptionManager().findByProperty("fromIndex");
      stringindexof25.setFromIndex((adams.core.Index) argOption.valueOf("last"));
      stringindexof25.setBackward(true);

      actors24[0] = stringindexof25;

      // Flow.Tee-2.GlobalSink
      adams.flow.sink.CallableSink globalsink28 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink28.getOptionManager().findByProperty("callableName");
      globalsink28.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors24[1] = globalsink28;
      tee22.setActors(actors24);

      actors1[4] = tee22;

      // Flow.Tee-3
      adams.flow.control.Tee tee30 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee30.getOptionManager().findByProperty("name");
      tee30.setName((java.lang.String) argOption.valueOf("Tee-3"));
      argOption = (AbstractArgumentOption) tee30.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors32 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-3.StringIndexOf
      adams.flow.transformer.StringIndexOf stringindexof33 = new adams.flow.transformer.StringIndexOf();
      argOption = (AbstractArgumentOption) stringindexof33.getOptionManager().findByProperty("find");
      stringindexof33.setFind((java.lang.String) argOption.valueOf("abC"));
      actors32[0] = stringindexof33;

      // Flow.Tee-3.GlobalSink
      adams.flow.sink.CallableSink globalsink35 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink35.getOptionManager().findByProperty("callableName");
      globalsink35.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors32[1] = globalsink35;
      tee30.setActors(actors32);

      actors1[5] = tee30;

      // Flow.Tee-4
      adams.flow.control.Tee tee37 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee37.getOptionManager().findByProperty("name");
      tee37.setName((java.lang.String) argOption.valueOf("Tee-4"));
      argOption = (AbstractArgumentOption) tee37.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors39 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-4.StringIndexOf
      adams.flow.transformer.StringIndexOf stringindexof40 = new adams.flow.transformer.StringIndexOf();
      argOption = (AbstractArgumentOption) stringindexof40.getOptionManager().findByProperty("find");
      stringindexof40.setFind((java.lang.String) argOption.valueOf("123"));
      argOption = (AbstractArgumentOption) stringindexof40.getOptionManager().findByProperty("fromIndex");
      stringindexof40.setFromIndex((adams.core.Index) argOption.valueOf("5"));
      actors39[0] = stringindexof40;

      // Flow.Tee-4.GlobalSink
      adams.flow.sink.CallableSink globalsink43 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink43.getOptionManager().findByProperty("callableName");
      globalsink43.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors39[1] = globalsink43;
      tee37.setActors(actors39);

      actors1[6] = tee37;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener46 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener46);

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

