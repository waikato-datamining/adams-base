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
 * BaseHtmlEditorPane.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;

import java.io.IOException;
import java.net.URL;

/**
 * Extends the {@link BaseEditorPane} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseHtmlEditorPane
  extends BaseEditorPane {

  private static final long serialVersionUID = -468246726891443535L;

  /** the default hyperlink listener. */
  protected DefaultHyperlinkListener m_DefaultHyperlinkListener;

  /**
   * Default constructor.
   */
  public BaseHtmlEditorPane() {
    super();
  }

  /**
   * Initializes the pane and loads the specified page.
   *
   * @param url		the page to load
   * @throws IOException	if loading the page fails
   */
  public BaseHtmlEditorPane(URL url) throws IOException {
    super(url);
  }

  /**
   * Initializes the pane and loads the specified page.
   *
   * @param url		the page to load
   * @throws IOException	if loading the page fails
   */
  public BaseHtmlEditorPane(String url) throws IOException {
    super(url);
  }

  /**
   * Initializes the pane with the specified mimetype and text.
   *
   * @param type	the mimetype
   * @param text 	the text to display
   */
  public BaseHtmlEditorPane(String type, String text) {
    super(type, text);
  }

  /**
   * Initializes the widget.
   */
  protected void initialize() {
    super.initialize();
    setEditable(false);
    setAutoscrolls(true);
    setContentType("text/html");
    m_DefaultHyperlinkListener = new DefaultHyperlinkListener();
  }

  /**
   * Adds the default hyperlink listener.
   */
  public void addDefaultHyperlinkListener() {
    addHyperlinkListener(m_DefaultHyperlinkListener);
  }

  /**
   * Removes the default hyperlink listener.
   */
  public void removeDefaultHyperlinkListener() {
    removeHyperlinkListener(m_DefaultHyperlinkListener);
  }
}
