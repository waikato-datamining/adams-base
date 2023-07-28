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
 * ContentHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.option.OptionHandler;

import java.io.File;

/**
 * Interface for content handlers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ContentHandler
  extends OptionHandler {

  /** the match-all extension. */
  public final static String MATCH_ALL = "*";

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  public String[] getExtensions();

  /**
   * Reuses the last preview, if possible.
   * <br>
   * Default implementation just creates a new preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  public PreviewPanel reusePreview(File file, PreviewPanel lastPreview);

  /**
   * Creates the actual preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  public PreviewPanel createPreview(File file);

  /**
   * Returns the preview for the specified file.
   *
   * @param file	the file to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  public PreviewPanel getPreview(File file);
}
