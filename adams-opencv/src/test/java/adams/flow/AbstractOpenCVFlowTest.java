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
 * AbstractOpenCVFlowTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow;

import adams.data.opencv.OpenCVHelper;

/**
 * Ancestor for OpenCV flow tests.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOpenCVFlowTest
  extends AbstractFlowTest{

  /**
   * Constructs the <code>AbstractOpenCVFlowTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractOpenCVFlowTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default actor.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // skip test if opencv is unavailable
    if (!OpenCVHelper.isAvailable())
      m_SkipTests.add(getClass().getName());
  }
}
