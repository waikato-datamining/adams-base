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
 * AbstractIdSupplier.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;

import adams.core.QuickInfoHelper;

/**
 * Abstract ancestor for ID suppliers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractIdSupplier
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /** whether to ignore not finding any IDs. */
  protected boolean m_Lenient;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "lenient", "lenient",
	    false);
  }

  /**
   * Sets whether to ignore empty list of IDs instead of reporting it as error.
   *
   * @param value	true if to ignore empty lists
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to ignore empty list of IDs instead of reporting it as error.
   *
   * @return		true if empty lists are ignored
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return "If enabled, the source no longer reports an error when not finding any IDs.";
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the IDs as array or one by one.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Integer.class;
  }

  /**
   * Returns the IDs from the database.
   *
   * @param errors	for storing any error messages
   * @return		the IDs
   */
  protected abstract ArrayList getIDs(StringBuilder errors);

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    StringBuilder	errors;

    result  = null;
    errors  = new StringBuilder();
    m_Queue = getIDs(errors);
    if (errors.length() > 0)
      result = errors.toString();
    else if ((m_Queue.size() == 0) && !m_Lenient)
      result = "No IDs found!";

    return result;
  }
}
