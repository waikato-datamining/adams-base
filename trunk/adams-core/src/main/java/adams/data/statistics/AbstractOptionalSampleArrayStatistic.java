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
 * AbstractOptionalSampleArrayStatistic.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import java.io.Serializable;

/**
 * Abstract super class for array statistics that can interprete the arrays
 * either as samples or populations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of array
 */
public abstract class AbstractOptionalSampleArrayStatistic<T extends Serializable>
  extends AbstractArrayStatistic<T>
  implements OptionalSampleArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 4031396019597112143L;

  /** whether the arrays are samples or populations. */
  protected boolean m_IsSample;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "is-sample", "isSample",
	    false);
  }

  /**
   * Sets whether the arrays represent samples instead of populations.
   *
   * @param value	true if arrays are samples and not populations
   */
  public void setIsSample(boolean value) {
    m_IsSample = value;
    reset();
  }

  /**
   * Returns whether the arrays represent samples instead of populations.
   *
   * @return		true if arrays are samples and not populations
   */
  public boolean getIsSample() {
    return m_IsSample;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String isSampleTipText() {
    return "If set to true, the arrays are treated as samples and not as populations.";
  }
}
