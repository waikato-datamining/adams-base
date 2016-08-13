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
 * WekaInvestigatorDataEvent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.event;

import adams.gui.tools.wekainvestigator.InvestigatorPanel;

import java.util.EventObject;

/**
 * Event that gets sent when the data in an {@link InvestigatorPanel} changes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInvestigatorDataEvent
  extends EventObject {

  private static final long serialVersionUID = 5757793515769813912L;

  /** the whole table changed. */
  public final static int TABLE_CHANGED = 1;

  /** rows got added. */
  public final static int ROWS_ADDED = 2;

  /** rows got deleted. */
  public final static int ROWS_DELETED = 3;

  /** rows got modified. */
  public final static int ROWS_MODIFIED = 4;

  /** the event type. */
  protected int m_Type;

  /** the affected rows, null for all. */
  protected int[] m_Rows;

  /**
   * Constructor if the whole table changed.
   *
   * @param source	the source panel
   */
  public WekaInvestigatorDataEvent(InvestigatorPanel source) {
    this(source, TABLE_CHANGED, null);
  }

  /**
   * Constructor for specifying the type of change.
   *
   * @param source	the source panel
   */
  public WekaInvestigatorDataEvent(InvestigatorPanel source, int type, int row) {
    this(source, type, new int[]{row});
  }

  /**
   * Constructor for specifying the type of change.
   *
   * @param source	the source panel
   */
  public WekaInvestigatorDataEvent(InvestigatorPanel source, int type, int[] rows) {
    super(source);
    m_Type = type;
    m_Rows = rows;
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public int getType() {
    return m_Type;
  }

  /**
   * The affected rows.
   *
   * @return        	the rows, null for all
   */
  public int[] getRows() {
    return m_Rows;
  }
}
