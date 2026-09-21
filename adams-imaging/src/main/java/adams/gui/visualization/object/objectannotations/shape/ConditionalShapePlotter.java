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
 * ConditionalShapePlotter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.shape;

import adams.core.QuickInfoHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Applies the base shape plotter only if the object is located by the object finder.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConditionalShapePlotter
  extends AbstractShapePlotter {

  private static final long serialVersionUID = 5427077606547735205L;

  /** the object finder to apply. */
  protected ObjectFinder m_ObjectFinder;

  /** the shape plotter to use. */
  protected ShapePlotter m_ShapePlotter;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the shape only if the object is located by the object finder.";
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
      "shape-plotter", "shapePlotter",
      new NoShape());
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
   * Sets the shape plotter to use.
   *
   * @param value 	the plotter
   */
  public void setShapePlotter(ShapePlotter value) {
    m_ShapePlotter = value;
    reset();
  }

  /**
   * Returns the shape plotter in use.
   *
   * @return 		the plotter
   */
  public ShapePlotter getShapePlotter() {
    return m_ShapePlotter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapePlotterTipText() {
    return "The shape plotter to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  protected String generateQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "objectFinder", m_ObjectFinder, "finder: ");
    result += QuickInfoHelper.toString(this, "shapePlotter", m_ShapePlotter, ", plotter: ");

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
  protected void doPlotShape(LocatedObject object, Color color, Graphics2D g) {
    LocatedObjects	objects;
    LocatedObjects	located;

    if (m_ObjectFinder instanceof AllFinder) {
      m_ShapePlotter.plotShape(object, color, g);
      return;
    }

    objects = new LocatedObjects();
    objects.add(object);
    located = m_ObjectFinder.findObjects(objects);
    if (!located.isEmpty())
      m_ShapePlotter.plotShape(object, color, g);
  }
}
