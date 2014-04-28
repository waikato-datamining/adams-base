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
 * ReportDateFieldPaintlet.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainer;
import adams.gui.visualization.container.DataContainerPanel;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.container.VisibilityContainerManager;
import adams.gui.visualization.core.AbstractPaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting an indicator based on the date(s) stored in the report.<br/>
 * For more details on the date format, see:<br/>
 * Javadoc. java.text.SimpleDateFormat.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The date field(s) in the report to highlight.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The date format to use for parsing the value stored in the report.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the date label printed next to the indicator.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-offset-x &lt;int&gt; (property: offsetX)
 * &nbsp;&nbsp;&nbsp;The number of pixels to offset the string from the left of the indicator.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 * 
 * <pre>-offset-y &lt;int&gt; (property: offsetY)
 * &nbsp;&nbsp;&nbsp;The number of pixels to offset the string from the top of the panel.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportDateFieldPaintlet
  extends AbstractPaintlet
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4296847364394457330L;

  /** the date fields in the report. */
  protected Field[] m_Fields;

  /** the date format. */
  protected String m_Format;

  /** the prefix for the date label. */
  protected String m_Prefix;
  
  /** the date formatter. */
  protected transient DateFormat m_DateFormat;

  /** the pixel offset from the top. */
  protected int m_OffsetY;

  /** the pixel offset from the left. */
  protected int m_OffsetX;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Paintlet for painting an indicator based on the date(s) stored in the "
	+ "report.\n"
	+ "For more details on the date format, see:\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new Field[0]);

    m_OptionManager.add(
	    "format", "format",
	    Constants.TIMESTAMP_FORMAT);

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "offset-x", "offsetX",
	    10);

    m_OptionManager.add(
	    "offset-y", "offsetY",
	    10, 0, null);
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return new DateFormat().getTechnicalInformation();
  }

  /**
   * Sets the date fields in the report to use.
   *
   * @param value	the date fields
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    memberChanged();
  }

  /**
   * Returns the currently set date fields in use.
   *
   * @return		the date fields
   */
  public Field[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The date field(s) in the report to highlight.";
  }

  /**
   * Sets the date format.
   *
   * @param value	the date format
   */
  public void setFormat(String value) {
    if (    (value != m_Format)
	|| ((value != null) && !value.equals(m_Format)) ) {
      m_Format = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set date format.
   *
   * @return		the date format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The date format to use for parsing the value stored in the report.";
  }

  /**
   * Sets the prefix for the date label.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    if (    (value != m_Prefix)
	|| ((value != null) && !value.equals(m_Prefix)) ) {
      m_Prefix = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set prefix for the date label.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the date label printed next to the indicator.";
  }

  /**
   * Sets the pixel offset from the left.
   *
   * @param value	the offset
   */
  public void setOffsetX(int value) {
    if (value != m_OffsetX) {
      m_OffsetX = value;
      memberChanged();
    }
  }

  /**
   * Returns the pixel offset from the left.
   *
   * @return		the offset
   */
  public int getOffsetX() {
    return m_OffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetXTipText() {
    return "The number of pixels to offset the string from the left of the indicator.";
  }

  /**
   * Sets the pixel offset from the top.
   *
   * @param value	the offset
   */
  public void setOffsetY(int value) {
    if (    (value != m_OffsetY)
	 && (value >= 0) ) {
      m_OffsetY = value;
      memberChanged();
    }
  }

  /**
   * Returns the pixel offset from the top.
   *
   * @return		the offset
   */
  public int getOffsetY() {
    return m_OffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetYTipText() {
    return "The number of pixels to offset the string from the top of the panel.";
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    if (value instanceof DataContainerPanel) {
      super.setPanel(value);
    }
    else {
      throw new IllegalArgumentException(
	  "Panel is not derived from " + DataContainerPanel.class.getName() + "!");
    }
  }

  /**
   * Returns the formatter/parser.
   * 
   * @return		the formatter/parser
   */
  protected synchronized DateFormat getDateFormat() {
    if (m_DateFormat == null)
      m_DateFormat = new DateFormat(m_Format);
    return m_DateFormat;
  }
  
  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    DataContainerPanel		panel;
    AbstractContainerManager		manager;
    AxisPanel			axisX;
    Report			report;
    HashSet<String>		dates;
    String			dateStr;
    Date			date;
    int				i;
    AbstractContainer			cont;
    Color			color;
    DateFormat			dformat;

    panel   = (DataContainerPanel) getPanel();
    manager = panel.getContainerManager();
    dformat = getDateFormat();
    dates   = new HashSet<String>();
    
    for (i = 0; i < manager.count(); i++) {
      cont = manager.get(i);
      
      // visible?
      if (manager instanceof VisibilityContainerManager) {
	if (!((VisibilityContainer) cont).isVisible())
	  continue;
      }
      
      // report
      if (cont.getPayload() instanceof ReportHandler)
	report = ((ReportHandler) cont.getPayload()).getReport();
      else if (cont.getPayload() instanceof Report)
	report = (Report) cont.getPayload();
      else
	report = null;
      
      // color
      if (cont instanceof ColorContainer)
	color = ((ColorContainer) cont).getColor();
      else
	color = Color.RED;
      
      // paint indicator
      for (Field field: m_Fields) {
	if ((report != null) && (report.hasValue(field))) {
	  axisX = getPanel().getPlot().getAxis(Axis.BOTTOM);
	  try {
	    dateStr = report.getStringValue(field);
	    date    = dformat.parse(dateStr);
	  }
	  catch (Exception e) {
	    dateStr = null;
	    date    = null;
	    getLogger().log(Level.SEVERE, "Failed to parse field '" + field + "' using format '" + m_Format + "':", e);
	  }
	  if ((dateStr != null) && !dates.contains(dateStr)) {
	    dates.add(dateStr);
	    g.setColor(color);
	    g.drawLine(
		axisX.valueToPos(date.getTime()),
		0,
		axisX.valueToPos(date.getTime()),
		getPanel().getHeight());
	    g.drawString(
		m_Prefix + axisX.valueToDisplay(date.getTime()),
		axisX.valueToPos(date.getTime()) + m_OffsetX,
		m_OffsetY);
	  }
	}
      }
    }
  }
}
