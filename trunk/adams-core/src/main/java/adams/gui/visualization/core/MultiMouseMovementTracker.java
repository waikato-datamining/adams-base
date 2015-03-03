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
 * MultiMouseMovementTracker.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import adams.core.option.OptionHandler;
import adams.gui.event.PaintEvent.PaintMoment;

/**
 <!-- globalinfo-start -->
 * Applies multiple trackers sequentially.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-tracker &lt;adams.gui.visualization.core.MouseMovementTracker&gt; [-tracker ...] (property: trackers)
 * &nbsp;&nbsp;&nbsp;The mouse movement trackers to apply.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiMouseMovementTracker
  extends AbstractPaintlet 
  implements MouseMovementTracker {

  /** for serialization. */
  private static final long serialVersionUID = -6562614432508466918L;
  
  /** the trackers to use. */
  protected MouseMovementTracker[] m_Trackers;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Applies multiple trackers sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tracker", "trackers",
	    new MouseMovementTracker[0]);
  }

  /**
   * Sets the trackers to use.
   *
   * @param value	the trackers
   */
  public void setTrackers(MouseMovementTracker[] value) {
    m_Trackers = value;
    reset();
  }

  /**
   * Returns the trackers to use.
   *
   * @return		the trackers
   */
  public MouseMovementTracker[] getTrackers() {
    return m_Trackers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trackersTipText() {
    return "The mouse movement trackers to apply.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  public void performPaint(Graphics g, PaintMoment moment) {
    for (MouseMovementTracker tracker: m_Trackers) {
      if (tracker instanceof Paintlet)
	((Paintlet) tracker).paint(g);
    }
  }

  /**
   * Gets triggered when the mouse moved.
   * 
   * @param e		the mouse event that triggered the event
   */
  public void mouseMovementTracked(MouseEvent e) {
    boolean	paintlet;
    
    paintlet = false;
    for (MouseMovementTracker tracker: m_Trackers) {
      // we call the plot panel's repaint only once for all the paintlet-trackers
      if (tracker instanceof Paintlet) {
	paintlet = true;
	continue;
      }
      tracker.mouseMovementTracked(e);
    }
    
    if (paintlet)
      getPanel().getPlot().getContent().repaint();
  }
  
  /**
   * Returns a shallow copy of the tracker. Doesn't expand variables in case
   * of {@link OptionHandler} objects.
   * 
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker() {
    return (MouseMovementTracker) shallowCopy();
  }
  
  /**
   * Returns a shallow copy of the tracker.
   * 
   * @param expand	whether to expand variables to their actual value
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker(boolean expand) {
    return (MouseMovementTracker) shallowCopy(expand);
  }
}
