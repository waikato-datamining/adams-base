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

/**
 * AbstractBoofCVTransformerTestCase.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.RenderedOp;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.JAIHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageInt16;

/**
 * Ancestor for test cases tailored for BoofCV transformers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBoofCVTransformerTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractBoofCVTransformerTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/boofcv/transformer/data");
  }

  /**
   * Loads the image to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the image, null in case of an error
   */
  protected BoofCVImageContainer load(String filename) {
    String			fullName;
    RenderedOp			op;
    BoofCVImageContainer	result;

    result = null;

    m_TestHelper.copyResourceToTmp(filename);
    fullName = m_TestHelper.getTmpDirectory() + File.separator + filename;
    op       = JAIHelper.read(fullName);
    if (op != null) {
      result = new BoofCVImageContainer();
      result.setImage(ConvertBufferedImage.convertFromSingle(op.getAsBufferedImage(), null, ImageInt16.class));
      result.getReport().setStringValue(BoofCVImageContainer.FIELD_FILENAME, fullName);
    }
    m_TestHelper.deleteFileFromTmp(filename);

    return result;
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param img		the image to work on
   * @param scheme	the scheme to process the data with
   * @return		the generated data
   */
  protected BoofCVImageContainer[] process(BoofCVImageContainer img, AbstractBoofCVTransformer scheme) {
    return scheme.transform(img);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(BoofCVImageContainer data, String filename) {
    TmpFile		file;
    StringBuilder	content;
    Report		report;

    file    = new TmpFile(filename);
    content = new StringBuilder();

    content.append("Image:\n");
    content.append(
	Utils.flatten(
	    Utils.breakUp(
		Utils.arrayToString(
		    BufferedImageHelper.getPixelRaster(data.toBufferedImage())), 
		80), 
	    "\n"));
    content.append("\n");

    content.append("Report:\n");
    report = data.getReport().getClone();
    report.removeValue(new Field(AbstractImageContainer.FIELD_FILENAME, DataType.STRING));
    content.append(report);
    content.append("\n");

    content.append("Notes:\n");
    content.append(data.getNotes());
    content.append("\n");

    FileUtils.writeToFile(file.getAbsolutePath(), content, false);

    return file.exists();
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractBoofCVTransformer[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param input	the input filename (no path)
   * @param no		the number of the test
   * @param imgNo	the number of the image
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(String input, int no, int imgNo) {
    String	result;
    int		index;
    String	ext;

    ext = "-out" + no + "_" + imgNo;

    index = input.lastIndexOf('.');
    if (index == -1) {
      result = input + ext;
    }
    else {
      result  = input.substring(0, index);
      result += ext;
    }
    result += ".txt";

    return result;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    BoofCVImageContainer	data;
    BoofCVImageContainer[]	processed;
    boolean			ok;
    String			regression;
    int				i;
    int				n;
    String[]			input;
    AbstractBoofCVTransformer[]	setups;
    String[][]			output;
    List<TmpFile>		outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputFiles();
    output  = new String[input.length][];
    setups  = getRegressionSetups();
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);

      processed = process(data, setups[i]);
      assertNotNull("Failed to process data?", processed);

      output[i] = new String[processed.length];
      for (n = 0; n < output[i].length; n++) {
	output[i][n] = createOutputFilename(input[i], i, n);
	ok        = save(processed[n], output[i][n]);
	assertTrue("Failed to save regression data?", ok);
      }
    }

    // test regression
    outputFiles = new ArrayList<TmpFile>();
    for (i = 0; i < output.length; i++) {
      for (n = 0; n < output[i].length; n++)
	outputFiles.add(new TmpFile(output[i][n]));
    }
    regression = m_Regression.compare(outputFiles.toArray(new TmpFile[outputFiles.size()]));
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      for (n = 0; n < output[i].length; n++)
	m_TestHelper.deleteFileFromTmp(output[i][n]);
    }
  }
}
