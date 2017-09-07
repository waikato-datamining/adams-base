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
 * MultiMonitor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import adams.core.QuickInfoHelper;

import java.io.File;

/**
 * Combines the results of the specified monitors according to the combination
 * type. Uses short-circuit evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiMonitor
  extends AbstractFileChangeMonitor {

  private static final long serialVersionUID = -847317789367221526L;

  /**
   * Defines how to combine the results from the base monitors.
   */
  public enum CombinationType {
    AND,
    OR,
  }

  /** the base monitors to use. */
  protected FileChangeMonitor[] m_Monitors;

  /** the combination. */
  protected CombinationType m_CombinationType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Combines the results of the specified monitors according to the "
        + "combination type.\n"
      + "Uses short-circuit evaluation to speed up the process.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "monitors", "monitors",
      new FileChangeMonitor[0]);

    m_OptionManager.add(
      "combination-type", "combinationType",
      CombinationType.OR);
  }

  /**
   * Sets the monitors to apply.
   *
   * @param value	the monitors
   */
  public void setMonitors(FileChangeMonitor[] value) {
    m_Monitors = value;
    reset();
  }

  /**
   * Returns the monitors to apply.
   *
   * @return  		the monitors
   */
  public FileChangeMonitor[] getMonitors() {
    return m_Monitors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String monitorsTipText() {
    return "The monitors to apply and combine.";
  }

  /**
   * Sets how to combine the results of the monitors.
   *
   * @param value	the type
   */
  public void setCombinationType(CombinationType value) {
    m_CombinationType = value;
    reset();
  }

  /**
   * Returns how to combine the results of the monitors.
   *
   * @return  		the type
   */
  public CombinationType getCombinationType() {
    return m_CombinationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String combinationTypeTipText() {
    return "How to combine the results of the monitors.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "monitors", m_Monitors, "monitors: ");
  }

  /**
   * Performs the actual initialization of the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File file) {
    String	result;
    int		i;

    result = null;

    if (m_Monitors.length == 0) {
      result = "No monitors defined!";
    }
    else {
      for (i = 0; i < m_Monitors.length; i++) {
        result = m_Monitors[i].initialize(file);
        if (result != null) {
	  result = "Monitor #" + (i + 1) + ": " + result;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Performs the actual check whether the file has changed.
   *
   * @param file	the file to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File file) {
    boolean		result;
    int			i;

    result = false;

    for (i = 0; i < m_Monitors.length; i++) {
      switch (m_CombinationType) {
	case AND:
	  result = m_Monitors[i].hasChanged(file);
	  if (!result)
	    break;
	  break;

	case OR:
	  result = m_Monitors[i].hasChanged(file);
	  if (result)
	    break;
	  break;

	default:
	  throw new IllegalStateException("Unhandled combination type: " + m_CombinationType);
      }
    }

    return result;
  }

  /**
   * Performs the actual updating of the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File file) {
    String	result;
    int		i;

    result = null;

    if (m_Monitors.length == 0) {
      result = "No monitors defined!";
    }
    else {
      for (i = 0; i < m_Monitors.length; i++) {
        result = m_Monitors[i].update(file);
        if (result != null) {
	  result = "Monitor #" + (i + 1) + ": " + result;
	  break;
	}
      }
    }

    return result;
  }
}
