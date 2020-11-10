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
 * ScatterPaintletMetaDataValue.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.Fonts;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Paintlet for displaying points on the scatter point as circles.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Stroke color for the paintlet
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing lines.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use when using a column for the plot colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-meta-data-key &lt;java.lang.String&gt; (property: metaDataKey)
 * &nbsp;&nbsp;&nbsp;The key of the meta-data value to paint.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the meta-data value.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of each data point.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ScatterPaintletMetaDataValue
  extends AbstractScatterPlotPaintlet
  implements SizeBasedPaintlet {

  /** for serialization	*/
  private static final long serialVersionUID = -4535962737391965432L;

  /** the meta-data key. */
  protected String m_MetaDataKey;

  /** the label font. */
  protected Font m_Font;

  /** the dimensions cache (text -> dimension). */
  protected Map<String,Dimension> m_DimensionsCache;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Paintlet for displaying points on the scatter point as circles.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "meta-data-key", "metaDataKey",
      "");

    m_OptionManager.add(
      "font", "font",
      Fonts.getSansFont(14));

    m_OptionManager.add(
      "size", "size",
      5, 1, null);
  }

  /**
   * Executes a repaints only if the changes to members are not ignored.
   */
  @Override
  public void memberChanged() {
    super.memberChanged();
    m_DimensionsCache = new HashMap<>();
  }

  /**
   * Sets the meta-data key.
   *
   * @param value	the key
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    memberChanged();
  }

  /**
   * Returns the meta-data key.
   *
   * @return		the key
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTipText() {
    return "The key of the meta-data value to paint.";
  }

  /**
   * Sets the meta-data value font.
   *
   * @param value 	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the meta-data value font.
   *
   * @return 		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the meta-data value.";
  }

  /**
   * Set the size of each data point
   * @param val		size in pixels
   */
  public void setSize(int val) {
    m_Size = val;
    memberChanged();
  }

  /**
   * Get the size of each data point
   * @return		size in pixels
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of each data point.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractScatterPlotHitDetector newHitDetector() {
    return new ScatterPlotCircleHitDetector(this);
  }

  /**
   * draws the data on the graphics object
   * @param g		Graphics object to draw on
   */
  protected void drawData(Graphics g) {
    SpreadSheetColumnIndex	colIndex;
    int				col;
    int 			i;
    Graphics2D 			g2d;
    int 			posX;
    int 			posY;
    FontMetrics 		metrics;
    int 			ascent;
    int				width;
    String			text;
    Dimension 			size;

    super.drawData(g);

    if ((m_XData == null) || (m_YData == null))
      return;

    colIndex = new SpreadSheetColumnIndex(m_MetaDataKey);
    colIndex.setData(m_Data);
    col = colIndex.getIntIndex();

    // font
    g.setFont(m_Font);
    metrics = g.getFontMetrics(m_Font);
    ascent  = metrics.getAscent();

    g2d = (Graphics2D)g;
    for(i = 0; i < m_XData.length; i++) {
      posX = m_AxisBottom.valueToPos(m_XData[i]);
      posY = m_AxisLeft.valueToPos(m_YData[i]);

      g2d.setColor(getActualColor(i, m_Color));

      // meta-data value?
      if ((col > -1) && m_Data.getRow(i).hasCell(col) && !m_Data.getRow(i).getCell(col).isMissing()) {
        text = m_Data.getRow(i).getCell(col).getContent();
        if (!m_DimensionsCache.containsKey(text)) {
	  width = metrics.stringWidth(text);
	  size  = new Dimension(width, ascent);
	  m_DimensionsCache.put(text, size);
	}
	else {
          size = m_DimensionsCache.get(text);
	}
	posX -= (size.width / 2);
        posY += (size.height / 2);

        // draw text
	g.drawString(text, posX, posY);
      }
      else {
	g2d.setStroke(new BasicStroke(m_StrokeThickness));
	g2d.drawOval(posX - (int) (.5 * m_Size), posY - (int) (.5 * m_Size), m_Size, m_Size);
      }
    }
  }
}
