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
 * MarkdownPane.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.net.MarkdownHelper;
import adams.env.Environment;

import java.awt.BorderLayout;

/**
 * Renders Markdown text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MarkdownPane
  extends BasePanel{

  private static final long serialVersionUID = -3021897813785552183L;

  /** the markdown text. */
  protected String m_Markdown;

  /** for rendering the markdown. */
  protected HtmlPane m_PaneView;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Markdown = "";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PaneView = new HtmlPane();
    add(m_PaneView, BorderLayout.CENTER);
  }

  /**
   * Sets the Markdown text and renders it.
   *
   * @param value	the markdown text
   */
  public void setText(String value) {
    String	html;

    if (value == null)
      value = "";
    m_Markdown = value;

    m_PaneView.setText(MarkdownHelper.markdownToHtml(m_Markdown));
  }

  /**
   * Returns the Markdown text.
   *
   * @return		the markdown text
   */
  public String getText() {
    return m_Markdown;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    MarkdownPane pane = new MarkdownPane();
    pane.setText("# Markdown test\n\n* item 1\n* item 2\n\n## Other stuff\n*italic* __bold__");
    BaseFrame frame = new BaseFrame("Markdown test");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(pane, BorderLayout.CENTER);
    frame.setSize(GUIHelper.getDefaultSmallDialogDimension());
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
