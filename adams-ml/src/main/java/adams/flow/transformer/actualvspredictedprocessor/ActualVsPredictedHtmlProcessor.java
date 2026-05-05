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
 * ActualVsPredictedProcessor.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

/**
 * Interface for processors that generate HTML output from a spreadsheet with
 * actual vs predicted data.
 * The 1st column in the spreadsheet contains the actual values and the 2nd the predicted ones.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ActualVsPredictedHtmlProcessor
  extends ActualVsPredictedProcessor<String> {

  /**
   * Sets the title to use.
   *
   * @param value	the title
   */
  public void setTitle(String value);

  /**
   * Returns the title to use.
   *
   * @return		the title
   */
  public String getTitle();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText();
}
