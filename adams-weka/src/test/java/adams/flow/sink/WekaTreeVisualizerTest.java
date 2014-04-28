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
 * WekaTreeVisualizerTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for WekaTreeVisualizer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaTreeVisualizerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaTreeVisualizerTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("anneal.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal.arff");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaTreeVisualizerTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[6];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.WekaClassifierSetup
      adams.flow.source.WekaClassifierSetup wekaclassifiersetup4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) wekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      weka.classifiers.trees.J48 j486 = new weka.classifiers.trees.J48();
      j486.setOptions(OptionUtils.splitOptions("-C 0.25 -M 2 \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\" \"\""));
      wekaclassifiersetup4.setClassifier(j486);

      actors3[0] = wekaclassifiersetup4;
      globalactors2.setActors(actors3);

      actors1[0] = globalactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files8 = new adams.core.io.PlaceholderFile[1];
      files8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff");
      filesupplier7.setFiles(files8);
      actors1[1] = filesupplier7;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader11 = new weka.core.converters.ArffLoader();
      wekafilereader9.setCustomLoader(arffloader11);

      actors1[2] = wekafilereader9;

      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector wekaclassselector12 = new adams.flow.transformer.WekaClassSelector();
      actors1[3] = wekaclassselector12;

      // Flow.WekaClassifier
      adams.flow.transformer.WekaTrainClassifier wekatrainclassifier13 = new adams.flow.transformer.WekaTrainClassifier();
      argOption = (AbstractArgumentOption) wekatrainclassifier13.getOptionManager().findByProperty("name");
      wekatrainclassifier13.setName((java.lang.String) argOption.valueOf("WekaClassifier"));
      actors1[4] = wekatrainclassifier13;

      // Flow.WekaTreeVisualizer
      adams.flow.sink.WekaTreeVisualizer wekatreevisualizer15 = new adams.flow.sink.WekaTreeVisualizer();
      argOption = (AbstractArgumentOption) wekatreevisualizer15.getOptionManager().findByProperty("x");
      wekatreevisualizer15.setX((Integer) argOption.valueOf("-3"));
      argOption = (AbstractArgumentOption) wekatreevisualizer15.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter18 = new adams.gui.print.NullWriter();
      wekatreevisualizer15.setWriter(nullwriter18);

      actors1[5] = wekatreevisualizer15;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener20 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener20);

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

