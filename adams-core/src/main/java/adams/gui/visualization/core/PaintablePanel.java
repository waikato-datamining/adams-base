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
 * PaintablePanel.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;


import java.awt.Graphics;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import adams.gui.core.BasePanel;
import adams.gui.event.PaintEvent;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.event.PaintListener;
import adams.gui.visualization.core.axis.FixedLabelTickGenerator;
import adams.gui.visualization.core.plot.Axis;

/**
 * An abstract superclass for panels that paint "stuff", e.g., plots.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class PaintablePanel
  extends BasePanel
  implements PaintListener {

  /** for serialization. */
  private static final long serialVersionUID = -1394066820727332049L;

  /** additional paintlets to execute. */
  protected HashSet<Paintlet> m_Paintlets;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Paintlets = new HashSet<Paintlet>();
  }

  /**
   * Adds the paintlet to the internal list.
   *
   * @param p		the paintlet to add.
   */
  public void addPaintlet(Paintlet p) {
    synchronized(m_Paintlets) {
      m_Paintlets.add(p);
    }
  }

  /**
   * Removes this paintlet from its internal list.
   *
   * @param p		the paintlet to remove
   */
  public void removePaintlet(Paintlet p) {
    synchronized(m_Paintlets) {
      m_Paintlets.remove(p);
    }
  }

  /**
   * Returns an iterator over all currently stored paintlets.
   *
   * @return		the paintlets
   */
  public Iterator<Paintlet> paintlets() {
    synchronized(m_Paintlets) {
      return m_Paintlets.iterator();
    }
  }
  
  /**
   * Returns whether the panel can handle fixed lables for its axes.
   * <br><br>
   * Default implementation returns false.
   * 
   * @return		true if panel can handle it
   */
  public boolean canHandleFixedLabels() {
    return false;
  }

  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  public abstract PlotPanel getPlot();

  /**
   * Performs some setup checks.
   */
  protected void check() {
    if (!canHandleFixedLabels()) {
      for (Axis axis: Axis.values()) {
	if (getPlot().getAxis(axis).getTickGenerator() instanceof FixedLabelTickGenerator) {
	  throw new IllegalStateException(
	      getClass().getName() + " cannot handle fixed labels, "
		  + "but using " + getPlot().getAxis(axis).getTickGenerator().getClass().getName() 
		  + " as tick generator for axis " + axis + ".");
	}
      }
    }
  }
  
  /**
   * Prepares the update, i.e., calculations etc.
   */
  protected abstract void prepareUpdate();

  /**
   * Performs the actual update.
   */
  protected void performUpdate() {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	validate();
	repaint();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Hook method, called after the update was performed.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void postUpdate() {
  }

  /**
   * Updates the display.
   */
  public synchronized void update() {
    check();
    prepareUpdate();
    performUpdate();
    postUpdate();
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  protected abstract boolean canPaint(Graphics g);

  /**
   * Executes all paintlets of the specified paint moment.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   */
  protected void paint(Graphics g, PaintMoment moment) {
    Iterator<Paintlet>	iter;
    Paintlet		paintlet;

    synchronized(m_Paintlets) {
      iter = m_Paintlets.iterator();
      while (iter.hasNext()) {
	paintlet = iter.next();
	if (paintlet.canPaint(moment))
	  paintlet.performPaint(g, moment);
      }
    }
  }

  /**
   * draws the data.
   *
   * @param e		the paint event
   */
  public void painted(PaintEvent e) {
    Graphics	g;

    g = e.getGraphics();

    // not yet ready?
    if (!canPaint(g))
      return;

    // execute paintlets
    paint(g, e.getPaintMoment());
  }
  
  /**
   * Invoke this method to print the component. This method invokes
   * <code>print</code> on the component.
   *
   * @param g the <code>Graphics</code> context in which to paint
   */
  @Override
  public void printAll(Graphics g) {
    check();
    prepareUpdate();
    validate();
    repaint();
    postUpdate();
    super.printAll(g);
  }
}
