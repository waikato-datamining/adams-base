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
 * FlowJavadoc.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import adams.core.ClassLocator;
import adams.core.Utils;
import adams.flow.container.AbstractContainer;
import adams.flow.core.ActorWithConditionalEquivalent;

/**
 * Generates Javadoc comments for the AbstractActor. Can
 * automatically update the comments if they're surrounded by
 * the FLOW_STARTTAG and FLOW_ENDTAG (the indention is determined via
 * the FLOW_STARTTAG).
 * <p/>
 * In addition to the flow tags, one can also place the tags
 * ACCEPTS_STARTTAG/ACCEPTS_ENDTAG and GENERATES_STARTTAG/GENERATES_ENDTAG
 * in the Javadoc. These tags don't add blocks of comments, but just a single
 * classname.
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 *         The class to use for determining the environment.
 *         default: adams.core.Environment
 * </pre>
 *
 * <pre>-W &lt;java.lang.String&gt; (property: classname)
 *         The class to load.
 *         default: adams.doc.AllJavadoc
 * </pre>
 *
 * <pre>-nostars (property: useStars)
 *         Controls the use of '*' in the Javadoc.
 * </pre>
 *
 * <pre>-dir &lt;java.lang.String&gt; (property: dir)
 *         The directory above the package hierarchy of the class.
 *         default: .
 * </pre>
 *
 * <pre>-silent (property: silent)
 *         Suppresses printing in the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #FLOW_STARTTAG
 * @see #FLOW_ENDTAG
 * @see #ACCEPTS_METHOD
 * @see #ACCEPTS_STARTTAG
 * @see #ACCEPTS_ENDTAG
 * @see #GENERATES_METHOD
 * @see #GENERATES_STARTTAG
 * @see #GENERATES_ENDTAG
 */
