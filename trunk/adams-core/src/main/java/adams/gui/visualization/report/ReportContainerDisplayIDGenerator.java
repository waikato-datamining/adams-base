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
 * ReportContainerDisplayIDGenerator.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import adams.gui.visualization.container.AbstractContainerDisplayStringGenerator;

/**
 * Class for generating display IDs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportContainerDisplayIDGenerator
  extends AbstractContainerDisplayStringGenerator<ReportContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 1410620240917299353L;

  /**
   * Returns the display ID for the report.
   *
   * @param c		the report to get the display ID for
   * @return		the ID
   */
  public String getDisplay(ReportContainer c) {
    return c.getID();
  }
}