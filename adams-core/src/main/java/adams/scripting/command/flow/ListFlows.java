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
 * ListFlows.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.io.GzipUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.scripting.command.AbstractCommandWithResponse;

import java.io.StringReader;

/**
 * Sends a list of registered running flows back.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class  ListFlows
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the flows. */
  protected SpreadSheet m_Flows;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Requests a list of all registered running flows.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Flows = newSheet();
  }

  /**
   * Creates an empty spreadsheet of the correct format.
   *
   * @return		the template
   */
  protected SpreadSheet newSheet() {
    SpreadSheet 	result;

    result = new DefaultSpreadSheet();
    result.getHeaderRow().addCell("I").setContent("ID");
    result.getHeaderRow().addCell("R").setContent("Root");
    result.getHeaderRow().addCell("A").setContent("Annotation");

    return result;
  }

  /**
   * Sets the payload for the command.
   *
   * @param value	the payload
   */
  @Override
  public void setPayload(byte[] value) {
    SpreadSheet			sheet;
    CsvSpreadSheetReader 	csv;
    StringReader 		reader;
    byte[]			decomp;

    if (value.length == 0) {
      m_Flows = newSheet();
      return;
    }

    decomp = GzipUtils.decompress(value, 1024);
    sheet  = new DefaultSpreadSheet();
    if (decomp != null) {
      reader = new StringReader(new String(decomp));
      csv    = new CsvSpreadSheetReader();
      sheet  = csv.read(reader);
      if (sheet == null)
	getLogger().severe("Failed to read payload:\n" + new String(decomp));
    }
    else {
      getLogger().severe("Failed to decompress payload!");
    }

    m_Flows = sheet;
  }

  /**
   * Returns the payload of the command, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getPayload() {
    if (isRequest())
      return new byte[0];
    else
      return GzipUtils.compress(m_Flows.toString().getBytes());
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    SpreadSheet 	sheet;
    Row			row;
    Flow		flow;

    super.prepareResponsePayload();

    sheet = newSheet();
    for (Integer id: RunningFlowsRegistry.getSingleton().ids()) {
      flow = RunningFlowsRegistry.getSingleton().getFlow(id);
      if (flow == null)
	continue;
      row  = sheet.addRow();
      row.addCell("I").setContent(id);
      row.addCell("R").setContent(flow.getRoot().getName());
      row.addCell("A").setContent(flow.getRoot().getAnnotations().getValue());
    }

    m_Flows = sheet;
  }

  /**
   * Returns the flow spreadsheet.
   *
   * @return		the flows
   */
  public SpreadSheet getFlows() {
    return m_Flows;
  }

  /**
   * Returns a short description of the command.
   *
   * @return		the description
   */
  public String toString() {
    return m_Flows.toString();
  }
}
