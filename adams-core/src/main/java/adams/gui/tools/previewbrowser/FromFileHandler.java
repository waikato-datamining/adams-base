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
 * FromFileHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;

import javax.swing.JPanel;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays files using the preview handler configuration (command-line or nested format) stored in the specified file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-configuration &lt;adams.core.io.PlaceholderFile&gt; (property: configuration)
 * &nbsp;&nbsp;&nbsp;The file with the preview handler configuration to use (command-line or
 * &nbsp;&nbsp;&nbsp;nested format).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromFileHandler
    extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4859255638148506547L;

  /** the file with the configuration. */
  protected PlaceholderFile m_Configuration;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays files using the preview handler configuration (command-line or nested format) stored in the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "configuration", "configuration",
        new PlaceholderFile());
  }

  /**
   * Sets the file with the preview handler configuration to use.
   *
   * @param value	the configuration to use
   */
  public void setConfiguration(PlaceholderFile value) {
    m_Configuration = value;
    reset();
  }

  /**
   * Returns the file with the preview handler configuration in use.
   *
   * @return		the configuration to use
   */
  public PlaceholderFile getConfiguration() {
    return m_Configuration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String configurationTipText() {
    return "The file with the preview handler configuration to use (command-line or nested format).";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{MATCH_ALL};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    JPanel 			panel;
    List<String> 		lines;
    String			setup;
    AbstractContentHandler	handler;
    String			msg;

    msg = checkFile(m_Configuration);
    if (msg != null) {
      getLogger().severe("Problem with configuration: " + msg);
      return new NoPreviewAvailablePanel();
    }

    try {
      lines = FileUtils.loadFromFile(m_Configuration, null, true);
      setup = Utils.flatten(lines, "\n");
      handler = (AbstractContentHandler) OptionUtils.forString(AbstractContentHandler.class, setup);
      panel = handler.createPreview(file);
      return new PreviewPanel(panel, panel);
    }
    catch (Exception e) {
      return new NoPreviewAvailablePanel();
    }
  }
}
