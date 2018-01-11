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
 * WekaNewInstanceTest.java
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
 * Test for WekaNewInstance actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaNewInstanceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaNewInstanceTest(String name) {
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

    m_TestHelper.copyResourceToTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.arff");
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
    return new TestSuite(WekaNewInstanceTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.sink.DumpFile tmp4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("out"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp4.setAppend(true);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.FileSupplier tmp7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("files");
      tmp7.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/labor.arff")});

      tmp1[1] = tmp7;
      adams.flow.transformer.WekaFileReader tmp9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp11 = new weka.core.converters.ArffLoader();
      tmp9.setCustomLoader(tmp11);

      tmp1[2] = tmp9;
      adams.flow.transformer.WekaClassSelector tmp12 = new adams.flow.transformer.WekaClassSelector();
      tmp1[3] = tmp12;
      adams.flow.control.Branch tmp13 = new adams.flow.control.Branch();
      tmp13.setNumThreads(1);
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("branches");
      adams.flow.core.Actor[] tmp14 = new adams.flow.core.Actor[2];
      adams.flow.control.Sequence tmp15 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("dense"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp17 = new adams.flow.core.Actor[2];
      adams.flow.transformer.WekaNewInstance tmp18 = new adams.flow.transformer.WekaNewInstance();
      tmp17[0] = tmp18;
      adams.flow.sink.CallableSink tmp19 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("callableName");
      tmp19.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("out"));

      tmp17[1] = tmp19;
      tmp15.setActors(tmp17);

      tmp14[0] = tmp15;
      adams.flow.control.Sequence tmp21 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("name");
      tmp21.setName((java.lang.String) argOption.valueOf("sparse"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp23 = new adams.flow.core.Actor[2];
      adams.flow.transformer.WekaNewInstance tmp24 = new adams.flow.transformer.WekaNewInstance();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("instanceClass");
      tmp24.setInstanceClass((java.lang.String) argOption.valueOf("weka.core.SparseInstance"));

      tmp23[0] = tmp24;
      adams.flow.sink.CallableSink tmp26 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("callableName");
      tmp26.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("out"));

      tmp23[1] = tmp26;
      tmp21.setActors(tmp23);

      tmp14[1] = tmp21;
      tmp13.setBranches(tmp14);

      tmp1[4] = tmp13;
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

