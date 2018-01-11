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
 * WekaClusterAssignmentsTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.MakeContainer;
import adams.flow.source.SequenceSource;
import adams.flow.source.WekaClustererSetup;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.clusterers.SimpleKMeans;
import weka.core.converters.ArffLoader;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for WekaClusterAssignments actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaClusterAssignmentsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaClusterAssignmentsTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("anneal_train.arff");
    m_TestHelper.copyResourceToTmp("anneal_test.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal_train.arff");
    m_TestHelper.deleteFileFromTmp("anneal_test.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaClusterAssignmentsTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.WekaClustererSetup
      WekaClustererSetup wekaclusterersetup = new WekaClustererSetup();
      SimpleKMeans simplekmeans = new SimpleKMeans();
      simplekmeans.setOptions(OptionUtils.splitOptions("-init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
      wekaclusterersetup.setClusterer(simplekmeans);

      actors2.add(wekaclusterersetup);

      // Flow.CallableActors.train
      SequenceSource sequencesource = new SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource.getOptionManager().findByProperty("name");
      sequencesource.setName((String) argOption.valueOf("train"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.CallableActors.train.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/anneal_train.arff"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors3.add(filesupplier);

      // Flow.CallableActors.train.WekaFileReader
      WekaFileReader wekafilereader = new WekaFileReader();
      ArffLoader arffloader = new ArffLoader();
      wekafilereader.setCustomLoader(arffloader);

      actors3.add(wekafilereader);

      // Flow.CallableActors.train.WekaFilter
      WekaFilter wekafilter = new WekaFilter();
      Remove remove = new Remove();
      remove.setOptions(OptionUtils.splitOptions("-R last"));
      wekafilter.setFilter(remove);

      actors3.add(wekafilter);
      sequencesource.setActors(actors3.toArray(new Actor[0]));

      actors2.add(sequencesource);

      // Flow.CallableActors.test
      SequenceSource sequencesource2 = new SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource2.getOptionManager().findByProperty("name");
      sequencesource2.setName((String) argOption.valueOf("test"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.CallableActors.test.FileSupplier
      FileSupplier filesupplier2 = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files2 = new ArrayList<>();
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/anneal_test.arff"));
      filesupplier2.setFiles(files2.toArray(new PlaceholderFile[0]));
      actors4.add(filesupplier2);

      // Flow.CallableActors.test.WekaFileReader
      WekaFileReader wekafilereader2 = new WekaFileReader();
      ArffLoader arffloader2 = new ArffLoader();
      wekafilereader2.setCustomLoader(arffloader2);

      actors4.add(wekafilereader2);

      // Flow.CallableActors.test.WekaFilter
      WekaFilter wekafilter2 = new WekaFilter();
      Remove remove2 = new Remove();
      remove2.setOptions(OptionUtils.splitOptions("-R last"));
      wekafilter2.setFilter(remove2);

      actors4.add(wekafilter2);

      // Flow.CallableActors.test.put spreadsheet in storage
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("put spreadsheet in storage"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.CallableActors.test.put spreadsheet in storage.Convert
      Convert convert = new Convert();
      WekaInstancesToSpreadSheet wekainstancestospreadsheet = new WekaInstancesToSpreadSheet();
      DenseDataRow densedatarow = new DenseDataRow();
      wekainstancestospreadsheet.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      wekainstancestospreadsheet.setSpreadSheetType(defaultspreadsheet);

      convert.setConversion(wekainstancestospreadsheet);

      actors5.add(convert);

      // Flow.CallableActors.test.put spreadsheet in storage.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("test"));
      actors5.add(setstoragevalue);
      tee.setActors(actors5.toArray(new Actor[0]));

      actors4.add(tee);
      sequencesource2.setActors(actors4.toArray(new Actor[0]));

      actors2.add(sequencesource2);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.MakeContainer
      MakeContainer makecontainer = new MakeContainer();
      argOption = (AbstractArgumentOption) makecontainer.getOptionManager().findByProperty("callableActors");
      List<CallableActorReference> callableactors2 = new ArrayList<>();
      callableactors2.add((CallableActorReference) argOption.valueOf("train"));
      callableactors2.add((CallableActorReference) argOption.valueOf("test"));
      makecontainer.setCallableActors(callableactors2.toArray(new CallableActorReference[0]));
      argOption = (AbstractArgumentOption) makecontainer.getOptionManager().findByProperty("valueNames");
      List<BaseString> valuenames = new ArrayList<>();
      valuenames.add((BaseString) argOption.valueOf("Train"));
      valuenames.add((BaseString) argOption.valueOf("Test"));
      makecontainer.setValueNames(valuenames.toArray(new BaseString[0]));
      WekaTrainTestSetContainer wekatraintestsetcontainer = new WekaTrainTestSetContainer();
      makecontainer.setContainerClass(wekatraintestsetcontainer);

      actors.add(makecontainer);

      // Flow.WekaTrainTestSetClustererEvaluator
      WekaTrainTestSetClustererEvaluator wekatraintestsetclustererevaluator = new WekaTrainTestSetClustererEvaluator();
      wekatraintestsetclustererevaluator.setOutputModel(true);

      actors.add(wekatraintestsetclustererevaluator);

      // Flow.WekaClusterAssignments
      WekaClusterAssignments wekaclusterassignments = new WekaClusterAssignments();
      actors.add(wekaclusterassignments);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors.add(dumpfile);
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

