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
 * AbstractGraphicsGenerator.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import adams.core.CleanUpHandler;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BaseFrame;
import adams.gui.print.JComponentWriter;
import adams.gui.print.NullWriter;

/**
 * Ancestor for actors that generate graphics of some kind.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGraphicsGenerator
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -3180921421553773745L;

  /** the title of the dialog. */
  protected String m_Title;

  /** an optional suffix for the filename (before the extension). */
  protected String m_Suffix;

  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /** the file/dir to write to. */
  protected PlaceholderFile m_Output;

  /** the writer to use. */
  protected JComponentWriter m_Writer;

  /** the component to create the screenshot from. */
  protected JComponent m_Component;

  /** the frame in use. */
  protected BaseFrame m_Frame;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "title", "title",
	    getDefaultTitle());

    m_OptionManager.add(
	    "suffix", "suffix",
	    "");

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth());

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight());

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Returns the default title for the dialog.
   *
   * @return		the default title
   */
  protected abstract String getDefaultTitle();

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected abstract int getDefaultWidth();

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected abstract int getDefaultHeight();

  /**
   * Sets the title of the dialog.
   *
   * @param value 	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the currently set title of the dialog.
   *
   * @return 		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title of the dialog.";
  }

  /**
   * Sets the suffix for the filename.
   *
   * @param value 	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the currently set suffix for the filename.
   *
   * @return 		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "An optional suffix for the filename, inserted before the extension.";
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }

  /**
   * Sets the output.
   *
   * @param value 	the output
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the output.
   *
   * @return 		the output
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String outputTipText();

  /**
   * Sets the writer.
   *
   * @param value 	the writer
   */
  public void setWriter(JComponentWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer.
   *
   * @return 		the writer
   */
  public JComponentWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for generating the graphics output.";
  }

  /**
   * Generates the component to display in the frame.
   *
   * @return		the component
   */
  protected abstract JComponent generateComponent();

  /**
   * Generates the filename for the output.
   *
   * @return		the file
   */
  protected abstract PlaceholderFile generateFilename();

  /**
   * Generates a frame with the specified title. Places the component in the
   * CENTER of the frame.
   *
   * @return		the generated frame
   */
  protected BaseFrame generateFrame() {
    BaseFrame	result;

    result = new BaseFrame(m_Title);
    result.getContentPane().setLayout(new BorderLayout());
    result.getContentPane().add(m_Component, BorderLayout.CENTER);
    result.setSize(m_Width, m_Height);
    result.setDefaultCloseOperation(BaseFrame.HIDE_ON_CLOSE);

    return result;
  }

  /**
   * Generates and displays the frame.
   */
  protected void displayFrame() {
    m_Component = generateComponent();
    m_Frame     = generateFrame();
    m_Frame.setVisible(true);
    m_Frame.toBack();
  }

  /**
   * Generates output for the component, stores it in the specified
   * file.
   *
   * @return		null if everything OK, otherwise the error message
   */
  protected String generateOutput() {
    String		result;
    PlaceholderFile	filename;

    result = null;

    filename = generateFilename();
    getLogger().info("Saving to: " + filename);

    m_Writer.setComponent(m_Component);
    m_Writer.setFile(filename);
    try {
      m_Writer.toOutput();
    }
    catch (Exception e) {
      result = handleException("Failed to write output", e);
    }
    m_Writer.setComponent(null);

    return result;
  }

  /**
   * Hides the frame.
   */
  protected void hideFrame() {
    m_Frame.setVisible(false);
    m_Frame.dispose();
    m_Frame = null;
  }

  /**
   * Disposes the generated component again.
   */
  protected void disposeComponent() {
    if (m_Component instanceof CleanUpHandler)
      ((CleanUpHandler) m_Component).cleanUp();

    m_Component = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (!isHeadless()) {
      displayFrame();
      result = generateOutput();
      hideFrame();
      disposeComponent();
    }

    return result;
  }
}
