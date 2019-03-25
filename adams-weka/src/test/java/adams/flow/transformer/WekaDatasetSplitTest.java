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
 * WekaDatasetSplitTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;
import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.data.weka.columnfinder.ByName;
import adams.data.weka.datasetsplitter.ColumnSplitter;
import adams.flow.control.Tee;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.WekaFileWriter;
import adams.flow.source.FileSupplier;
import weka.core.converters.SimpleArffLoader;
import weka.core.converters.SimpleArffSaver;

/**
 * Test for WekaDatasetSplit actor.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class WekaDatasetSplitTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaDatasetSplitTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile-1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile-2.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile-1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile-2.arff");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
	new TmpFile("dumpfile-1.arff"),
	new TmpFile("dumpfile-2.arff")
      });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaDatasetSplitTest.class);
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

      // Flow.WekaDatasetSplit
      WekaDatasetSplit wekadatasetsplit = new WekaDatasetSplit();
      ColumnSplitter columnsplitter = new ColumnSplitter();
      ByName byname = new ByName();
      argOption = (AbstractArgumentOption) byname.getOptionManager().findByProperty("regExp");
      byname.setRegExp((BaseRegExp) argOption.valueOf("^class$"));
      columnsplitter.setColumnFinder(byname);

      wekadatasetsplit.setSplitter(columnsplitter);

      actors.add(wekadatasetsplit);

      // Flow.Tee
      Tee tee = new Tee();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.Tee.GetArrayElement
      GetArrayElement getarrayelement = new GetArrayElement();
      actors2.add(getarrayelement);

      // Flow.Tee.WekaFileWriter
      WekaFileWriter wekafilewriter = new WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter.getOptionManager().findByProperty("outputFile");
      wekafilewriter.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile-1.arff"));
      SimpleArffSaver simplearffsaver = new SimpleArffSaver();
      adams.core.option.WekaCommandLineHandler wekacommandlinehandler = new adams.core.option.WekaCommandLineHandler();
      wekacommandlinehandler.setOptions(simplearffsaver, OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter.setCustomSaver(simplearffsaver);

      actors2.add(wekafilewriter);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.Tee (2)
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("Tee (2)"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.Tee (2).GetArrayElement
      GetArrayElement getarrayelement2 = new GetArrayElement();
      argOption = (AbstractArgumentOption) getarrayelement2.getOptionManager().findByProperty("index");
      getarrayelement2.setIndex((Index) argOption.valueOf("2"));
      actors3.add(getarrayelement2);

      // Flow.Tee (2).WekaFileWriter
      WekaFileWriter wekafilewriter2 = new WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter2.getOptionManager().findByProperty("outputFile");
      wekafilewriter2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile-2.arff"));
      SimpleArffSaver simplearffsaver2 = new SimpleArffSaver();
      adams.core.option.WekaCommandLineHandler wekacommandlinehandler2 = new adams.core.option.WekaCommandLineHandler();
      wekacommandlinehandler2.setOptions(simplearffsaver2, OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter2.setCustomSaver(simplearffsaver2);

      actors3.add(wekafilewriter2);
      tee2.setActors(actors3.toArray(new Actor[0]));

      actors.add(tee2);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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
