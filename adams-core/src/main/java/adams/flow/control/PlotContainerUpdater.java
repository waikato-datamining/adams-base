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
 * PlotContainerUpdater.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.container.AbstractContainer;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;

/**
 <!-- globalinfo-start -->
 * Applies all sub-actors to process either the selected value of the plot container.
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
 * Conditional equivalent:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.control.ConditionalSubProcess
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
 * &nbsp;&nbsp;&nbsp;default: PlotContainerUpdater
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-container-value &lt;PLOT_NAME|X_VALUE|Y_VALUE|X_ERROR_VALUE|Y_ERROR_VALUE|META_DATA&gt; (property: containerValue)
 * &nbsp;&nbsp;&nbsp;The type of value to update.
 * &nbsp;&nbsp;&nbsp;default: Y_VALUE
 * </pre>
 * 
 * <pre>-meta-data-key &lt;java.lang.String&gt; (property: metaDataKey)
 * &nbsp;&nbsp;&nbsp;The key to use when updating meta-data.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlotContainerUpdater
  extends AbstractContainerUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 7140175689043000123L;

  /**
   * The values of the plot container that can be modified.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PlotContainerValue {
    /** the name of the container. */
    PLOT_NAME,
    /** the X value. */
    X_VALUE,
    /** the Y value. */
    Y_VALUE,
    /** the X error value. */
    X_ERROR_VALUE,
    /** the Y error value. */
    Y_ERROR_VALUE,
    /** meta-data. */
    META_DATA
  }

  /** the value to modify. */
  protected PlotContainerValue m_ContainerValue;

  /** the key in case of meta-data. */
  protected String m_MetaDataKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies all sub-actors to process either the selected value of the plot container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "container-value", "containerValue",
	    PlotContainerValue.Y_VALUE);

    m_OptionManager.add(
	    "meta-data-key", "metaDataKey",
	    "");
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

    result = QuickInfoHelper.toString(this, "containerValue", m_ContainerValue);
    value = QuickInfoHelper.toString(this, "metaDataKey", m_MetaDataKey, ", meta-data: ");
    if (value != null)
      result += value;

    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets the type of value to update.
   *
   * @param value	the type of value
   */
  public void setContainerValue(PlotContainerValue value) {
    switch (value) {
      case PLOT_NAME:
	setContainerValueName(SequencePlotterContainer.VALUE_PLOTNAME);
	break;
      case X_VALUE:
	setContainerValueName(SequencePlotterContainer.VALUE_X);
	break;
      case Y_VALUE:
	setContainerValueName(SequencePlotterContainer.VALUE_Y);
	break;
      case X_ERROR_VALUE:
	setContainerValueName(SequencePlotterContainer.VALUE_ERROR_X);
	break;
      case Y_ERROR_VALUE:
	setContainerValueName(SequencePlotterContainer.VALUE_ERROR_Y);
	break;
      case META_DATA:
	setContainerValueName(SequencePlotterContainer.VALUE_METADATA);
	break;
      default:
	throw new IllegalArgumentException("Unhandled container value: " + value);
    }
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Compatibility	comp;
    Class[]		cls;

    result = super.setUp();

    if (result == null) {
      switch (m_ContainerValue) {
	case PLOT_NAME:
	  cls = new Class[]{String.class};
	  break;
	case X_ERROR_VALUE:
	case Y_ERROR_VALUE:
	  cls = new Class[]{Object[].class};
	  break;
	case X_VALUE:
	case Y_VALUE:
	  cls = new Class[]{Object.class};
	  break;
        case META_DATA:
	  cls = new Class[]{Object.class};
	  break;
	default:
	  cls    = null;
	  result = "Unhandled container value: " + m_ContainerValue;
	  break;
      }
      
      if (result == null) {
	comp = new Compatibility();
	if (!comp.isCompatible(((InputConsumer) firstActive()).accepts(), cls)) {
	  result = "First active sub-actor does not accept " + cls[0].getName();
	}
	else {
	  if (!comp.isCompatible(((OutputProducer) lastActive()).generates(), cls))
	    result = "Last active sub-actor does not generate " + cls[0].getName();
	}
      }
    }

    return result;
  }

  /**
   * Tries to obtain the container value.
   *
   * @param cont      the container to obtain the value from
   * @return          the value, if available
   * @throws java.lang.IllegalStateException  if failed to obtain value
   */
  protected Object getContainerValue(AbstractContainer cont) {
    SequencePlotterContainer  plotcont;

    if (m_ContainerValue == PlotContainerValue.META_DATA) {
      plotcont = (SequencePlotterContainer) cont;
      if (plotcont.getMetaData().containsKey(m_MetaDataKey))
        return plotcont.getMetaData().get(m_MetaDataKey);
      else
        throw new IllegalStateException("Meta-data value not present: " + m_MetaDataKey);
    }
    else {
      return super.getContainerValue(cont);
    }
  }
}
