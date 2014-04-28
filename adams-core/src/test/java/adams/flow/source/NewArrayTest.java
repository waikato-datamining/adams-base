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
 * NewArrayTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for NewArray actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class NewArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NewArrayTest(String name) {
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
    return new TestSuite(NewArrayTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      adams.flow.source.NewArray tmp2 = new adams.flow.source.NewArray();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("dimensions");
      tmp2.setDimensions((adams.core.base.ArrayDimensions) argOption.valueOf("[3]"));

      tmp1[0] = tmp2;
      adams.flow.transformer.SetArrayElement tmp4 = new adams.flow.transformer.SetArrayElement();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("value");
      tmp4.setValue((java.lang.String) argOption.valueOf("1st"));

      tmp1[1] = tmp4;
      adams.flow.transformer.SetArrayElement tmp6 = new adams.flow.transformer.SetArrayElement();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("name");
      tmp6.setName((java.lang.String) argOption.valueOf("SetArrayElement-1"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("index");
      tmp6.setIndex((adams.core.Index) argOption.valueOf("2"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("value");
      tmp6.setValue((java.lang.String) argOption.valueOf("2nd"));

      tmp1[2] = tmp6;
      adams.flow.transformer.SetArrayElement tmp10 = new adams.flow.transformer.SetArrayElement();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("name");
      tmp10.setName((java.lang.String) argOption.valueOf("SetArrayElement-2"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("index");
      tmp10.setIndex((adams.core.Index) argOption.valueOf("3"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("value");
      tmp10.setValue((java.lang.String) argOption.valueOf("3rd"));

      tmp1[3] = tmp10;
      adams.flow.transformer.Convert tmp14 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp16 = new adams.data.conversion.AnyToString();
      tmp14.setConversion(tmp16);

      tmp1[4] = tmp14;
      adams.flow.sink.DumpFile tmp17 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("outputFile");
      tmp17.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp1[5] = tmp17;
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
