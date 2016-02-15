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
 * SetArrayElementTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.base.BaseClassname;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for SetArrayElement actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetArrayElementTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetArrayElementTest(String name) {
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
    return new TestSuite(SetArrayElementTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      adams.flow.source.StringConstants tmp2 = new adams.flow.source.StringConstants();
      tmp2.setOutputArray(true);

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp3 = new adams.core.base.BaseString[10];
      tmp3[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      tmp3[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      tmp3[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      tmp3[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      tmp3[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      tmp3[5] = (adams.core.base.BaseString) argOption.valueOf("6");
      tmp3[6] = (adams.core.base.BaseString) argOption.valueOf("7");
      tmp3[7] = (adams.core.base.BaseString) argOption.valueOf("8");
      tmp3[8] = (adams.core.base.BaseString) argOption.valueOf("9");
      tmp3[9] = (adams.core.base.BaseString) argOption.valueOf("10");
      tmp2.setStrings(tmp3);

      tmp1[0] = tmp2;
      adams.flow.control.ArrayProcess tmp4 = new adams.flow.control.ArrayProcess();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp5 = new adams.flow.core.Actor[1];
      adams.flow.transformer.Convert tmp6 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToDouble tmp8 = new adams.data.conversion.StringToDouble();
      tmp6.setConversion(tmp8);

      tmp5[0] = tmp6;
      tmp4.setActors(tmp5);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("arrayClass");
      tmp4.setArrayClass((BaseClassname) argOption.valueOf("java.lang.Double"));

      tmp1[1] = tmp4;
      adams.flow.transformer.SetArrayElement tmp10 = new adams.flow.transformer.SetArrayElement();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("index");
      tmp10.setIndex(new Index("5"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("value");
      tmp10.setValue((java.lang.String) argOption.valueOf("5.5"));

      tmp1[2] = tmp10;
      adams.flow.transformer.ArrayToSequence tmp13 = new adams.flow.transformer.ArrayToSequence();
      tmp1[3] = tmp13;
      adams.flow.sink.DumpFile tmp14 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("outputFile");
      tmp14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp14.setAppend(true);

      tmp1[4] = tmp14;
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

