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
 * AbstractPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image.paintlet;


import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 * An abstract superclass for paint engines that can be plugged into panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPaintlet
  extends AbstractOptionHandler
  implements Paintlet {

  /** for serialization. */
  private static final long serialVersionUID = -8877675053270937462L;

  /** the panel this paintlet is for. */
  protected transient ImagePanel m_Panel;

  /** whether the paintlet is enabled. */
  protected boolean m_Enabled;

  /** whether the paintlet reacts with repaints to changes of its members. */
  protected boolean m_RepaintOnChange;

  /** whether the paintlet is currently being initialized and should ignore
   * repaint requests. */
  protected boolean m_Initializing;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panel           = null;
    m_Initializing    = true;
    m_Enabled         = true;
    m_RepaintOnChange = true;
  }

  /**
   * Finishes the initialization in the constructor.
   * <br><br>
   * Calls memberChanged.
   *
   * @see		#memberChanged()
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_Initializing = false;
    memberChanged();
  }

  /**
   * Whether the paintlet is currently being initialized.
   *
   * @return		true if the paintlet is currently being initialized
   */
  protected boolean isInitializing() {
    return m_Initializing;
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(ImagePanel value) {
    setPanel(value, true);
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   * @param register	whether to register the paintlet
   */
  public void setPanel(ImagePanel value, boolean register) {
    if (register) {
      if (value != m_Panel) {
	// remove old registration
	if (m_Panel != null)
	  m_Panel.removePaintlet(this);

	m_Panel = value;

	// register with new panel
	if (m_Panel != null)
	  m_Panel.addPaintlet(this);

	memberChanged(true);
      }
    }
    else {
      m_Panel = value;
    }
  }

  /**
   * Returns the panel currently in use.
   *
   * @return		the panel in use
   */
  public ImagePanel getPanel() {
    return m_Panel;
  }

  /**
   * Returns the paint panel of the panel, null if no panel present.
   *
   * @return		the paint panel
   */
  public PaintPanel getPaintPanel() {
    return m_Panel.getPaintPanel();
  }

  /**
   * Returns whether a panel has been set.
   *
   * @return		true if a panel is currently set
   */
  public boolean hasPanel() {
    return (m_Panel != null);
  }

  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  public PaintPanel getPlot() {
    PaintPanel	result;

    result = null;

    if (m_Panel != null)
      result = m_Panel.getPaintPanel();

    return result;
  }

  /**
   * Sets whether the paintlet is enabled or not. Setting it to true
   * automatically initiates a repaint. Is not affected by m_RepaintOnChange.
   *
   * @param value	if true then the paintlet is enabled
   * @see		#m_RepaintOnChange
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    repaint();
  }

  /**
   * Returns whether the paintlet is currently enabled.
   *
   * @return		true if the paintlet is enabled.
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Sets whether the paintlet reacts with repaints to changes of its members.
   *
   * @param value	if true then the paintlet repaints whenever members
   * 			get changed
   */
  public void setRepaintOnChange(boolean value) {
    m_RepaintOnChange = value;
  }

  /**
   * Returns whether the paintlet reacts with repaints to changes of its members.
   *
   * @return		true if paintlet repaints whenever members get changed
   */
  public boolean getRepaintOnChange() {
    return m_RepaintOnChange;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   */
  public abstract void performPaint(Graphics g);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @see		#isEnabled()
   */
  public void paint(Graphics g) {
    if (isEnabled() && hasPanel())
      performPaint(g);
  }

  /**
   * Executes a repaints only if the changes to members are not ignored.
   *
   * @see		#getRepaintOnChange()
   * @see		#isInitializing()
   * @see		#repaint()
   */
  public void memberChanged() {
    memberChanged(false);
  }

  /**
   * Executes a repaints only if the changes to members are not ignored.
   *
   * @param updatePanel	whether to call the update() method of the associated
   * 			panel
   * @see		#getRepaintOnChange()
   * @see		#isInitializing()
   * @see		#repaint()
   */
  public void memberChanged(boolean updatePanel) {
    reset();

    if (!isInitializing()) {
      if (getRepaintOnChange() || updatePanel)
	repaint();
    }
  }

  /**
   * Repaints if possible (and enabled!).
   *
   * @see		#m_Panel
   * @see		#isEnabled()
   */
  public void repaint() {
    if (isEnabled() && hasPanel())
      m_Panel.repaint();
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public Paintlet shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public Paintlet shallowCopy(boolean expand) {
    return (Paintlet) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of paintlets.
   *
   * @return		the filter classnames
   */
  public static String[] getPaintlets() {
    return ClassLister.getSingleton().getClassnames(Paintlet.class);
  }

  /**
   * Instantiates the paintlet with the given options.
   *
   * @param classname	the classname of the paintlet to instantiate
   * @param options	the options for the paintlet
   * @return		the instantiated paintlet or null if an error occurred
   */
  public static Paintlet forName(String classname, String[] options) {
    Paintlet result;

    try {
      result = (Paintlet) OptionUtils.forName(Paintlet.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the paintlet from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			paintlet to instantiate
   * @return		the instantiated paintlet
   * 			or null if an error occurred
   */
  public static Paintlet forCommandLine(String cmdline) {
    return (Paintlet) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
