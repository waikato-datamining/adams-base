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
 * ImageObjectInfo.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRectangle;
import adams.data.geometry.GeometryUtils;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

import java.awt.Rectangle;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Outputs the requested type of information for either the incoming adams.flow.transformer.locateobjects.LocatedObject or the specified image object in the report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageObjectInfo
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-index &lt;java.lang.String&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the object to retrieve the information for.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;X|Y|WIDTH|HEIGHT|META_DATA|RECTANGLE|BASE_RECTANGLE|INDEX_STRING|INDEX_INT|BITMAP|RECTANGLE_AREA|POLYGON_AREA&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: X
 * </pre>
 *
 * <pre>-perform-scaling &lt;boolean&gt; (property: performScaling)
 * &nbsp;&nbsp;&nbsp;If enabled, the scale factors for X&#47;Y get applied.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-scale-x &lt;double&gt; (property: scaleX)
 * &nbsp;&nbsp;&nbsp;The scale factor for the X axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-scale-y &lt;double&gt; (property: scaleY)
 * &nbsp;&nbsp;&nbsp;The scale factor for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectInfo
  extends AbstractTransformer
  implements DataInfoActor, ObjectPrefixHandler {

  private static final long serialVersionUID = -5644432725273726622L;

  /**
   * The type of info to provide.
   */
  public enum InfoType {
    X,
    Y,
    WIDTH,
    HEIGHT,
    META_DATA,
    RECTANGLE,
    BASE_RECTANGLE,
    INDEX_STRING,
    INDEX_INT,
    BITMAP,
    RECTANGLE_AREA,
    POLYGON_AREA,
  }

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** the index to retrieve. */
  protected String m_Index;

  /** the info to provide. */
  protected InfoType m_Type;

  /** whether to scale coordinates/areas. */
  protected boolean m_PerformScaling;

  /** the scale factor for X. */
  protected double m_ScaleX;

  /** the scale factor for Y. */
  protected double m_ScaleY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs the requested type of information for either the incoming "
	+ Utils.classToString(LocatedObject.class) + " or the specified image "
	+ "object in the report.\n"
       + "NB: When performing scaling, the output of " + InfoType.RECTANGLE + " and "
	+ InfoType.BASE_RECTANGLE + " will use coordinates that were cast to int.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "index", "index",
      "");

    m_OptionManager.add(
      "type", "type",
      InfoType.X);

    m_OptionManager.add(
      "perform-scaling", "performScaling",
      false);

    m_OptionManager.add(
      "scale-x", "scaleX",
      1.0, 0.0, null);

    m_OptionManager.add(
      "scale-y", "scaleY",
      1.0, 0.0, null);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
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
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the index of the object to get the information for.
   *
   * @param value 	the index
   */
  public void setIndex(String value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the object to get the information for.
   *
   * @return 		the index
   */
  public String getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the object to retrieve the information for.";
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets whether to perform scaling.
   *
   * @param value 	true if to perform
   */
  public void setPerformScaling(boolean value) {
    m_PerformScaling = value;
    reset();
  }

  /**
   * Returns whether to perform scaling.
   *
   * @return 		true if to perform
   */
  public boolean getPerformScaling() {
    return m_PerformScaling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String performScalingTipText() {
    return "If enabled, the scale factors for X/Y get applied.";
  }

  /**
   * Sets the scale factor for the X axis.
   *
   * @param value 	the scale factor
   */
  public void setScaleX(double value) {
    if (getOptionManager().isValid("scaleX", value)) {
      m_ScaleX = value;
      reset();
    }
  }

  /**
   * Returns the scale factor for the X axis.
   *
   * @return 		the scale factor
   */
  public double getScaleX() {
    return m_ScaleX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleXTipText() {
    return "The scale factor for the X axis.";
  }

  /**
   * Sets the scale factor for the Y axis.
   *
   * @param value 	the scale factor
   */
  public void setScaleY(double value) {
    if (getOptionManager().isValid("scaleY", value)) {
      m_ScaleY = value;
      reset();
    }
  }

  /**
   * Returns the scale factor for the Y axis.
   *
   * @return 		the scale factor
   */
  public double getScaleY() {
    return m_ScaleY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleYTipText() {
    return "The scale factor for the Y axis.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ReportHandler.class, Report.class, LocatedObject.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case X:
      case Y:
      case WIDTH:
      case HEIGHT:
	if (m_PerformScaling)
	  return new Class[]{Double.class};
	else
	  return new Class[]{Integer.class};

      case INDEX_INT:
	return new Class[]{Integer.class};

      case META_DATA:
	return new Class[]{Map.class};

      case RECTANGLE:
	return new Class[]{Rectangle.class};

      case BASE_RECTANGLE:
	return new Class[]{BaseRectangle.class};

      case INDEX_STRING:
	return new Class[]{String.class};

      case BITMAP:
	return new Class[]{BufferedImageContainer.class};

      case RECTANGLE_AREA:
      case POLYGON_AREA:
	return new Class[]{Double.class};

      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    result += QuickInfoHelper.toString(this, "index", (m_Index.isEmpty() ? "-none-" : m_Index), ", index: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Report				report;
    LocatedObjects			objs;
    LocatedObject 			obj;
    BufferedImageContainer		cont;
    LocatedObjects			newObjs;
    org.locationtech.jts.geom.Polygon	polygon;

    result = null;

    report = null;
    obj    = null;
    if (m_InputToken.hasPayload(ReportHandler.class))
      report = m_InputToken.getPayload(ReportHandler.class).getReport();
    else if (m_InputToken.hasPayload(Report.class))
      report = m_InputToken.getPayload(Report.class);
    else if (m_InputToken.hasPayload(LocatedObject.class))
      obj = m_InputToken.getPayload(LocatedObject.class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      if (report != null) {
	objs = LocatedObjects.fromReport(report, m_Prefix);
	obj = objs.find(m_Index);
      }
      if (obj != null) {
	switch (m_Type) {
	  case X:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(obj.getX() * m_ScaleX);
	    else
	      m_OutputToken = new Token(obj.getX());
	    break;
	  case Y:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(obj.getY() * m_ScaleY);
	    else
	      m_OutputToken = new Token(obj.getY());
	    break;
	  case WIDTH:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(obj.getWidth() * m_ScaleX);
	    else
	      m_OutputToken = new Token(obj.getWidth());
	    break;
	  case HEIGHT:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(obj.getHeight() * m_ScaleY);
	    else
	      m_OutputToken = new Token(obj.getHeight());
	    break;
	  case META_DATA:
	    m_OutputToken = new Token(obj.getMetaData());
	    break;
	  case RECTANGLE:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(obj.getRectangle(m_ScaleX, m_ScaleY));
	    else
	      m_OutputToken = new Token(obj.getRectangle());
	    break;
	  case BASE_RECTANGLE:
	    if (m_PerformScaling)
	      m_OutputToken = new Token(new BaseRectangle(obj.getRectangle(m_ScaleX, m_ScaleY)));
	    else
	      m_OutputToken = new Token(new BaseRectangle(obj.getRectangle()));
	    break;
	  case INDEX_STRING:
	    m_OutputToken = new Token(obj.getIndexString());
	    break;
	  case INDEX_INT:
	    m_OutputToken = new Token(obj.getIndex());
	    break;
	  case BITMAP:
	    if (obj.getImage() != null) {
	      newObjs = new LocatedObjects();
	      newObjs.add(obj);
	      cont = new BufferedImageContainer();
	      cont.setImage(obj.getImage());
	      cont.getReport().mergeWith(newObjs.toReport(m_Prefix));
	      m_OutputToken = new Token(cont);
	    }
	    break;
	  case RECTANGLE_AREA:
	  case POLYGON_AREA:
	    polygon = obj.toGeometry();
	    if (m_PerformScaling)
	      polygon = GeometryUtils.scale(polygon, m_ScaleX, m_ScaleY);
	    m_OutputToken = new Token(polygon.getArea());
	    break;
	  default:
	    throw new IllegalStateException("Unhandled type: " + m_Type);
	}
      }
      else {
	result = "Index not found: " + m_Index;
      }
    }

    return result;
  }
}
