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
 * RemoveExceptionPrefix.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.errorpostprocessor;

import adams.core.base.BaseRegExp;
import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

import java.util.regex.Pattern;

/**
 * Uses the supplied regular expression to remove the exception prefix, e.g., '.*java.lang.Error:'.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveExceptionPrefix
  extends AbstractErrorPostProcessor {

  private static final long serialVersionUID = 5079344539822761524L;

  /** the regexp to apply. */
  protected BaseRegExp m_RegExp;

  /** the compiled pattern. */
  protected transient Pattern m_Pattern;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the supplied regular expression to remove the exception prefix, "
	     + "e.g., '.*java.lang.Error:' gets replaced with the empty string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("^.*java.lang.Error:"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Pattern = null;
  }

  /**
   * Sets the regexp to apply.
   *
   * @param value 	the regexp
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regexp to apply.
   *
   * @return 		the regexp
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The string that this regular expression matches is replaced with the empty string.";
  }

  /**
   * Performs the actual post-processing of the error.
   *
   * @param handler the error handler that this call comes from
   * @param source  the source actor where the error originated
   * @param type    the type of error
   * @param msg     the error message
   * @return the (potentially) updated error message
   */
  @Override
  protected String doPostProcessError(ErrorHandler handler, Actor source, String type, String msg) {
    if (m_Pattern == null)
      m_Pattern = Pattern.compile(m_RegExp.getValue(), Pattern.DOTALL);
    return m_Pattern.matcher(msg).replaceAll("").trim();
  }
}
