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
 * RestoreVariable.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.Variables;
import adams.core.base.BaseText;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.VariableValueType;
import adams.flow.source.SelectFile;
import adams.flow.standalone.SetVariable;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RestoreVariable
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the name of the restore variable. */
  protected VariableName m_RestoreVar;

  /** the name of the restore file. */
  protected String m_RestoreFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simply generates a " + Utils.classToString(SetVariable.class) + " actor with a .props "
	+ "file name that can be used for storing the parameters of interactive actors "
	+ "like " + Utils.classToString(SelectFile.class) + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "restore-var", "restoreVar",
      new VariableName("restore"));

    m_OptionManager.add(
      "restore-file", "restoreFile",
      Variables.padName(ActorUtils.FLOW_FILENAME_LONG) + ".props");
  }

  /**
   * Sets the variable name to use for the restore file.
   *
   * @param value	the variable name
   */
  public void setRestoreVar(VariableName value) {
    m_RestoreVar = value;
    reset();
  }

  /**
   * Returns the variable name to use for the restore file.
   *
   * @return 		the variable name
   */
  public VariableName getRestoreVar() {
    return m_RestoreVar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String restoreVarTipText() {
    return "The name of the variable to use for storing the restore file.";
  }

  /**
   * Sets the file to store the settings in.
   *
   * @param value	the settings file
   */
  public void setRestoreFile(String value) {
    m_RestoreFile = value;
    reset();
  }

  /**
   * Returns the file to store the settings in.
   *
   * @return 		the settings file
   */
  public String getRestoreFile() {
    return m_RestoreFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String restoreFileTipText() {
    return "The file to store the settings in.";
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return true;
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected Actor doGenerate() {
    SetVariable 	result;

    result = new SetVariable();
    result.setName("restore file");
    result.setVariableValue(new BaseText(m_RestoreFile));
    result.setValueType(VariableValueType.FILE_FORWARD_SLASHES);
    result.setExpandValue(true);
    result.setVariableName(new VariableName(m_RestoreVar.getValue()));

    return result;
  }
}
