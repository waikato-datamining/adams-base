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
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.io.FileUtils;
import adams.core.io.TempUtils;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tests RemoveSampleIDs. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.instance.RemoveSampleIDsTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveSampleIDsTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RemoveSampleIDsTest(String name) {
    super(name);
  }

  /**
   * Returns the location in the tmp directory for given resource.
   *
   * @param resource	the resource (path in project) to get the tmp location for
   * @return		the tmp location
   */
  protected String getTmpLocationFromResource(String resource) {
    String	result;
    File file;

    file   = new File(resource);
    result = TempUtils.getTempDirectoryStr() + File.separator + file.getName();

    return result;
  }

  /**
   * Copies the given resource to the tmp directory.
   *
   * @param resource	the resource (path in project) to copy
   * @return		false if copying failed
   * @see		#getTmpLocationFromResource(String)
   */
  protected boolean copyResourceToTmp(String resource) {
    boolean			result;
    BufferedInputStream 	input;
    BufferedOutputStream 	output;
    FileOutputStream 		fos;
    byte[]			buffer;
    int				read;
    String			ext;

    input    = null;
    output   = null;

    fos = null;
    try {
      input  = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(resource));
      fos    = new FileOutputStream(getTmpLocationFromResource(resource));
      output = new BufferedOutputStream(fos);
      buffer = new byte[1024];
      while ((read = input.read(buffer)) != -1) {
	output.write(buffer, 0, read);
	if (read < buffer.length)
	  break;
      }
      result = true;
    }
    catch (IOException e) {
      if (e.getMessage().equals("Stream closed")) {
	ext = resource.replaceAll(".*\\.", "");
	System.err.println(
	  "Resource '" + resource + "' not available? "
	    + "Or extension '*." + ext + "' not in pom.xml ('project.build.testSourceDirectory') listed?");
      }
      e.printStackTrace();
      result = false;
    }
    catch (Exception e) {
      e.printStackTrace();
      result = false;
    }

    FileUtils.closeQuietly(input);
    FileUtils.closeQuietly(output);
    FileUtils.closeQuietly(fos);

    return result;
  }

  /**
   * Deletes the given resource from the tmp directory.
   *
   * @param resource	the resource (path in project) to delete
   * @return		false if deletion failed
   * @see		#getTmpLocationFromResource(String)
   */
  protected boolean deleteResourceFromTmp(String resource) {
    String	path;

    path = getTmpLocationFromResource(resource);
    return FileUtils.delete(path);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    ArffLoader	loader;

    super.setUp();

    loader = new ArffLoader();
    loader.setSource(ClassLoader.getSystemResource("weka/filters/data/RemoveSampleIDs.arff"));
    m_Instances = loader.getDataSet();
    m_Instances.setClassIndex(m_Instances.numAttributes() - 1);

    copyResourceToTmp("weka/filters/data/RemoveSampleIDs.txt");
  }

  /** Called by JUnit after each test method */
  @Override
  protected void tearDown() {
    super.tearDown();
    deleteResourceFromTmp("weka/filters/data/RemoveSampleIDs.txt");
  }

  /**
   * Does nothing.
   *
   * @return		null
   */
  protected Instances getFilteredClassifierData() {
    return null;
  }

  /**
   * Does not generate data for a classifier.
   */
  public void testFilteredClassifier() {
  }

  /**
   * Creates a default RemoveSampleIDs.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return new RemoveSampleIDs();
  }

  /**
   * performs the actual test.
   */
  protected void performTest() {
    Instances icopy = new Instances(m_Instances);
    Instances result = null;
    try {
      m_Filter.setInputFormat(icopy);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on setInputFormat(): \n" + ex.getMessage());
    }
    try {
      result = Filter.useFilter(icopy, m_Filter);
      assertNotNull(result);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on useFilter(): \n" + ex.getMessage());
    }

    assertEquals("Number of attributes", icopy.numAttributes(), result.numAttributes());
  }

  /**
   * Test default.
   */
  public void testDefault() {
    m_Filter = getFilter();
    testBuffered();
    performTest();
  }

  @Override
  public void testRegression() {
    m_Filter = new RemoveSampleIDs();
    ((RemoveSampleIDs) m_Filter).setSampleIDFile(new TmpFile("RemoveSampleIDs.txt"));
    super.testRegression();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RemoveSampleIDsTest.class);
  }

  /**
   * Runs the test from the commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
