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
 * OpenCVFeatureGeneratorTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.featureconverter.SpreadSheet;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.OpenCVImageReader;
import adams.data.opencv.ColorConversionCode;
import adams.data.opencv.features.Otsu;
import adams.data.opencv.transformer.ConvertType;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for OpenCVFeatureGenerator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class OpenCVFeatureGeneratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public OpenCVFeatureGeneratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("6486290583_633d994a25_z.jpg");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("6486290583_633d994a25_z.jpg");
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
    return new TestSuite(OpenCVFeatureGeneratorTest.class);
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
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/6486290583_633d994a25_z.jpg"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      filesupplier.setUseForwardSlashes(true);

      actors.add(filesupplier);

      // Flow.ImageFileReader
      ImageFileReader imagefilereader = new ImageFileReader();
      OpenCVImageReader opencvimagereader = new OpenCVImageReader();
      imagefilereader.setReader(opencvimagereader);

      DefaultSimpleReportReader defaultsimplereportreader = new DefaultSimpleReportReader();
      imagefilereader.setMetaDataReader(defaultsimplereportreader);

      actors.add(imagefilereader);

      // Flow.OpenCVTransformer
      ConvertType convert = new ConvertType();
      convert.setConversion(ColorConversionCode.COLOR_BGR2GRAY);
      OpenCVTransformer transformer = new OpenCVTransformer();
      transformer.setTransformAlgorithm(convert);
      actors.add(transformer);

      // Flow.OpenCVFeatureGenerator
      OpenCVFeatureGenerator opencvfeaturegenerator = new OpenCVFeatureGenerator();
      Otsu otsu = new Otsu();
      SpreadSheet spreadsheet = new SpreadSheet();
      DenseDataRow densedatarow = new DenseDataRow();
      spreadsheet.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      spreadsheet.setSpreadSheetType(defaultspreadsheet);

      otsu.setConverter(spreadsheet);

      opencvfeaturegenerator.setAlgorithm(otsu);

      actors.add(opencvfeaturegenerator);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors.add(dumpfile);
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

