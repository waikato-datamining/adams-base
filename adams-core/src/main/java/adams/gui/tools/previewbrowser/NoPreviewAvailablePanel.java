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
 * NoPreviewAvailablePanel.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

/**
 * Dummy preview if no preview is available.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoPreviewAvailablePanel
  extends PreviewPanel {

  /** for serialization. */
  private static final long serialVersionUID = 5863705257041774410L;

  /**
   * Initializes the panel.
   */
  public NoPreviewAvailablePanel() {
    super(new MessagePanel());
    ((MessagePanel) getContent()).setMessage("No preview available");
  }
}
