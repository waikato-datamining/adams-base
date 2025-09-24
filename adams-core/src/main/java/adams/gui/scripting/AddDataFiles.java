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
 * AddDataFiles.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.data.io.input.DataContainerReader;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.visualization.container.AbstractContainerManager;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   add-data-files "&lt;datacontainer-reader scheme&gt;" "file1" "file2" ...</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Adds the data container loaded via the given reader to the currently loaded
 *    ones.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AddDataFiles
  extends AbstractFileReaderScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -3048379013750352301L;

  /** the action to execute. */
  public final static String ACTION = "add-data-files";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  @Override
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  @Override
  protected String getOptionsDescription() {
    return "\"<datacontainer-reader scheme>\" \"<file1>\" \"<file2>\" ...";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  @Override
  public String getDescription() {
    return
        "Adds the data containers loaded from files via the given reader to the currently "
      + "loaded ones.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  @Override
  protected String doProcess(String options) throws Exception {
    String			result;
    List<DataContainer> 	data;
    DataContainerReader		reader;
    int				n;
    int				i;
    AbstractContainerManager	manager;
    String[]			opts;
    String			msg;
    AntiAliasingSupporter	supporter;

    result = null;

    opts = OptionUtils.splitOptions(options);
    if (opts.length < 2)
      throw new IllegalArgumentException(
	"At least data container reader commandline and one file is expected, provided: " + options);

    // obtain reader
    reader = (DataContainerReader) OptionUtils.forCommandLine(DataContainerReader.class, opts[0]);

    // undo
    addUndoPoint("Saving undo data...", "Loading data with: " + OptionUtils.getCommandLine(reader));

    manager = getDataContainerPanel().getContainerManager();
    manager.startUpdate();

    for (n = 1; n < opts.length; n++) {
      if (isStopped())
        break;

      msg = null;

      showStatus("Loading (" + n + "/" + opts.length + "): " + opts[n]);

      // load data
      try {
	reader.setInput(new PlaceholderFile(opts[n]));
	data = reader.read();
        storeReaderData(data, reader);
      }
      catch (Exception e) {
	data = new ArrayList<>();
	msg = "Error reading data from '" + opts[n] + "': " + LoggingHelper.throwableToString(e);
	getLogger().severe(result);
      }

      if (msg == null) {
	for (i = 0; i < data.size(); i++)
	  manager.add(manager.newContainer(data.get(i)));
      }

      if (msg != null) {
	if (result == null)
	  result = msg;
	else
	  result += "\n" + msg;
      }

      if (getDataContainerPanel() instanceof AntiAliasingSupporter) {
        supporter = (AntiAliasingSupporter) getDataContainerPanel();
        // turn off anti-aliasing to speed up display
        if (manager.count() + data.size() > getOwner().getOwner().getProperties().getInteger("MaxNumContainersWithAntiAliasing", 50)) {
          if (supporter.isAntiAliasingEnabled())
            supporter.setAntiAliasingEnabled(false);
        }
      }
    }

    if (isStopped())
      showStatus("Interrupted!");
    else
      showStatus("");

    manager.finishUpdate();

    return result;
  }
}
