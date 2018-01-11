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
 * WekaNewExperimentTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.data.io.input.ArffSpreadSheetReader;
import adams.data.io.output.ArffSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.weka.classattribute.LastAttribute;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.WekaExperimentFileWriter;
import adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.FileResultsHandler;
import adams.gui.tools.wekamultiexperimenter.io.DefaultAdamsExperimentIO;
import adams.multiprocess.LocalJobRunner;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for WekaNewExperiment actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaNewExperimentTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaNewExperimentTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.expjson");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.expjson");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.expjson")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaNewExperimentTest.class);
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
      List<Actor> actors = new ArrayList<>();

      // Flow.WekaNewExperiment
      WekaNewExperiment wekanewexperiment = new WekaNewExperiment();
      CrossValidationExperiment crossvalidationexperiment = new CrossValidationExperiment();
      FileResultsHandler fileresultshandler = new FileResultsHandler();
      ArffSpreadSheetReader arffspreadsheetreader = new ArffSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      arffspreadsheetreader.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      arffspreadsheetreader.setSpreadSheetType(defaultspreadsheet);

      fileresultshandler.setReader(arffspreadsheetreader);

      ArffSpreadSheetWriter arffspreadsheetwriter = new ArffSpreadSheetWriter();
      fileresultshandler.setWriter(arffspreadsheetwriter);

      crossvalidationexperiment.setResultsHandler(fileresultshandler);

      List<Classifier> classifiers = new ArrayList<>();
      J48 j48 = new J48();
      j48.setOptions(OptionUtils.splitOptions("-C 0.25 -M 2"));
      classifiers.add(j48);
      ZeroR zeror = new ZeroR();
      classifiers.add(zeror);
      crossvalidationexperiment.setClassifiers(classifiers.toArray(new Classifier[0]));

      argOption = (AbstractArgumentOption) crossvalidationexperiment.getOptionManager().findByProperty("datasets");
      List<PlaceholderFile> datasets = new ArrayList<>();
      datasets.add((PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff"));
      datasets.add((PlaceholderFile) argOption.valueOf("${TMP}/iris.arff"));
      crossvalidationexperiment.setDatasets(datasets.toArray(new PlaceholderFile[0]));
      LastAttribute lastattribute = new LastAttribute();
      crossvalidationexperiment.setClassAttribute(lastattribute);

      LocalJobRunner localjobrunner = new LocalJobRunner();
      crossvalidationexperiment.setJobRunner(localjobrunner);

      wekanewexperiment.setExperiment(crossvalidationexperiment);

      actors.add(wekanewexperiment);

      // Flow.WekaExperimentFileWriter
      WekaExperimentFileWriter wekaexperimentfilewriter = new WekaExperimentFileWriter();
      argOption = (AbstractArgumentOption) wekaexperimentfilewriter.getOptionManager().findByProperty("outputFile");
      wekaexperimentfilewriter.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.expjson"));
      DefaultAdamsExperimentIO defaultadamsexperimentio = new DefaultAdamsExperimentIO();
      wekaexperimentfilewriter.setHandler(defaultadamsexperimentio);

      actors.add(wekaexperimentfilewriter);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

