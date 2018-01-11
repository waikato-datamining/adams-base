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
 * WekaNearestNeighborSearchTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.ContainerValuePicker;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.WekaInstanceBuffer.Operation;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.converters.SimpleArffLoader;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for WekaNearestNeighborSearch actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaNearestNeighborSearchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaNearestNeighborSearchTest(String name) {
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
    return new TestSuite(WekaNearestNeighborSearchTest.class);
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

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/iris.arff"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.WekaFileReader
      WekaFileReader wekafilereader = new WekaFileReader();
      SimpleArffLoader simplearffloader = new SimpleArffLoader();
      wekafilereader.setCustomLoader(simplearffloader);

      actors.add(wekafilereader);

      // Flow.WekaClassSelector
      WekaClassSelector wekaclassselector = new WekaClassSelector();
      actors.add(wekaclassselector);

      // Flow.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("data"));
      actors.add(setstoragevalue);

      // Flow.randomize
      WekaFilter wekafilter = new WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter.getOptionManager().findByProperty("name");
      wekafilter.setName((String) argOption.valueOf("randomize"));
      Randomize randomize = new Randomize();
      randomize.setOptions(OptionUtils.splitOptions("-S 42"));
      wekafilter.setFilter(randomize);

      wekafilter.setKeepRelationName(true);

      actors.add(wekafilter);

      // Flow.WekaFilter
      WekaFilter wekafilter2 = new WekaFilter();
      RemovePercentage removepercentage = new RemovePercentage();
      removepercentage.setOptions(OptionUtils.splitOptions("-P 95.0"));
      wekafilter2.setFilter(removepercentage);

      wekafilter2.setKeepRelationName(true);

      actors.add(wekafilter2);

      // Flow.WekaInstanceBuffer
      WekaInstanceBuffer wekainstancebuffer = new WekaInstanceBuffer();
      argOption = (AbstractArgumentOption) wekainstancebuffer.getOptionManager().findByProperty("operation");
      wekainstancebuffer.setOperation((Operation) argOption.valueOf("INSTANCES_TO_INSTANCE"));
      actors.add(wekainstancebuffer);

      // Flow.WekaNearestNeighborSearch
      WekaNearestNeighborSearch wekanearestneighborsearch = new WekaNearestNeighborSearch();
      LinearNNSearch linearnnsearch = new LinearNNSearch();
      linearnnsearch.setOptions(OptionUtils.splitOptions("-A \"weka.core.EuclideanDistance -R first-last\""));
      wekanearestneighborsearch.setSearch(linearnnsearch);

      argOption = (AbstractArgumentOption) wekanearestneighborsearch.getOptionManager().findByProperty("storage");
      wekanearestneighborsearch.setStorage((StorageName) argOption.valueOf("data"));
      actors.add(wekanearestneighborsearch);

      // Flow.ContainerValuePicker
      ContainerValuePicker containervaluepicker = new ContainerValuePicker();
      argOption = (AbstractArgumentOption) containervaluepicker.getOptionManager().findByProperty("valueName");
      containervaluepicker.setValueName((String) argOption.valueOf("Neighborhood"));
      containervaluepicker.setSwitchOutputs(true);

      actors.add(containervaluepicker);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

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

