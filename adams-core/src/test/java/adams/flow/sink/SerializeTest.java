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
 * SerializeTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for Serialize actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SerializeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SerializeTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("file.ser");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("file.ser");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SerializeTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[2];

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      stringconstants2.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring3 = new adams.core.base.BaseString[8];
      basestring3[0] = (adams.core.base.BaseString) argOption.valueOf("3");
      basestring3[1] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring3[2] = (adams.core.base.BaseString) argOption.valueOf("4");
      basestring3[3] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring3[4] = (adams.core.base.BaseString) argOption.valueOf("6");
      basestring3[5] = (adams.core.base.BaseString) argOption.valueOf("9");
      basestring3[6] = (adams.core.base.BaseString) argOption.valueOf("2");
      basestring3[7] = (adams.core.base.BaseString) argOption.valueOf("6");
      stringconstants2.setStrings(basestring3);

      abstractactor1[0] = stringconstants2;

      // Flow.Serialize
      adams.flow.sink.Serialize serialize4 = new adams.flow.sink.Serialize();
      argOption = (AbstractArgumentOption) serialize4.getOptionManager().findByProperty("outputFile");
      serialize4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/file.ser"));

      abstractactor1[1] = serialize4;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener7 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener7);

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

