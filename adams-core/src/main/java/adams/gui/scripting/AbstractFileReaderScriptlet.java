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
 * AbstractFileReaderScriptlet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.scripting;

import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

import java.util.List;

/**
 * Ancestor for scriptlets that use a reader for loading files.
 * The reader setup will be added to the report of the data containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileReaderScriptlet
  extends AbstractDataContainerPanelScriptlet {

  private static final long serialVersionUID = -1034823252709521416L;

  /** the key in the report for storing the reader setup. */
  public final static String READER_SETUP = "Reader.Setup";

  /**
   * Adds the reader setup and the file to the report.
   *
   * @param data	the list of containers to process
   * @param reader	the reader to store
   */
  protected void storeReaderData(List<DataContainer> data, OptionHandler reader) {
    ReportHandler	handler;
    Report		report;
    Field		field;
    String		cmdline;

    cmdline  = OptionUtils.getCommandLine(reader);
    for (DataContainer cont: data) {
      if (cont instanceof ReportHandler) {
	handler = (ReportHandler) cont;
	report  = handler.getReport();
	// reader
	field = new Field(READER_SETUP, DataType.STRING);
	report.addField(field);
	report.setStringValue(field.getName(), cmdline);
      }
    }
  }
}
