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
 * AbstractWekaSpreadSheetReader.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;
import java.util.logging.Level;

import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for WEKA file format readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaSpreadSheetReader
  extends AbstractSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = -8635190668122194492L;
  
  /** the file loader to use. */
  protected AbstractFileLoader m_Loader;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Loader = newLoader();
  }
  
  /**
   * Returns an instance of the file loader.
   * 
   * @return		the file loader
   */
  protected abstract AbstractFileLoader newLoader();
  
  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return m_Loader.getFileDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_Loader.getFileExtensions();
  }
  
  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(File file) {
    SpreadSheet			result;
    Instances			data;
    WekaInstancesToSpreadSheet	convert;
    String			msg;
    
    result = null;
    
    try {
      // load
      m_Loader.setFile(new File(file.getAbsolutePath()));
      m_Loader.reset();
      data = m_Loader.getDataSet();
      m_Loader.reset();
      // convert
      convert = new WekaInstancesToSpreadSheet();
      convert.setSpreadSheetType(m_SpreadSheetType);
      convert.setInput(data);
      msg = convert.convert();
      if (msg == null)
	result = (SpreadSheet) convert.getOutput();
      else
	getLogger().severe("Failed to convert WEKA Instances into spreadsheet:\n" + msg);
      convert.cleanUp();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load dataset: " + file, e);
      result = null;
    }
    
    return result;
  }
}
