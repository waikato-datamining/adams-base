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
 * MarkdownHelper.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Helper class for Markdown content.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MarkdownHelper {

  /** the default options. */
  protected static MutableDataSet OPTIONS;

  /** the default parser. */
  protected static Parser PARSER;

  /** the default renderer. */
  protected static HtmlRenderer RENDERER;

  /**
   * Returns the default options.
   *
   * @return		the options
   */
  public static synchronized MutableDataSet getDefaultOptions() {
    if (OPTIONS == null)
      OPTIONS = new MutableDataSet();
    return OPTIONS;
  }

  /**
   * Returns the default parser.
   *
   * @return		the parser
   */
  public static synchronized Parser getDefaultParser() {
    if (PARSER == null)
      PARSER = Parser.builder(getDefaultOptions()).build();
    return PARSER;
  }

  /**
   * Returns the default renderer.
   *
   * @return		the renderer
   */
  public static synchronized HtmlRenderer getDefaultRenderer() {
    if (RENDERER == null)
      RENDERER = HtmlRenderer.builder(getDefaultOptions()).build();
    return RENDERER;
  }

  /**
   * Converts markdown to HTML.
   *
   * @param content	the markdown content
   * @return		the generated HTML
   */
  public static String markdownToHtml(String content) {
    Parser 		parser;
    HtmlRenderer 	renderer;
    Node 		document;

    parser   = getDefaultParser();
    renderer = getDefaultRenderer();
    document = parser.parse(content);

    return renderer.render(document);
  }
}
