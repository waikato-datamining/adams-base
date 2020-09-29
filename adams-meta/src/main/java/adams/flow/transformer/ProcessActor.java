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
 * ProcessActor.java
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.ProcessActorContainer;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.processor.ActorProcessor;
import adams.flow.processor.GraphicalOutputProducingProcessor;
import adams.flow.processor.ListingProcessor;
import adams.flow.processor.ModifyingProcessor;
import adams.flow.processor.MultiProcessor;
import adams.gui.core.BasePanel;

import java.awt.BorderLayout;
import java.awt.Component;

/**
 <!-- globalinfo-start -->
 * Applies the specified processor to the incoming actor and forwards the result.<br>
 * For processors implementing adams.flow.processor.ModifyingProcessor the modified actor is forwarded (or, if not modified, the original one), all others just forward the incoming actor.<br>
 * If the processor should implement adams.flow.processor.ListingProcessor then any generated list gets stored as string array in the outgoing container as well.<br>
 * Processors implementing adams.flow.processor.GraphicalOutputProducingProcessor can optionall display the graphical output as well (off by default).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ProcessActor
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-processor &lt;adams.flow.processor.ActorProcessor&gt; (property: processor)
 * &nbsp;&nbsp;&nbsp;The actor processor to apply to the incoming actor.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.processor.MultiProcessor
 * </pre>
 *
 * <pre>-show-graphical-output &lt;boolean&gt; (property: showGraphicalOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, any Swing components generated by a adams.flow.processor.GraphicalOutputProducingProcessor
 * &nbsp;&nbsp;&nbsp;processor will get displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ProcessActor
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = 1877006726746922569L;

  /** the processor to apply. */
  protected ActorProcessor m_Processor;

  /** whether to display any graphical output. */
  protected boolean m_ShowGraphicalOutput;

  /** the graphical component (if any). */
  protected transient Component m_Component;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified processor to the incoming actor and forwards the result.\n"
      + "For processors implementing " + Utils.classToString(ModifyingProcessor.class) + " "
      + "the modified actor is forwarded (or, if not modified, the original one), all others just forward the incoming actor.\n"
      + "If the processor should implement " + Utils.classToString(ListingProcessor.class) + " "
      + "then any generated list gets stored as string array in the outgoing container as well.\n"
      + "Processors implementing " + Utils.classToString(GraphicalOutputProducingProcessor.class) + " "
      + "can optionall display the graphical output as well (off by default).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processor",
      new MultiProcessor());

    m_OptionManager.add(
      "show-graphical-output", "showGraphicalOutput",
      false);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Component = null;
  }

  /**
   * Sets the processor to use.
   *
   * @param value	the processor
   */
  public void setProcessor(ActorProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the processor to use.
   *
   * @return		the processor
   */
  public ActorProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String processorTipText() {
    return "The actor processor to apply to the incoming actor.";
  }

  /**
   * Sets whether to show any graphical output generated by the processor.
   *
   * @param value	true if to show
   */
  public void setShowGraphicalOutput(boolean value) {
    m_ShowGraphicalOutput = value;
    reset();
  }

  /**
   * Returns whether to show any graphical output generated by the processor.
   *
   * @return		true if to show
   */
  public boolean getShowGraphicalOutput() {
    return m_ShowGraphicalOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String showGraphicalOutputTipText() {
    return "If enabled, any Swing components generated by a "
      + Utils.classToString(GraphicalOutputProducingProcessor.class)
      + " processor will get displayed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if ((m_Processor instanceof GraphicalOutputProducingProcessor) && m_ShowGraphicalOutput)
      result = super.getQuickInfo() + ", ";
    else
      result = "";

    result += QuickInfoHelper.toString(this, "processor", m_Processor, "processor: ");
    result += QuickInfoHelper.toString(this, "showGraphicalOutput", m_ShowGraphicalOutput, "show output", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Actor.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{ProcessActorContainer.class};
  }

  /**
   * Does nothing.
   */
  @Override
  public void clearPanel() {
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result = new BasePanel(new BorderLayout());
    if (m_Component != null)
      result.add(m_Component, BorderLayout.CENTER);

    return result;
  }

  /**
   * Creates a title for the dialog. Default implementation only returns
   * the full name of the actor.
   *
   * @return		the title of the dialog
   */
  @Override
  protected String createTitle() {
    String	result;

    result = super.createTitle();

    if (m_Processor instanceof GraphicalOutputProducingProcessor)
      result += " - " + ((GraphicalOutputProducingProcessor) m_Processor).getTitle();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Actor			actor;
    ProcessActorContainer	cont;

    result      = null;
    actor       = m_InputToken.getPayload(Actor.class);
    m_Component = null;
    cont        = null;

    try {
      m_Processor.process(actor);

      if (m_Processor instanceof ModifyingProcessor) {
        if (((ModifyingProcessor) m_Processor).isModified()) {
          cont = new ProcessActorContainer(((ModifyingProcessor) m_Processor).getModifiedActor());
          if (isLoggingEnabled())
            getLogger().info("Actor got modified, forwarding modified one");
        }
        else {
          cont = new ProcessActorContainer(actor);
          if (isLoggingEnabled())
            getLogger().info("Actor didn't get modified, forwarding original one");
        }
      }
      else {
        cont = new ProcessActorContainer(actor);
        if (isLoggingEnabled())
          getLogger().info("Forwarding original actor");
      }

      if (m_Processor instanceof ListingProcessor) {
        cont.setValue(ProcessActorContainer.VALUE_LIST, ((ListingProcessor) m_Processor).getList().toArray(new String[0]));
        if (isLoggingEnabled())
          getLogger().info("Added list");
      }

      if (m_Processor instanceof GraphicalOutputProducingProcessor) {
        if (((GraphicalOutputProducingProcessor) m_Processor).hasGraphicalOutput()) {
          m_Component = ((GraphicalOutputProducingProcessor) m_Processor).getGraphicalOutput();
          if (isLoggingEnabled())
            getLogger().info("Generated graphical output");
        }
      }
    }
    catch (Exception e) {
      result = handleException("Failed to process actor!", e);
    }

    // only show dialog if we have graphical output and what to display it
    if ((result == null) && (m_Component != null) && m_ShowGraphicalOutput)
      result = super.doExecute();

    if ((result == null) && (cont != null))
      m_OutputToken = new Token(cont);

    return result;
  }
}
