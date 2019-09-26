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
 * PrettyPrintJSONConversionPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.data.conversion.Conversion;
import adams.data.conversion.JsonToString;
import adams.data.conversion.MultiConversion;
import adams.data.conversion.StringToJson;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

/**
 * Pretty prints JSON.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrettyPrintJSONConversionPanel
  extends AbstractSimpleConversionPanel {

  private static final long serialVersionUID = 4355704946007035655L;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_TextAreaInput.setTextFont(Fonts.getMonospacedFont());
    m_TextAreaOutput.setTextFont(Fonts.getMonospacedFont());
  }

  /**
   * Returns the file chooser to use for the input.
   *
   * @return		the file chooser
   */
  @Override
  protected BaseFileChooser newInputFileChooser() {
    BaseFileChooser	result;
    ExtensionFileFilter	filter;

    result = new BaseFileChooser();
    filter = new ExtensionFileFilter("JSON files", "json");
    result.addChoosableFileFilter(filter);
    result.setAcceptAllFileFilterUsed(true);
    result.setFileFilter(filter);

    return result;
  }

  /**
   * Returns the file chooser to use for the output.
   *
   * @return		the file chooser
   */
  protected BaseFileChooser newOutputFileChooser() {
    return newInputFileChooser();
  }

  /**
   * Performs the conversion.
   */
  @Override
  protected void convert() {
    MultiConversion	multi;
    StringToJson 	s2j;
    JsonToString 	j2s;
    String		msg;

    m_TextAreaOutput.setText("");

    s2j = new StringToJson();
    j2s = new JsonToString();
    j2s.setPrettyPrinting(true);
    multi = new MultiConversion();
    multi.setSubConversions(new Conversion[]{s2j, j2s});

    multi.setInput(m_TextAreaInput.getText());
    msg = multi.convert();
    if (msg == null) {
      m_TextAreaOutput.setText((String) multi.getOutput());
      m_TextAreaOutput.setCaretPosition(0);
    }
    else {
      GUIHelper.showErrorMessage(this, msg);
    }
  }
}
