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
 * AbstractFileSplitter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for file splitters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileSplitter
  extends AbstractOptionHandler
  implements Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = 929718454245646770L;

  /** the file prefix. */
  protected PlaceholderFile m_Prefix;

  /** the file extension to use. */
  protected String m_Extension;
  
  /** the number of digits to use for the index of output files. */
  protected int m_NumDigits;
  
  /** whether the splitting has been stopped. */
  protected boolean m_Stopped;
  
  /** the files that were generated. */
  protected List<File> m_Generated;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    new PlaceholderFile("./split"));

    m_OptionManager.add(
	    "extension", "extension",
	    ".bin");

    m_OptionManager.add(
	    "num-digits", "numDigits",
	    3, 1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Generated = new ArrayList<File>();
  }
  
  /**
   * Sets the prefix for the generated files.
   *
   * @param value	the prefix
   */
  public void setPrefix(PlaceholderFile value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix for the generated files.
   *
   * @return		the prefix
   */
  public PlaceholderFile getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix for the generated files.";
  }
  
  /**
   * Sets the extension for the generated files.
   *
   * @param value	the extension
   */
  public void setExtension(String value) {
    m_Extension = value;
    reset();
  }

  /**
   * Returns the extension for the generated files.
   *
   * @return		the extension
   */
  public String getExtension() {
    return m_Extension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionTipText() {
    return "The file extension to use.";
  }
  
  /**
   * Sets the number of digits to use for the index of the generated files.
   *
   * @param value	the number of digits
   */
  public void setNumDigits(int value) {
    m_NumDigits = value;
    reset();
  }

  /**
   * Returns the number of digits to use for the index of the generated files.
   *
   * @return		the number of digits
   */
  public int getNumDigits() {
    return m_NumDigits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDigitsTipText() {
    return "The number of digits to use for the index of the generated files.";
  }

  /**
   * Performs checks on the file.
   * <p/>
   * Default implementation only ensures that the file is present and not a 
   * directory.
   * 
   * @param file	the file to check
   */
  protected void check(PlaceholderFile file) {
    if (!file.exists())
      throw new IllegalArgumentException("File does not exist: " + file);
    if (file.isDirectory())
      throw new IllegalArgumentException("File is a directory: " + file);
  }
  
  /**
   * Creates a new filename, automatically adding it to the list of generated
   * files.
   * 
   * @return		the next file
   */
  protected File nextFile() {
    PlaceholderFile	result;
    String		file;
    
    file  = m_Prefix.getAbsolutePath();
    file += Utils.padLeft("" + (m_Generated.size() + 1), '0', m_NumDigits);
    file += m_Extension;
    
    result = new PlaceholderFile(file);
    m_Generated.add(result);
    
    return result;
  }
  
  /**
   * Performs the actual splitting of the file.
   * 
   * @param file	the file to split
   */
  protected abstract void doSplit(PlaceholderFile file);
  
  /**
   * Splits the file and returns the filenames of the generated files.
   * 
   * @param file	the file to split
   * @return		the filenames of the new files generated
   */
  public String[] split(PlaceholderFile file) {
    String[]	result;
    int		i;
    
    m_Stopped = false;
    m_Generated.clear();
    
    check(file);
    doSplit(file);
    
    if (m_Stopped) {
      result = new String[0];
    }
    else {
      result = new String[m_Generated.size()];
      for (i = 0; i < m_Generated.size(); i++)
	result[i] = m_Generated.get(i).getAbsolutePath();
    }
      
    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }
}
