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
 * WekaMarginCurveTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for WekaMarginCurve actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaMarginCurveTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaMarginCurveTest(String name) {
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
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.arff");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaMarginCurveTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[6];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.WekaClassifier
      adams.flow.source.WekaClassifierSetup wekaclassifiersetup4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) wekaclassifiersetup4.getOptionManager().findByProperty("name");
      wekaclassifiersetup4.setName((java.lang.String) argOption.valueOf("WekaClassifier"));
      argOption = (AbstractArgumentOption) wekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      weka.classifiers.bayes.NaiveBayes naivebayes7 = new weka.classifiers.bayes.NaiveBayes();
      wekaclassifiersetup4.setClassifier(naivebayes7);

      actors3[0] = wekaclassifiersetup4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier8 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier8.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files9 = new adams.core.io.PlaceholderFile[1];
      files9[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/labor.arff");
      filesupplier8.setFiles(files9);
      actors1[1] = filesupplier8;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader10 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader10.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader12 = new weka.core.converters.ArffLoader();
      wekafilereader10.setCustomLoader(arffloader12);

      actors1[2] = wekafilereader10;

      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector wekaclassselector13 = new adams.flow.transformer.WekaClassSelector();
      actors1[3] = wekaclassselector13;

      // Flow.WekaCrossValidationEvaluator
      adams.flow.transformer.WekaCrossValidationEvaluator wekacrossvalidationevaluator14 = new adams.flow.transformer.WekaCrossValidationEvaluator();
      argOption = (AbstractArgumentOption) wekacrossvalidationevaluator14.getOptionManager().findByProperty("output");
      weka.classifiers.evaluation.output.prediction.Null null16 = new weka.classifiers.evaluation.output.prediction.Null();
      wekacrossvalidationevaluator14.setOutput(null16);

      argOption = (AbstractArgumentOption) wekacrossvalidationevaluator14.getOptionManager().findByProperty("classifier");
      wekacrossvalidationevaluator14.setClassifier((adams.flow.core.CallableActorReference) argOption.valueOf("WekaClassifier"));
      argOption = (AbstractArgumentOption) wekacrossvalidationevaluator14.getOptionManager().findByProperty("numThreads");
      wekacrossvalidationevaluator14.setNumThreads((Integer) argOption.valueOf("-1"));
      actors1[4] = wekacrossvalidationevaluator14;

      // Flow.WekaMarginCurve
      adams.flow.sink.WekaMarginCurve wekamargincurve19 = new adams.flow.sink.WekaMarginCurve();
      argOption = (AbstractArgumentOption) wekamargincurve19.getOptionManager().findByProperty("x");
      wekamargincurve19.setX((Integer) argOption.valueOf("-3"));
      argOption = (AbstractArgumentOption) wekamargincurve19.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter22 = new adams.gui.print.NullWriter();
      wekamargincurve19.setWriter(nullwriter22);

      actors1[5] = wekamargincurve19;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener24 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener24);

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

