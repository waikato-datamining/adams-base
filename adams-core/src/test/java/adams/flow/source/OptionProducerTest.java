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
 * OptionProducerTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for OptionProducer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class OptionProducerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public OptionProducerTest(String name) {
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
    return new TestSuite(OptionProducerTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[3];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      abstractactor1[0] = start2;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((java.lang.String) argOption.valueOf("Trigger-1"));

      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor5 = new adams.flow.core.Actor[2];

      // Flow.Trigger-1.StringConstants
      adams.flow.source.StringConstants stringconstants6 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants6.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring7 = new adams.core.base.BaseString[3];
      basestring7[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring7[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      basestring7[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      stringconstants6.setStrings(basestring7);

      abstractactor5[0] = stringconstants6;

      // Flow.Trigger-1.Null
      adams.flow.sink.Null null8 = new adams.flow.sink.Null();
      abstractactor5[1] = null8;
      trigger3.setActors(abstractactor5);

      abstractactor1[1] = trigger3;

      // Flow.Trigger
      adams.flow.control.Trigger trigger9 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger9.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor10 = new adams.flow.core.Actor[2];

      // Flow.Trigger.OptionProducer
      adams.flow.source.OptionProducer optionproducer11 = new adams.flow.source.OptionProducer();
      argOption = (AbstractArgumentOption) optionproducer11.getOptionManager().findByProperty("producer");
      adams.core.option.ArrayProducer arrayproducer13 = new adams.core.option.ArrayProducer();
      optionproducer11.setProducer(arrayproducer13);

      abstractactor10[0] = optionproducer11;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile14 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile14.getOptionManager().findByProperty("outputFile");
      dumpfile14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor10[1] = dumpfile14;
      trigger9.setActors(abstractactor10);

      abstractactor1[2] = trigger9;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener17 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener17);

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

