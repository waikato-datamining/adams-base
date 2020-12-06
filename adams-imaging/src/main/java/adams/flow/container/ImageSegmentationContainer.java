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
 * ImageSegmentationContainer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Container for storing image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageSegmentationContainer
  extends AbstractContainer {

  private static final long serialVersionUID = -472411106843171690L;

  /** the image name. */
  public final static String VALUE_NAME = "name";

  /** the base image. */
  public final static String VALUE_BASE = "base";

  /** the layers. */
  public final static String VALUE_LAYERS = "layers";

  /**
   * Initializes the container no data.
   */
  public ImageSegmentationContainer() {
    super();
  }

  /**
   * Initializes the container with the specified base image.
   *
   * @param name    	the name of the image
   * @param base	the base image
   */
  public ImageSegmentationContainer(String name, BufferedImage base) {
    this(name, base, null);
  }

  /**
   * Initializes the container with the specified base image and layers.
   *
   * @param name    	the name of the image
   * @param base	the base image
   * @param layers 	the layers
   */
  public ImageSegmentationContainer(String name, BufferedImage base, Map<String,BufferedImage> layers) {
    this();

    store(VALUE_NAME,   name);
    store(VALUE_BASE,   base);
    store(VALUE_LAYERS, layers);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_NAME, "image name", String.class);
    addHelp(VALUE_BASE, "base image", BufferedImage.class);
    addHelp(VALUE_LAYERS, "map of layers (name -> BufferedImage)", Map.class);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_NAME);
    result.add(VALUE_BASE);
    result.add(VALUE_LAYERS);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_NAME) && hasValue(VALUE_BASE);
  }

  /**
   * Returns the base image.
   *
   * @return		the base image
   */
  public BufferedImage getBaseImage() {
    return (BufferedImage) getValue(VALUE_BASE);
  }
}
