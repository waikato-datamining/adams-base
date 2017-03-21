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
 * RotatingFileHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import adams.core.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for rotating log files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RotatingFileHandler
  extends FileHandler {

  /** the extensions for the rotating logs (incl dots). */
  protected String[] m_RotatingExtensions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    setRotatingExtensions(new String[]{".log.1", ".log.2", ".log.3", ".log.4", ".log.5"});
  }

  /**
   * Sets the extensions to use for the rotating logs.
   *
   * @param value	the extensions (incl. dots)
   */
  public void setRotatingExtensions(String[] value) {
    m_RotatingExtensions = value;
    reset();
  }

  /**
   * Returns the extensions in use for the rotating logs.
   *
   * @return		the extensions (incl dots)
   */
  public String[] getRotatingExtensions() {
    return m_RotatingExtensions;
  }

  /**
   * Hook method for performing setup before processing first log record.
   */
  @Override
  protected void setUp() {
    int		i;
    List<File>	logs;
    File 	logNew;
    File 	logOld;

    super.setUp();

    // rotate log files
    if (!m_LogIsDir && m_RotatingExtensions.length > 0) {
      logs = new ArrayList<>();
      logs.add(m_LogFile);
      for (i = 0; i < m_RotatingExtensions.length; i++)
	logs.add(FileUtils.replaceExtension(m_LogFile, m_RotatingExtensions[i]));
      for (i = logs.size() - 1; i >= 1; i--) {
	logOld = logs.get(i);
	logNew = logs.get(i - 1);
	if (logOld.exists())
	  logOld.delete();
	if (logNew.exists()) {
	  try {
	    FileUtils.move(logNew, logOld);
	  }
	  catch (Exception e) {
	    System.err.println(getClass().getName() + ": Failed to move log files " + logNew + " -> " + logOld);
	    e.printStackTrace();
	  }
	}
      }
    }
  }
}
