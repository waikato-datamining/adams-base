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
 * SequencePlotterSequence.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.container.DataPointComparator;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;

/**
 * Extended {@link XYSequence}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SequencePlotSequence
  extends XYSequence {

  /** for serialization. */
  private static final long serialVersionUID = 331392414841660594L;

  /** the meta-data key to include in the comparison of data points. */
  protected String m_MetaDataKey;

  /**
   * Initializes the sequence.
   */
  public SequencePlotSequence() {
    super();
    m_MetaDataKey = null;
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator to use
   */
  @Override
  public DataPointComparator<XYSequencePoint> newComparator() {
    return new SequencePlotPointComparator(m_Comparison, true, m_MetaDataKey);
  }

  /**
   * Sets the meta-data key to use.
   *
   * @param value	the key, null if not to use
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    m_Comparator  = newComparator();
  }

  /**
   * Returns the meta-data key in use.
   *
   * @return		the key, null if not used
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }
}
