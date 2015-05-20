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
 * PlotProcessor.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.control.plotprocessor.AbstractPlotProcessor;
import adams.flow.control.plotprocessor.PassThrough;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractTransformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the specified processing algorithm to the stream of plot containers passing through. Injects any additionally created plot containers into the stream.
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
 * &nbsp;&nbsp;&nbsp;default: PlotProcessor
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
 * <pre>-processor &lt;adams.flow.control.plotprocessor.AbstractPlotProcessor&gt; (property: processor)
 * &nbsp;&nbsp;&nbsp;The plot processor to apply to the stream of plot containers passing through.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.plotprocessor.PassThrough
 * </pre>
 * 
 * <pre>-type &lt;PLOT|MARKER|OVERLAY|UPDATE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of plot container to create.
 * &nbsp;&nbsp;&nbsp;default: OVERLAY
 * </pre>
 * 
 * <pre>-drop-input &lt;boolean&gt; (property: dropInput)
 * &nbsp;&nbsp;&nbsp;If enabled, then the input plot container is dropped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlotProcessor
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7830411664107227699L;

  /** the key for storing the additional output tokens in the backup. */
  public final static String BACKUP_ADDITIONALOUTPUT = "additional output";

  /** the processor to apply. */
  protected AbstractPlotProcessor m_Processor;

  /** the type to use. */
  protected ContentType m_Type;

  /** whether to drop the input. */
  protected boolean m_DropInput;

  /** the additional container tokens that were generated. */
  protected List<Token> m_AdditionalOutputTokens;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the specified processing algorithm to the stream of plot "
	+ "containers passing through. Injects any additionally created "
	+ "plot containers into the stream.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processor",
      new PassThrough());

    m_OptionManager.add(
      "type", "type",
      ContentType.OVERLAY);

    m_OptionManager.add(
      "drop-input", "dropInput",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_AdditionalOutputTokens = new ArrayList<Token>();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = m_Processor.getQuickInfo();
    if (result == null)
      result = QuickInfoHelper.toString(this, "processor", m_Processor);
    else
      result = m_Processor.getClass().getSimpleName() + ": " + result;
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");
    value = QuickInfoHelper.toString(this, "dropInput", m_DropInput, ", drop");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Sets the processor to apply to the plot containers.
   *
   * @param value	the processor
   */
  public void setProcessor(AbstractPlotProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the processor to apply to the plot containers.
   *
   * @return		the processor
   */
  public AbstractPlotProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorTipText() {
    return "The plot processor to apply to the stream of plot containers passing through.";
  }

  /**
   * Sets the type of container to create.
   *
   * @param value	the type
   */
  public void setType(ContentType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of container to create.
   *
   * @return		the type
   */
  public ContentType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of plot container to create.";
  }

  /**
   * Sets whether to drop the input plot container completely.
   *
   * @param value	true if to drop input
   */
  public void setDropInput(boolean value) {
    m_DropInput = value;
    reset();
  }

  /**
   * Returns whether to drop the input plot container completely.
   *
   * @return		true if input dropped
   */
  public boolean getDropInput() {
    return m_DropInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropInputTipText() {
    return "If enabled, then the input plot container is dropped.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_ADDITIONALOUTPUT);
  }
  
  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_AdditionalOutputTokens != null)
      result.put(BACKUP_ADDITIONALOUTPUT, m_AdditionalOutputTokens);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ADDITIONALOUTPUT)) {
      m_AdditionalOutputTokens = (List<Token>) state.get(BACKUP_ADDITIONALOUTPUT);
      state.remove(BACKUP_ADDITIONALOUTPUT);
    }

    super.restoreState(state);
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    List<SequencePlotterContainer>	cont;
    
    m_AdditionalOutputTokens.clear();

    if (isLoggingEnabled())
      getLogger().fine("input: " + m_InputToken.getPayload());
    
    cont   = m_Processor.process((SequencePlotterContainer) m_InputToken.getPayload());
    result = m_Processor.getLastError();
    if ((result == null) && (cont != null)) {
      for (SequencePlotterContainer c: cont) {
	c.setValue(SequencePlotterContainer.VALUE_CONTENTTYPE, m_Type);
	m_AdditionalOutputTokens.add(new Token(c));
	if (isLoggingEnabled())
	  getLogger().fine("additional: " + c);
      }
    }
    
    if (!m_DropInput)
      m_OutputToken = m_InputToken;
    
    return result;
  }
  
  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_AdditionalOutputTokens.size() > 0) || super.hasPendingOutput();
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    
    if (m_AdditionalOutputTokens.size() > 0) {
      result = m_AdditionalOutputTokens.get(0);
      m_AdditionalOutputTokens.remove(0);
    }
    else {
      result = super.output();
    }
    
    return result;
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_AdditionalOutputTokens.clear();
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_Processor.cleanUp();
    
    super.cleanUp();
  }
}
