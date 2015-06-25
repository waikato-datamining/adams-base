/*
 * PassThroughTest.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekaclusterer;

import adams.flow.core.CallableActorReference;
import adams.flow.standalone.CallableActors;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for PassThrough post-processor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class PassThroughTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PassThroughTest(String name) {
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
   * Post-processors don't have an image.
   */
  @Override
  public void testImage() {
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
    return new TestSuite(PassThroughTest.class);
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
      CallableActors call = new CallableActors();

      adams.flow.source.WekaClustererSetup tmp10 = new adams.flow.source.WekaClustererSetup();
      tmp10.setName("clusterer");
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("clusterer");
      weka.clusterers.SimpleKMeans tmp12 = new weka.clusterers.SimpleKMeans();
      tmp12.setOptions(OptionUtils.splitOptions("-N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10"));
      tmp10.setClusterer(tmp12);
      call.add(tmp10);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

      tmp1[0] = tmp2;
      adams.flow.transformer.WekaFileReader tmp4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp6 = new weka.core.converters.ArffLoader();
      tmp4.setCustomLoader(tmp6);

      tmp1[1] = tmp4;
      adams.flow.transformer.WekaFilter tmp7 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.attribute.Remove tmp9 = new weka.filters.unsupervised.attribute.Remove();
      tmp9.setOptions(OptionUtils.splitOptions("\"\" \"\" \"\""));
      tmp7.setFilter(tmp9);

      tmp1[2] = tmp7;
      adams.flow.transformer.WekaTrainClusterer tmp10b = new adams.flow.transformer.WekaTrainClusterer();
      tmp10b.setClusterer(new CallableActorReference("clusterer"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("postProcessor");
      adams.flow.transformer.wekaclusterer.PassThrough tmp14 = new adams.flow.transformer.wekaclusterer.PassThrough();
      tmp10b.setPostProcessor(tmp14);

      tmp1[3] = tmp10b;
      adams.flow.control.ContainerValuePicker tmp15 = new adams.flow.control.ContainerValuePicker();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("valueName");
      tmp15.setValueName((java.lang.String) argOption.valueOf("Clustered dataset"));

      tmp15.setSwitchOutputs(true);

      tmp1[4] = tmp15;
      adams.flow.sink.DumpFile tmp17 = new adams.flow.sink.DumpFile();
      tmp17.setOutputFile(new TmpFile("dumpfile.txt"));
      tmp1[5] = tmp17;
      flow.setActors(tmp1);
      flow.add(0, call);

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

