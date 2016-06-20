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
 * ImageMetaDataHelperTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.image;

import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.Regression;
import adams.test.TestHelper;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Tests the meta-data extraction from images using the {@link ImageMetaDataHelper}
 * class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMetaDataHelperTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public ImageMetaDataHelperTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/image/data");
  }

  /**
   * Removes the specified key (first column) from the spreadsheet.
   *
   * @param sheet	the spreadsheet to process
   * @param key		the key to remove
   */
  protected void removeKey(SpreadSheet sheet, String key) {
    int		i;
    Cell 	cell;

    for (i = 0; i < sheet.getRowCount(); i++) {
      cell = sheet.getCell(i, 0);
      if ((cell != null) && !cell.isMissing()) {
	if (cell.getContent().equals(key)) {
	  sheet.removeRow(i);
	  break;
	}
      }
    }
  }

  /**
   * Saves the meta-data to the specified file.
   *
   * @param meta	the meta-data to save
   * @param file	the file to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(SpreadSheet meta, String file) {
    CsvSpreadSheetWriter	writer;
    RowComparator		comp;

    comp = new RowComparator(new int[]{0, 1});
    meta.sort(comp);

    writer = new CsvSpreadSheetWriter();
    return writer.write(meta, new TmpFile(file));
  }

  /**
   * Returns the files to use in the regression tests.
   *
   * @return		the files (without path)
   */
  protected String[] getRegressionInput() {
    return new String[]{
      "3666455665_18795f0741.jpg",
      "ColorChecker100423.jpg",
    };
  }

  /**
   * Tests the {@link ImageMetaDataHelper#getMetaData(File)} method.
   */
  public void testGetMetaData() {
    Regression	reg;
    String[] 	input;
    String[]	output;
    TmpFile[]	regFiles;
    SpreadSheet	meta;
    int		i;
    String	diff;

    reg = new Regression(ImageMetaDataHelper.class);
    reg.setReferenceFile(Regression.createReferenceFile(ImageMetaDataHelper.class, "-Sanselan"));

    input    = getRegressionInput();
    output   = new String[input.length];
    regFiles = new TmpFile[input.length];
    for (i = 0; i < input.length; i++) {
      m_TestHelper.copyResourceToTmp(input[i]);
      output[i] = "out-" + i + ".csv";
      m_TestHelper.deleteFileFromTmp(output[i]);
      regFiles[i] = new TmpFile(output[i]);
      try {
	meta = ImageMetaDataHelper.getMetaData(new TmpFile(input[i]));
	removeKey(meta, "File Modified Date");
	save(meta, output[i]);
      }
      catch (Exception e) {
	fail("Failed to extract meta-data from: " + input[i]);
      }
      m_TestHelper.deleteFileFromTmp(input[i]);
    }

    diff = reg.compare(regFiles);
    if (diff != null)
      fail(diff);
  }

  /**
   * Tests the {@link ImageMetaDataHelper#getMetaDataExtractor(File)} method.
   */
  public void testGetMetaDataExtractor() {
    Regression	reg;
    String[] 	input;
    String[]	output;
    TmpFile[]	regFiles;
    SpreadSheet	meta;
    int		i;
    String	diff;

    reg = new Regression(ImageMetaDataHelper.class);
    reg.setReferenceFile(Regression.createReferenceFile(ImageMetaDataHelper.class, "-MetaDataExtractor"));

    input    = getRegressionInput();
    output   = new String[input.length];
    regFiles = new TmpFile[input.length];
    for (i = 0; i < input.length; i++) {
      m_TestHelper.copyResourceToTmp(input[i]);
      output[i] = "out-" + i + ".csv";
      m_TestHelper.deleteFileFromTmp(output[i]);
      regFiles[i] = new TmpFile(output[i]);
      try {
	meta = ImageMetaDataHelper.getMetaDataExtractor(new TmpFile(input[i]));
	removeKey(meta, "File Modified Date");
	save(meta, output[i]);
      }
      catch (Exception e) {
	fail("Failed to extract meta-data from: " + input[i]);
      }
      m_TestHelper.deleteFileFromTmp(input[i]);
    }

    diff = reg.compare(regFiles);
    if (diff != null)
      fail(diff);
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ImageMetaDataHelperTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
