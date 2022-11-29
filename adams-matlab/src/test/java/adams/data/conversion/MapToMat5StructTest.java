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
 * MapToMat5StructTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;
import us.hebi.matlab.mat.types.Struct;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the MapToMat5Struct conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToMat5StructTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MapToMat5StructTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Struct 		struct;
    Matrix		mat;
    Map<String,Object>	map;
    Map<String,Object>	map2;

    map = new HashMap<>();
    map.put("mat1", Mat5.newMatrix(new int[]{3, 3}));
    map.put("dm1", new Double[][]{{1.0, 2.0}, {3.0, 4.0}});

    struct = Mat5.newStruct();
    mat    = Mat5.newMatrix(new int[]{3, 3});
    struct.set("mat2", mat);
    map.put("str1", struct);

    map2 = new HashMap<>();
    map2.put("mat3", Mat5.newMatrix(new int[]{4, 4}));
    map.put("map2", map2);

    return new Object[]{map};
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    return new Conversion[]{new MapToMat5Struct()};
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MapToMat5StructTest.class);
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
