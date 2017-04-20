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
 * ByNamePaintlet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.core.base.BaseRegExp;
import adams.flow.core.Actor;
import adams.gui.visualization.core.FlowAwarePaintlet;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.sequence.PaintletWithCustomDataSupport;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A wrapper for error paintlets, plots only sequences if the ID matches
 * the regular expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByNameErrorPaintlet
  extends AbstractErrorPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 3270329510617886683L;

  /** the regular expression that determines whether to plot or not. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching. */
  protected boolean m_Invert;

  /** the actual paintlet to use. */
  protected AbstractErrorPaintlet m_Paintlet;

  /** the actor the paintlet belongs to. */
  protected Actor m_Actor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Meta-paintlet that uses a regular expression on the sequence ID to "
	+ "determine whether to plot the error data with the specified base-paintlet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("strokeThickness");

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);

    m_OptionManager.add(
      "paintlet", "paintlet",
      getDefaultPaintlet());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    setPaintlet(getDefaultPaintlet());

    super.initialize();
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default paintlet
   */
  protected AbstractErrorPaintlet getDefaultPaintlet() {
    return new NoErrorPaintlet();
  }

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(value, false);

    super.setPanel(value);
  }

  /**
   * Sets the regular expression to use for matching the sequence IDs.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    memberChanged(true);
  }

  /**
   * Returns the regular expression to use for matching the sequence IDs.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to use for matching the sequence IDs.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    memberChanged(true);
  }

  /**
   * Returns whether to invert the matchin.
   *
   * @return		true if to invert
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, the machine sense of the regular expression gets inverted.";
  }

  /**
   * Sets the actual paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(AbstractErrorPaintlet value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(null);

    m_Paintlet = value;
    m_Paintlet.setPanel(getPanel(), false);

    if (!(m_Paintlet instanceof PaintletWithCustomDataSupport)) {
      getLogger().warning(
	"Base paintlet " + m_Paintlet.getClass().getName() + " does not implement "
	  + PaintletWithCustomDataSupport.class.getName() + ", cannot perform plotting of subset!");
    }

    if (m_Paintlet instanceof FlowAwarePaintlet)
      ((FlowAwarePaintlet) m_Paintlet).setActor(m_Actor);

    memberChanged();
  }

  /**
   * Returns the painlet in use.
   *
   * @return		the paintlet
   */
  public AbstractErrorPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The actual paintlet to use for drawing the error data.";
  }

  /**
   * Draws the error data with the given color.
   *
   * @param g		the graphics context
   * @param data	the error data to draw
   * @param color	the color to draw in
   */
  @Override
  protected void drawData(Graphics g, SequencePlotSequence data, Color color) {
    boolean	match;

    match = m_RegExp.isMatch(data.getID());
    if ((!match && !m_Invert) || (match && m_Invert))
      return;
    if (data.size() == 0)
      return;
    m_Paintlet.drawData(g, data, color);
  }
}
