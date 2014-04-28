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
 * MultiPaintlet.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import java.awt.Graphics;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.PaintablePanel;

/**
 * Paintlet that combines multiple XYSequence paintlets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiPaintlet
  extends AbstractXYSequencePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 159999248427405834L;
  
  /** the paintlets to use. */
  protected XYSequencePaintlet[] m_SubPaintlets;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple paintlets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "paintlet", "subPaintlets",
	    getDefaultSubPaintlets());
  }

  /**
   * Returns the default paintlets.
   * 
   * @return		the paintlets
   */
  protected XYSequencePaintlet[] getDefaultSubPaintlets() {
    return new XYSequencePaintlet[0];
  }
  
  /**
   * Sets the paintlets to use.
   *
   * @param value	the paintlets
   */
  public void setSubPaintlets(XYSequencePaintlet[] value) {
    m_SubPaintlets = value;
    setPanel(getPanel());  // update sub paintlets
    memberChanged();
  }

  /**
   * Returns the paintlets to use.
   *
   * @return		the paintlets
   */
  public XYSequencePaintlet[] getSubPaintlets() {
    return m_SubPaintlets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subPaintletsTipText() {
    return "The paintlets to combine.";
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    super.setPanel(value);
    for (XYSequencePaintlet paintlet: m_SubPaintlets)
      paintlet.setPanel(value);
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.MULTIPLE;
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    for (XYSequencePaintlet paintlet: m_SubPaintlets) {
      if (paintlet.canPaint(moment))
	paintlet.performPaint(g, moment);
    }
  }
}
