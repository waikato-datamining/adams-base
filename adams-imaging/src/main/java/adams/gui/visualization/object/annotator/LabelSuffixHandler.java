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
 * LabelSuffixHandler.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

/**
 * Interface for annotators that support a label suffix.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LabelSuffixHandler {

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
}
