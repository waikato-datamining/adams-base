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
 * ConditionalOutlinePlotter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.core.QuickInfoHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Plots the outline only if the object is located by the specified object finder.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConditionalOutlinePlotter
  extends AbstractOutlinePlotter {

  private static final long serialVersionUID = 5785535211868441100L;

  /** the object finder to apply. */
  protected ObjectFinder m_ObjectFinder;

  /** the outline plotter to use. */
  protected OutlinePlotter m_OutlinePlotter;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the outline only if the object is located by the specified object finder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "object-finder", "objectFinder",
      new AllFinder());

    m_OptionManager.add(
      "outline-plotter", "outlinePlotter",
      new NoOutline());
  }

  /**
   * Sets the object finder to use.
   *
   * @param value 	the finder
   */
  public void setObjectFinder(ObjectFinder value) {
    m_ObjectFinder = value;
    reset();
  }

  /**
   * Returns the object finder in use.
   *
   * @return 		the finder
   */
  public ObjectFinder getObjectFinder() {
    return m_ObjectFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectFinderTipText() {
    return "The object finder to use.";
  }

  /**
   * Sets the outline plotter to use.
   *
   * @param value 	the plotter
   */
  public void setOutlinePlotter(OutlinePlotter value) {
    m_OutlinePlotter = value;
    reset();
  }

  /**
   * Returns the outline plotter in use.
   *
   * @return 		the plotter
   */
  public OutlinePlotter getOutlinePlotter() {
    return m_OutlinePlotter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlinePlotterTipText() {
    return "The outline plotter to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  protected String generateQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "objectFinder", m_ObjectFinder, "finder: ");
    result += QuickInfoHelper.toString(this, "outlinePlotter", m_OutlinePlotter, ", plotter: ");

    return result;
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotOutline(LocatedObject object, Color color, Graphics2D g) {
    LocatedObjects objects;
    LocatedObjects	located;

    if (m_ObjectFinder instanceof AllFinder) {
      m_OutlinePlotter.plotOutline(object, color, g);
      return;
    }

    objects = new LocatedObjects();
    objects.add(object);
    located = m_ObjectFinder.findObjects(objects);
    if (!located.isEmpty())
      m_OutlinePlotter.plotOutline(object, color, g);
  }
}
