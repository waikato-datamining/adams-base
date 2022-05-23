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
 * MultiCheck.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.check;

import adams.core.MessageCollection;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Applies the specified checks sequentially.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiCheck
    extends AbstractAnnotationCheck {

  private static final long serialVersionUID = 2081359805181761621L;

  /** the checks to apply. */
  protected AnnotationCheck[] m_Checks;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified checks sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"check", "check",
	new AnnotationCheck[0]);
  }

  /**
   * Sets the checks to apply.
   *
   * @param value 	the checks
   */
  public void setChecks(AnnotationCheck[] value) {
    m_Checks = value;
    reset();
  }

  /**
   * Returns the checks to apply.
   *
   * @return 		the checks
   */
  public AnnotationCheck[] getChecks() {
    return m_Checks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checksTipText() {
    return "The annotation checks to apply sequentially.";
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
    String		msg;
    int			i;

    result = new MessageCollection();

    for (i = 0; i < m_Checks.length;  i++) {
      msg = m_Checks[i].checkAnnotations(objects);
      if (msg != null)
        result.add("Check #" + (i+1) + " reported:\n" + msg);
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }
}
