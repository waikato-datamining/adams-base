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
 * AbstractHelpGenerator.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.help;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.net.HtmlUtils;
import adams.core.option.AbstractOptionHandler;

import java.util.logging.Level;

/**
 * Ancestor for help generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractHelpGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8839514944196436439L;

  /** for logging in static context. */
  protected static Logger LOGGER = LoggingHelper.getLogger(AbstractHelpGenerator.class);

  /** whether to generate "fake" class cross reference links. */
  protected boolean m_ClassCrossRefLinks;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-crossref-links", "classCrossRefLinks",
      false);
  }

  /**
   * Sets whether to generate class cross-reference links for the help user interface.
   *
   * @param value	true if to generate
   */
  public void setClassCrossRefLinks(boolean value) {
    m_ClassCrossRefLinks = value;
    reset();
  }

  /**
   * Returns whether to generate class cross-reference links for the help user interface.
   *
   * @return		true if to generate
   */
  public boolean getClassCrossRefLinks() {
    return m_ClassCrossRefLinks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classCrossRefLinksTipText() {
    return "If enabled, class cross-reference links get generated that get interpreted by the ADAMS help interface.";
  }

  /**
   * Checks whether the generator handles this object.
   *
   * @param obj		the object to check
   * @return		true if it can handle the object
   */
  public boolean handles(Object obj) {
    return (obj != null) && handles(obj.getClass());
  }

  /**
   * Checks whether the generator handles this class.
   *
   * @param cls		the class to check
   * @return		true if it can handle the class
   */
  public abstract boolean handles(Class cls);

  /**
   * Turns the string into HTML. Line feeds are automatically converted
   * into &lt;br&gt;.
   *
   * @param s		the string to convert to HTML
   * @return		the HTML string
   * @see		HtmlUtils#markUpURLs(String, boolean)
   */
  protected String toHTML(String s) {
    return toHTML(s, false);
  }

  /**
   * Turns the string into HTML. Line feeds are automatically converted
   * into &lt;br&gt;.
   *
   * @param s		the string to convert to HTML
   * @param nbsp	whether to convert leading blanks to non-breaking spaces
   * @return		the HTML string
   * @see		HtmlUtils#markUpURLs(String, boolean)
   */
  protected String toHTML(String s, boolean nbsp) {
    String	result;

    if (s == null)
      return null;

    result = HtmlUtils.markUpURLs(s, true);
    result = HtmlUtils.convertLines(result, nbsp);
    result = HtmlUtils.hyperlinkClassnames(result, m_ClassCrossRefLinks);

    return result;
  }

  /**
   * Generates the help for the object in the requested format.
   *
   * @param obj		the object to generate the help for
   * @param format	the format of the output
   * @return		the generated help
   */
  public abstract String generate(Object obj, HelpFormat format);

  /**
   * Generates the combined help using all generators.
   *
   * @param obj		the object to generate the help for
   * @param format	the format of the output
   * @return		the generated help
   */
  public static String generateAll(Object obj, HelpFormat format) {
    StringBuilder		result;
    Class[]			classes;
    AbstractHelpGenerator	generator;

    result  = new StringBuilder();
    classes = ClassLister.getSingleton().getClasses(AbstractHelpGenerator.class);
    for (Class cls: classes) {
      try {
        generator = (AbstractHelpGenerator) cls.getDeclaredConstructor().newInstance();
        if (generator.handles(obj))
          result.append(generator.generate(obj, format));
      }
      catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to generate help using: " + Utils.classToString(cls), e);
      }
    }

    return result.toString();
  }
}
