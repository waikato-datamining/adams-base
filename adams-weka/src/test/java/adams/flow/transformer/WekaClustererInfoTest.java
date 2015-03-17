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
 * WekaClustererInfoTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;

/**
 * Test for WekaClustererInfo actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaClustererInfoTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaClustererInfoTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
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
    return new TestSuite(WekaClustererInfoTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.WekaClustererSetup
      adams.flow.source.WekaClustererSetup wekaclusterersetup4 = new adams.flow.source.WekaClustererSetup();
      argOption = (AbstractArgumentOption) wekaclusterersetup4.getOptionManager().findByProperty("clusterer");
      weka.clusterers.EM em6 = new weka.clusterers.EM();
      em6.setOptions(OptionUtils.splitOptions("-I 100 -N -1 -X 10 -max -1 -ll-cv 1.0E-6 -ll-iter 1.0E-6 -M 1.0E-6 -K 10 -num-slots 1 -S 100"));
      wekaclusterersetup4.setClusterer(em6);

      actors3[0] = wekaclusterersetup4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files8 = new adams.core.io.PlaceholderFile[1];
      files8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris.arff");
      filesupplier7.setFiles(files8);
      actors1[1] = filesupplier7;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.AArffLoader aarffloader11 = new weka.core.converters.AArffLoader();
      wekafilereader9.setCustomLoader(aarffloader11);

      actors1[2] = wekafilereader9;

      // Flow.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter12 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter12.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove remove14 = new weka.filters.unsupervised.attribute.Remove();
      remove14.setOptions(OptionUtils.splitOptions("-R last"));
      wekafilter12.setFilter(remove14);

      actors1[3] = wekafilter12;

      // Flow.WekaTrainClusterer
      adams.flow.transformer.WekaTrainClusterer wekatrainclusterer15 = new adams.flow.transformer.WekaTrainClusterer();
      argOption = (AbstractArgumentOption) wekatrainclusterer15.getOptionManager().findByProperty("postProcessor");
      adams.flow.transformer.wekaclusterer.PassThrough passthrough17 = new adams.flow.transformer.wekaclusterer.PassThrough();
      wekatrainclusterer15.setPostProcessor(passthrough17);

      actors1[4] = wekatrainclusterer15;

      // Flow.WekaClustererInfo
      adams.flow.transformer.WekaClustererInfo wekaclustererinfo18 = new adams.flow.transformer.WekaClustererInfo();
      actors1[5] = wekaclustererinfo18;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile19.getOptionManager().findByProperty("outputFile");
      dumpfile19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[6] = dumpfile19;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener22 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener22);

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

