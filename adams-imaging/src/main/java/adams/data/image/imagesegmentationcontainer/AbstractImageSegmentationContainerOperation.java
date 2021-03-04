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
 * AbstractImageSegmentationContainerOperation.java
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.imagesegmentationcontainer;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Abstract base class for operations that require multiple images.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationContainerOperation
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1185449853784824033L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the minimum number of containers that are required for the operation.
   *
   * @return		the number of containers that are required, <= 0 means no lower limit
   */
  public abstract int minNumContainersRequired();

  /**
   * Returns the maximum number of containers that are required for the operation.
   *
   * @return		the number of containers that are required, <= 0 means no upper limit
   */
  public abstract int maxNumContainersRequired();

  /**
   * The type of data that is generated.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Checks whether the two containers have the same dimensions.
   *
   * @param cont1	the first container
   * @param cont2	the second container
   * @return		true if the same dimensions
   */
  protected boolean checkSameDimensions(ImageSegmentationContainer cont1, ImageSegmentationContainer cont2) {
    return
      (cont1.getBaseImage().getWidth() == cont2.getBaseImage().getWidth())
      && (cont1.getBaseImage().getHeight() == cont2.getBaseImage().getHeight());
  }

  /**
   * Checks whether the containers have the same dimensions.
   *
   * @param containers	the containers
   * @return		null if the same dimensions, other error message
   */
  protected String checkSameDimensions(ImageSegmentationContainer[] containers) {
    int		i;

    for (i = 1; i < containers.length; i++) {
      if (!checkSameDimensions(containers[0], containers[i]))
	return
	  "All images need to have the same dimensions: "
	    + containers[0].getBaseImage().getWidth() + "x" + containers[0].getBaseImage().getHeight() + " (#1)"
	    + " != "
	    + containers[i].getBaseImage().getWidth() + "x" + containers[i].getBaseImage().getHeight() + "(#" + (i+1) +")";
    }

    return null;
  }

  /**
   * Checks the containers.
   * <br><br>
   * Default implementation only ensures that containers are present.
   *
   * @param containers	the containers to check
   */
  protected void check(ImageSegmentationContainer[] containers) {
    if ((containers == null) || (containers.length == 0))
      throw new IllegalStateException("No containers provided!");

    if (minNumContainersRequired() > 0) {
      if (containers.length < minNumContainersRequired())
	throw new IllegalStateException(
	  "Not enough containers supplied (min > supplied): " + minNumContainersRequired() + " > " + containers.length);
    }

    if (maxNumContainersRequired() > 0) {
      if (containers.length > maxNumContainersRequired())
	throw new IllegalStateException(
	  "Too many containers supplied (max < supplied): " + maxNumContainersRequired() + " < " + containers.length);
    }
  }

  /**
   * Performs the actual processing of the containers.
   *
   * @param containers	the containers to process
   * @return		the generated data
   */
  protected abstract Object doProcess(ImageSegmentationContainer[] containers);

  /**
   * Processes the containers.
   *
   * @param containers	the containers to process
   * @return		the generated data
   */
  public Object process(ImageSegmentationContainer[] containers) {
    check(containers);
    return doProcess(containers);
  }
}
