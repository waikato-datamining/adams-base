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
 * AbstractWekaSpreadSheetWriter.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;

import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Ancestor for WEKA file format readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaSpreadSheetWriter
  extends AbstractSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -8635190668122194492L;
  
  /** the file loader to use. */
  protected AbstractFileSaver m_Saver;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Saver = newSaver();
  }
  
  /**
   * Returns an instance of the file loader.
   * 
   * @return		the file loader
   */
  protected abstract AbstractFileSaver newSaver();

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.STREAM;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return m_Saver.getFileDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_Saver.getFileExtensions();
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   *
   * @param content	the spreadsheet to write
   * @param out		the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, OutputStream out) {
    boolean			result;
    Instances			data;
    SpreadSheetToWekaInstances	convert;
    String			msg;
    
    result = false;
    
    try {
      convert = new SpreadSheetToWekaInstances();
      convert.setInput(content);
      msg = convert.convert();
      if (msg == null) {
	data = (Instances) convert.getOutput();
        if (data.relationName().equals(Environment.getInstance().getProject())) {
          if (content.hasName())
            data.setRelationName(content.getName());
        }
	m_Saver.setInstances(data);
	m_Saver.setDestination(out);
	m_Saver.writeBatch();
	result = true;
      }
      else {
	getLogger().severe("Failed to convert spreadsheet into WEKA Instances:\n" + msg);
	result = false;
      }
      convert.cleanUp();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to save dataset!", e);
      result = false;
    }
    
    return result;
  }
}
