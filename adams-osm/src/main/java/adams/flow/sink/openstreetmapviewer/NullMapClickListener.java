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
 * NullMapClickListener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.event.MouseEvent;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 <!-- globalinfo-start -->
 * Does not process any clicks, simply ignores them.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullMapClickListener
  extends AbstractMapClickListener {

  /** for serialization. */
  private static final long serialVersionUID = 3729353252465470725L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not process any clicks, simply ignores them.";
  }

  /**
   * Does nothing.
   * 
   * @param viewer	the associated viewer
   * @param e		the associated event
   * @return		always false
   */
  @Override
  protected boolean processClick(JMapViewer viewer, MouseEvent e) {
    return false;
  }

  /**
   * Returns whether a database connection is required.
   * 
   * @return		true if connection required
   */
  @Override
  public boolean requiresDatabaseConnection() {
    return false;
  }
}
