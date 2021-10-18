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
 * FileContainer.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.io.PlaceholderFile;
import adams.core.io.filechanged.FileChangeMonitor;
import adams.core.io.filechanged.LastModified;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;

/**
 * File-based dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileContainer
  extends AbstractDataContainer
  implements MonitoringDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the source. */
  protected File m_Source;

  /** the reader used to load the data. */
  protected AbstractFileLoader m_Loader;

  /** the file monitor to use. */
  protected FileChangeMonitor m_Monitor;

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
      if ((loader.retrieveFile() == null) || !loader.retrieveFile().getAbsoluteFile().equals(source.getAbsoluteFile()))
        loader.setFile(source.getAbsoluteFile());
      m_Data   = loader.getDataSet();
      m_Source = source;
      m_Loader = loader;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load dataset: " + source, e);
    }
    m_Monitor = new LastModified();
    m_Monitor.initialize(source);
  }

  /**
   * Uses the provided data, but also stores the reader/file for reloading it.
   *
   * @param loader	the loader to use
   * @param source	the file to load
   * @param data	the data to use
   */
  public FileContainer(AbstractFileLoader loader, File source, Instances data) {
    super();
    try {
      m_Data   = data;
      m_Source = source;
      m_Loader = loader;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load dataset: " + source, e);
    }
    m_Monitor = new LastModified();
    m_Monitor.initialize(source);
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
   * @return		null if successfully reloaded, otherwise error message
   */
  @Override
  protected String doReload() {
    DataSource 	source;

    if (!m_Source.getAbsoluteFile().exists())
      return "File does not exist: " + m_Source.getAbsoluteFile();

    try {
      m_Loader.setFile(m_Source.getAbsoluteFile());
      source = new DataSource(m_Loader);
      m_Data = source.getDataSet();
      return null;
    }
    catch (Exception e) {
      return handleException("Failed to reload: " + m_Source, e);
    }
  }

  /**
   * Sets whether the data has been modified.
   *
   * @param value	true if modified
   */
  @Override
  public void setModified(boolean value) {
    super.setModified(value);
    if ((m_Source != null) && m_Source.exists() && m_Source.isFile())
      m_Monitor.update(m_Source.getAbsoluteFile());
  }

  /**
   * Returns true if the source has changed.
   *
   * @return		true if changed
   */
  public boolean hasSourceChanged() {
    if ((m_Source != null) && m_Source.exists() && m_Source.isFile())
      return m_Monitor.hasChanged(m_Source.getAbsoluteFile());
    else
      return false;
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Serializable[] getUndoData() {
    return new Serializable[]{
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
  protected void applyUndoData(Serializable[] data) {
    m_Data     = (Instances) data[0];
    m_Modified = (Boolean) data[1];
    m_Loader   = (AbstractFileLoader) data[2];
    m_Source   = (File) data[3];
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Loader = null;
    m_Source = null;
  }
}
