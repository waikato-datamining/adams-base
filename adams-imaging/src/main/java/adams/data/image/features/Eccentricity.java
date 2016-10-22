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
 * OrientationVector.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.moments.MomentHelper;
import adams.data.report.DataType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns the eccentricity of the object
 *
 * @author sjb90
 * @version $Revision$
 */
public class Eccentricity extends AbstractBufferedImageFeatureGenerator {

  /** the value of the background colour **/
  protected Color m_BackgroundValue;

  public Color getBackgroundValue() {
    return m_BackgroundValue;
  }

  public void setBackgroundValue(Color m_Background) {
    this.m_BackgroundValue = m_Background;
    reset();
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("background-value", "backgroundValue", Color.WHITE);
  }

  /**
   * Creates the header from a template image.
   *
   * @param img the image to act as a template
   * @return the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;

    result = new HeaderDefinition();
    result.add("Eccentricity", DataType.NUMERIC);


    return result;
  }

  /**
   * Performs the actual feature genration.
   *
   * @param img the image to process
   * @return the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[] result = new List[1];
    result[0] = new ArrayList<>();
    result[0].add(MomentHelper.eccentricity(imageToMatrix(img)));

    return result;
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return null;
  }

  public boolean[][] imageToMatrix(BufferedImageContainer img) {
    BufferedImage image = img.toBufferedImage();
    int bg = m_BackgroundValue.getRGB();
    boolean[][] result = new boolean[image.getHeight()][image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	if(image.getRGB(x, y) == bg)
	  result[y][x] = false;
	else
	  result[y][x] = true;
      }
    }
    return result;
  }
}
