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
 * WekaCapabilitiesToSpreadSheet.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.option.OptionUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 <!-- globalinfo-start -->
 * Turns a weka.core.Capabilities object into a spreadsheet, listing all individual capabilities and whether they are supported.
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCapabilitiesToSpreadSheet
  extends AbstractConversion {

  private static final long serialVersionUID = -2595383708535316457L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a " + Capabilities.class.getName() + " object into a "
      + "spreadsheet, listing all individual capabilities and whether they "
      + "are supported.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Capabilities.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Capabilities	caps;
    Row			row;

    caps   = (Capabilities) m_Input;
    result = new DefaultSpreadSheet();
    result.setName(OptionUtils.getCommandLine(caps.getOwner()));

    // header
    row = result.getHeaderRow();
    row.addCell("K").setContent("Capability");
    row.addCell("S").setContent("Supported");
    row.addCell("D").setContent("Dependency");

    // data
    for (Capability cap: Capability.values()) {
      row = result.addRow();
      row.addCell("K").setContentAsString(cap.toString());
      row.addCell("S").setContent(caps.handles(cap));
      row.addCell("D").setContent(caps.hasDependency(cap));
    }
    row = result.addRow();
    row.addCell("K").setContentAsString("Minimum # Instances ");
    row.addCell("S").setContent(caps.getMinimumNumberInstances());

    return result;
  }
}
