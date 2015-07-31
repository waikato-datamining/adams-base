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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image.paintlet;


import adams.core.ShallowCopySupporter;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 * An interface for image paintlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Paintlet 
  extends ShallowCopySupporter<Paintlet>{

  /**
   * Sets the image panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(ImagePanel value);

  /**
   * Sets the image panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   * @param register	whether to register the panel
   */
  public void setPanel(ImagePanel value, boolean register);

  /**
   * Returns the image panel currently in use.
   *
   * @return		the panel in use
   */
  public ImagePanel getPanel();

  /**
   * Returns whether a panel has been set.
   *
   * @return		true if a panel is currently set
   */
  public boolean hasPanel();

  /**
   * Returns the paint panel of the panel, null if no panel present.
   *
   * @return		the paint panel
   */
  public PaintPanel getPaintPanel();

  /**
   * Sets whether the paintlet is enabled or not. Setting it to true
   * automatically initiates a repaint. Is not affected by m_RepaintOnChange.
   *
   * @param value	if true then the paintlet is enabled
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
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @see		#isEnabled()
   */
  public void paint(Graphics g);
}
