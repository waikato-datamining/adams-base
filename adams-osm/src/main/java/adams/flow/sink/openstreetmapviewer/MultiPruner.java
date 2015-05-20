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
 * MultiPruner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.JMapViewerTree;

import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * A meta-pruner that applies multiple pruners.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-pruner &lt;adams.flow.sink.openstreetmapviewer.AbstractMapObjectPruner&gt; [-pruner ...] (property: pruners)
 * &nbsp;&nbsp;&nbsp;The array of pruners to apply.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiPruner
  extends AbstractMapObjectPruner {

  /** for serialization. */
  private static final long serialVersionUID = 805661569976845842L;

  /** the pruners. */
  protected AbstractMapObjectPruner[] m_Overlays;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-pruner that applies multiple pruners.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "pruner", "pruners",
	    new AbstractMapObjectPruner[0]);
  }

  /**
   * Sets the pruners to use.
   *
   * @param value	the oruners to use
   */
  public void setPruners(AbstractMapObjectPruner[] value) {
    if (value != null) {
      m_Overlays = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": pruners cannot be null!");
    }
  }

  /**
   * Returns the pruners in use.
   *
   * @return		the pruners
   */
  public AbstractMapObjectPruner[] getPruners() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String prunersTipText() {
    return "The array of pruners to apply.";
  }

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  @Override
  protected void doPrune(JMapViewerTree tree) {
    int		i;
    
    for (i = 0; i < m_Overlays.length; i++) {
      getLogger().info(
	    "Pruner " + (i+1) + "/" + m_Overlays.length + ": "
	    + OptionUtils.getCommandLine(m_Overlays[i]));

      m_Overlays[i].prune(tree);
    }

    getLogger().info("Finished!");
  }
}
