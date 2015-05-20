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
 * SetPlotContainerValue.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.control.PlotContainerUpdater.PlotContainerValue;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Updates a value of a plot container before it is being displayed in the SequencePlotter sink. Usually, you would attach a variable to the 'value' property of this actor, in order to update the specified value of the container with a value that you calculated somewhere else in the flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y, MetaData<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y, MetaData
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
 * &nbsp;&nbsp;&nbsp;default: SetPlotContainerValue
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-container-value &lt;PLOT_NAME|X_VALUE|Y_VALUE|X_ERROR_VALUE|Y_ERROR_VALUE|META_DATA&gt; (property: containerValue)
 * &nbsp;&nbsp;&nbsp;The type of value to update.
 * &nbsp;&nbsp;&nbsp;default: PLOT_NAME
 * </pre>
 * 
 * <pre>-value &lt;adams.core.base.BaseString&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The actual value to use for updating; values for X_VALUE and Y_VALUE get 
 * &nbsp;&nbsp;&nbsp;automatically parsed to doubles; in case of PLOT_NAME you can also use variables 
 * &nbsp;&nbsp;&nbsp;in the string.
 * &nbsp;&nbsp;&nbsp;default: Plot
 * </pre>
 * 
 * <pre>-meta-data-key &lt;java.lang.String&gt; (property: metaDataKey)
 * &nbsp;&nbsp;&nbsp;The key to use when updating meta-data.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionFromString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to the meta-data value string before adding it.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetPlotContainerValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2488434486963278287L;

  /** the value to change. */
  protected PlotContainerValue m_ContainerValue;

  /** the value to set. */
  protected BaseString m_Value;

  /** the meta-data key to use. */
  protected String m_MetaDataKey;

  /** the conversion for turning the string into another object type. */
  protected ConversionFromString m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Updates a value of a plot container before it is being displayed "
      + "in the SequencePlotter sink. Usually, you would attach a variable to "
      + "the 'value' property of this actor, in order to update the specified "
      + "value of the container with a value that you calculated somewhere "
      + "else in the flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "container-value", "containerValue",
      PlotContainerValue.PLOT_NAME);

    m_OptionManager.add(
      "value", "value",
      new BaseString("Plot"));

    m_OptionManager.add(
      "meta-data-key", "metaDataKey",
      "");

    m_OptionManager.add(
      "conversion", "conversion",
      new StringToString());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String      value;

    result  = QuickInfoHelper.toString(this, "containerValue", m_ContainerValue);
    result += QuickInfoHelper.toString(this, "value", m_Value, ": ");
    value = QuickInfoHelper.toString(this, "metaDataKey", m_MetaDataKey, ", meta-data: ");
    if (value != null) {
      result += value;
      result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");
    }

    return result;
  }

  /**
   * Sets the type of value to update.
   *
   * @param value	the type of value
   */
  public void setContainerValue(PlotContainerValue value) {
    m_ContainerValue = value;
    reset();
  }

  /**
   * Returns the type of value to update.
   *
   * @return		the type of value
   */
  public PlotContainerValue getContainerValue() {
    return m_ContainerValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String containerValueTipText() {
    return "The type of value to update.";
  }

  /**
   * Sets the value to use for updating.
   *
   * @param value	the actual value
   */
  public void setValue(BaseString value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to use for updating.
   *
   * @return		the value
   */
  public BaseString getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return
        "The actual value to use for updating; "
      + "values for " + PlotContainerValue.X_VALUE + " and "
      + PlotContainerValue.Y_VALUE + " get automatically parsed to doubles; "
      + "in case of " + PlotContainerValue.PLOT_NAME + " you can also use "
      + "variables in the string.";
  }

  /**
   * Sets the meta-data key to use.
   *
   * @param value	the key
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    reset();
  }

  /**
   * Returns the meta-data key to use.
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
    return "The key to use when updating meta-data.";
  }

  /**
   * Sets the conversion to apply to the meta-data value string.
   *
   * @param value	the conversion
   */
  public void setConversion(ConversionFromString value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to apply to the meta-data value string.
   *
   * @return		the conversion
   */
  public ConversionFromString getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion to apply to the meta-data value string before adding it.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SequencePlotterContainer	cont;
    SequencePlotterContainer	newCont;
    double			dvalue;
    Object                      parsed;

    result = null;

    cont    = (SequencePlotterContainer) m_InputToken.getPayload();
    newCont = (SequencePlotterContainer) cont.getClone();

    switch (m_ContainerValue) {
      case PLOT_NAME:
	newCont.setValue(SequencePlotterContainer.VALUE_PLOTNAME, getVariables().expand(m_Value.getValue()));
	break;

      case X_VALUE:
	dvalue = Utils.toDouble(m_Value.getValue());
	newCont.setValue(SequencePlotterContainer.VALUE_X, dvalue);
	break;

      case Y_VALUE:
	dvalue = Utils.toDouble(m_Value.getValue());
	newCont.setValue(SequencePlotterContainer.VALUE_Y, dvalue);
	break;

      case META_DATA:
        parsed = m_Value.getValue();
        if (!(m_Conversion instanceof StringToString)) {
          m_Conversion.setInput(parsed);
          result = m_Conversion.convert();
          if (result == null)
            parsed = m_Conversion.getOutput();
          m_Conversion.cleanUp();
        }
        if (result == null)
          newCont.addMetaData(m_MetaDataKey, parsed);
        break;

      default:
	throw new IllegalStateException("Unhandled container value: " + m_ContainerValue);
    }

    if (newCont != null)
      m_OutputToken = new Token(newCont);

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SequencePlotterContainer.class};
  }
}
