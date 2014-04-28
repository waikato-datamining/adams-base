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
 * MultipleFileContentHandler.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.File;

import javax.swing.JPanel;

/**
 * Interface for content handlers that can handle more than one file at a time.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MultipleFileContentHandler {

  /**
   * Returns the preview for the specified files.
   *
   * @param files	the files to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  public JPanel getPreview(File[] files);
}
