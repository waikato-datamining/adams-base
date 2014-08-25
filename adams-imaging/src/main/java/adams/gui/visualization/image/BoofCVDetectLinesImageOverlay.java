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
 * BoofCVDetectLinesImageOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.BoofCVDetectLines;
import adams.gui.visualization.image.ImagePanel.PaintPanel;
import boofcv.alg.feature.detect.line.LineImageOps;

/**
 * Overlays the image with lines detected by {@link BoofCVDetectLines}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVDetectLinesImageOverlay
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -4190767869077702132L;

  /** the line color. */
  protected Color m_Color;
  
  /** the sheet with the line definitions. */
  protected SpreadSheet m_Lines;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays the image with lines detected by " + BoofCVDetectLines.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color", "color",
	    Color.RED);
  }

  /**
   * Sets the color for the lines.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color for the lines.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color for the lines.";
  }

  /**
   * Sets the spreadsheet with the lines data.
   * 
   * @param value	the data
   */
  public void setLines(SpreadSheet value) {
    m_Lines = value;
  }
  
  /**
   * Returns the current spreadsheet with lines data.
   * 
   * @return		the data, null if none set
   */
  public SpreadSheet getLines() {
    return m_Lines;
  }
  
  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    float			sx;
    float			sy;
    float			x;
    float			y;
    LineParametric2D_F32	lp;
    LineSegment2D_F32		ls;
    Graphics2D			g2;
    
    if (m_Lines == null)
      return;
    
    g.setColor(m_Color);
    g2 = (Graphics2D) g;
    for (Row row: m_Lines.rows()) {
      sx = row.getCell(1).toDouble().floatValue();
      sy = row.getCell(2).toDouble().floatValue();
      x  = row.getCell(3).toDouble().floatValue();
      y  = row.getCell(4).toDouble().floatValue();
      lp = new LineParametric2D_F32(x, y, sx, sy);
      ls = LineImageOps.convert(lp, panel.getWidth(), panel.getHeight());
      g2.drawLine((int) ls.a.x, (int) ls.a.y, (int) ls.b.x, (int) ls.b.y);
    }
  }
}
