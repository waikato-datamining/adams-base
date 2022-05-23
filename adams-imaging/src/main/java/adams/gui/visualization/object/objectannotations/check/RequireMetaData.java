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
 * RequireMetaData.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.check;

import adams.core.MessageCollection;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Requires the specified meta-data key to be present in all objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RequireMetaData
    extends AbstractAnnotationCheck {

  private static final long serialVersionUID = 2081359805181761621L;

  /** the meta-data key to be present. */
  protected String m_Key;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Requires the specified meta-data key to be present.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"key", "key",
	"type");
  }

  /**
   * Sets the meta-data key that must be present.
   *
   * @param value 	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the meta-data key that must be present.
   *
   * @return 		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The meta-data key that must be present.";
  }

  /**
   * Checks the annotations.
   *
   * @param objects 	the annotations to check
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String doCheckAnnotations(LocatedObjects objects) {
    MessageCollection	result;
    int			i;

    result = new MessageCollection();

    for (i = 0; i < objects.size(); i++) {
      if (!objects.get(i).getMetaData().containsKey(m_Key))
        result.add("Object #" + (i+1) + " is missing key '" + m_Key + "': " + objects.get(i));
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }
}
