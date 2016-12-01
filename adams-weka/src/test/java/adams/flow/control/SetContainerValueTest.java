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
 * SetContainerValueTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.SetStorageValue;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFilter;
import adams.flow.transformer.WekaRandomSplit;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.converters.AArffLoader;
import weka.filters.unsupervised.instance.Resample;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SetContainerValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetContainerValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetContainerValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("anneal.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.arff")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SetContainerValueTest.class);
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
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.WekaFileReader
      WekaFileReader wekafilereader = new WekaFileReader();
      AArffLoader aarffloader = new AArffLoader();
      wekafilereader.setCustomLoader(aarffloader);

      actors.add(wekafilereader);

      // Flow.WekaClassSelector
      WekaClassSelector wekaclassselector = new WekaClassSelector();
      actors.add(wekaclassselector);

      // Flow.WekaRandomSplit
      WekaRandomSplit wekarandomsplit = new WekaRandomSplit();
      actors.add(wekarandomsplit);

      // Flow.ContainerValuePicker
      ContainerValuePicker containervaluepicker = new ContainerValuePicker();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.ContainerValuePicker.WekaFilter
      WekaFilter wekafilter = new WekaFilter();
      Resample resample = new Resample();
      resample.setOptions(OptionUtils.splitOptions("-S 1 -Z 50.0 -no-replacement"));
      wekafilter.setFilter(resample);

      actors2.add(wekafilter);

      // Flow.ContainerValuePicker.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("test"));
      actors2.add(setstoragevalue);
      containervaluepicker.setActors(actors2.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) containervaluepicker.getOptionManager().findByProperty("valueName");
      containervaluepicker.setValueName((String) argOption.valueOf("Test"));
      actors.add(containervaluepicker);

      // Flow.SetContainerValue
      SetContainerValue simpleupdatecontainervalue = new SetContainerValue();
      argOption = (AbstractArgumentOption) simpleupdatecontainervalue.getOptionManager().findByProperty("storageName");
      simpleupdatecontainervalue.setStorageName((StorageName) argOption.valueOf("test"));
      simpleupdatecontainervalue.setUseStorage(true);

      argOption = (AbstractArgumentOption) simpleupdatecontainervalue.getOptionManager().findByProperty("valueName");
      simpleupdatecontainervalue.setValueName((BaseString) argOption.valueOf("Test"));
      actors.add(simpleupdatecontainervalue);

      // Flow.ContainerValuePicker-1
      ContainerValuePicker containervaluepicker2 = new ContainerValuePicker();
      argOption = (AbstractArgumentOption) containervaluepicker2.getOptionManager().findByProperty("name");
      containervaluepicker2.setName((String) argOption.valueOf("ContainerValuePicker-1"));
      argOption = (AbstractArgumentOption) containervaluepicker2.getOptionManager().findByProperty("valueName");
      containervaluepicker2.setValueName((String) argOption.valueOf("Test"));
      containervaluepicker2.setSwitchOutputs(true);

      actors.add(containervaluepicker2);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.arff"));
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

