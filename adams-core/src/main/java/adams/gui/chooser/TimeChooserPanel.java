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
 * TimeChooserPanel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.util.Date;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Time;
import adams.gui.core.BasePanel;

/**
 * Represents a text field and a button to open up a dialog for selecting a
 * date.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeChooserPanel
  extends AbstractDateBasedChooserPanel<Time> {

  /** for serialization. */
  private static final long serialVersionUID = 7053822955518874219L;

  /**
   * Creates an instance of the date formatter to use.
   * 
   * @return		the formatter
   */
  @Override
  protected DateFormat createFormatter() {
    return DateUtils.getTimeFormatter();
  }

  /**
   * Returns the title for the dialog.
   * 
   * @return		the dialog
   */
  @Override
  protected String getDialogTitle() {
    return "Select time";
  }

  /**
   * Creates the panel to display in the dialog.
   * <p/>
   * Must implement {@link DateProvider}.
   * 
   * @return		the panel
   */
  @Override
  protected BasePanel createPanel() {
    return new TimePanel();
  }
  
  /**
   * Converts the date into the appropriate derived type.
   * 
   * @param date	the date to convert
   * @return		the converted type
   */
  @Override
  protected Time convert(Date date) {
    return new Time(date);
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(Time value) {
    return toString((Date) value);
  }
}
