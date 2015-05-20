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
 * AbstractForecasterGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.wekaforecastersetup;

import weka.classifiers.timeseries.AbstractForecaster;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that generate {@link AbstractForecaster} instances.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractForecasterGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 836902202780240884L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }
  
  /**
   * Generates an instance of a {@link AbstractForecaster}.
   * 
   * @return		the forecaster instance
   * @throws Exception	if the setup fails
   */
  public abstract AbstractForecaster generate() throws Exception;
}
