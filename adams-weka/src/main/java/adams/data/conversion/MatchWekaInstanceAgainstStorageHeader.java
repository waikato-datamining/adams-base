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
 * MatchWekaInstanceAgainstStorageHeader.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import weka.core.Instances;
import adams.flow.control.StorageHandler;
import adams.flow.control.StorageName;

/**
 <!-- globalinfo-start -->
 * Matches an Instance against a dataset header from storage, i.e., it automatically converts STRING attributes into NOMINAL ones and vice versa.
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
 * <pre>-header &lt;adams.flow.control.StorageName&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The name of the storage object that represents the dataset header to use.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatchWekaInstanceAgainstStorageHeader
  extends AbstractMatchWekaInstanceAgainstHeader {

  /** for serialization. */
  private static final long serialVersionUID = 5788507217652026285L;

  /** the header to load from storage. */
  protected StorageName m_Header;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Matches an Instance against a dataset header from storage, i.e., "
      + "it automatically converts STRING attributes into NOMINAL ones and "
      + "vice versa.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "header", "header",
	    new StorageName());
  }

  /**
   * Sets the name of the storage value representing the dataset header.
   *
   * @param value	the header
   */
  public void setHeader(StorageName value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the name of the storage value representing the dataset header.
   *
   * @return		the header
   */
  public StorageName getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The name of the storage object that represents the dataset header to use.";
  }

  /**
   * Acquires the header.
   *
   * @return		the header to match against
   */
  protected Instances getDatasetHeader() {
    Object	result;

    if (!(getOwner() instanceof StorageHandler))
      throw new IllegalStateException("No access to " + StorageHandler.class.getName() + "!");

    result = ((StorageHandler) m_Owner).getStorage().get(m_Header);
    if (result == null)
      throw new IllegalStateException("No storage value available: " + m_Header);
    if (!(result instanceof Instances))
      throw new IllegalStateException("Storage value '" + m_Header + "' not of type " + Instances.class.getName() + "!");

    return (Instances) result;
  }
}
