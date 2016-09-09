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
 * FileWrapperListModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.io.FileWrapper;

import javax.swing.AbstractListModel;
import java.util.List;

/**
 * List model for showing the files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileWrapperListModel
  extends AbstractListModel<String> {

  private static final long serialVersionUID = -5631974196097641601L;

  /** the files. */
  protected List<FileWrapper> m_Files;

  /** whether to show parent dirs. */
  protected boolean m_ShowParentDirs;

  /**
   * Initializes the model.
   *
   * @param files		the files to display
   * @param showParentDirs	true if to show parent dirs
   */
  public FileWrapperListModel(List<FileWrapper> files, boolean showParentDirs) {
    m_Files          = files;
    m_ShowParentDirs = showParentDirs;
  }

  /**
   * Returns the number of files/elements.
   *
   * @return		the number of elements
   */
  @Override
  public int getSize() {
    return m_Files.size();
  }

  /**
   * Returns the element at the specified position.
   *
   * @param index	the index
   * @return		the element
   */
  @Override
  public String getElementAt(int index) {
    FileWrapper	wrapper;

    wrapper = m_Files.get(index);

    if (m_ShowParentDirs)
      return wrapper.getFile().getAbsolutePath();
    else
      return wrapper.getName();
  }
}
