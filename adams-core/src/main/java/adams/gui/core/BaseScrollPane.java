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
 * BaseScrollPane.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * JScrollPane with proper scroll unit/block increments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseScrollPane
  extends JScrollPane {

  /** for serialization. */
  private static final long serialVersionUID = 256425097215624088L;

  /** the default unit increment for the scrollbars. */
  public final static int UNIT_INCREMENT = 20;

  /** the default block increment for the scrollbars. */
  public final static int BLOCK_INCREMENT = 100;

  /**
   * Creates an empty (no viewport view) <code>JScrollPane</code>
   * where both horizontal and vertical scrollbars appear when needed.
   */
  public BaseScrollPane() {
    super();
    initialize();
  }

  /**
   * Creates an empty (no viewport view) <code>BaseScrollPane</code>
   * with specified
   * scrollbar policies. The available policy settings are listed at
   * {@link #setVerticalScrollBarPolicy} and
   * {@link #setHorizontalScrollBarPolicy}.
   *
   * @param vsbPolicy an integer that specifies the vertical scrollbar policy
   * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
   */
  public BaseScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy, hsbPolicy);
    initialize();
  }

  /**
   * Creates a <code>BaseScrollPane</code> that displays the
   * contents of the specified
   * component, where both horizontal and vertical scrollbars appear
   * whenever the component's contents are larger than the view.
   *
   * @param view the component to display in the scrollpane's viewport
   */
  public BaseScrollPane(Component view) {
    super(view);
    initialize();
  }

  /**
   * Creates a <code>BaseScrollPane</code> that displays the view
   * component in a viewport
   * whose view position can be controlled with a pair of scrollbars.
   * The scrollbar policies specify when the scrollbars are displayed,
   * For example, if <code>vsbPolicy</code> is
   * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>
   * then the vertical scrollbar only appears if the view doesn't fit
   * vertically. The available policy settings are listed at
   * {@link #setVerticalScrollBarPolicy} and
   * {@link #setHorizontalScrollBarPolicy}.
   *
   * @param view the component to display in the scrollpanes viewport
   * @param vsbPolicy an integer that specifies the vertical
   *		scrollbar policy
   * @param hsbPolicy an integer that specifies the horizontal
   *		scrollbar policy
   */
  public BaseScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
    initialize();
  }

  /**
   * Initializes the scrollpane.
   */
  protected void initialize() {
    setScrollBarUnitIncrement(UNIT_INCREMENT);
    setScrollBarBlockIncrement(BLOCK_INCREMENT);
    setWheelScrollingEnabled(true);
  }

  /**
   * Sets the unit increments of both bars.
   *
   * @param inc		the increment to use
   * @see		#UNIT_INCREMENT
   */
  public void setScrollBarUnitIncrement(int inc) {
    getVerticalScrollBar().setUnitIncrement(inc);
    getHorizontalScrollBar().setUnitIncrement(inc);
  }

  /**
   * Sets the block increments of both bars.
   *
   * @param inc		the increment to use
   * @see		#BLOCK_INCREMENT
   */
  public void setScrollBarBlockIncrement(int inc) {
    getVerticalScrollBar().setBlockIncrement(inc);
    getHorizontalScrollBar().setBlockIncrement(inc);
  }
}
