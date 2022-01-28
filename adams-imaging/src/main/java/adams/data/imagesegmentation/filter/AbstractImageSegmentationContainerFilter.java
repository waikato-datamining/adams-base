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
 * AbstractImageSegmentationContainerFilter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.filter;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Ancestor for filters for image segmentation containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationContainerFilter
    extends AbstractOptionHandler {

  private static final long serialVersionUID = 3781202843152967644L;

  /**
   * Hook method for checking the container before filtering.
   *
   * @param cont	the container to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(ImageSegmentationContainer cont) {
    if (cont == null)
      return "No image segmentation container provided!";

    return null;
  }

  /**
   * Performs the filtering of the container.
   *
   * @param cont	the container to filter
   * @return		the filtered container
   */
  protected abstract ImageSegmentationContainer doFilter(ImageSegmentationContainer cont);

  /**
   * Performs the filtering of the container.
   *
   * @param cont	the container to filter
   * @return		the filtered container
   */
  public ImageSegmentationContainer filter(ImageSegmentationContainer cont) {
    String	msg;

    msg = check(cont);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doFilter(cont);
  }
}
