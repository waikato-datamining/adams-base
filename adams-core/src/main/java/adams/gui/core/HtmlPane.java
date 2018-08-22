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
 * HtmlPane.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.env.Environment;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import java.awt.BorderLayout;

/**
 * Renders HTML.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HtmlPane
  extends BasePanel {

  private static final long serialVersionUID = -3021897813785552183L;

  /** the html source. */
  protected String m_HTML;

  /** for rendering the html. */
  protected JFXPanel m_PaneView;

  /** the Webview. */
  protected WebView m_WebView;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_HTML    = "";
    m_WebView = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PaneView = new JFXPanel();
    add(m_PaneView, BorderLayout.CENTER);
  }

  /**
   * Sets the HTML source and renders it.
   *
   * @param value	the markdown text
   */
  public void setText(String value) {
    if (value == null)
      value = "";
    m_HTML = value;

    Platform.setImplicitExit(false);
    Platform.runLater(() -> {
      if (m_WebView == null) {
	m_WebView = new WebView();
	m_PaneView.setScene(new Scene(m_WebView));
      }
      m_WebView.getEngine().loadContent(m_HTML);
    });
  }

  /**
   * Returns the HTML source.
   *
   * @return		the HTML
   */
  public String getText() {
    return m_HTML;
  }

  /**
   * Returns the webview.
   *
   * @return		the view
   */
  public WebView getWebView() {
    return m_WebView;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    HtmlPane pane = new HtmlPane();
    pane.setText("<html><body><h1>Header 1</h1><em>emphasis</emp></body></html>");
    BaseFrame frame = new BaseFrame("Markdown test");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(pane, BorderLayout.CENTER);
    frame.setSize(GUIHelper.getDefaultSmallDialogDimension());
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
