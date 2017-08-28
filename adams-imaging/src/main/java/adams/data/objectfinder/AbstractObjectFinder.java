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
 * AbstractObjectFinder.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;

/**
 * Ancestor for finders that locate objects in the report of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectFinder<T extends AbstractImageContainer>
  extends AbstractOptionHandler
  implements ObjectFinder<T>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2092237222859238898L;

  /** the prefix of the objects in the report. */
  protected String m_Prefix;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks.
   * <br><br>
   * Default implementation returns null.
   *
   * @param img		the image to check
   * @return		null if successful check, otherwise error message
   */
  protected String check(T img) {
    return null;
  }
  
  /**
   * Performs the actual finding of the objects in the report.
   * 
   * @param img		the image to process
   * @return		the indices
   */
  protected abstract int[] doFind(T img);
  
  /**
   * Finds the objects in the report.
   * 
   * @param img		the image to process
   * @return		the indices
   */
  public int[] find(T img) {
    String	msg;

    msg = check(img);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doFind(img);
  }
}
