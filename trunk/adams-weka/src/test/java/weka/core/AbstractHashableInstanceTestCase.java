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
 * Copyright (C) 2012 University of Waikato, Hamilton, NZ
 */

package weka.core;

import java.util.ArrayList;

import weka.test.AdamsTestHelper;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests HashableInstanceUsingString. Run from the command line with:<p/>
 * java weka.core.HashableInstanceUsingStringTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHashableInstanceTestCase
  extends AdamsTestCase {
  
  static {
    AdamsTestHelper.setRegressionRoot();
  }

  /**
   * Constructs the <code>HashableInstanceUsingStringTest</code>.
   *
   * @param name 	the name of the test
   */
  public AbstractHashableInstanceTestCase(String name) {
    super(name);
  }
  
  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    Environment.setEnvironmentClass(adams.env.Environment.class);

    super.setUp();
  }
  
  /**
   * Wraps the instance.
   * 
   * @param data	the instance to wrap
   * @param exclClass	whether to exclude the class
   * @param exclWeight	whether to exclude the weight
   * @return		the wrapped instance
   */
  protected abstract AbstractHashableInstance wrap(Instance data, boolean exclClass, boolean exclWeight);
  
  /**
   * Tests whether the hashcode stays consistent.
   */
  protected void checkHashStaysSame(Instance dense, Instance sparse) {
    assertEquals("hashcode differs", wrap(dense, false, false).hashCode(), wrap(sparse, false, false).hashCode());
    assertEquals("hashcode differs (no class)", wrap(dense, true, false).hashCode(), wrap(sparse, true, false).hashCode());
    assertEquals("hashcode differs (no weight)", wrap(dense, false, true).hashCode(), wrap(sparse, false, true).hashCode());
    assertEquals("hashcode differs (no class+weight)", wrap(dense, true, true).hashCode(), wrap(sparse, true, true).hashCode());
    assertEquals("hashcode differs (copy dense)", wrap(dense, false, false).hashCode(), wrap(dense, false, false).copy().hashCode());
    assertEquals("hashcode differs (copy dense, no class)", wrap(dense, true, false).hashCode(), wrap(dense, true, false).copy().hashCode());
    assertEquals("hashcode differs (copy dense, no weight)", wrap(dense, false, true).hashCode(), wrap(dense, false, true).copy().hashCode());
    assertEquals("hashcode differs (copy dense, no class, no weight)", wrap(dense, true, true).hashCode(), wrap(dense, true, true).copy().hashCode());
    assertEquals("hashcode differs (copy sparse, no class)", wrap(sparse, true, false).hashCode(), wrap(sparse, true, false).copy().hashCode());
    assertEquals("hashcode differs (copy sparse, no weight)", wrap(sparse, false, true).hashCode(), wrap(sparse, false, true).copy().hashCode());
    assertEquals("hashcode differs (copy sparse, no class, no weight)", wrap(sparse, true, true).hashCode(), wrap(sparse, true, true).copy().hashCode());
  }
  
  /**
   * Tests whether the hashcode stays consistent.
   */
  public void testHashStaysSame() {
    Instances			data;
    ArrayList<Attribute>	atts;
    DenseInstance		dense;
    SparseInstance		sparse;
    double[]			values;
    int				i;
    TestInstances		test;
    
    // simple dataset
    values = new double[]{1.0, 2.0, 3.0, 4.0, 0.0};
    atts   = new ArrayList<Attribute>();
    for (i = 0; i < values.length; i++)
      atts.add(new Attribute("" + i));
    data = new Instances("test", atts, 0);
    
    dense = new DenseInstance(1.0, values);
    dense.setDataset(data);
    sparse = new SparseInstance(1.0, values);
    sparse.setDataset(data);
    
    checkHashStaysSame(dense, sparse);
    
    // random dataset
    test = new TestInstances();
    test.setNumClasses(3);
    test.setNumInstances(10);
    test.setNumDate(2);
    test.setNumNominal(2);
    test.setNumNumeric(2);
    test.setNumString(2);
    test.setNumRelational(2);
    test.setNumRelationalDate(2);
    test.setNumRelationalNominal(2);
    test.setNumRelationalNumeric(2);
    test.setNumRelationalString(2);
    try {
      data = test.generate();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to generate test data: " + e);
      data = null;
    }
    
    for (i = 0; i < data.numInstances(); i++) {
      dense = new DenseInstance(data.get(i));
      dense.setDataset(data);
      sparse = new SparseInstance(data.get(i));
      sparse.setDataset(data);

      checkHashStaysSame(dense, sparse);
    }
  }
  
  /**
   * Tests the equals method.
   */
  public void testEquals() {
    TestInstances		test;
    Instances			data;
    DenseInstance		dense;
    SparseInstance		sparse;
    
    test = new TestInstances();
    try {
      data = test.generate();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to generate test data: " + e);
      data = null;
    }

    dense = new DenseInstance(data.get(0));
    dense.setDataset(data);
    sparse = new SparseInstance(data.get(0));
    sparse.setDataset(data);
    
    assertFalse("null", dense.equals(null));
    assertTrue("same object (dense)", wrap(dense, false, false).equals(wrap(dense, false, false)));
    assertTrue("same object (sparse)", wrap(sparse, false, false).equals(wrap(sparse, false, false)));
  }

  /**
   * Does nothing.
   */
  @Override
  public void testSerializable() {
    TestInstances		test;
    Instances			data;
    SerializedObject		obj;
    
    test = new TestInstances();
    try {
      data = test.generate();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to generate test data: " + e);
      data = null;
    }
    
    try {
      obj = new SerializedObject(wrap(data.get(0), false, false));
      obj.getObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to serialize instance: " + e);
    }
    
    try {
      obj = new SerializedObject(wrap(data.get(0), true, false));
      obj.getObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to serialize instance (no class): " + e);
    }
    
    try {
      obj = new SerializedObject(wrap(data.get(0), false, true));
      obj.getObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to serialize instance (no weight): " + e);
    }
    
    try {
      obj = new SerializedObject(wrap(data.get(0), true, true));
      obj.getObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failed to serialize instance (no class+weight): " + e);
    }
  }
}
