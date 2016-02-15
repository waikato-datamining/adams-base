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
 * MultiProcessor.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import adams.core.option.OptionUtils;
import adams.flow.core.Actor;

/**
 <!-- globalinfo-start -->
 * A meta-processor that processes the actor sequentially with all sub-processors.
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
 * <pre>-processor &lt;adams.flow.processor.AbstractActorProcessor&gt; [-processor ...] (property: subProcessors)
 * &nbsp;&nbsp;&nbsp;The array of processors to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiProcessor
  extends AbstractModifyingProcessor 
  implements CheckProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 916259679452752997L;

  /** the processors. */
  protected AbstractActorProcessor[] m_Processors;
  
  /** the warnings. */
  protected StringBuilder m_Warnings;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-processor that processes the actor sequentially with all sub-processors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "processor", "subProcessors",
	    new AbstractActorProcessor[0]);
  }

  /**
   * Sets the processors to use.
   *
   * @param value	the processors to use
   */
  public void setSubProcessors(AbstractActorProcessor[] value) {
    if (value != null) {
      m_Processors = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": processors cannot be null!");
    }
  }

  /**
   * Returns the processors in use.
   *
   * @return		the processors
   */
  public AbstractActorProcessor[] getSubProcessors() {
    return m_Processors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String subProcessorsTipText() {
    return "The array of processors to use.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original)
   */
  @Override
  protected void processActor(Actor actor) {
    int				i;
    Actor			input;
    Actor			output;
    AbstractActorProcessor	processor;
    ModifyingProcessor		modifying;
    CheckProcessor		check;

    input      = actor;
    output     = actor;  // in case there are no processors provided
    m_Warnings = new StringBuilder();

    for (i = 0; i < m_Processors.length; i++) {
      getLogger().info(
	    "Processor " + (i+1) + "/" + m_Processors.length + ": "
	    + OptionUtils.getCommandLine(m_Processors[i]));

      processor = m_Processors[i];
      if (processor instanceof ModifyingProcessor) {
	modifying = (ModifyingProcessor) processor;
	modifying.setNoCopy(true);
      }
      processor.process(input);
      output = null;
      if (processor.hasErrors()) {
	addError("--> " + processor.getClass().getSimpleName(), false);
	for (String error: processor.getErrors())
	  addError(error, false);
      }
      if (processor instanceof ModifyingProcessor) {
	modifying = (ModifyingProcessor) processor;
	if (modifying.isModified()) {
	  output     = modifying.getModifiedActor();
	  m_Modified = true;
	}
      }
      if (processor instanceof CheckProcessor) {
	check = (CheckProcessor) processor;
	if (check.getWarnings() != null) {
	  m_Warnings.append("--> " + processor.getClass().getSimpleName() + ":\n");
	  if (check.getWarningHeader() != null)
	    m_Warnings.append(check.getWarningHeader() + "\n");
	  m_Warnings.append(check.getWarnings());
	  m_Warnings.append("\n\n");
	}
      }

      // prepare input for next filter
      if (output != null)
	input = output;
    }

    getLogger().info("Finished!");
  }

  /**
   * Returns the string that explains the warnings.
   * 
   * @return		the heading for the warnings, null if not available
   */
  public String getWarningHeader() {
    return null;
  }

  /**
   * Returns the warnings, if any, resulting from the check.
   * 
   * @return		the warnings, null if no warnings.
   */
  public String getWarnings() {
    if (m_Warnings.length() == 0)
      return null;
    else
      return m_Warnings.toString();
  }
}
