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
 * Javadoc.java
 * Copyright (C) 2006-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.doc.javadoc;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.logging.Level;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.core.net.HtmlUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * Abstract superclass for classes that generate Javadoc comments and replace
 * the content between certain comment tags.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.core.Javadoc
 */
public abstract class Javadoc
  extends LoggingObject
  implements OptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4088919470813459046L;

  /** the start tag. */
  protected String[] m_StartTag;

  /** the end tag. */
  protected String[] m_EndTag;

  /** whether to use identation. */
  protected boolean[] m_IsBlock;

  /** the classname. */
  protected String m_Classname;

  /** whether to include the stars in the Javadoc. */
  protected boolean m_UseStars;

  /** the directory above the class to update. */
  protected String m_Dir;

  /** whether to suppress error messages (no printout in the console). */
  protected boolean m_Silent;

  /** the environment class (dummy option, happens all in runJavadoc method). */
  protected String m_Environment;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /**
   * Initializes the object, sets default options.
   */
  public Javadoc() {
    super();
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_StartTag  = null;
    m_EndTag    = null;
    m_IsBlock   = null;
    m_Classname = Javadoc.class.getName();
    m_UseStars  = true;
    m_Dir       = "";
    m_Silent    = false;
  }

  /**
   * Returns a new instance of the option manager.
   *
   * @return		the manager to use
   */
  protected OptionManager newOptionManager() {
    return new OptionManager(this);
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    m_OptionManager = newOptionManager();

    // dummy option, is queried manually in runJavadoc method
    m_OptionManager.add(
	"env", "environment",
	Environment.class.getName());

    m_OptionManager.add(
	"W", "classname",
	AllJavadoc.class.getName());

    m_OptionManager.add(
	"nostars", "useStars",
	true);

    m_OptionManager.add(
	"dir", "dir",
        ".");

    m_OptionManager.add(
	"silent", "silent",
	false);
  }

  /**
   * Returns the option manager.
   *
   * @return		the manager
   */
  public OptionManager getOptionManager() {
    if (m_OptionManager == null)
      defineOptions();

    return m_OptionManager;
  }

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions() {
    if (m_OptionManager != null) {
      m_OptionManager.cleanUp();
      m_OptionManager = null;
    }
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  public void destroy() {
    cleanUpOptions();
  }

  /**
   * sets the classname of the environment class to use.
   *
   * @param value	the environment class name
   */
  public void setEnvironment(String value) {
    m_Environment = value;
  }

  /**
   * returns the current classname of the environment class to use.
   *
   * @return	the current classname of the environment class
   */
  public String getEnvironment() {
    return m_Environment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String environmentTipText() {
    return "The class to use for determining the environment.";
  }

  /**
   * sets the classname of the class to generate the Javadoc for.
   *
   * @param value	the new classname
   */
  public void setClassname(String value) {
    m_Classname = value;
  }

  /**
   * returns the current classname.
   *
   * @return	the current classname
   */
  public String getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The class to load.";
  }

  /**
   * sets whether to prefix the Javadoc with "*".
   *
   * @param value	true if stars are used
   */
  public void setUseStars(boolean value) {
    m_UseStars = value;
  }

  /**
   * whether the Javadoc is prefixed with "*".
   *
   * @return 		whether stars are used
   */
  public boolean getUseStars() {
    return m_UseStars;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useStarsTipText() {
    return "Controls the use of '*' in the Javadoc.";
  }

  /**
   * sets the dir containing the file that is to be updated. It is the dir
   * above the package hierarchy of the class.
   *
   * @param value	the directory containing the classes
   */
  public void setDir(String value) {
    m_Dir = value;
  }

  /**
   * returns the current dir containing the class to update. It is the dir
   * above the package name of the class.
   *
   * @return		the  current directory
   */
  public String getDir() {
    return m_Dir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dirTipText() {
    return "The directory above the package hierarchy of the class.";
  }

  /**
   * sets whether to suppress output in the console.
   *
   * @param value	true if output is to be suppressed
   */
  public void setSilent(boolean value) {
    m_Silent = value;
  }

  /**
   * whether output in the console is suppressed.
   *
   * @return 		true if output is suppressed
   */
  public boolean getSilent() {
    return m_Silent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String silentTipText() {
    return "Suppresses printing in the console.";
  }

  /**
   * prints the given object to System.err.
   *
   * @param o		the object to print
   */
  protected void println(Object o) {
    if (!getSilent())
      getLogger().severe(o.toString());
  }

  /**
   * returns true if the class can be instantiated, i.e., has a default
   * constructor.
   *
   * @return true if the class can be instantiated
   */
  protected boolean canInstantiateClass() {
    boolean	result;
    Class	cls;

    result = true;
    cls    = null;

    try {
      cls = Class.forName(getClassname());
    }
    catch (Exception e) {
      result = false;
      println("Cannot instantiate '" + getClassname() + "'! Class in CLASSPATH?");
    }

    if (result) {
      try {
	cls.newInstance();
      }
      catch (Exception e) {
	result = false;
	println("Cannot instantiate '" + getClassname() + "'! Missing default constructor?");
      }
    }

    return result;
  }

  /**
   * Returns a new instance of the class.
   *
   * @return a new instance of the class
   */
  protected Object getInstance() {
    Object	result;
    Class	cls;

    result = null;

    try {
      cls    = Class.forName(getClassname());
      result = cls.newInstance();
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * converts the given String into HTML, i.e., replacing some char entities
   * with HTML entities.
   *
   * @param s		the string to convert
   * @return		the HTML conform string
   */
  protected String toHTML(String s) {
    String	result;

    result = HtmlUtils.toHTML(s);
    result = result.replaceAll("\n", "<br>\n");
    result = result.replaceAll("\t", "&nbsp;&nbsp;&nbsp;");
    result = result.replace("\\u", "\\\\u");

    return result;
  }

  /**
   * indents the given string by a given number of indention strings.
   *
   * @param content	the string to indent
   * @param count	the number of times to indent one line
   * @param indentStr	the indention string
   * @return		the indented content
   */
  protected String indent(String content, int count, String indentStr) {
    String		result;
    StringTokenizer	tok;
    int			i;

    tok = new StringTokenizer(content, "\n", true);
    result = "";
    while (tok.hasMoreTokens()) {
      if (result.endsWith("\n") || (result.length() == 0)) {
	for (i = 0; i < count; i++)
	  result += indentStr;
      }
      result += tok.nextToken();
    }

    return result;
  }

  /**
   * generates and returns the Javadoc for the specified start/end tag pair.
   *
   * @param index	the index in the start/end tag array
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  protected abstract String generateJavadoc(int index) throws Exception;

  /**
   * generates and returns the Javadoc.
   *
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  protected String generateJavadoc() throws Exception {
    String	result;
    int		i;

    result = "";

    for (i = 0; i < m_StartTag.length; i++) {
      if (i > 0)
	result += "\n\n";
      result += generateJavadoc(i).trim();
    }

    return result;
  }

  /**
   * determines the base string of the given indention string, whether it's
   * either only spaces (one space will be retured) or mixed mode (tabs and
   * spaces, in that case the same string will be returned).
   *
   * @param str		the string to analyze
   * @return 		the indention string
   */
  protected String getIndentionString(String str) {
    String	result;

    // only spaces?
    if (str.replaceAll(" ", "").length() == 0)
      result = " ";
    // only tabs?
    else if (str.replaceAll("\t", "").length() == 0)
      result = "\t";
    else
      result = str;

    return result;
  }

  /**
   * determines the number of indention strings that have to be inserted to
   * generated the given indention string.
   *
   * @param str 	the string to analyze
   * @return		the number of base indention strings to insert
   */
  protected int getIndentionLength(String str) {
    int		result;

    // only spaces?
    if (str.replaceAll(" ", "").length() == 0)
      result = str.length();
    // only tabs?
    else if (str.replaceAll("\t", "").length() == 0)
      result = str.length();
    else
      result = 1;

    return result;
  }

  /**
   * generates and returns the Javadoc for the specified start/end tag pair.
   *
   * @param content	the current source code
   * @param index	the index in the start/end tag array
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  protected String updateJavadoc(String content, int index) throws Exception {
    StringBuilder	resultBuf;
    int			indentionLen;
    String		indentionStr;
    String		part;
    String		tmpStr;

    // start and end tag?
    if (    (content.indexOf(m_StartTag[index]) == -1)
	   || (content.indexOf(m_EndTag[index]) == -1) ) {
      println(
	  "No start and/or end tags found: "
	  + m_StartTag[index] + "/" + m_EndTag[index]);
      return content;
    }

    // replace tags
    resultBuf = new StringBuilder();
    while (content.length() > 0) {
      if (content.indexOf(m_StartTag[index]) > -1) {
	part = content.substring(0, content.indexOf(m_StartTag[index]));
	// is it a Java constant? -> skip
	if (part.endsWith("\"")) {
	  resultBuf.append(part);
	  resultBuf.append(m_StartTag[index]);
	  content = content.substring(part.length() + m_StartTag[index].length());
	}
	else {
	  tmpStr       = part.substring(part.lastIndexOf("\n") + 1);
	  part         = part.substring(0, part.lastIndexOf("\n") + 1);
	  indentionLen = getIndentionLength(tmpStr);
	  indentionStr = getIndentionString(tmpStr);

	  resultBuf.append(part);
	  if (m_IsBlock[index]) {
	    resultBuf.append(indent(m_StartTag[index], indentionLen, indentionStr));
	    resultBuf.append("\n");
	    resultBuf.append(indent(generateJavadoc(index), indentionLen, indentionStr));
	    resultBuf.append(indent(m_EndTag[index], indentionLen, indentionStr));
	  }
	  else {
	    resultBuf.append(indent(m_StartTag[index], indentionLen, indentionStr));
	    resultBuf.append(indent(generateJavadoc(index), 0, ""));
	    resultBuf.append(indent(m_EndTag[index], 0, ""));
	  }
	  content = content.substring(content.indexOf(m_EndTag[index]));
	  content = content.substring(m_EndTag[index].length());
	}
      }
      else {
	resultBuf.append(content);
	content = "";
      }
    }

    return resultBuf.toString().trim();
  }

  /**
   * updates the Javadoc in the given source code.
   *
   * @param content	the source code
   * @return		the updated source code
   * @throws Exception 	in case the generation fails
   */
  protected String updateJavadoc(String content) throws Exception {
    String	result;
    int		i;

    result = content;

    for (i = 0; i < m_StartTag.length; i++) {
      result = updateJavadoc(result, i);
    }

    return result;
  }

  /**
   * generates the Javadoc and returns it applied to the source file if one
   * was provided, otherwise an empty string.
   *
   * @return		the generated Javadoc
   * @throws Exception 	in case the generation fails
   */
  public String updateJavadoc() throws Exception {
    StringBuilder	contentBuf;
    BufferedReader	reader;
    String		line;
    String		result;
    File		file;

    result = "";

    // non-existing?
    file = new File(getDir() + "/" + getClassname().replaceAll("\\.", "/") + ".java");
    if (!file.exists()) {
      println("File '" + file.getAbsolutePath() + "' doesn't exist!");
      return result;
    }

    try {
      // load file
      reader     = new BufferedReader(new FileReader(file));
      contentBuf = new StringBuilder();
      while ((line = reader.readLine()) != null) {
	contentBuf.append(line + "\n");
      }
      reader.close();
      result = updateJavadoc(contentBuf.toString());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to update javadoc", e);
    }

    return result.trim();
  }

  /**
   * generates either the plain Javadoc (if no filename specified) or the
   * updated file (if a filename is specified). The start and end tag for
   * the global info have to be specified in the file in the latter case.
   *
   * @return 		either the plain Javadoc or the modified file
   * @throws Exception 	in case the generation fails
   */
  public String generate() throws Exception {
    if (getDir().length() == 0)
      return generateJavadoc();
    else
      return updateJavadoc();
  }

  /**
   * Returns the commandline of this object.
   *
   * @return		the commandline
   */
  @Override
  public String toString() {
    return toCommandLine();
  }

  /**
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  @Override
  public String toCommandLine() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Instantiates the javadoc generator with the given options.
   *
   * @param classname	the classname of the javadoc generator to instantiate
   * @param options	the options for the javadoc generator
   * @return		the instantiated javadoc generator or null if an error occurred
   */
  public static Javadoc forName(String classname, String[] options) {
    Javadoc	result;

    try {
      result = (Javadoc) OptionUtils.forName(Javadoc.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the javadoc generator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			javadoc generator to instantiate
   * @return		the instantiated javadoc generator
   * 			or null if an error occurred
   */
  public static Javadoc forCommandLine(String cmdline) {
    return (Javadoc) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * runs the javadoc producer with the given commandline options.
   *
   * @param javadoc	the javadoc producer to execute
   * @param options	the commandline options
   */
  public static void runJavadoc(Class javadoc, String[] options) {
    Javadoc	javadocInst;
    String	env;

    // we have to set the environment before anything else happens
    env = OptionUtils.getOption(options, "-env");
    if ((env == null) || (env.length() == 0))
      env = Environment.class.getName();
    try {
      Environment.setEnvironmentClass(Class.forName(env));
    }
    catch (Exception e) {
      e.printStackTrace();
      Environment.setEnvironmentClass(Environment.class);
    }

    try {
      try {
	if (OptionUtils.helpRequested(options)) {
	  System.out.println("Help requested...\n");
	  javadocInst = forName(javadoc.getName(), new String[0]);
	  System.out.println("\n" + OptionUtils.list(javadocInst));
	  return;
	}
	else {
	  javadocInst = forName(javadoc.getName(), options);
	  // directory is necessary!
	  if (javadocInst.getDir().length() == 0)
	    throw new Exception("No directory provided!");
	}
      }
      catch (Exception ex) {
        String result = "\n" + ex.getMessage() + "\n\n" + OptionUtils.list(forName(javadoc.getName(), new String[0]));
        throw new Exception(result);
      }

      System.out.println(javadocInst.generate());
      // System.exit is necessary for some GUI related classes, due to
      // invisible frames
      System.exit(0);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   * Returns a list with classnames of Javadoc generators.
   *
   * @return		the classnames
   */
  public static String[] getJavadocs() {
    return ClassLister.getSingleton().getClassnames(Javadoc.class);
  }
}
