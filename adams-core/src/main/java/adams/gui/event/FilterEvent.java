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
 * FilterEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import java.util.EventObject;

import adams.data.container.DataContainer;
import adams.data.filter.AbstractFilter;
import adams.gui.visualization.container.FilterDialog;

/**
 * Event that gets sent from a FilterDialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see FilterDialog
 */
public class FilterEvent<T extends DataContainer>
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 5898322483535512283L;

  /** the filter to use. */
  protected AbstractFilter<T> m_Filter;

  /** whether to overlay the original data. */
  protected boolean m_OverlayOriginalData;

  /**
   * Initializes the event.
   *
   * @param source	the dialog that triggered the event
   * @param filter	the selected filter
   * @param overlay	if true then the original data will be overlayed
   */
  public FilterEvent(FilterDialog source, AbstractFilter<T> filter, boolean overlay) {
    super(source);

    m_Filter              = filter;
    m_OverlayOriginalData = overlay;
  }

  /**
   * Returns the dialog that triggered the event.
   *
   * @return		the dialog
   */
  public FilterDialog getDialog() {
    return (FilterDialog) getSource();
  }

  /**
   * Returns the filter.
   *
   * @return		the filter
   */
  public AbstractFilter<T> getFilter() {
    return m_Filter;
  }

  /**
   * Returns whether the original data should be overlayed.
   *
   * @return		true if original data should be overlayed
   */
  public boolean getOverlayOriginalData() {
    return m_OverlayOriginalData;
  }

  /**
   * Returns a string representation of the event.
   *
   * @return		the string
   */
  public String toString() {
    return "Dialog=" + getDialog() + ", overlayOriginalData=" + getOverlayOriginalData();
  }
}
