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
 * SupportedTags.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.core.option.help;

import adams.core.Utils;
import adams.core.net.HtmlUtils;
import adams.core.tags.TagDataType;
import adams.core.tags.TagHandler;
import adams.core.tags.TagInfo;
import adams.core.tags.TagProcessor;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Generates help for classes that implement the adams.data.processing.TagProcessor interface.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title to use in the help output.
 * &nbsp;&nbsp;&nbsp;default: Supported tags
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see TagHandler
 */
public class SupportedTags
  extends AbstractHelpGenerator {

  private static final long serialVersionUID = -3885494293535045819L;

  /** the title to use. */
  protected String m_Title;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates help for classes that implement the "
	+ Utils.classToString(TagProcessor.class) + " interface.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "title", "title",
      "Supported tags");
  }

  /**
   * Sets the title to use for the help.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title to use for the help.
   *
   * @return		the title
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
    return "The title to use in the help output.";
  }

  /**
   * Checks whether the generator handles this class.
   *
   * @param cls		the class to check
   * @return		true if it can handle the class
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.matches(TagProcessor.class, cls);
  }

  /**
   * Generates the help for the object in the requested format.
   *
   * @param obj		the object to generate the help for
   * @param format	the format of the output
   * @return		the generated help
   */
  @Override
  public String generate(Object obj, HelpFormat format) {
    StringBuilder	result;
    Set<TagInfo> 	tags;
    List<TagInfo>	sorted;

    result = new StringBuilder();

    tags = ((TagProcessor) obj).getSupportedTags();
    if (tags.size() > 0) {
      sorted = new ArrayList<>(tags);
      Collections.sort(sorted);
      switch (format) {
	case PLAIN_TEXT:
	  result.append(m_Title).append("\n");
	  for (TagInfo tag: sorted) {
	    result.append("- ").append(tag.getName()).append("\n");
	    result.append("  ").append(tag.getInformation()).append("\n");
	    result.append("  type: ").append(tag.getDataType());
	    if (tag.getDataType() == TagDataType.BOOLEAN)
	      result.append(" (true|false)");
	    result.append("\n");
	    if (tag.getAppliesTo().length > 0) {
	      result.append("  can be attached to:\n");
	      for (Class cls: tag.getAppliesTo())
	        result.append("  - ").append(Utils.classToString(cls)).append("\n");
	    }
	  }
	  result.append("\n");
	  break;

	case HTML:
	  result.append("<h2>").append(m_Title).append("</h2>\n");
	  result.append("<ul>\n");
	  for (TagInfo tag: sorted) {
	    result.append("<li>");
	    result.append("<b>").append(HtmlUtils.toHTML(tag.getName())).append("</b>");
	    result.append("<br>");
	    result.append("\n");
	    result.append(HtmlUtils.toHTML(tag.getInformation()));
	    result.append("<br>");
	    result.append("\n");
	    result.append("<i>type:</i> ").append(tag.getDataType());
	    if (tag.getDataType() == TagDataType.BOOLEAN)
	      result.append(" (true|false)");
	    if (tag.getAppliesTo().length > 0) {
	      result.append("<br>");
	      result.append("\n");
	      result.append("<i>can be attached to:</i>\n");
	      result.append("<ul>");
	      for (Class cls: tag.getAppliesTo())
	        result.append("  <li>").append(Utils.classToString(cls)).append("</li>\n");
	      result.append("</ul>");
	    }
	    result.append("</li>\n");
	  }
	  result.append("</ul>\n");
	  result.append("\n");
	  break;

	default:
	  throw new IllegalStateException("Unhandled format: " + format);
      }
    }

    return result.toString();
  }
}
