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
 * PDFBoxRenderer.java
 * Copyright (C) 2016 Apache
 */

package adams.core.io;

import adams.core.License;
import adams.core.annotation.ThirdPartyCopyright;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import java.io.IOException;

/**
 * PDFRenderer subclass, uses PDFBoxPageDrawer for custom rendering.
 *
 * @author John Hewson
 * @version $Revision$
 */
@ThirdPartyCopyright(
  author = "John Hewson",
  license = License.APACHE2,
  url = "https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/rendering/CustomPageDrawer.java"
)
public class PDFBoxRenderer extends PDFRenderer {

  /**
   * Initializes the renderer with the document.
   *
   * @param document	the document to render
   */
  public PDFBoxRenderer(PDDocument document) {
    super(document);
  }

  /**
   * Returns a new PageDrawer instance, using the given parameters.
   */
  @Override
  protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
    return new PDFBoxPageDrawer(parameters);
  }
}
