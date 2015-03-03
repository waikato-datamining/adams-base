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
 * TimeseriesCommandProcessor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import adams.data.timeseries.Timeseries;
import adams.gui.visualization.container.DataContainerPanel;
import adams.gui.visualization.timeseries.TimeseriesExplorer;
import adams.gui.visualization.timeseries.TimeseriesPanel;

/**
 <!-- globalinfo-start -->
 * General actions:<br/>
 * <br/>
 * connect &lt;driver&gt; &lt;URL&gt; &lt;user&gt; [password]<br/>
 * &nbsp;&nbsp;&nbsp;Connects to the database.<br/>
 * <br/>
 * delete-data &lt;comma-separated list of DB-IDs&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Deletes the spectra with the specified DB-IDs from the database.<br/>
 * <br/>
 * disconnect<br/>
 * &nbsp;&nbsp;&nbsp;Disconnects from the database.<br/>
 * <br/>
 * run-tool &lt;tool + options&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Runs the specified tool.<br/>
 * <br/>
 * <br/>
 * Actions for adams.gui.core.UndoHandler:<br/>
 * <br/>
 * disable-undo<br/>
 * &nbsp;&nbsp;&nbsp;Disables the undo support, if available.<br/>
 * <br/>
 * enable-undo<br/>
 * &nbsp;&nbsp;&nbsp;Enables the undo support, if available.<br/>
 * <br/>
 * <br/>
 * Actions for knir.gui.visualization.spectrum.TimeseriesPanel:<br/>
 * <br/>
 * add-data &lt;comma-separated list of DB-IDss&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Adds the spectrums to the currently loaded ones.<br/>
 * <br/>
 * add-data-file &lt;spectrum-reader scheme&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Adds the spectrum loaded via the given reader to the currently loaded ones.<br/>
 * <br/>
 * clear-data<br/>
 * &nbsp;&nbsp;&nbsp;Removes all spectra.<br/>
 * <br/>
 * filter &lt;classname + options&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Executes the filter on the visible spectra, replaces the original ones.<br/>
 * <br/>
 * filter-overlay &lt;classname + options&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Executes the filter on the visible spectra, overlays the original ones.<br/>
 * <br/>
 * invisible &lt;comma-separated list of 1-based indices&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Sets the visibility of the specified spectrums to false.<br/>
 * &nbsp;&nbsp;&nbsp;NB: index is based on the order the spectrums haven beeen loaded into the <br/>
 * &nbsp;&nbsp;&nbsp;system, includes all spectrums, not just visible ones.<br/>
 * <br/>
 * remove-data &lt;comma-separated list of 1-based indices&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Removes the spectrums with the specified indices.<br/>
 * &nbsp;&nbsp;&nbsp;NB: index is based on the order the spectrums haven beeen loaded into the <br/>
 * &nbsp;&nbsp;&nbsp;system, includes all spectrums, not just visible ones.<br/>
 * <br/>
 * run-flow &lt;filename&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Executes the flow stored in the given file.<br/>
 * &nbsp;&nbsp;&nbsp;The base actor has to be 'adams.flow.control.SubProcess'.<br/>
 * &nbsp;&nbsp;&nbsp;The processed spectra replace the currently loaded ones.<br/>
 * <br/>
 * run-flow-overlay &lt;filename&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Executes the flow stored in the given file.<br/>
 * &nbsp;&nbsp;&nbsp;The base actor has to be 'adams.flow.control.SubProcess'.<br/>
 * &nbsp;&nbsp;&nbsp;The processed spectra overlay the currently loaded ones.<br/>
 * <br/>
 * select-wave-number &lt;wave number&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Selects a wave number.<br/>
 * <br/>
 * set-data &lt;index&gt; &lt;DB-ID&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Replaces the currently loaded spectrum at the specified 1-based index with <br/>
 * &nbsp;&nbsp;&nbsp;the one associated with the database ID.<br/>
 * <br/>
 * visible &lt;comma-separated list of 1-based indices&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Sets the visibility of the specified spectrums to true.<br/>
 * &nbsp;&nbsp;&nbsp;NB: index is based on the order the spectrums haven beeen loaded into the <br/>
 * &nbsp;&nbsp;&nbsp;system, includes all spectrums, not just visible ones.<br/>
 * <br/>
 * write-spectrum &lt;1-based index&gt; &lt;filename&gt;<br/>
 * &nbsp;&nbsp;&nbsp;Saves the spectrum at the specified position to the file.<br/>
 * <br/>
 * <br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesCommandProcessor
  extends CommandProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -4413109517993138096L;

  /**
   * Initializes the processor. Still needs to set the owner.
   *
   * @see	#setOwner(AbstractScriptingEngine)
   */
  public TimeseriesCommandProcessor() {
    super();
  }

  /**
   * Initializes the processor.
   *
   * @param owner	the owning scripting engine
   */
  public TimeseriesCommandProcessor(AbstractScriptingEngine owner) {
    super(owner);
  }

  /**
   * Returns the object that is to be used for the undo point.
   *
   * @return		the object to store as undo point
   */
  @Override
  protected Object getUndoObject() {
    return getTimeseriesPanel().getContainerManager().getAll();
  }

  /**
   * Returns the DataContainer panel.
   *
   * @return		the panel or null
   */
  @Override
  public DataContainerPanel getDataContainerPanel() {
    if (getBasePanel() instanceof TimeseriesExplorer)
      return ((TimeseriesExplorer) getBasePanel()).getTimeseriesPanel();
    else
      return super.getDataContainerPanel();
  }

  /**
   * Returns the spectrum panel, if available.
   *
   * @return		the panel
   */
  public TimeseriesPanel getTimeseriesPanel() {
    if (getBasePanel() instanceof TimeseriesPanel)
      return (TimeseriesPanel) getBasePanel();
    else if (getBasePanel() instanceof TimeseriesExplorer)
      return ((TimeseriesExplorer) getBasePanel()).getTimeseriesPanel();
    else
      return null;
  }

  /**
   * Returns the class that is required in the flow.
   *
   * @return		the required class
   */
  @Override
  protected Class getRequiredFlowClass() {
    return Timeseries.class;
  }

  /**
   * Checks the following requirement.
   *
   * @param requirement	the requirement class that needs to be present
   * @return		"" if met, error message if not met, null if not processed
   */
  @Override
  protected String checkRequirement(Class requirement) {
    String	result;

    result = super.checkRequirement(requirement);

    if (result == null) {
      if (requirement == TimeseriesPanel.class) {
	if (getTimeseriesPanel() == null)
	  result = createRequirementError(requirement);
	else
	  result = "";
      }
    }

    return result;
  }
}
