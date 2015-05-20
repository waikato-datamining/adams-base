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
 * ClassificationOverlay.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Highlights the chosen classifications.<br>
 * <br>
 * Some actions that generate data for this overlay:<br>
 * adams.flow.transformer.pixelselector.AddClassification
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the overlay.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 * <pre>-show-index (property: showIndex)
 * &nbsp;&nbsp;&nbsp;If enabled, the index is shown as part of the text accompanying the location 
 * &nbsp;&nbsp;&nbsp;of the pixel.
 * </pre>
 * 
 * <pre>-show-label (property: showLabel)
 * &nbsp;&nbsp;&nbsp;If enabled, the classification label is shown as part of the text accompanying 
 * &nbsp;&nbsp;&nbsp;the location of the pixel.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassificationOverlay
  extends AbstractSingleColorPixelSelectorOverlay {

  /** for serialization. */
  private static final long serialVersionUID = 5524667354695674686L;
  
  /** whether to display the index. */
  protected boolean m_ShowIndex;
  
  /** whether to display the label. */
  protected boolean m_ShowLabel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected String getGlobalInfo() {
    return "Highlights the chosen classifications.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "show-index", "showIndex",
	    false);

    m_OptionManager.add(
	    "show-label", "showLabel",
	    false);
  }

  /**
   * Sets whether to show the index.
   *
   * @param value	if true then the index is displayed
   */
  public void setShowIndex(boolean value) {
    m_ShowIndex = value;
    reset();
  }

  /**
   * Returns whether the index is shown.
   *
   * @return		true if the index is displayed
   */
  public boolean getShowIndex() {
    return m_ShowIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showIndexTipText() {
    return "If enabled, the index is shown as part of the text accompanying the location of the pixel.";
  }

  /**
   * Sets whether to show the label.
   *
   * @param value	if true then the label is displayed
   */
  public void setShowLabel(boolean value) {
    m_ShowLabel = value;
    reset();
  }

  /**
   * Returns whether the label is shown.
   *
   * @return		true if the label is displayed
   */
  public boolean getShowLabel() {
    return m_ShowLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showLabelTipText() {
    return "If enabled, the classification label is shown as part of the text accompanying the location of the pixel.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  public void imageChanged(PaintPanel panel) {
  }

  /**
   * Returns some actions that generate data for this overlay.
   * 
   * @return		the actions
   */
  @Override
  public Class[] getSuggestedActions() {
    return new Class[]{AddClassification.class};
  }

  /**
   * Returns the classification indices.
   * 
   * @return		the indices
   */
  protected Integer[] getClassificationIndices() {
    ArrayList<Integer>	result;
    List<AbstractField>	fields;
    
    result = new ArrayList<Integer>();
    fields = m_Image.getReport().getFields();
    for (AbstractField field: fields) {
      if (field.getName().startsWith(AddClassification.CLASSIFICATION))
	result.add(Integer.parseInt(field.getName().substring(AddClassification.CLASSIFICATION.length())));
    }
    
    return result.toArray(new Integer[result.size()]);
  }
  
  /**
   * Returns the pixel location to paint.
   * 
   * @param index	the pixel location
   * @return		the location, null if none found
   */
  protected Point getPixelLocation(int index) {
    Point	result;
    Report	report;
    
    result = null;
    
    if ((m_Image != null) && (m_Image.hasReport())) {
      report = m_Image.getReport();
      if (report.hasValue(AddClassification.PIXEL_X + index) && report.hasValue(AddClassification.PIXEL_Y + index)) {
	result = new Point(
	    report.getDoubleValue(AddClassification.PIXEL_X + index).intValue(),
	    report.getDoubleValue(AddClassification.PIXEL_Y + index).intValue());
      }
    }
    
    return result;
  }
  
  /**
   * Paints the actual overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    Point		loc;
    Integer[]		indices;
    String		label;
    int			x;
    int			y;
    StringBuilder	str;
    
    indices = getClassificationIndices();
    if (indices.length == 0)
      return;
    
    for (Integer index: indices) {
      loc = getPixelLocation(index);
      if (loc == null)
	continue;

      g.setColor(m_Color);
      
      x = (int) loc.getX() - 1;
      y = (int) loc.getY() - 1;
      g.drawRect(x, y, 3, 3);

      // text?
      if (m_ShowIndex || m_ShowLabel) {
	str = new StringBuilder();
	// index
	if (m_ShowIndex)
	  str.append("" + index );
	// label
	label = m_Image.getReport().getStringValue(AddClassification.CLASSIFICATION + index);
	if (m_ShowLabel && (label != null)) {
	  if (str.length() > 0)
	    str.append(": ");
	  str.append(label);
	}
	// draw
	if (str.length() > 0)
	  g.drawString(str.toString(), x + 5, y);
      }
    }
  }
}
