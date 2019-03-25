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
 * WekaDatasetsMergeTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.weka.columnfinder.ByName;
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
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.WekaFileWriter;
import adams.flow.source.FileSupplier;
import adams.flow.source.Start;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.wekadatasetsmerge.JoinOnID;
import adams.flow.transformer.wekadatasetsmerge.Simple;
import weka.core.converters.SimpleArffLoader;
import weka.core.converters.SimpleArffSaver;

/**
 * Test for WekaDatasetsMerge actor.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class WekaDatasetsMergeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaDatasetsMergeTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("test-input-1.arff");
    m_TestHelper.copyResourceToTmp("test-input-2.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("test-input-1.arff");
    m_TestHelper.deleteFileFromTmp("test-input-2.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
        new TmpFile("dumpfile1.arff"),
        new TmpFile("dumpfile2.arff")
      });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaDatasetsMergeTest.class);
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

      // Flow.CallableActors.ARFFFilesToDatasets
      ArrayProcess arrayprocess = new ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess.getOptionManager().findByProperty("name");
      arrayprocess.setName((String) argOption.valueOf("ARFFFilesToDatasets"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.CallableActors.ARFFFilesToDatasets.WekaFileReader
      WekaFileReader wekafilereader = new WekaFileReader();
      SimpleArffLoader simplearffloader = new SimpleArffLoader();
      wekafilereader.setCustomLoader(simplearffloader);

      actors3.add(wekafilereader);

      // Flow.CallableActors.ARFFFilesToDatasets.WekaClassSelector
      WekaClassSelector wekaclassselector = new WekaClassSelector();
      actors3.add(wekaclassselector);
      arrayprocess.setActors(actors3.toArray(new Actor[0]));

      actors2.add(arrayprocess);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.simple-pass
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("simple-pass"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.simple-pass.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      filesupplier.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-1.arff"));
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-2.arff"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors4.add(filesupplier);

      // Flow.simple-pass.CallableTransformer
      CallableTransformer callabletransformer = new CallableTransformer();
      argOption = (AbstractArgumentOption) callabletransformer.getOptionManager().findByProperty("callableName");
      callabletransformer.setCallableName((CallableActorReference) argOption.valueOf("ARFFFilesToDatasets"));
      actors4.add(callabletransformer);

      // Flow.simple-pass.WekaDatasetsMerge
      WekaDatasetsMerge wekadatasetsmerge = new WekaDatasetsMerge();
      Simple simple = new Simple();
      ByName byName = new ByName();
      byName.setRegExp(new BaseRegExp("^.*class.*$"));
      simple.setClassFinder(byName);
      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("datasetNames");
      List<BaseString> datasetnames = new ArrayList<>();
      datasetnames.add((BaseString) argOption.valueOf("input1"));
      datasetnames.add((BaseString) argOption.valueOf("input2"));
      simple.setDatasetNames(datasetnames.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("attributeRenamesExp");
      List<BaseRegExp> attributerenamesexp = new ArrayList<>();
      attributerenamesexp.add((BaseRegExp) argOption.valueOf(".*"));
      attributerenamesexp.add((BaseRegExp) argOption.valueOf(".*"));
      simple.setAttributeRenamesExp(attributerenamesexp.toArray(new BaseRegExp[0]));
      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("attributeRenamesFormat");
      List<BaseString> attributerenamesformat = new ArrayList<>();
      attributerenamesformat.add((BaseString) argOption.valueOf("{DATASET}-$0"));
      attributerenamesformat.add((BaseString) argOption.valueOf("{DATASET}-$0"));
      simple.setAttributeRenamesFormat(attributerenamesformat.toArray(new BaseString[0]));
      wekadatasetsmerge.setMergeMethod(simple);

      actors4.add(wekadatasetsmerge);

      // Flow.simple-pass.WekaFileWriter
      WekaFileWriter wekafilewriter = new WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter.getOptionManager().findByProperty("outputFile");
      wekafilewriter.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile1.arff"));
      SimpleArffSaver simplearffsaver = new SimpleArffSaver();
      adams.core.option.WekaCommandLineHandler wekacommandlinehandler = new adams.core.option.WekaCommandLineHandler();
      wekacommandlinehandler.setOptions(simplearffsaver, OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter.setCustomSaver(simplearffsaver);

      actors4.add(wekafilewriter);
      trigger.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.id-pass
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("id-pass"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.id-pass.FileSupplier
      FileSupplier filesupplier2 = new FileSupplier();
      filesupplier2.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files2 = new ArrayList<>();
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-1.arff"));
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-2.arff"));
      filesupplier2.setFiles(files2.toArray(new PlaceholderFile[0]));
      actors5.add(filesupplier2);

      // Flow.id-pass.CallableTransformer
      CallableTransformer callabletransformer2 = new CallableTransformer();
      argOption = (AbstractArgumentOption) callabletransformer2.getOptionManager().findByProperty("callableName");
      callabletransformer2.setCallableName((CallableActorReference) argOption.valueOf("ARFFFilesToDatasets"));
      actors5.add(callabletransformer2);

      // Flow.id-pass.WekaDatasetsMerge
      WekaDatasetsMerge wekadatasetsmerge2 = new WekaDatasetsMerge();
      JoinOnID joinonid = new JoinOnID();
      byName = new ByName();
      byName.setRegExp(new BaseRegExp("^.*class.*$"));
      joinonid.setClassFinder(byName);
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("datasetNames");
      List<BaseString> datasetnames2 = new ArrayList<>();
      datasetnames2.add((BaseString) argOption.valueOf("input1"));
      datasetnames2.add((BaseString) argOption.valueOf("input2"));
      joinonid.setDatasetNames(datasetnames2.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("attributeRenamesExp");
      List<BaseRegExp> attributerenamesexp2 = new ArrayList<>();
      attributerenamesexp2.add((BaseRegExp) argOption.valueOf("^.*$"));
      attributerenamesexp2.add((BaseRegExp) argOption.valueOf("^.*$"));
      joinonid.setAttributeRenamesExp(attributerenamesexp2.toArray(new BaseRegExp[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("attributeRenamesFormat");
      List<BaseString> attributerenamesformat2 = new ArrayList<>();
      attributerenamesformat2.add((BaseString) argOption.valueOf("{DATASET}-$0"));
      attributerenamesformat2.add((BaseString) argOption.valueOf("{DATASET}-$0"));
      joinonid.setAttributeRenamesFormat(attributerenamesformat2.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("uniqueID");
      joinonid.setUniqueID((String) argOption.valueOf("id"));
      joinonid.setCompleteRowsOnly(true);

      wekadatasetsmerge2.setMergeMethod(joinonid);

      actors5.add(wekadatasetsmerge2);

      // Flow.id-pass.WekaFileWriter
      WekaFileWriter wekafilewriter2 = new WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter2.getOptionManager().findByProperty("outputFile");
      wekafilewriter2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile2.arff"));
      SimpleArffSaver simplearffsaver2 = new SimpleArffSaver();
      adams.core.option.WekaCommandLineHandler wekacommandlinehandler2 = new adams.core.option.WekaCommandLineHandler();
      wekacommandlinehandler2.setOptions(simplearffsaver2, OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter2.setCustomSaver(simplearffsaver2);

      actors5.add(wekafilewriter2);
      trigger2.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger2);
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
