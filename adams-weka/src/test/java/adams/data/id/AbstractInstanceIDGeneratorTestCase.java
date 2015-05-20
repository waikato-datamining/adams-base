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
 * AbstractIDGeneratorTestCase.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.id;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.data.container.DataContainer;
import adams.data.id.AbstractIDGeneratorTestCase;
import adams.data.id.IDHandler;
import adams.data.instance.Instance;
import adams.test.TmpFile;

/**
 * Ancestor for ID generator test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of data to data
 */
public abstract class AbstractInstanceIDGeneratorTestCase<D extends DataContainer & IDHandler>
  extends AbstractIDGeneratorTestCase<D> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractInstanceIDGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   * @see		#getDataDirectory()
   */
  protected D[] load(String filename) {
    Instance[]	result;
    Instances	data;
    int		i;

    result = null;

    m_TestHelper.copyResourceToTmp(filename);

    try {
      data   = DataSource.read(new TmpFile(filename).getAbsolutePath());
      result = new Instance[data.numInstances()];
      for (i = 0; i < data.numInstances(); i++) {
	result[i] = new Instance();
	result[i].set(data.instance(i));
	result[i].setID(new TmpFile(filename).getName() + "-" + i);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      result = new Instance[0];
    }

    m_TestHelper.deleteFileFromTmp(filename);

    return (D[]) result;
  }
}
