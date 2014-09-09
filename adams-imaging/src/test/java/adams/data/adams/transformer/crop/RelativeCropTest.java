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
 * RelativeCropTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.adams.transformer.crop;

import adams.data.image.ImageAnchor;

/**
 * Tests the RelativeCrop cropping algorithm.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8487 $
 */
public class RelativeCropTest
  extends AbstractCropAlgorithmTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RelativeCropTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"yellow_plate.jpg",
	"yellow_plate.jpg",
	"horizontal_color_gradient.png",
	"horizontal_color_gradient.png",
	"horizontal_color_gradient.png",
	"horizontal_color_gradient.png",
	"horizontal_color_gradient.png",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractCropAlgorithm[] getRegressionSetups() {
    RelativeCrop[]	result;

    result    = new RelativeCrop[7];
    result[0] = new RelativeCrop();
    result[1] = new RelativeCrop();
    result[1].setX(0.1);
    result[1].setY(0.2);
    result[1].setWidth(0.5);
    result[1].setHeight(0.66);
    result[2] = new RelativeCrop();
    result[2].setWidth(100);
    result[2].setHeight(100);
    result[2].setAnchor(ImageAnchor.TOP_LEFT);
    result[3] = new RelativeCrop();
    result[3].setWidth(100);
    result[3].setHeight(100);
    result[3].setAnchor(ImageAnchor.TOP_RIGHT);
    result[4] = new RelativeCrop();
    result[4].setWidth(100);
    result[4].setHeight(100);
    result[4].setAnchor(ImageAnchor.MIDDLE_CENTER);
    result[5] = new RelativeCrop();
    result[5].setWidth(100);
    result[5].setHeight(100);
    result[5].setAnchor(ImageAnchor.BOTTOM_LEFT);
    result[6] = new RelativeCrop();
    result[6].setWidth(100);
    result[6].setHeight(100);
    result[6].setAnchor(ImageAnchor.BOTTOM_RIGHT);

    return result;
  }
}
