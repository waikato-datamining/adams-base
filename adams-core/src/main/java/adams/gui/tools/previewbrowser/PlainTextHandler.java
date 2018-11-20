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
 * PlainTextHandler.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.core.TextEditorPanel;

import javax.swing.JPanel;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following plain text file types: *
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PlainTextHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4859255638148506547L;

  /** the tab size. */
  protected int m_TabSize;

  /** whether to perform line wrap. */
  protected boolean m_LineWrap;

  /** whether to wrap word style. */
  protected boolean m_WrapStyleWord;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following plain text file types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "tab-size", "tabSize",
      8, 0, null);

    m_OptionManager.add(
      "line-wrap", "lineWrap",
      false);

    m_OptionManager.add(
      "wrap-style-word", "wrapStyleWord",
      true);
  }

  /**
   * Sets the tab size, i.e., the number of maximum width characters.
   *
   * @param value	the number of maximum width chars
   */
  public void setTabSize(int value) {
    if (getOptionManager().isValid("tabSize", value)) {
      m_TabSize = value;
      reset();
    }
  }

  /**
   * Returns the tab size, i.e., the number of maximum width characters.
   *
   * @return		the number of maximum width chars
   */
  public int getTabSize() {
    return m_TabSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tabSizeTipText() {
    return "The number of characters to use for a tab.";
  }

  /**
   * Enables/disables line wrap.
   *
   * @param value	if true line wrap gets enabled
   */
  public void setLineWrap(boolean value) {
    m_LineWrap = value;
    reset();
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap enabled
   */
  public boolean getLineWrap() {
    return m_LineWrap;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lineWrapTipText() {
    return "Whether to wrap lines or not.";
  }

  /**
   * Sets the style of wrapping used if the text area is wrapping
   * lines.  If set to true the lines will be wrapped at word
   * boundaries (whitespace) if they are too long
   * to fit within the allocated width.  If set to false,
   * the lines will be wrapped at character boundaries.
   * By default this property is false.
   *
   * @param word indicates if word boundaries should be used
   *   for line wrapping
   * @see #getWrapStyleWord
   */
  public void setWrapStyleWord(boolean word) {
    m_WrapStyleWord = word;
    reset();
  }

  /**
   * Gets the style of wrapping used if the text area is wrapping
   * lines.  If set to true the lines will be wrapped at word
   * boundaries (ie whitespace) if they are too long
   * to fit within the allocated width.  If set to false,
   * the lines will be wrapped at character boundaries.
   *
   * @return if the wrap style should be word boundaries
   *  instead of character boundaries
   * @see #setWrapStyleWord
   */
  public boolean getWrapStyleWord() {
    return m_WrapStyleWord;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String wrapStyleWordTipText() {
    return "Whether to wrap lines at word boundaries or characters.";
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
    JPanel 		panel;
    List<String> 	lines;

    try {
      lines = FileUtils.loadFromFile(file, null, true);
      if (lines == null) {
        panel = new FailedToCreatePreviewPanel();
        return new PreviewPanel(panel, panel);
      }
      else {
        panel = new TextEditorPanel();
        ((TextEditorPanel) panel).setContent(Utils.flatten(lines, "\n"));
        ((TextEditorPanel) panel).setEditable(false);
        ((TextEditorPanel) panel).setTabSize(m_TabSize);
        ((TextEditorPanel) panel).setLineWrap(m_LineWrap);
        ((TextEditorPanel) panel).setWrapStyleWord(m_WrapStyleWord);
        return new PreviewPanel(panel, ((TextEditorPanel) panel).getTextArea());
      }
    }
    catch (Exception e) {
      return new NoPreviewAvailablePanel();
    }
  }
}
