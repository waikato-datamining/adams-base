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
 * ReportDateFieldRangePaintlet.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;
import java.util.logging.Level;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DataContainerPanel;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.container.VisibilityContainerManager;
import adams.gui.visualization.core.AbstractPaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.axis.FlippableAxisModel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting a background region based on the date stored in a report.<br/>
 * For more details on the date format, see:<br/>
 * Javadoc. java.text.SimpleDateFormat.
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
 * <pre>-start &lt;adams.data.report.Field&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The date field in the report to use as start of the region.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-end &lt;adams.data.report.Field&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;The date field in the report to use as end of the region.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The date format to use for parsing the value stored in the report.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the background region.
 * &nbsp;&nbsp;&nbsp;default: #c0c0c0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportDateFieldRangePaintlet
  extends AbstractPaintlet
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5013286925457654660L;

  /** the date field in the report (start). */
  protected Field m_Start;

  /** the date field in the report (end). */
  protected Field m_End;

  /** the date format. */
  protected String m_Format;

  /** the color to paint the point with. */
  protected Color m_Color;
  
  /** the date formatter. */
  protected transient DateFormat m_DateFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Paintlet for painting a background region based on the date stored in a "
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
	    "start", "start",
	    new Field(null, DataType.STRING));

    m_OptionManager.add(
	    "end", "end",
	    new Field(null, DataType.STRING));

    m_OptionManager.add(
	    "format", "format",
	    Constants.TIMESTAMP_FORMAT);

    m_OptionManager.add(
	    "color", "color",
	    GUIHelper.getColor(getClass(), "color", Color.LIGHT_GRAY));
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
   * Sets the date field in the report to use for the start of the region.
   *
   * @param value	the date field
   */
  public void setStart(Field value) {
    if (    (value != m_Start)
	|| ((value != null) && !value.equals(m_Start)) ) {
      m_Start = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set date field in use for the start of the region.
   *
   * @return		the date field
   */
  public Field getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The date field in the report to use as start of the region.";
  }

  /**
   * Sets the date field in the report to use for the end of the region.
   *
   * @param value	the date field
   */
  public void setEnd(Field value) {
    if (    (value != m_End)
	|| ((value != null) && !value.equals(m_End)) ) {
      m_End = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set date field in use for the end of the region.
   *
   * @return		the date field
   */
  public Field getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The date field in the report to use as end of the region.";
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
   * Sets the color to paint the background with.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    if (    (value != m_Color)
	|| ((value != null) && !value.equals(m_Color)) ) {
      m_Color = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set color to paint the background with.
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
    return "The color to use for the background region.";
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
    return PaintMoment.BACKGROUND;
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
    Date			startDate;
    Date			endDate;
    Date			startDateGlobal;
    Date			endDateGlobal;
    String			dateStr;
    int				i;
    AbstractContainer			cont;
    DateFormat			dformat;
    boolean			flipped;

    panel           = (DataContainerPanel) getPanel();
    manager         = panel.getContainerManager();
    dformat         = getDateFormat();
    startDateGlobal = null;
    endDateGlobal   = null;
    axisX           = getPanel().getPlot().getAxis(Axis.BOTTOM);
    flipped         = (axisX.getAxisModel() instanceof FlippableAxisModel) && ((FlippableAxisModel) axisX.getAxisModel()).isFlipped();

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
      
      // parse dates
      if ((report != null) && (report.hasValue(m_Start))) {
	try {
	  dateStr   = report.getStringValue(m_Start);
	  startDate = dformat.parse(dateStr);
	}
	catch (Exception e) {
	  startDate = null;
	  getLogger().log(Level.SEVERE, "Failed to parse field '" + m_Start + "' using format '" + m_Format + "':", e);
	}
	try {
	  dateStr = report.getStringValue(m_End);
	  endDate = dformat.parse(dateStr);
	}
	catch (Exception e) {
	  endDate = null;
	  getLogger().log(Level.SEVERE, "Failed to parse field '" + m_End + "' using format '" + m_Format + "':", e);
	}
	
	// update date borders
	if ((startDate != null) && (endDate != null)) {
	  if (startDateGlobal == null)
	    startDateGlobal = startDate;
	  else if (DateUtils.isBefore(startDateGlobal, startDate))
	    startDateGlobal = startDate;
	  if (endDateGlobal == null)
	    endDateGlobal = endDate;
	  else if (DateUtils.isAfter(endDateGlobal, endDate))
	    endDateGlobal = endDate;
	}
      }

      // draw background
      if ((startDateGlobal != null) && (endDateGlobal != null)) {
	g.setColor(getColor());
	if (flipped)
	  g.fillRect(
	      axisX.valueToPos(endDateGlobal.getTime()), 
	      0, 
	      axisX.valueToPos(startDateGlobal.getTime()) - axisX.valueToPos(endDateGlobal.getTime()), 
	      getPanel().getHeight());
	else
	  g.fillRect(
	      axisX.valueToPos(startDateGlobal.getTime()), 
	      0, 
	      axisX.valueToPos(endDateGlobal.getTime()) - axisX.valueToPos(startDateGlobal.getTime()), 
	      getPanel().getHeight());
      }
    }
  }
}
