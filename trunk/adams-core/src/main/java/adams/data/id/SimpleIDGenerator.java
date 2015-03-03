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
 * SimpleIDGenerator.java
 * Copyright (C) 2009-20914 University of Waikato, Hamilton, New Zealand
 */
package adams.data.id;

import adams.core.Constants;

/**
 <!-- globalinfo-start -->
 * A simple ID generator that can make use of data provided by classes implementing IDHandler and DatabaseIDHandler.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-filename (property: makeFilename)
 * &nbsp;&nbsp;&nbsp;If set to true, all characters that cannot appear in a filename are replaced
 * &nbsp;&nbsp;&nbsp;with underscores '_'.
 * </pre>
 *
 * <pre>-filename-replace &lt;java.lang.String&gt; (property: filenameReplaceChar)
 * &nbsp;&nbsp;&nbsp;The character for replacing invalid characters in IDs that are used for
 * &nbsp;&nbsp;&nbsp;filenames; use empty string for removing the invalid characters.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format for the ID ({ID} = ID of IDHandler, {DBID} = database ID of DatabaseIDHandler
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: {DBID}-{ID}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleIDGenerator
  extends AbstractIDFilenameGenerator<Object> {

  /** for serialization. */
  private static final long serialVersionUID = -3963694054822483252L;

  /** the missing string, if the object doesn't implement a certain interface. */
  public static final String MISSING = "MISSING";

  /** the format of the ID to generate. */
  protected String m_Format;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A simple ID generator that can make use of data provided by classes "
      + "implementing IDHandler and DatabaseIDHandler.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    Constants.PLACEHOLDER_DATABASEID + "-" + Constants.PLACEHOLDER_ID);
  }

  /**
   * Sets the format for the ID.
   *
   * @param value 	the format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the currently set format for the ID.
   *
   * @return 		the format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return
        "The format for the ID (" + Constants.PLACEHOLDER_ID + " = ID of IDHandler, " +
        Constants.PLACEHOLDER_DATABASEID + " = database ID of DatabaseIDHandler).";
  }

  /**
   * Generates the actual ID.
   *
   * @param o		the object to generate the ID for
   * @return		the generated ID
   */
  @Override
  protected String assemble(Object o) {
    String	result;

    result = m_Format;

    // ID
    if (o instanceof IDHandler)
      result = result.replace(Constants.PLACEHOLDER_ID, ((IDHandler) o).getID().replace("'", ""));
    else
      result = result.replace(Constants.PLACEHOLDER_ID, MISSING);

    // DB-ID
    if (o instanceof DatabaseIDHandler)
      result = result.replace(Constants.PLACEHOLDER_DATABASEID, "" + ((DatabaseIDHandler) o).getDatabaseID());
    else
      result = result.replace(Constants.PLACEHOLDER_DATABASEID, MISSING);

    return result;
  }
}
