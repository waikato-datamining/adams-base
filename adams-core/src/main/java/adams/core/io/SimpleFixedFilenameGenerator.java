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
 * SimpleFixedFilenameGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

/**
 <!-- globalinfo-start -->
 * Simply returns the supplied file name.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;adams.core.io.PlaceholderFile&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The file name to use, including path and extension.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleFixedFilenameGenerator
  extends AbstractFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -5985801362418398850L;

  /** the file to use. */
  protected PlaceholderFile m_Name;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply returns the supplied file name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "name", "name",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the name to use.
   *
   * @param value	the name
   */
  public void setName(PlaceholderFile value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the suffix in use.
   *
   * @return		the suffix
   */
  public PlaceholderFile getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The file name to use, including path and extension.";
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  @Override
  protected String doGenerate(Object obj) {
    return m_Name.getAbsolutePath();
  }
}
