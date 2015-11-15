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
 * MarkdownTextAreaWithPreview.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.env.Environment;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import org.markdownj.MarkdownProcessor;

import javax.swing.JEditorPane;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;

/**
 * Text area for handling Markdown with code and preview tabs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MarkdownTextAreaWithPreview
  extends BasePanel {

  private static final long serialVersionUID = -1823780286250700366L;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** the text area for writing markdown code. */
  protected BaseTextArea m_TextCode;

  /** the preview. */
  protected JEditorPane m_PanePreview;

  /** the markdown processor. */
  protected MarkdownProcessor m_Processor;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Processor = new MarkdownProcessor();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_TextCode = new BaseTextArea();
    m_TextCode.setFont(GUIHelper.getMonospacedFont());
    m_TabbedPane.addTab("Write", new BaseScrollPane(m_TextCode));

    m_PanePreview = new JEditorPane();
    m_PanePreview.setEditable(false);
    m_PanePreview.setContentType("text/html");
    m_TabbedPane.addTab("Preview", new BaseScrollPane(m_PanePreview));

    m_TabbedPane.addChangeListener((ChangeEvent e) -> update());
  }

  /**
   * Sets the markdown code to display.
   *
   * @param value	the markdown code
   */
  public void setText(String value) {
    if (value == null)
      value = "";
    m_TextCode.setText(value);
    update();
  }

  /**
   * Returns the markdown code to display.
   *
   * @return		the markdown code
   */
  public String getText() {
    return m_TextCode.getText();
  }

  /**
   * Updates the markdown display.
   */
  protected void update() {
    String	html;

    html = m_Processor.markdown(getText());
    try {
      m_PanePreview.setText("<html>" + html + "</html>");
      m_PanePreview.setCaretPosition(0);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to update preview!", e);
    }
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    BaseFrame frame = new BaseFrame("Markdown test");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(new MarkdownTextAreaWithPreview(), BorderLayout.CENTER);
    frame.setSize(600, 400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
