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
 * ScaleReportObjects.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.annotation.DeprecatedClass;
import adams.data.RoundingType;
import adams.data.RoundingUtils;
import adams.data.objectfilter.Scale;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Scales the objects in the report using the provided scale factors.<br>
 * Processes the following suffixes of all the report fields that match the provided prefix:<br>
 * - .x<br>
 * - .y<br>
 * - .width<br>
 * - .height
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ScaleReportObjects
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
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix of the objects to scale.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 * <pre>-scale-x &lt;double&gt; (property: scaleX)
 * &nbsp;&nbsp;&nbsp;The factor for scaling x&#47;width.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 * <pre>-scale-y &lt;double&gt; (property: scaleY)
 * &nbsp;&nbsp;&nbsp;The factor for scaling y&#47;width.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 * <pre>-round &lt;boolean&gt; (property: round)
 * &nbsp;&nbsp;&nbsp;If enabled, the scaled values get round.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-rounding-type &lt;ROUND|CEILING|FLOOR&gt; (property: roundingType)
 * &nbsp;&nbsp;&nbsp;The type of rounding to perform.
 * &nbsp;&nbsp;&nbsp;default: ROUND
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@DeprecatedClass(useInstead = {ImageObjectFilter.class, Scale.class})
public class ScaleReportObjects
  extends AbstractTransformer {

  private static final long serialVersionUID = 3910027464955482939L;

  /** the prefix of the objects. */
  protected String m_Prefix;

  /** the scale factor for x/width. */
  protected double m_ScaleX;

  /** the scale factor for y/height. */
  protected double m_ScaleY;

  /** whether to round the scaled values. */
  protected boolean m_Round;

  /** the rounding type. */
  protected RoundingType m_RoundingType;

  /** the number of decimals. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Scales the objects in the report using the provided scale factors.\n"
	+ "Processes the following suffixes of all the report fields that match "
	+ "the provided prefix:\n"
	+ "- " + LocatedObjects.KEY_X + "\n"
	+ "- " + LocatedObjects.KEY_Y + "\n"
	+ "- " + LocatedObjects.KEY_WIDTH + "\n"
	+ "- " + LocatedObjects.KEY_HEIGHT;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "scale-x", "scaleX",
      1.0, 0.0, 1.0);

    m_OptionManager.add(
      "scale-y", "scaleY",
      1.0, 0.0, 1.0);

    m_OptionManager.add(
      "round", "round",
      false);

    m_OptionManager.add(
      "rounding-type", "roundingType",
      RoundingType.ROUND);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      0, 0, null);
  }

  /**
   * Sets the prefix.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix.
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
    return "The prefix of the objects to scale.";
  }

  /**
   * Sets the scale factor for x/width.
   *
   * @param value	the factor
   */
  public void setScaleX(double value) {
    if (getOptionManager().isValid("scaleX", value)) {
      m_ScaleX = value;
      reset();
    }
  }

  /**
   * Returns the scale factor for x/width.
   *
   * @return		the factor
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
    return "The factor for scaling x/width.";
  }

  /**
   * Sets the scale factor for y/width.
   *
   * @param value	the factor
   */
  public void setScaleY(double value) {
    if (getOptionManager().isValid("scaleY", value)) {
      m_ScaleY = value;
      reset();
    }
  }

  /**
   * Returns the scale factor for y/width.
   *
   * @return		the factor
   */
  public double getScaleY() {
    return m_ScaleY;
  }

  /**
   * Returns the tip teyt for this property.
   *
   * @return 		tip teyt for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleYTipText() {
    return "The factor for scaling y/width.";
  }

  /**
   * Sets whether to round the scaled values.
   *
   * @param value	true if to round
   */
  public void setRound(boolean value) {
    m_Round = value;
    reset();
  }

  /**
   * Returns whether to round the scaled values.
   *
   * @return		true if to round
   */
  public boolean getRound() {
    return m_Round;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String roundTipText() {
    return "If enabled, the scaled values get round.";
  }

  /**
   * Sets the type of rounding to perform.
   *
   * @param value	the type
   */
  public void setRoundingType(RoundingType value) {
    m_RoundingType = value;
    reset();
  }

  /**
   * Returns the type of rounding to perform.
   *
   * @return		the type
   */
  public RoundingType getRoundingType() {
    return m_RoundingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String roundingTypeTipText() {
    return "The type of rounding to perform.";
  }

  /**
   * Sets the number of decimals after the decimal point to use.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (getOptionManager().isValid("numDecimals", value)) {
      m_NumDecimals = value;
      reset();
    }
  }

  /**
   * Returns the number of decimals after the decimal point to use.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals after the decimal point to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "scaleX", m_ScaleX, "x: ");
    result += QuickInfoHelper.toString(this, "scaleY", m_ScaleY, ", y: ");
    if (m_Round) {
      result += QuickInfoHelper.toString(this, "roundingType", m_RoundingType, ", rounding: ");
      result += QuickInfoHelper.toString(this, "numDecimals", m_NumDecimals, ", decimals: ");
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Rounds the value according to the parameters.
   *
   * @param value	the value to round
   * @return		the potentially rounded value
   * @see		#m_Round
   * @see		#m_RoundingType
   */
  protected double round(double value) {
    if (!m_Round)
      return value;
    else
      return RoundingUtils.apply(m_RoundingType, value, m_NumDecimals);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Report		report;
    double		value;
    MessageCollection	errors;

    result = null;

    report = null;
    if (m_InputToken.getPayload() instanceof Report)
      report = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unhandled input type: " + Utils.classToString(m_InputToken.getPayload());

    if (result == null) {
      errors = new MessageCollection();
      for (AbstractField field: report.getFields()) {
	try {
	  if (!field.getName().startsWith(m_Prefix))
	    continue;
	  if (field.getName().endsWith(LocatedObjects.KEY_X) || field.getName().endsWith(LocatedObjects.KEY_WIDTH)) {
	    value = report.getDoubleValue(field);
	    value = round(value * m_ScaleX);
	    report.setNumericValue(field.getName(), value);
	  }
	  else if (field.getName().endsWith(LocatedObjects.KEY_Y) || field.getName().endsWith(LocatedObjects.KEY_HEIGHT)) {
	    value = report.getDoubleValue(field);
	    value = round(value * m_ScaleY);
	    report.setNumericValue(field.getName(), value);
	  }
	}
	catch (Exception e) {
	  errors.add("Failed to process field '" + field + "'!", e);
	}
      }
      if (!errors.isEmpty())
	result = errors.toString();
      m_OutputToken = new Token(m_InputToken.getPayload());
    }

    return result;
  }
}
