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
 * ObjectFinder.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.data.image.AbstractImageContainer;

/**
 * Interface for finders that locate objects in the report of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 */
public interface ObjectFinder<T extends AbstractImageContainer>
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Finds the objects in the report.
   * 
   * @param img		the image to process
   * @return		the indices
   */
  public int[] find(T img);
}
