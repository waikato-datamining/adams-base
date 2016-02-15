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
 * ArrayCombinationsTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for ArrayCombinations actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ArrayCombinationsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayCombinationsTest(String name) {
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
    return new TestSuite(ArrayCombinationsTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[4];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.control.Sequence tmp4 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("Output"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp6 = new adams.flow.core.Actor[2];
      adams.flow.transformer.Convert tmp7 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp9 = new adams.data.conversion.AnyToString();
      tmp7.setConversion(tmp9);

      tmp6[0] = tmp7;
      adams.flow.sink.DumpFile tmp10 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("outputFile");
      tmp10.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp10.setAppend(true);

      tmp6[1] = tmp10;
      tmp4.setActors(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.StringConstants tmp12 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp13 = new adams.core.base.BaseString[1];
      tmp13[0] = (adams.core.base.BaseString) argOption.valueOf("1,2,3,4,5,6,7,8,9,10");
      tmp12.setStrings(tmp13);

      tmp1[1] = tmp12;
      adams.flow.transformer.StringSplit tmp14 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("expression");
      tmp14.setExpression(",");

      tmp1[2] = tmp14;
      adams.flow.control.Branch tmp16 = new adams.flow.control.Branch();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("branches");
      adams.flow.core.Actor[] tmp17 = new adams.flow.core.Actor[3];
      adams.flow.control.Sequence tmp18 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("name");
      tmp18.setName((java.lang.String) argOption.valueOf("combinations"));

      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp20 = new adams.flow.core.Actor[2];
      adams.flow.transformer.ArrayCombinations tmp21 = new adams.flow.transformer.ArrayCombinations();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("length");
      tmp21.setLength((Integer) argOption.valueOf("2"));

      tmp20[0] = tmp21;
      adams.flow.sink.CallableSink tmp23 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("callableName");
      tmp23.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Output"));

      tmp20[1] = tmp23;
      tmp18.setActors(tmp20);

      tmp17[0] = tmp18;
      adams.flow.control.Trigger tmp25 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("name");
      tmp25.setName((java.lang.String) argOption.valueOf("separator"));

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("teeActor");
      adams.flow.core.Actor[] tmp29 = new adams.flow.core.Actor[2];
      adams.flow.source.StringConstants tmp30 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp31 = new adams.core.base.BaseString[1];
      tmp31[0] = (adams.core.base.BaseString) argOption.valueOf("----------------");
      tmp30.setStrings(tmp31);

      tmp29[0] = tmp30;
      adams.flow.sink.CallableSink tmp32 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("callableName");
      tmp32.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Output"));

      tmp29[1] = tmp32;
      tmp25.setActors(tmp29);

      tmp17[1] = tmp25;
      adams.flow.control.Sequence tmp34 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("name");
      tmp34.setName((java.lang.String) argOption.valueOf("permutations"));

      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp36 = new adams.flow.core.Actor[2];
      adams.flow.transformer.ArrayCombinations tmp37 = new adams.flow.transformer.ArrayCombinations();
      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("length");
      tmp37.setLength((Integer) argOption.valueOf("2"));

      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("subsets");
      tmp37.setSubsets((adams.flow.transformer.ArrayCombinations.SubsetsType) argOption.valueOf("PERMUTATIONS"));

      tmp36[0] = tmp37;
      adams.flow.sink.CallableSink tmp40 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("callableName");
      tmp40.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Output"));

      tmp36[1] = tmp40;
      tmp34.setActors(tmp36);

      tmp17[2] = tmp34;
      tmp16.setBranches(tmp17);

      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("numThreads");
      tmp16.setNumThreads((Integer) argOption.valueOf("0"));

      tmp1[3] = tmp16;
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
