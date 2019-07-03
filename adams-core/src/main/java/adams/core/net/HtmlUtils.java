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
 * HtmlUtils.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import adams.core.ClassLister;
import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;

import java.util.Set;

/**
 * Utility functions regarding HTML.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HtmlUtils {

  /** the default replacement for URL markup. */
  public final static String DEFAULT_MARKUP = "<a href=\"$0\">$0</a>";

  /** the prefix to use in URLs. */
  public final static String CROSSREF_PREFIX = "classcrossref-";

  /** the URL prefix for the Java Javadoc. */
  public static final String JAVA_API_URL = "https://docs.oracle.com/javase/8/docs/api/";

  /**
   * Converts the given String into HTML, i.e., replacing some char entities
   * with HTML entities.
   *
   * @param s		the string to convert
   * @return		the HTML conform string, empty string if input was null
   */
  public static String toHTML(String s) {
    String	result;

    if (s == null)
      return "";
    
    result = s;

    result = result.replaceAll("&", "&amp;");
    result = result.replaceAll("<", "&lt;");
    result = result.replaceAll(">", "&gt;");
    result = result.replaceAll("@", "&#64;");
    result = result.replaceAll("/", "&#47;");

    return result;
  }

  /**
   * Line feeds are converted into &lt;br&gt;.
   *
   * @param s		the string to convert the line breaks into HTML
   * @param nbsp	whether to convert leading blanks to non-breaking spaces
   * @return		the converted string
   */
  public static String convertLines(String s, boolean nbsp) {
    String		result;
    String[]		lines;
    StringBuilder	newLine;
    int			i;
    int			n;
    boolean		first;

    result = s;
    result = result.replace("\n", "<br>\n");

    if (nbsp) {
      lines = result.split("\n");
      for (i = 0; i < lines.length; i++) {
	first   = true;
	newLine = new StringBuilder();
	for (n = 0; n < lines[i].length(); n++) {
	  if (first) {
	    if (lines[i].charAt(n) == ' ') {
	      newLine.append("&#160;");
	    }
	    else {
	      first = false;
	      newLine.append(lines[i].charAt(n));
	    }
	  }
	  else {
	    newLine.append(lines[i].charAt(n));
	  }
	}
	lines[i] = newLine.toString();
      }
      result = Utils.flatten(lines, "\n");
    }

    return result;
  }

  /**
   * Converts some HTML entities with the corresponding characters.
   *
   * @param s		the HTML string to convert
   * @return		the converted string
   */
  public static String fromHTML(String s) {
    String	result;

    result = s;

    result = result.replaceAll("&lt;", "<");
    result = result.replaceAll("&gt;", ">");
    result = result.replaceAll("&#64;", "@");
    result = result.replaceAll("&#47;", "/");
    result = result.replaceAll("&amp;", "&");

    return result;
  }
  
  /**
   * Replaces URLs in the string with the HTML URL tags.
   * 
   * @param raw		the raw text with URLs
   * @param toHtml	whether to transform &lt; and &gt; into their HTML counterparts
   * @return		the marked up text
   * @see		#DEFAULT_MARKUP
   */
  public static String markUpURLs(String raw, boolean toHtml) {
    return markUpURLs(raw, DEFAULT_MARKUP, toHtml);
  }
  
  /**
   * Replaces URLs in the string with the URL replaced by the replacement string.
   * Use "$0" to reference the URL in the replacement string. For example, for 
   * turning a URL in to a link, use the following replacement string:
   * <pre>
   * "<a href=\"$0\">$0</a>"
   * </pre>
   * 
   * @param raw		the raw text with URLs
   * @param replacement	the replacement string (use $0 as placeholder for the URL)
   * @param toHtml	whether to transform &lt; and &gt; into their HTML counterparts
   * @return		the marked up text
   * @see		#DEFAULT_MARKUP
   */
  @MixedCopyright(
      author = "Jesper",
      url = "http://stackoverflow.com/a/7658574",
      license = License.CC_BY_SA_3,
      note = "from comment in stackoverflow answer"
  )
  public static String markUpURLs(String raw, String replacement, boolean toHtml) {
    String	result;
    
    if (raw == null)
      return null;
    
    result = raw;
    
    if (toHtml) {
      result = result.replaceAll("<", "&lt;");
      result = result.replaceAll(">", "&gt;");
    }

    result = result.replaceAll("\\b((file|ftps?|https?)\\:\\/\\/[\\w\\d:#@%/;$()~_?!+-=.,&]+)", "<a href=\"$0\">$0</a>");
    
    return result;
  }

  /**
   * Generates a classname cross reference fake link used in the help.
   *
   * @param classname	the class to generate the link for
   * @return		the fake url
   */
  public static String toClassCrossRefURL(String classname) {
    return "http://" + CROSSREF_PREFIX + classname;
  }

  /**
   * Checks whether the URL is a fake class cross reference link.
   *
   * @param url		the url to check
   * @return		true if a class cross reference
   */
  public static boolean isClassCrossRefURL(String url) {
    return url.startsWith("http://" + CROSSREF_PREFIX);
  }

  /**
   * Returns the classname from the cross reference URL.
   *
   * @param url		the cross reference url
   * @return		the classname, null if failed to extract
   */
  public static String extractClassFromCrossRefURL(String url) {
    if (isClassCrossRefURL(url))
      return url.substring(("http://" + CROSSREF_PREFIX).length());
    else
      return null;
  }

  /**
   * Generates an API URL from the Java class.
   *
   * @param cls		the class to generate the URL for
   * @return		the URL
   */
  public static String toJavaApiURL(Class cls) {
    return toJavaApiURL(cls.getName());
  }

  /**
   * Generates an API URL from the Java classname.
   *
   * @param classname	the classname to generate the URL for
   * @return		the URL
   */
  public static String toJavaApiURL(String classname) {
    return JAVA_API_URL + classname.replace(".", "/") + ".html";
  }

  /**
   * Hyperlinks class names in the string.
   *
   * @param s		the string to convert to HTML
   * @param classCrossRefLinks	whether to generate class cross-reference links interpreted by the ADAMS help interface
   * @return		the HTML string
   */
  public static String hyperlinkClassnames(String s, boolean classCrossRefLinks) {
    String	result;
    Set<String> classnames;
    ClassLister clister;
    String	url;

    if (s == null)
      return null;

    result = s;
    classnames = Utils.extractClassnames(result, false);
    if (classnames.size() > 0) {
      clister = ClassLister.getSingleton();
      for (String classname: classnames) {
        if (!clister.isManaged(classname) && (classname.startsWith("java.") || classname.startsWith("javax.")))
          url = toJavaApiURL(classname);
        else if (classCrossRefLinks)
          url = toClassCrossRefURL(classname);
        else
          url = null;
        if (url != null)
          result = result.replace(classname, "<a href=\"" + url + "\">" + classname + "</a>");
      }
    }

    return result;
  }
}
