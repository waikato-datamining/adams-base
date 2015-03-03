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
 * PeriodicityTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adams.data.timeseries.PeriodicityHelper;
import adams.data.timeseries.PeriodicityType;

/**
 * A periodicity tick generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PeriodicityTickGenerator
  extends AbstractTickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /** the periodicity type. */
  protected PeriodicityType m_Periodicity;

  /** the format for outputting the values (SimpleDateFormat or DecimalFormat). */
  protected Formatter m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A periodicity tick generator.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Formatter = Formatter.getDateFormatter(getDefaultFormat());
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "periodicity", "periodicity",
	    PeriodicityType.NONE);

    m_OptionManager.add(
	    "format", "format",
	    getDefaultFormat());
  }

  /**
   * Sets the type of periodicity to use.
   *
   * @param value	the type
   */
  public void setPeriodicity(PeriodicityType value) {
    m_Periodicity = value;
    reset();
  }

  /**
   * Returns the type of periodicity to use.
   *
   * @return		the type
   */
  public PeriodicityType getPeriodicity() {
    return m_Periodicity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String periodicityTipText() {
    return "The type of periodicity to use.";
  }

  /**
   * Returns the default format.
   *
   * @return		the format
   */
  protected String getDefaultFormat() {
    return "y/M/d";
  }

  /**
   * Sets the format used for the ticks.
   *
   * @param value	the format
   */
  public void setFormat(String value) {
    m_Formatter.applyPattern(value);
    reset();
  }

  /**
   * Returns the format used for the ticks.
   *
   * @return		the format
   */
  public String getFormat() {
    return m_Formatter.toPattern();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format to use for the ticks (numeric or date/time).";
  }

  /**
   * Generate the ticks of this axis.
   */
  @Override
  protected void doGenerate() {
    TDoubleArrayList	val;
    double		left;
    double		right;
    Date		leftDate;
    Date		rightDate;
    Calendar		leftCal;
    Calendar		rightCal;
    int			last;
    int			i;
    int			current;
    String		label;

    left      = m_Parent.getActualMinimum();
    leftDate  = new Date((long) left);
    leftCal   = new GregorianCalendar();
    leftCal.setTime(leftDate);
    right     = m_Parent.getActualMaximum();
    rightDate = new Date((long) right);
    rightCal  = new GregorianCalendar();
    rightCal.setTime(rightDate);
    val       = PeriodicityHelper.calculate(m_Periodicity, left, leftDate, leftCal, right, rightDate, rightCal);
    current   = -1;
    for (i = 0; i < val.size(); i++) {
      last    = current;
      current = m_Parent.valueToPos(val.get(i));
      if (current != last) {
	label = fixLabel(m_Formatter.format(val.get(i)));
	m_Ticks.add(new Tick(current, label));
	addLabel(label);
     }
    }
  }
}