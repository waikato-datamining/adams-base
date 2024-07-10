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
 * AbstractErrorPaintlet.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;
import adams.gui.visualization.sequence.PaintletWithOptionalPointPreprocessor;
import adams.gui.visualization.sequence.pointpreprocessor.PassThrough;
import adams.gui.visualization.sequence.pointpreprocessor.PointPreprocessor;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Ancestor for error paintlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractErrorPaintlet
  extends AbstractStrokePaintlet
  implements PaintletWithOptionalPointPreprocessor {

  /** for serialization. */
  private static final long serialVersionUID = -4384114526759056961L;

  /** the preprocessor. */
  protected PointPreprocessor m_PointPreprocessor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    if (supportsPointPreprocessor()) {
      m_OptionManager.add(
	"point-preprocessor", "pointPreprocessor",
	newPointPreprocessor());
    }
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PointPreprocessor = newPointPreprocessor();
  }

  /**
   * Returns the point preprocessor in use.
   *
   * @return		the preprocessor
   */
  public PointPreprocessor newPointPreprocessor() {
    return new PassThrough();
  }

  /**
   * Returns whether point preprocessing is actually supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsPointPreprocessor() {
    return false;
  }

  /**
   * Sets the point preprocessor to use.
   *
   * @param value	the preprocessor
   */
  @Override
  public void setPointPreprocessor(PointPreprocessor value) {
    m_PointPreprocessor = value;
    memberChanged();
  }

  /**
   * Returns the point preprocessor in use.
   *
   * @return		the preprocessor
   */
  @Override
  public PointPreprocessor getPointPreprocessor() {
    return m_PointPreprocessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pointPreprocessorTipText() {
    return "The point preprocessor to use.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  /**
   * Returns the plotter panel.
   *
   * @return		the plotter panel
   */
  public SequencePlotterPanel getPlotterPanel() {
    if (getPanel() instanceof SequencePlotterPanel)
      return (SequencePlotterPanel) getPanel();
    else
      return null;
  }

  /**
   * Returns the color for the data with the given index.
   *
   * @param index	the index of the spectrum
   * @return		the color for the spectrum
   */
  public Color getColor(int index) {
    return getPlotterPanel().getContainerManager().get(index).getColor();
  }

  /**
   * Draws the error data with the given color.
   *
   * @param g		the graphics context
   * @param data	the error data to draw
   * @param color	the color to draw in
   */
  protected abstract void drawData(Graphics g, SequencePlotSequence data, Color color);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
    int		i;
    XYSequence 	data;

    // paint all markers
    synchronized(getPlotterPanel().getContainerManager()) {
      for (i = 0; i < getPlotterPanel().getContainerManager().count(); i++) {
	if (!getPlotterPanel().getContainerManager().isVisible(i))
	  continue;
	data = getPlotterPanel().getContainerManager().get(i).getData();
	if (data instanceof SequencePlotSequence) {
	  synchronized(data) {
	    drawData(g, (SequencePlotSequence) data, getColor(i));
	  }
	}
      }
    }
  }
}
