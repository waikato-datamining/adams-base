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
 * BaseDateTimeMsecMsecEditor.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseDateTimeMsec;
import adams.core.option.AbstractOption;
import adams.gui.chooser.DateTimePanel;

import java.util.Date;

/**
 * A PropertyEditor for BaseDateTimeMsec objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see BaseDateTimeMsec
 */
public class BaseDateTimeMsecEditor
  extends AbstractBaseDateTypeEditor<BaseDateTimeMsec, DateTimePanel> {

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseDateTimeMsec) object).getValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseDateTimeMsec(str);
  }

  /**
   * Returns a new instance of the date type.
   *
   * @param s		the string to instantiate with, can be null
   * @return		the instance
   */
  @Override
  protected BaseDateTimeMsec newDateType(String s) {
    return new BaseDateTimeMsec(s);
  }

  /**
   * Returns a new instance of the date type.
   *
   * @param d		the date to initialize with
   * @return		the instance
   */
  @Override
  protected BaseDateTimeMsec newDateType(Date d) {
    return new BaseDateTimeMsec(d);
  }

  /**
   * Instantiates a new panel for picking the date type.
   *
   * @return		the panel
   */
  @Override
  protected DateTimePanel newPanel() {
    return new DateTimePanel();
  }

  /**
   * Returns the text to use for the "now" button.
   *
   * @return		the button text
   */
  protected String getNowButtonText() {
    return "Now";
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
        return BaseDateTimeMsec.INF_PAST;
      case NOW:
        return BaseDateTimeMsec.NOW;
      case INF_FUTURE:
        return BaseDateTimeMsec.INF_FUTURE;
      default:
        throw new IllegalStateException("Unhandled placeholder: " + ph);
    }
  }
}
