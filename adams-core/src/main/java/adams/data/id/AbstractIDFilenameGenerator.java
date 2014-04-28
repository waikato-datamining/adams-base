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
 * AbstractIDFilenameGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.id;

import adams.core.io.FileUtils;

/**
 * Abstract base class for ID string generators.
 *
 * Derived classes only have to override the <code>assemble(Object)</code>
 * method. The <code>reset()</code> method can be used to reset a
 * scheme's internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type to accept
 */
public abstract class AbstractIDFilenameGenerator<T>
  extends AbstractIDGenerator<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5141285178186856446L;

  /** whether to make the ID filename compliant. */
  protected boolean m_MakeFilename;

  /** the filename replacement character. */
  protected String m_FilenameReplaceChar;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filename", "makeFilename",
	    false);

    m_OptionManager.add(
	    "filename-replace", "filenameReplaceChar",
	    "");
  }

  /**
   * Sets whether to make the ID filename compliant.
   *
   * @param value 	if true then all non-filename characters are removed
   */
  public void setMakeFilename(boolean value) {
    m_MakeFilename = value;
    reset();
  }

  /**
   * Returns whether to make the ID filename compliant.
   *
   * @return 		true if all non-filename characters are removed
   */
  public boolean getMakeFilename() {
    return m_MakeFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String makeFilenameTipText() {
    return "If set to true, all characters that cannot appear in a filename are replaced with underscores '_'.";
  }

  /**
   * Sets the replacement character for filenames.
   *
   * @param value 	the character or empty string
   */
  public void setFilenameReplaceChar(String value) {
    m_FilenameReplaceChar = value;
    reset();
  }

  /**
   * Returns replacement character for filenames.
   *
   * @return 		the character or empty string
   */
  public String getFilenameReplaceChar() {
    return m_FilenameReplaceChar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameReplaceCharTipText() {
    return
        "The character for replacing invalid characters in IDs that are used "
      + "for filenames; use empty string for removing the invalid characters.";
  }

  /**
   * For post-processing the ID.
   * <p/>
   * The default implementation can make the ID filename-compliant.
   *
   * @param id		the ID to post-process
   * @return		the post-processed id
   * @see		#m_MakeFilename
   */
  protected String postProcess(String id) {
    String	result;

    result = id;
    if (m_MakeFilename)
      result = FileUtils.createFilename(result, m_FilenameReplaceChar);

    return result;
  }
}
