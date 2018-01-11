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
 * WekaGenericPLSMatrixAccessTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.instancesanalysis.pls.PreprocessingType;
import adams.data.instancesanalysis.pls.PLS1;

/**
 * Test for WekaGenericPLSMatrixAccess actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaGenericPLSMatrixAccessTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaGenericPLSMatrixAccessTest(String name) {
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
    return new TestSuite(WekaGenericPLSMatrixAccessTest.class);
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
      Actor[] abstractactor1 = new Actor[7];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      Actor[] abstractactor3 = new Actor[1];

      // Flow.CallableActors.WekaClassifierSetup
      adams.flow.source.WekaClassifierSetup wekaclassifiersetup4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) wekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      weka.classifiers.functions.PLSWeighted plsweighted = new weka.classifiers.functions.PLSWeighted();
      PLS1 pls1 = new PLS1();
      pls1.setReplaceMissing(true);
      pls1.setPreprocessingType(PreprocessingType.CENTER);
      pls1.setNumComponents(3);
      plsweighted.setAlgorithm(pls1);
      wekaclassifiersetup4.setClassifier(plsweighted);

      abstractactor3[0] = wekaclassifiersetup4;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile8 = new adams.core.io.PlaceholderFile[1];
      placeholderfile8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.arff");
      filesupplier7.setFiles(placeholderfile8);

      abstractactor1[1] = filesupplier7;

      // Flow.WekaFileReader
      WekaFileReader wekafilereader9 = new WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader11 = new weka.core.converters.ArffLoader();
      wekafilereader9.setCustomLoader(arffloader11);

      abstractactor1[2] = wekafilereader9;

      // Flow.WekaClassSelector
      WekaClassSelector wekaclassselector12 = new WekaClassSelector();
      abstractactor1[3] = wekaclassselector12;

      // Flow.WekaTrainClassifier
      WekaTrainClassifier wekatrainclassifier13 = new WekaTrainClassifier();
      abstractactor1[4] = wekatrainclassifier13;

      // Flow.WekaGenericPLSMatrixAccess
      WekaGenericPLSMatrixAccess wekaextractplsmatrix14 = new WekaGenericPLSMatrixAccess();
      wekaextractplsmatrix14.setMatrix("P");
      abstractactor1[5] = wekaextractplsmatrix14;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[6] = dumpfile15;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener18 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener18);

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
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}

