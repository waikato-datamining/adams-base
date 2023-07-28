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
 * ObjectContentHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

/**
 * Interface for content handlers that can generate previews from objects as well, not just files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ObjectContentHandler
  extends ContentHandler {

  /**
   * Checks whether the object is handled by this content handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean canHandle(Object obj);

  /**
   * Checks whether the class is handled by this content handler.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  public boolean canHandle(Class cls);

  /**
   * Reuses the last preview, if possible.
   *
   * @param obj		the object to create the view for
   * @return		the preview
   */
  public PreviewPanel reusePreview(Object obj, PreviewPanel lastPreview);

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the view for
   * @return		the preview
   */
  public PreviewPanel createPreview(Object obj);

  /**
   * Returns the preview for the specified object.
   *
   * @param obj		the object to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  public PreviewPanel getPreview(Object obj);
}
