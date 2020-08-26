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
 * NullPreview.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheetpreview;

/**
 * Dummy, does not generate a preview.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullPreview
  extends AbstractSpreadSheetPreview {

  private static final long serialVersionUID = 6041892781802016568L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does not generate a preview.";
  }

  /**
   * Generates the preview panel.
   *
   * @return		always null
   */
  @Override
  public AbstractSpreadSheetPreviewPanel generate() {
    return null;
  }
}
