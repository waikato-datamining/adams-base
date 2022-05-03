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
 * X11ColorProvider.java
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.gui.core.ColorHelper;

/**
 <!-- globalinfo-start -->
 * Color provider using a subset of colors from the X11 RGB text file.<br>
 * <br>
 * For more information, see:<br>
 * Jim Gettys, Paul Ravelling, John C. Thomas, Jim Fulton (1980s). X11 color names.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{JimGettys1980s,
 *    author = {Jim Gettys, Paul Ravelling, John C. Thomas, Jim Fulton},
 *    title = {X11 color names},
 *    year = {1980s},
 *    HTTP = {https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;X11_color_names}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class X11ColorProvider
  extends AbstractColorProvider
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Color provider using a subset of colors from the X11 RGB text file.\n\n"
      + "For more information, see:\n"
      + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Jim Gettys, Paul Ravelling, John C. Thomas, Jim Fulton");
    result.setValue(Field.YEAR, "1980s");
    result.setValue(Field.TITLE, "X11 color names");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/X11_color_names");

    return result;
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_CheckTooDark   = false;
    m_AllowDarkening = false;

    m_DefaultColors.add(ColorHelper.valueOf("#00FFFF"));
    m_DefaultColors.add(ColorHelper.valueOf("#7FFFD4"));
    m_DefaultColors.add(ColorHelper.valueOf("#000000"));
    m_DefaultColors.add(ColorHelper.valueOf("#0000FF"));
    m_DefaultColors.add(ColorHelper.valueOf("#8A2BE2"));
    m_DefaultColors.add(ColorHelper.valueOf("#A52A2A"));
    m_DefaultColors.add(ColorHelper.valueOf("#DEB887"));
    m_DefaultColors.add(ColorHelper.valueOf("#5F9EA0"));
    m_DefaultColors.add(ColorHelper.valueOf("#7FFF00"));
    m_DefaultColors.add(ColorHelper.valueOf("#D2691E"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF7F50"));
    m_DefaultColors.add(ColorHelper.valueOf("#6495ED"));
    m_DefaultColors.add(ColorHelper.valueOf("#DC143C"));
    m_DefaultColors.add(ColorHelper.valueOf("#00FFFF"));
    m_DefaultColors.add(ColorHelper.valueOf("#00008B"));
    m_DefaultColors.add(ColorHelper.valueOf("#008B8B"));
    m_DefaultColors.add(ColorHelper.valueOf("#B8860B"));
    m_DefaultColors.add(ColorHelper.valueOf("#A9A9A9"));
    m_DefaultColors.add(ColorHelper.valueOf("#006400"));
    m_DefaultColors.add(ColorHelper.valueOf("#BDB76B"));
    m_DefaultColors.add(ColorHelper.valueOf("#8B008B"));
    m_DefaultColors.add(ColorHelper.valueOf("#556B2F"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF8C00"));
    m_DefaultColors.add(ColorHelper.valueOf("#9932CC"));
    m_DefaultColors.add(ColorHelper.valueOf("#8B0000"));
    m_DefaultColors.add(ColorHelper.valueOf("#E9967A"));
    m_DefaultColors.add(ColorHelper.valueOf("#8FBC8F"));
    m_DefaultColors.add(ColorHelper.valueOf("#483D8B"));
    m_DefaultColors.add(ColorHelper.valueOf("#2F4F4F"));
    m_DefaultColors.add(ColorHelper.valueOf("#00CED1"));
    m_DefaultColors.add(ColorHelper.valueOf("#9400D3"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF1493"));
    m_DefaultColors.add(ColorHelper.valueOf("#00BFFF"));
    m_DefaultColors.add(ColorHelper.valueOf("#696969"));
    m_DefaultColors.add(ColorHelper.valueOf("#1E90FF"));
    m_DefaultColors.add(ColorHelper.valueOf("#B22222"));
    m_DefaultColors.add(ColorHelper.valueOf("#228B22"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF00FF"));
    m_DefaultColors.add(ColorHelper.valueOf("#FFD700"));
    m_DefaultColors.add(ColorHelper.valueOf("#DAA520"));
    m_DefaultColors.add(ColorHelper.valueOf("#808080"));
    m_DefaultColors.add(ColorHelper.valueOf("#00FF00"));
    m_DefaultColors.add(ColorHelper.valueOf("#008000"));
    m_DefaultColors.add(ColorHelper.valueOf("#ADFF2F"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF69B4"));
    m_DefaultColors.add(ColorHelper.valueOf("#CD5C5C"));
    m_DefaultColors.add(ColorHelper.valueOf("#4B0082"));
    m_DefaultColors.add(ColorHelper.valueOf("#7CFC00"));
    m_DefaultColors.add(ColorHelper.valueOf("#ADD8E6"));
    m_DefaultColors.add(ColorHelper.valueOf("#F08080"));
    m_DefaultColors.add(ColorHelper.valueOf("#90EE90"));
    m_DefaultColors.add(ColorHelper.valueOf("#FFB6C1"));
    m_DefaultColors.add(ColorHelper.valueOf("#FFA07A"));
    m_DefaultColors.add(ColorHelper.valueOf("#20B2AA"));
    m_DefaultColors.add(ColorHelper.valueOf("#87CEFA"));
    m_DefaultColors.add(ColorHelper.valueOf("#778899"));
    m_DefaultColors.add(ColorHelper.valueOf("#B0C4DE"));
    m_DefaultColors.add(ColorHelper.valueOf("#32CD32"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF00FF"));
    m_DefaultColors.add(ColorHelper.valueOf("#B03060"));
    m_DefaultColors.add(ColorHelper.valueOf("#7F0000"));
    m_DefaultColors.add(ColorHelper.valueOf("#66CDAA"));
    m_DefaultColors.add(ColorHelper.valueOf("#0000CD"));
    m_DefaultColors.add(ColorHelper.valueOf("#BA55D3"));
    m_DefaultColors.add(ColorHelper.valueOf("#9370DB"));
    m_DefaultColors.add(ColorHelper.valueOf("#3CB371"));
    m_DefaultColors.add(ColorHelper.valueOf("#7B68EE"));
    m_DefaultColors.add(ColorHelper.valueOf("#00FA9A"));
    m_DefaultColors.add(ColorHelper.valueOf("#48D1CC"));
    m_DefaultColors.add(ColorHelper.valueOf("#C71585"));
    m_DefaultColors.add(ColorHelper.valueOf("#191970"));
    m_DefaultColors.add(ColorHelper.valueOf("#000080"));
    m_DefaultColors.add(ColorHelper.valueOf("#808000"));
    m_DefaultColors.add(ColorHelper.valueOf("#6B8E23"));
    m_DefaultColors.add(ColorHelper.valueOf("#FFA500"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF4500"));
    m_DefaultColors.add(ColorHelper.valueOf("#DA70D6"));
    m_DefaultColors.add(ColorHelper.valueOf("#DB7093"));
    m_DefaultColors.add(ColorHelper.valueOf("#CD853F"));
    m_DefaultColors.add(ColorHelper.valueOf("#DDA0DD"));
    m_DefaultColors.add(ColorHelper.valueOf("#A020F0"));
    m_DefaultColors.add(ColorHelper.valueOf("#7F007F"));
    m_DefaultColors.add(ColorHelper.valueOf("#663399"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF0000"));
    m_DefaultColors.add(ColorHelper.valueOf("#BC8F8F"));
    m_DefaultColors.add(ColorHelper.valueOf("#4169E1"));
    m_DefaultColors.add(ColorHelper.valueOf("#8B4513"));
    m_DefaultColors.add(ColorHelper.valueOf("#FA8072"));
    m_DefaultColors.add(ColorHelper.valueOf("#F4A460"));
    m_DefaultColors.add(ColorHelper.valueOf("#2E8B57"));
    m_DefaultColors.add(ColorHelper.valueOf("#A0522D"));
    m_DefaultColors.add(ColorHelper.valueOf("#87CEEB"));
    m_DefaultColors.add(ColorHelper.valueOf("#6A5ACD"));
    m_DefaultColors.add(ColorHelper.valueOf("#708090"));
    m_DefaultColors.add(ColorHelper.valueOf("#00FF7F"));
    m_DefaultColors.add(ColorHelper.valueOf("#4682B4"));
    m_DefaultColors.add(ColorHelper.valueOf("#008080"));
    m_DefaultColors.add(ColorHelper.valueOf("#FF6347"));
    m_DefaultColors.add(ColorHelper.valueOf("#40E0D0"));
    m_DefaultColors.add(ColorHelper.valueOf("#EE82EE"));
    m_DefaultColors.add(ColorHelper.valueOf("#9ACD32"));
  }
}
