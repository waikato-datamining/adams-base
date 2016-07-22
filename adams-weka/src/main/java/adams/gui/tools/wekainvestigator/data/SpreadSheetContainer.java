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
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import weka.core.Instances;

import java.io.File;
import java.util.logging.Level;

/**
 * SpreadSheet-based dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the source. */
  protected File m_Source;

  /** the reader used to load the data. */
  protected SpreadSheetReader m_Reader;

  /**
   * Loads the data using the specified reader.
   *
   * @param reader	the reader to use
   * @param source	the file to load
   */
  public SpreadSheetContainer(SpreadSheetReader reader, File source) {
    this(reader, new PlaceholderFile(source));
  }

  /**
   * Loads the data using the specified reader.
   *
   * @param reader	the reader to use
   * @param source	the file to load
   */
  public SpreadSheetContainer(SpreadSheetReader reader, PlaceholderFile source) {
    super();
    try {
      SpreadSheet sheet = reader.read(source);
      SpreadSheetToWekaInstances conv = new SpreadSheetToWekaInstances();
      conv.setInput(sheet);
      String msg = conv.convert();
      if (msg != null)
        throw new IllegalArgumentException("Failed to convert spreadsheet!\n" + msg);
      m_Data   = (Instances) conv.getOutput();
      m_Source = source;
      m_Reader = reader;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load dataset: " + source, e);
    }
  }

  /**
   * Returns a short version of the source of the data item.
   *
   * @return		the source
   */
  public String getSourceShort() {
    if (m_Source == null)
      return "<unknown>";
    else
      return m_Source.getName();
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSourceFull() {
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
    return (m_Reader != null) && (m_Source != null) && (m_Source.exists());
  }

  /**
   * Reloads the data.
   *
   * @param heuristic 	the heuristic for determining the class
   * @return		true if succesfully reloaded
   */
  @Override
  protected boolean doReload(AbstractClassAttributeHeuristic heuristic) {
    int				index;
    SpreadSheet 		sheet;
    SpreadSheetToWekaInstances 	conv;
    String			msg;

    try {
      sheet = m_Reader.read(m_Source);
      conv  = new SpreadSheetToWekaInstances();
      conv.setInput(sheet);
      msg = conv.convert();
      if (msg != null)
        throw new IllegalArgumentException("Failed to convert spreadsheet!\n" + msg);
      m_Data = (Instances) conv.getOutput();
      if (m_Data.classIndex() == -1) {
	index = heuristic.determineClassAttribute(m_Data);
	m_Data.setClassIndex(index);
      }
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to reload: " + m_Source, e);
      return false;
    }
  }

  /**
   * Sets the data.
   *
   * @param value	the data to use
   */
  public void setData(Instances value) {
    super.setData(value);
    m_Source = null;
    m_Reader = null;
  }
}
