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
 * UpdatePropertiesTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for UpdateProperties actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class UpdatePropertiesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdatePropertiesTest(String name) {
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

    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
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
    return new TestSuite(UpdatePropertiesTest.class);
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
      adams.flow.source.ForLoop tmp2 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("loopUpper");
      tmp2.setLoopUpper((Integer) argOption.valueOf("5"));

      tmp1[0] = tmp2;
      adams.flow.control.Tee tmp4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("att index"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("teeActor");
      adams.flow.transformer.SetVariable tmp7 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("index"));

      tmp4.add(0, tmp7);

      tmp1[1] = tmp4;
      adams.flow.control.Tee tmp9 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("name");
      tmp9.setName((java.lang.String) argOption.valueOf("att name"));

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("teeActor");
      adams.flow.control.Sequence tmp12 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp13 = new adams.flow.core.AbstractActor[3];
      adams.flow.transformer.Convert tmp14 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString tmp16 = new adams.data.conversion.AnyToString();
      tmp14.setConversion(tmp16);

      tmp13[0] = tmp14;
      adams.flow.transformer.StringReplace tmp17 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("find");
      tmp17.setFind((adams.core.base.BaseRegExp) argOption.valueOf("^"));

      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("replace");
      tmp17.setReplace((java.lang.String) argOption.valueOf("att-"));

      tmp13[1] = tmp17;
      adams.flow.transformer.SetVariable tmp20 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("variableName");
      tmp20.setVariableName((adams.core.VariableName) argOption.valueOf("attname"));

      tmp13[2] = tmp20;
      tmp12.setActors(tmp13);

      tmp9.add(0, tmp12);

      tmp1[2] = tmp9;
      adams.flow.control.Trigger tmp22 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("teeActor");
      adams.flow.core.AbstractActor[] tmp25 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.FileSupplier tmp26 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("files");
      tmp26.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.arff")});

      tmp25[0] = tmp26;
      adams.flow.transformer.WekaFileReader tmp28 = new adams.flow.transformer.WekaFileReader();

      tmp25[1] = tmp28;
      adams.flow.control.UpdateProperties tmp31 = new adams.flow.control.UpdateProperties();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("properties");
      adams.core.base.BaseString[] tmp32 = new adams.core.base.BaseString[2];
      tmp32[0] = (adams.core.base.BaseString) argOption.valueOf("filter.attributeName");
      tmp32[1] = (adams.core.base.BaseString) argOption.valueOf("filter.attributeIndex");
      tmp31.setProperties(tmp32);

      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("variableNames");
      adams.core.VariableName[] tmp33 = new adams.core.VariableName[2];
      tmp33[0] = (adams.core.VariableName) argOption.valueOf("attname");
      tmp33[1] = (adams.core.VariableName) argOption.valueOf("index");
      tmp31.setVariableNames(tmp33);

      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("subActor");
      adams.flow.transformer.WekaFilter tmp35 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) tmp35.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Add tmp37 = new weka.filters.unsupervised.attribute.Add();
      tmp37.setOptions(OptionUtils.splitOptions("-N unnamed -C last"));
      tmp35.setFilter(tmp37);

      tmp31.setSubActor(tmp35);

      tmp25[2] = tmp31;
      adams.flow.sink.DumpFile tmp38 = new adams.flow.sink.DumpFile();
      tmp38.setAppend(true);
      tmp38.setOutputFile(new TmpFile("dumpfile.txt"));

      tmp25[3] = tmp38;
      tmp22.setActors(tmp25);

      tmp1[3] = tmp22;
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

