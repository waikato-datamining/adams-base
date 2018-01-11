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
 * AbstractInstanceOutlierDetectorTestCase.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.data.instance.Instance;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Test case for outlier detectors that work on Instances.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInstanceOutlierDetectorTestCase
  extends AbstractOutlierDetectorTestCase<Instance> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractInstanceOutlierDetectorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/outlier/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the first instance in the dataset, or null if it failed
   * 			to load; class attribute is always the last
   */
  protected Instance load(String filename) {
    Instance	result;
    Instances	data;

    result = new Instance();
    try {
      m_TestHelper.copyResourceToTmp(filename);
      data = DataSource.read(new TmpFile(filename).getAbsolutePath());
      data.setClassIndex(data.numAttributes() - 1);
      result.set(data.instance(0));
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }
    finally {
      m_TestHelper.deleteFileFromTmp(filename);
    }

    return result;
  }
}
