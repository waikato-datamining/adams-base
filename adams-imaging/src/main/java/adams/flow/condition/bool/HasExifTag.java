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
 * HasExifTag.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.transformer.exiftagoperation.ApacheCommonsExifTagExists;

/**
 * Checks whether the specified EXIF tag is present.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HasExifTag
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -8641162903464692580L;

  /** the operation to use. */
  protected adams.flow.transformer.exiftagoperation.ExifTagExistsOperation m_Operation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the specified EXIF tag is present.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new ApacheCommonsExifTagExists());
  }

  /**
   * Sets the operation to perform.
   *
   * @param value	the operation
   */
  public void setOperation(adams.flow.transformer.exiftagoperation.ExifTagExistsOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the operation to perform.
   *
   * @return		the operation
   */
  public adams.flow.transformer.exiftagoperation.ExifTagExistsOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The operation to execute.";
  }
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation, "operation: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return m_Operation.accepts();
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    Boolean		result;
    MessageCollection	errors;

    errors = new MessageCollection();
    result = (Boolean) m_Operation.process(token.getPayload(), errors);
    if (result == null)
      result = false;
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      result = false;
    }

    return result;
  }
}
