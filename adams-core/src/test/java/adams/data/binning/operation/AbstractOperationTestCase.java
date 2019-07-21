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
 * AbstractOperationTestCase.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.binning.operation;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import adams.data.binning.algorithm.BinningAlgorithm;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Ancestor for test cases tailored for binning operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to process
 */
public abstract class AbstractOperationTestCase<T>
  extends AdamsTestCase {

  public final static int NUM_DECIMALS = 8;

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractOperationTestCase(String name) {
    super(name);
  }

  /**
   * Generates random data (doubles).
   *
   * @param num 	the number of random values to generate
   * @return		the data
   */
  protected List<Binnable<Integer>> generateDoubles(int num) {
    List<Binnable<Integer>>	result;
    Random rnd;

    result = new ArrayList<>();
    rnd    = new Random(1);
    for (int i = 0; i < num; i++)
      result.add(new Binnable<>(i, rnd.nextDouble()));

    return result;
  }

  /**
   * Generates random data (integers).
   *
   * @param num 	the number of random values to generate
   * @return		the data
   */
  protected List<Binnable<Integer>> generateIntegers(int num, int max) {
    List<Binnable<Integer>>	result;
    Random rnd;

    result = new ArrayList<>();
    rnd    = new Random(1);
    for (int i = 0; i < num; i++)
      result.add(new Binnable<>(i, rnd.nextInt(max)));

    return result;
  }

  /**
   * Generates bins from the data using the specified algorithm.
   *
   * @param data	the data to use
   * @param algorithm	the binning algorithm to apply
   * @return		the generated bin
   */
  protected List<Bin<Integer>> generateBins(List<Binnable<Integer>> data, BinningAlgorithm algorithm) {
    return algorithm.generateBins(data);
  }

  /**
   * Turns the object into a string.
   *
   * @param o		the object to convert
   * @return		the generated string
   */
  protected String toString(Object o) {


    if (o instanceof Binnable)
      return ((Binnable) o).toString(NUM_DECIMALS);
    else if (o instanceof BinnableGroup)
      return ((BinnableGroup) o).toString(NUM_DECIMALS);
    else if (o instanceof Bin)
      return ((Bin) o).toString(NUM_DECIMALS);
    else if (o instanceof Number)
      return Utils.doubleToString(((Number) o).doubleValue(), NUM_DECIMALS);
    else
      return o.toString();
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean saveBins(List<Bin<T>> data, String filename) {
    int			i;
    StringBuilder	str;
    
    str = new StringBuilder();
    for (i = 0; i < data.size(); i++) {
      if (i > 0)
        str.append("\n");
      str.append(toString(data.get(i)));
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean saveData(List<Binnable<T>> data, String filename) {
    int			i;
    StringBuilder	str;

    str = new StringBuilder();
    for (i = 0; i < data.size(); i++) {
      if (i > 0)
        str.append("\n");
      str.append(toString(data.get(i)));
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Saves the groups in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean saveGroups(Map<String,BinnableGroup<T>> data, String filename) {
    int			i;
    StringBuilder	str;
    List<String>	groups;

    str    = new StringBuilder();
    groups = new ArrayList<>(data.keySet());
    Collections.sort(groups);
    for (String group: groups) {
      str.append(toString(data.get(group)));
      str.append("\n");
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Saves the groups in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean saveGroups(List<BinnableGroup<T>> data, String filename) {
    StringBuilder	str;

    str    = new StringBuilder();
    for (BinnableGroup<T> group: data) {
      str.append(toString(group));
      str.append("\n");
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Saves the object in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean saveObject(Object data, String filename) {
    int			i;
    StringBuilder	str;
    List		list;

    str = new StringBuilder();

    if (data instanceof List) {
      list = (List) data;
      for (i = 0; i < list.size(); i++) {
	if (i > 0)
	  str.append("\n");
	str.append(toString(list.get(i)));
      }
    }
    else if (data.getClass().isArray()) {
      for (i = 0; i < Array.getLength(data); i++) {
	if (i > 0)
	  str.append("\n");
	str.append(toString(Array.get(data, i)));
      }
    }
    else {
      str.append(toString(data));
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out" + no + ".txt";
  }

  /**
   * Generates the output data and returns the filenames.
   *
   * @return		the filenames of the generated output, no path
   */
  protected abstract List<String> generateOutput();

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    List<String> 	files;
    String		regression;
    int			i;
    TmpFile[] 		tmpFiles;

    if (m_NoRegressionTest)
      return;

    files    = generateOutput();
    tmpFiles = new TmpFile[files.size()];
    for (i = 0; i < files.size(); i++)
      tmpFiles[i] = new TmpFile(files.get(i));
    regression = m_Regression.compare(tmpFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < files.size(); i++)
      m_TestHelper.deleteFileFromTmp(files.get(i));
  }
}
