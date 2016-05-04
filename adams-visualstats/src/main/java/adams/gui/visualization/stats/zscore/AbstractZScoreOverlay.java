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
 * AbstractZScoreOverlay.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;

import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.stats.paintlet.AbstractZOverlayPaintlet;

import java.awt.Color;

/**
 * Abstract superclass for overlays on the z score graph.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractZScoreOverlay
extends AbstractOptionHandler{

  /** for serialization */
  private static final long serialVersionUID = -1577548974132918070L;

  /** parent z score plot to plot data on */
  protected ZScore m_Parent;

  /** Instances object containing the data */
  protected SpreadSheet m_Data;

  /** Paintlet for the plotting */
  protected AbstractZOverlayPaintlet m_Paintlet;

  /**colour to draw the overlay line */
  protected Color m_Color;

  /** Thickness of overlay */
  protected float m_Thickness;

  public void defineOptions() {
    super.defineOptions();
    //choose the colour of the overlay line
    m_OptionManager.add(
	"color", "color", Color.BLACK);
    //thickness of overlay
    m_OptionManager.add(
	"line-thickness", "thickness",
	2.0f, 1.0f, 5.0f);

  }

  /**
   * Set the thickness of the overlay
   * @param val			Thickness in pixels
   */
  public void setThickness(float val) {
    m_Thickness = val;
    reset();
  }

  /**
   * Get the thickness of the overlay
   * @return			Thickness in pixels
   */
  public float getThickness() {
    return m_Thickness;
  }

  /**
   * Return a tip text for the thickness property
   * @return		Tip text string
   */
  public String thicknessTipText() {
    return "Thickness of the overlay line";
  }

  /**
   * set up the overlay and it's paintlet
   */
  public abstract void setUp();

  /**
   * get the parent of the overlay
   * @return			parent z score plot
   */
  protected ZScore getParent() {
    return m_Parent;
  }

  /**
   * set the parent for the overlay
   * @param val			parent z score plot
   */
  public void setParent(ZScore val) {
    m_Parent = val;
  }

  /**
   * Set the instances for the overlay
   * @param value			Instances for plotting
   */
  public void setData(SpreadSheet value) {
    m_Data = value;
  }

  /**
   * get the paintlet used to plot the overlay
   * @return			Paintlet used
   */
  public AbstractZOverlayPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * create a shallow copy of an overlay
   * @return				Copy of the overlay
   */
  public AbstractZScoreOverlay shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * create a shallow copy of an overlay
   * @param expand	whether to expand variables to their current values
   * @return		Copy of the overlay
   */
  public AbstractZScoreOverlay shallowCopy(boolean expand) {
    return (AbstractZScoreOverlay) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Return a short name for the overlay to display in the key
   * @return
   */
  public abstract String shortName();

  /**
   * Set the color of the overlay
   * @param val			Color for the overlay
   */
  public void setColor(Color val) {
    m_Color = val;
    if(m_Paintlet != null)
      m_Paintlet.setColor(val);
    reset();
  }

  /**
   * get the color of the overlay
   * @return			Color of overlay
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Tip text for the color property
   * @return			String to describe this property
   */
  public String colorTipText() {
    return "Colour to draw the overlay";
  }
}