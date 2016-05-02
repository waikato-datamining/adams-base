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
 * JobRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.distributed;

import adams.core.SerializationHelper;
import adams.multiprocess.CallableWithResult;
import adams.scripting.command.AbstractCommandWithResponse;
import adams.scripting.engine.RemoteScriptingEngine;

import java.util.logging.Level;

/**
 * Encapsulates a JobRunner.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobRunner
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = 897550813042378111L;

  /** the JobRunner. */
  protected adams.multiprocess.JobRunner m_JobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates a JobRunner.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_JobRunner = null;
  }

  /**
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    Object[]	obj;

    if (value.length == 0) {
      m_JobRunner = null;
      return;
    }

    try {
      obj = SerializationHelper.fromByteArray(value);
      if (obj.length == 1)
	m_JobRunner = (adams.multiprocess.JobRunner) obj[0];
      else
	getLogger().severe("Failed to get single JobRunner from response byte array; got " + obj.length + " objects instead!");
    }
    catch (Exception e) {
      m_JobRunner = null;
      getLogger().log(Level.SEVERE, "Failed to create JobRunner from response byte array!", e);
    }
  }

  /**
   * Returns the payload of the request, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    if (m_JobRunner != null) {
      try {
	return SerializationHelper.toByteArray(m_JobRunner);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to serialize JobRunner (request)!", e);
	return new byte[0];
      }
    }
    else {
      return new byte[0];
    }
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  @Override
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_JobRunner};
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    Object[]	obj;

    if (value.length == 0) {
      m_JobRunner = null;
      return;
    }

    try {
      obj = SerializationHelper.fromByteArray(value);
      if (obj.length == 1)
	m_JobRunner = (adams.multiprocess.JobRunner) obj[0];
      else
	getLogger().severe("Failed to get single JobRunner from response byte array; got " + obj.length + " objects instead!");
    }
    catch (Exception e) {
      m_JobRunner = null;
      getLogger().log(Level.SEVERE, "Failed to create JobRunner from response byte array!", e);
    }
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    if (m_JobRunner != null) {
      try {
	return SerializationHelper.toByteArray(m_JobRunner);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to serialize JobRunner (response)!", e);
	return new byte[0];
      }
    }
    else {
      return new byte[0];
    }
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  @Override
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_JobRunner};
  }

  /**
   * Sets the JobRunner to use.
   *
   * @param value	the JobRunner
   */
  public void setJobRunner(adams.multiprocess.JobRunner value) {
    m_JobRunner = value;
  }

  /**
   * Returns the JobRunner in use.
   *
   * @return		the JobRunner, null if none set
   */
  public adams.multiprocess.JobRunner getJobRunner() {
    return m_JobRunner;
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doHandleRequest(final RemoteScriptingEngine engine) {
    CallableWithResult<String>  job;

    job = new CallableWithResult<String>() {
      @Override
      protected String doCall() throws Exception {
        // execute jobs
        m_JobRunner.start();
        m_JobRunner.stop();
        // send back result
        JobRunner cmd = new JobRunner();
        cmd.setRequest(false);
        cmd.setJobRunner(m_JobRunner);
        return m_ResponseConnection.sendResponse(cmd);
      }
    };

    // queue job
    engine.executeJob(job);

    return null;
  }
}
