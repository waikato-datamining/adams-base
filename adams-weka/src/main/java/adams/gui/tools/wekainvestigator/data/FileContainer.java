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
 * FileContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.io.PlaceholderFile;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.util.logging.Level;

/**
 * File-based dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the source. */
  protected File m_Source;

  /** the reader used to load the data. */
  protected AbstractFileLoader m_Loader;

  /**
   * Loads the data using the specified loader.
   *
   * @param loader	the loader to use
   * @param source	the file to load
   */
  public FileContainer(AbstractFileLoader loader, PlaceholderFile source) {
    this(loader, (File) source);
  }

  /**
   * Loads the data using the specified loader.
   *
   * @param loader	the loader to use
   * @param source	the file to load
   */
  public FileContainer(AbstractFileLoader loader, File source) {
    super();
    try {
      loader.setFile(source.getAbsoluteFile());
      m_Data   = DataSource.read(loader);
      m_Source = source;
      m_Loader = loader;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load dataset: " + source, e);
    }
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSource() {
    if (m_Source == null)
      return "<unknown>";
    else
      return m_Source.toString();
  }

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  @Override
  public boolean canReload() {
    return (m_Loader != null) && (m_Source != null) && (m_Source.exists());
  }

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  @Override
  protected boolean doReload() {
    try {
      m_Loader.setFile(m_Source.getAbsoluteFile());
      m_Data = m_Loader.getDataSet();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to reload: " + m_Source, e);
      return false;
    }
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Object[] getUndoData() {
    return new Object[]{
      m_Data,
      m_Modified,
      m_Loader,
      m_Source
    };
  }

  /**
   * Restores the data from the undo point.
   *
   * @param data	the undo point
   */
  protected void applyUndoData(Object[] data) {
    m_Data     = (Instances) data[0];
    m_Modified = (Boolean) data[1];
    m_Loader   = (AbstractFileLoader) data[2];
    m_Source   = (File) data[3];
  }
}