public class FlowJavadoc
  extends Javadoc {

  /** for serialization. */
  private static final long serialVersionUID = 3485326632649980955L;

  /** the start comment tag for inserting the generated Javadoc (all). */
  public final static String FLOW_STARTTAG = "<!-- flow-summary-start -->";

  /** the end comment tag for inserting the generated Javadoc (all). */
  public final static String FLOW_ENDTAG = "<!-- flow-summary-end -->";

  /** the accepts() method. */
  public final static String ACCEPTS_METHOD = "accepts";

  /** the start comment tag for inserting the generated Javadoc (just "accepts"). */
  public final static String ACCEPTS_STARTTAG = "<!-- flow-accepts-start -->";

  /** the end comment tag for inserting the generated Javadoc (just "accepts"). */
  public final static String ACCEPTS_ENDTAG = "<!-- flow-accepts-end -->";

  /** the generates() method. */
  public final static String GENERATES_METHOD = "generates";

  /** the start comment tag for inserting the generated Javadoc (just "generates"). */
  public final static String GENERATES_STARTTAG = "<!-- flow-generates-start -->";

  /** the end comment tag for inserting the generated Javadoc (just "generates"). */
  public final static String GENERATES_ENDTAG = "<!-- flow-generates-end -->";

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_StartTag    = new String[3];
    m_EndTag      = new String[3];
    m_IsBlock     = new boolean[3];
    m_StartTag[0] = FLOW_STARTTAG;
    m_EndTag[0]   = FLOW_ENDTAG;
    m_IsBlock[0]  = true;
    m_StartTag[1] = ACCEPTS_STARTTAG;
    m_EndTag[1]   = ACCEPTS_ENDTAG;
    m_IsBlock[1]  = false;
    m_StartTag[2] = GENERATES_STARTTAG;
    m_EndTag[2]   = GENERATES_ENDTAG;
    m_IsBlock[2]  = false;
  }
  
  /**
   * generates and returns the Javadoc for the specified start/end tag pair.
   *
   * @param index	the index in the start/end tag array
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  @Override
  protected String generateJavadoc(int index) throws Exception {
    String		result;
    Method		accepts;
    Method		generates;
    Class[]		classes;
    int			i;
    List<Class>		containers;
    AbstractContainer	cont;
    boolean		first;
    Iterator<String>	enm;
    StringBuilder	info;
    Class		condEquiv;

    result = "";

    if (!canInstantiateClass())
	return result;

    if (!(getInstance() instanceof adams.flow.core.Actor))
	return result;

    // try to get methods
    try {
      accepts = getInstance().getClass().getMethod(ACCEPTS_METHOD, (Class[]) null);
    }
    catch (Exception e) {
      accepts = null;
    }
    try {
      generates = getInstance().getClass().getMethod(GENERATES_METHOD, (Class[]) null);
    }
    catch (Exception e) {
      generates = null;
    }

    // all
    containers = new ArrayList<Class>();
    if ((index == 0) && ((accepts != null) || (generates != null))) {
      // retrieve classes
      if (accepts != null) {
	classes = (Class[]) accepts.invoke(getInstance(), (Object[]) null);
	result += "- accepts:\n";
	for (i = 0; i < classes.length; i++) {
	  result += "\t" + Utils.classToString(classes[i]) + "\n";
	  if (ClassLocator.isSubclass(AbstractContainer.class, classes[i]))
	    containers.add(classes[i]);
	}
      }
      if (generates != null) {
	classes = (Class[]) generates.invoke(getInstance(), (Object[]) null);
	result += "- generates:\n";
	for (i = 0; i < classes.length; i++) {
	  result += "\t" + Utils.classToString(classes[i]) + "\n";
	  if (ClassLocator.isSubclass(AbstractContainer.class, classes[i]))
	    containers.add(classes[i]);
	}
      }
      if (result.length() > 0) {
	result = toHTML("Input/output:\n" + result).trim().replaceAll("(\t)(.+)(<br\\/>)", "<pre>   $2</pre>");
	result += "\n<p/>\n";
      }

      // container information
      if (containers.size() > 0) {
	info = new StringBuilder();
	info.append("Container information:");
	for (i = 0; i < containers.size(); i++) {
	  info.append("\n- " + containers.get(i).getName() + ": ");
	  try {
	    cont  = (AbstractContainer) containers.get(i).newInstance();
	    first = true;
	    enm   = cont.names();
	    while (enm.hasNext()) {
	      if (!first)
		info.append(", ");
	      info.append(enm.next());
	      first = false;
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	result += toHTML(info.toString());
	result += "\n<p/>\n";
      }
      
      if (getInstance() instanceof ActorWithConditionalEquivalent) {
	condEquiv = ((ActorWithConditionalEquivalent) getInstance()).getConditionalEquivalent();
	if (condEquiv != null) {
	  info = new StringBuilder();
	  info.append("Conditional equivalent:\n");
	  info.append("\t" + condEquiv.getName());
	  result += toHTML(info.toString());
	  result += "\n<p/>\n";
	}
      }

      // stars?
      if (getUseStars())
	result = indent(result, 1, "* ");
    }

    // accepts
    if ((index == 1) && (accepts != null)) {
      // retrieve class
      classes = (Class[]) accepts.invoke(getInstance(), (Object[]) null);
      result = "";
      for (i = 0; i < classes.length; i++) {
	if (i > 0)
	  result += ", ";
	result += Utils.classToString(classes[i]) + ".class";
      }
      result = toHTML(result);
      result = result.trim();
    }

    // generates
    if ((index == 2) && (generates != null)) {
      // retrieve class
      classes = (Class[]) generates.invoke(getInstance(), (Object[]) null);
      result = "";
      for (i = 0; i < classes.length; i++) {
	if (i > 0)
	  result += ", ";
	result += Utils.classToString(classes[i]) + ".class";
      }
      result = toHTML(result);
      result = result.trim();
    }

    return result;
  }

  /**
   * Parses the given commandline parameters and generates the Javadoc.
   *
   * @param args	the commandline parameters for the object
   */
  public static void main(String[] args) {
    runJavadoc(FlowJavadoc.class, args);
  }
}
