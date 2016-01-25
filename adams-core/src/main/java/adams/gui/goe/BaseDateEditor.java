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
 * BaseDateEditor.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseDate;
import adams.core.option.AbstractOption;
import adams.gui.chooser.DatePanel;

import java.util.Date;

/**
 * A PropertyEditor for BaseDate objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.base.BaseDate
 */
public class BaseDateEditor
  extends AbstractBaseDateTypeEditor<BaseDate, DatePanel> {

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseDate) object).getValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseDate(str);
  }

  /**
   * Returns a new instance of the date type.
   *
   * @param s		the string to instantiate with, can be null
   * @return		the instance
   */
  @Override
  protected BaseDate newDateType(String s) {
    return new BaseDate(s);
  }

  /**
   * Returns a new instance of the date type.
   *
   * @param d		the date to initialize with
   * @return		the instance
   */
  @Override
  protected BaseDate newDateType(Date d) {
    return new BaseDate(d);
  }

  /**
   * Instantiates a new panel for picking the date type.
   *
   * @return		the panel
   */
  @Override
  protected DatePanel newPanel() {
    return new DatePanel();
  }

  /**
   * Returns the text to use for the "now" button.
   *
   * @return		the button text
   */
  protected String getNowButtonText() {
    return "Today";
  }

  /**
   * Returns the string equivalent of the placeholder.
   *
   * @param ph		the placeholder to get the string representation for
   * @return		the string representation
   */
  @Override
  protected String getPlaceholder(Placeholder ph) {
    switch (ph) {
      case INF_PAST:
        return BaseDate.INF_PAST;
      case NOW:
        return BaseDate.NOW;
      case INF_FUTURE:
        return BaseDate.INF_FUTURE;
      default:
        throw new IllegalStateException("Unhandled placeholder: " + ph);
    }
  }
}
