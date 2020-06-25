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
 * FileProcessingWithProgressBar.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.flow.control.CloseCallableDisplay;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.sink.ProgressBar;
import adams.flow.source.FileSystemSearch;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.ArrayLength;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.IncVariable;
import adams.flow.transformer.PassThrough;
import adams.flow.transformer.SetVariable;

/**
 <!-- globalinfo-start -->
 * Generates a Trigger with a FileSystemSearch which updates a progress bar.
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
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-max-variable &lt;adams.core.VariableName&gt; (property: maxVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the number of files (= maximum
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: max
 * </pre>
 *
 * <pre>-count-variable &lt;adams.core.VariableName&gt; (property: countVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the progress count.
 * &nbsp;&nbsp;&nbsp;default: count
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileProcessingWithProgressBar
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = -8975800423604842422L;

  public static final String PROGRESSBAR_NAME = "Progress";

  /** the variable to store the # of files. */
  protected VariableName m_MaxVariable;

  /** the variable to store the current progress in. */
  protected VariableName m_CountVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a Trigger with a FileSystemSearch which updates a progress bar.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-variable", "maxVariable",
      new VariableName("max"));

    m_OptionManager.add(
      "count-variable", "countVariable",
      new VariableName("count"));
  }

  /**
   * Sets the variable name to use for storing the number of files (= maximum).
   *
   * @param value	the variable name
   */
  public void setMaxVariable(VariableName value) {
    m_MaxVariable = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the number of files (= maximum).
   *
   * @return 		the variable name
   */
  public VariableName getMaxVariable() {
    return m_MaxVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String maxVariableTipText() {
    return "The name of the variable to use for storing the number of files (= maximum).";
  }

  /**
   * Sets the variable name to use for storing the progress count.
   *
   * @param value	the variable name
   */
  public void setCountVariable(VariableName value) {
    m_CountVariable = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the progress count.
   *
   * @return 		the variable name
   */
  public VariableName getCountVariable() {
    return m_CountVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String countVariableTipText() {
    return "The name of the variable to use for storing the progress count.";
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return false;
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected Actor doGenerate() {
    Trigger	result;

    result = new Trigger();
    result.setName(m_Name.isEmpty() ? "process" : m_Name);
    {
      CallableActors ca = new CallableActors();
      result.add(ca);
      {
	ProgressBar bar = new ProgressBar();
	bar.setName(PROGRESSBAR_NAME);
	bar.setShortTitle(true);
	bar.getOptionManager().setVariableForProperty("maximum", m_MaxVariable.getValue());
	ca.add(bar);
      }

      adams.flow.standalone.SetVariable reset = new adams.flow.standalone.SetVariable();
      reset.setVariableName((VariableName) m_CountVariable.getClone());
      reset.setVariableValue(new BaseText("0"));
      result.add(reset);

      FileSystemSearch search = new FileSystemSearch();
      search.setOutputArray(true);
      result.add(search);

      Tee numFiles = new Tee();
      numFiles.setName("# files");
      result.add(numFiles);
      {
        numFiles.add(new ArrayLength());

	SetVariable setVar = new SetVariable();
	setVar.setVariableName((VariableName) m_MaxVariable.getClone());
	numFiles.add(setVar);
      }

      Tee iterate = new Tee();
      iterate.setName("iterate");
      result.add(iterate);
      {
        iterate.add(new ArrayToSequence());

        Tee progress = new Tee();
        progress.setName("progress");
        iterate.add(progress);
	{
	  IncVariable inc = new IncVariable();
	  inc.setVariableName((VariableName) m_CountVariable.getClone());
	  inc.setOutputVariableValue(true);
	  progress.add(inc);

	  CallableSink cs = new CallableSink();
	  cs.setCallableName(new CallableActorReference(PROGRESSBAR_NAME));
	  progress.add(cs);
	}

	PassThrough doSomething = new PassThrough();
        doSomething.setName("do something with file");
        iterate.add(doSomething);
      }

      CloseCallableDisplay close = new CloseCallableDisplay();
      close.setCallableName(new CallableActorReference(PROGRESSBAR_NAME));
      result.add(close);
    }

    return result;
  }
}
