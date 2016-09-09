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
 * FileWrapperTableModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.io.FileWrapper;

import java.util.List;

/**
 * The model for the table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileWrapperTableModel
  extends AbstractBaseTableModel {

  private static final long serialVersionUID = -4874766549376555318L;

  /** the files. */
  protected List<FileWrapper> m_Files;

  /** whether to show parent dirs. */
  protected boolean m_ShowParentDirs;

  /** the date formatter. */
  protected DateFormat m_DateFormat;

  /**
   * Initializes the model.
   *
   * @param files		the files
   * @param showParentDirs	true if to show parent dirs
   */
  public FileWrapperTableModel(List<FileWrapper> files, boolean showParentDirs) {
    m_Files          = files;
    m_ShowParentDirs = showParentDirs;
    m_DateFormat     = DateUtils.getTimestampFormatter();
  }

  /**
   * Returns whether to show the parent directories.
   *
   * @return		true if to show
   */
  public boolean getShowParentDirs() {
    return m_ShowParentDirs;
  }

  /**
   * Returns the number of files/rows.
   *
   * @return		the number of rows
   */
  @Override
  public int getRowCount() {
    return m_Files.size();
  }

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    int	result;

    result = 0;

    result++;  // file
    result++;  // DIR
    result++;  // size
    result++;  // last mod

    return result;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the index of the column
   * @return		the name, null if not available
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return "File";
      case 1:
	return "Dir";
      case 2:
	return "Size";
      case 3:
	return "Date modified";
      default:
	return null;
    }
  }

  /**
   * Returns the class of the column.
   *
   * @param columnIndex	the index of the column
   * @return			the class, null if not available
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0:
	return String.class;
      case 1:
	return String.class;
      case 2:
	return Long.class;
      case 3:
	return String.class;
      default:
	return null;
    }
  }

  /**
   * Returns the value at the specified location.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			the value, null if not available
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    FileWrapper	wrapper;

    if (rowIndex >= m_Files.size())
      return null;

    wrapper = m_Files.get(rowIndex);
    switch (columnIndex) {
      case 0:
	if (m_ShowParentDirs)
	  return wrapper.getFile().getAbsolutePath();
	else
	  return wrapper.getName();
      case 1:
	if (wrapper.isDirectory())
	  return "DIR";
	else if (wrapper.isLink())
	  return "LNK";
	else
	  return null;
      case 2:
	if (wrapper.isDirectory())
	  return null;
	else
	  return wrapper.getLength();
      case 3:
	return m_DateFormat.format(wrapper.getLastModified());
      default:
	return null;
    }
  }
}
