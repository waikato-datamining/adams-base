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
 * Paintlet.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;


import java.awt.Graphics;

import adams.core.ShallowCopySupporter;
import adams.gui.event.PaintEvent.PaintMoment;

/**
 * An interface for paintlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Paintlet 
  extends ShallowCopySupporter<Paintlet>{

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(PaintablePanel value);

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   * @param register	whether to register the panel
   */
  public void setPanel(PaintablePanel value, boolean register);

  /**
   * Returns the spectrum panel currently in use.
   *
   * @return		the panel in use
   */
  public PaintablePanel getPanel();

  /**
   * Returns whether a panel has been set.
   *
   * @return		true if a panel is currently set
   */
  public boolean hasPanel();

  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  public PlotPanel getPlot();

  /**
   * Sets whether the paintlet is enabled or not. Setting it to true
   * automatically initiates a repaint. Is not affected by m_RepaintOnChange.
   *
   * @param value	if true then the paintlet is enabled
   * @see		#m_RepaintOnChange
   */
  public void setEnabled(boolean value);

  /**
   * Returns whether the paintlet is currently enabled.
   *
   * @return		true if the paintlet is enabled.
   */
  public boolean isEnabled();

  /**
   * Sets whether the paintlet reacts with repaints to changes of its members.
   *
   * @param value	if true then the paintlet repaints whenever members
   * 			get changed
   */
  public void setRepaintOnChange(boolean value);

  /**
   * Returns whether the paintlet reacts with repaints to changes of its members.
   *
   * @return		true if paintlet repaints whenever members get changed
   */
  public boolean getRepaintOnChange();

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  public PaintMoment getPaintMoment();

  /**
   * Checks whether the paintlet is supposed to paint for this 
   * {@link PaintMoment}.
   * 
   * @return		true if painting should occur
   */
  public boolean canPaint(PaintMoment moment);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  public void performPaint(Graphics g, PaintMoment moment);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @see		#isEnabled()
   */
  public void paint(Graphics g);
}
