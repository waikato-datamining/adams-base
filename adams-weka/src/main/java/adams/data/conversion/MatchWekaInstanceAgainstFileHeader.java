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
 * MatchWekaInstanceAgainstFileHeader.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.logging.Level;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Matches an Instance against a dataset header loaded from a file, i.e., it automatically converts STRING attributes into NOMINAL ones and vice versa.<br>
 * The file can be any format that WEKA recognizes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-header &lt;adams.core.io.PlaceholderFile&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The file to load the header from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatchWekaInstanceAgainstFileHeader
  extends AbstractMatchWekaInstanceAgainstHeader {

  /** for serialization. */
  private static final long serialVersionUID = -5909149413572601612L;

  /** the header to load from disk. */
  protected PlaceholderFile m_Header;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Matches an Instance against a dataset header loaded from a file, i.e., "
      + "it automatically converts STRING attributes into NOMINAL ones and "
      + "vice versa.\n"
      + "The file can be any format that WEKA recognizes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "header", "header",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the file to load the dataset header from.
   *
   * @param value	the file
   */
  public void setHeader(PlaceholderFile value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the file to load the dataset header from.
   *
   * @return		the file
   */
  public PlaceholderFile getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The file to load the header from.";
  }

  /**
   * Acquires the header.
   *
   * @return		the header to match against
   */
  @Override
  protected Instances getDatasetHeader() {
    Instances	result;

    try {
      result = DataSource.read(m_Header.getAbsolutePath());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load header from: " + m_Header, e);
      throw new IllegalStateException(e);
    }

    return result;
  }
}
