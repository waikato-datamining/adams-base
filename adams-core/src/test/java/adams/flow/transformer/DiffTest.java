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
 * DiffTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for Diff actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DiffTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DiffTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("diff1.txt");
    m_TestHelper.copyResourceToTmp("diff2.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("diff1.txt");
    m_TestHelper.deleteFileFromTmp("diff2.txt");
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
    return new TestSuite(DiffTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      tmp2.setOutputArray(true);

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] tmp3 = new adams.core.io.PlaceholderFile[2];
      tmp3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff1.txt");
      tmp3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff2.txt");
      tmp2.setFiles(tmp3);

      tmp1[0] = tmp2;
      adams.flow.control.Tee tmp4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("brief"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp6 = new adams.flow.core.AbstractActor[4];
      adams.flow.transformer.Diff tmp7 = new adams.flow.transformer.Diff();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("type");
      tmp7.setType((adams.flow.transformer.Diff.DiffType) argOption.valueOf("BRIEF"));

      tmp6[0] = tmp7;
      adams.flow.transformer.Convert tmp9 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp11 = new adams.data.conversion.AnyToString();
      tmp9.setConversion(tmp11);

      tmp6[1] = tmp9;
      adams.flow.transformer.StringInsert tmp12 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("position");
      tmp12.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("value");
      tmp12.setValue((BaseString) argOption.valueOf("\n--> brief\n\n"));

      tmp6[2] = tmp12;
      adams.flow.sink.DumpFile tmp15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("outputFile");
      tmp15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp6[3] = tmp15;
      tmp4.setActors(tmp6);

      tmp1[1] = tmp4;
      adams.flow.control.Tee tmp17 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("name");
      tmp17.setName((java.lang.String) argOption.valueOf("unified"));

      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp19 = new adams.flow.core.AbstractActor[3];
      adams.flow.transformer.Diff tmp20 = new adams.flow.transformer.Diff();
      tmp19[0] = tmp20;
      adams.flow.transformer.StringInsert tmp21 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("position");
      tmp21.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("value");
      tmp21.setValue((BaseString) argOption.valueOf("\n--> unified\n\n"));

      tmp19[1] = tmp21;
      adams.flow.sink.DumpFile tmp24 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("outputFile");
      tmp24.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp24.setAppend(true);

      tmp19[2] = tmp24;
      tmp17.setActors(tmp19);

      tmp1[2] = tmp17;
      adams.flow.control.Tee tmp26 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("name");
      tmp26.setName((java.lang.String) argOption.valueOf("side-by-side"));

      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp28 = new adams.flow.core.AbstractActor[4];
      adams.flow.transformer.Diff tmp29 = new adams.flow.transformer.Diff();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("type");
      tmp29.setType((adams.flow.transformer.Diff.DiffType) argOption.valueOf("SIDE_BY_SIDE"));

      tmp28[0] = tmp29;
      adams.flow.transformer.Convert tmp31 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("conversion");
      adams.data.conversion.SideBySideDiffToString tmp33 = new adams.data.conversion.SideBySideDiffToString();
      tmp31.setConversion(tmp33);

      tmp28[1] = tmp31;
      adams.flow.transformer.StringInsert tmp34 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("position");
      tmp34.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("value");
      tmp34.setValue((BaseString) argOption.valueOf("\n--> side-by-side\n\n"));

      tmp28[2] = tmp34;
      adams.flow.sink.DumpFile tmp37 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("outputFile");
      tmp37.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp37.setAppend(true);

      tmp28[3] = tmp37;
      tmp26.setActors(tmp28);

      tmp1[3] = tmp26;
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

