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
 * SelectionProcessorWithLabelSupport.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selection;

/**
 * Interface for selection processors that support labels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SelectionProcessorWithLabelSupport
  extends SelectionProcessor {

  /**
   * Sets the label to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setLabel(String value);

  /**
   * Returns the label to use for the objects.
   *
   * @return 		the label
   */
  public String getLabel();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText();

  /**
   * Sets the suffix to use for the label.
   *
   * @param value 	the suffix
   */
  public void setLabelSuffix(String value);

  /**
   * Returns the suffix to use for the label.
   *
   * @return 		the suffix
   */
  public String getLabelSuffix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelSuffixTipText();
}
