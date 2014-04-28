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
 * AbstractTwitterStatusConverter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

import java.util.Hashtable;

import twitter4j.Status;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.net.TwitterHelper;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that convert tweets into a different data structure.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of output data to generate
 */
public abstract class AbstractTwitterStatusConverter<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5446751589621732002L;

  /** the fields to generate the output from. */
  protected TwitterField[] m_Fields;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new TwitterField[]{TwitterField.TEXT});
  }

  /**
   * Sets fields to generate the output from.
   *
   * @param value	the fields
   */
  public void setFields(TwitterField[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the fields to generate the output from.
   *
   * @return		the fields
   */
  public TwitterField[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The fields to use for generating the output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "fields", Utils.flatten(m_Fields, ", "), "fields: ");
  }

  /**
   * Returns the class of the output data that is generated.
   *
   * @return		the data type
   */
  public abstract Class generates();

  /**
   * Checks whether the status can be converted.
   *
   * @param status	the status to check
   */
  protected void check(Status status) {
    if (status == null)
      throw new IllegalArgumentException("No status update provided!");
  }

  /**
   * Performs the actual conversion.
   *
   * @param fields	the status data to convert
   * @return		the generated output
   */
  protected abstract T doConvert(Hashtable<TwitterField,Object> fields);

  /**
   * Performs the conversion.
   *
   * @param status	the status to convert
   * @return		the generated output
   */
  public T convert(Status status) {
    check(status);
    return doConvert(TwitterHelper.statusToHashtable(status));
  }
}
