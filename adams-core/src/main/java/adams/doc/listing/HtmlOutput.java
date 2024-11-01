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
 * HtmlOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.util.List;
import java.util.Map;

/**
 * Outputs the listing in HTML to the specified file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HtmlOutput
  extends AbstractFileBasedListingOutputWithEncoding {

  private static final long serialVersionUID = -3733873069877729194L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the listing in HTML to the specified file.";
  }

  /**
   * Returns the default output file.
   *
   * @return		the default
   */
  @Override
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile("${CWD}/listing.html");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The HTML file to store the listing in.";
  }

  /**
   * Outputs the supplied listing.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to output (module -> classnames)
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doGenerate(Class superclass, Map<String, List<String>> listing) {
    StringBuilder 	output;

    output = new StringBuilder();
    output.append("<html>\n");
    output.append("  <head>\n");
    output.append("    <title>Listing for ").append(superclass.getName()).append("</title>\n");
    output.append("  </head>\n");
    output.append("  <body>\n");
    output.append("    <h1>Listing for ").append(superclass.getName()).append("</h1>\n");
    for (String module: getModules(listing)) {
      output.append("    <h2>").append(module).append("</h2>\n");
      output.append("    <ul>\n");
      for (String classname: listing.get(module))
        output.append("      <li>").append(classname).append("</li>\n");
      output.append("    </ul>\n");
    }
    output.append("  </body>\n");
    output.append("</html>\n");

    return FileUtils.writeToFileMsg(m_OutputFile.getAbsolutePath(), output.toString(), false, m_Encoding.getValue());
  }
}
