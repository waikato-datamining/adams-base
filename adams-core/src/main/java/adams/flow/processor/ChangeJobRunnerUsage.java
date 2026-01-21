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
 * ChangeJobRunnerUsage.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;
import adams.multiprocess.JobRunnerSupporter;
import adams.multiprocess.JobRunnerUser;

import java.lang.reflect.Array;

/**
 * Processor that updates {@link JobRunnerUser} and {@link JobRunnerSupporter} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeJobRunnerUsage
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** whether to update {@link JobRunnerUser} objects. */
  protected boolean m_UpdateJobRunnerUserObjects;

  /** the new state for {@link JobRunnerUser} objects. */
  protected boolean m_UseJobRunner;

  /** whether to update {@link JobRunnerSupporter} objects. */
  protected boolean m_UpdateJobRunnerSupporterObjects;

  /** the new state for {@link JobRunnerSupporter} objects. */
  protected boolean m_PreferJobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates " + Utils.classToString(JobRunnerUser.class) + " and " + Utils.classToString(JobRunnerSupporter.class) + " objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "update-job-runner-user-objects", "updateJobRunnerUserObjects",
      false);

    m_OptionManager.add(
      "use-job-runner", "useJobRunner",
      false);

    m_OptionManager.add(
      "update-job-runner-supporter-objects", "updateJobRunnerSupporterObjects",
      false);

    m_OptionManager.add(
      "prefer-job-runner", "preferJobRunner",
      false);
  }

  /**
   * Sets whether update {@link JobRunnerUser} objects.
   *
   * @param value	true if update
   */
  public void setUpdateJobRunnerUserObjects(boolean value) {
    m_UpdateJobRunnerUserObjects = value;
    reset();
  }

  /**
   * Returns whether update {@link JobRunnerUser} objects.
   *
   * @return		true if to update
   */
  public boolean getUpdateJobRunnerUserObjects() {
    return m_UpdateJobRunnerUserObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String updateJobRunnerUserObjectsTipText() {
    return "If enabled, " + Utils.classToString(JobRunnerUser.class) + " objects.";
  }

  /**
   * Sets the new state for {@link JobRunnerUser} objects.
   *
   * @param value	the new state
   */
  public void setUseJobRunner(boolean value) {
    m_UseJobRunner = value;
    reset();
  }

  /**
   * Returns the new state for {@link JobRunnerUser} objects.
   *
   * @return		the new state
   */
  public boolean getUseJobRunner() {
    return m_UseJobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String useJobRunnerTipText() {
    return "The new state for " + Utils.classToString(JobRunnerUser.class) + " objects.";
  }

  /**
   * Sets whether update {@link JobRunnerSupporter} objects.
   *
   * @param value	true if update
   */
  public void setUpdateJobRunnerSupporterObjects(boolean value) {
    m_UpdateJobRunnerSupporterObjects = value;
    reset();
  }

  /**
   * Returns whether update {@link JobRunnerSupporter} objects.
   *
   * @return		true if to update
   */
  public boolean getUpdateJobRunnerSupporterObjects() {
    return m_UpdateJobRunnerSupporterObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String updateJobRunnerSupporterObjectsTipText() {
    return "If enabled, " + Utils.classToString(JobRunnerSupporter.class) + " objects.";
  }

  /**
   * Sets the new state for {@link JobRunnerSupporter} objects.
   *
   * @param value	the new state
   */
  public void setPreferJobRunner(boolean value) {
    m_PreferJobRunner = value;
    reset();
  }

  /**
   * Returns the new state for {@link JobRunnerSupporter} objects.
   *
   * @return		the new state
   */
  public boolean getPreferJobRunner() {
    return m_PreferJobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String preferJobRunnerTipText() {
    return "The new state for " + Utils.classToString(JobRunnerSupporter.class) + " objects.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      private void process(Object obj) {
	if (m_UpdateJobRunnerUserObjects) {
	  if (obj instanceof JobRunnerUser) {
	    JobRunnerUser jobj = (JobRunnerUser) obj;
	    if (jobj.getUseJobRunner() != m_UseJobRunner) {
	      jobj.setUseJobRunner(m_UseJobRunner);
	      m_Modified = true;
	    }
	  }
	}
	if (m_UpdateJobRunnerSupporterObjects) {
	  if (obj instanceof JobRunnerSupporter) {
	    JobRunnerSupporter jobj = (JobRunnerSupporter) obj;
	    if (jobj.getPreferJobRunner() != m_PreferJobRunner) {
	      jobj.setPreferJobRunner(m_PreferJobRunner);
	      m_Modified = true;
	    }
	  }
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  for (int i = 0; i < Array.getLength(current); i++)
	    process(Array.get(current, i));
	}
	else {
	  process(current);
	}
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return true;
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
    });
  }
}
