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
 * CompactFlowProducer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.option;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.management.Java;
import adams.core.option.NestedFormatHelper.Line;
import adams.env.Environment;
import adams.env.Modules;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generates nested, compact actor flow format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompactFlowProducer
  extends AbstractRecursiveOptionProducer<List,List>
  implements EncodingSupporter {

  private static final long serialVersionUID = -5896308097097066657L;

  /** the line comment character in files storing nested option handlers. */
  public final static String COMMENT = "#";

  /** the information about the project. */
  public static final String PROJECT = "Project";

  /** the information on the date. */
  public static final String DATE = "Date";

  /** the information on the user. */
  public static final String USER = "User";

  /** the information on the character set used. */
  public static final String CHARSET = "Charset";

  /** the information on the modules used. */
  public static final String MODULES = "Modules";

  /** the information on the class path. */
  public static final String CLASS_PATH = "Class-Path";

  /** whether to suppress the prolog. */
  protected boolean m_OutputProlog;

  /** whether to print the classpath. */
  protected boolean m_OutputClasspath;

  /** whether to print line numbers. */
  protected boolean m_OutputLineNumbers;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** for formatting dates. */
  protected static DateFormat m_DateFormat;
  static {
    m_DateFormat = new DateFormat("yyyy-MM-dd HH:mm:ss");
  }

  /** for stripping down an actor. */
  protected NestedProducer m_StripProducer;

  /** for stripping down an actor. */
  protected NestedConsumer m_StripConsumer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the compact flow format (one actor per line, single blank for each depth level, comment block at start using '" + COMMENT + "').";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_OutputProlog      = true;
    m_OutputClasspath   = false;
    m_OutputLineNumbers = false;
    m_Encoding          = new BaseCharset();
    m_StripConsumer     = new NestedConsumer();
    m_StripProducer     = new NestedProducer();
    m_StripProducer.setBlacklisted(new Class[]{AbstractActor[].class, AbstractActor.class, Actor[].class, Actor.class});
  }

  /**
   * Sets whether to output the prolog (in comments) or not.
   *
   * @param value	if true then the prolog is generated
   */
  public void setOutputProlog(boolean value) {
    m_OutputProlog = value;
  }

  /**
   * Returns whether the prolog (comments) is generated.
   *
   * @return		true if the prolog is generated
   */
  public boolean getOutputProlog() {
    return m_OutputProlog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPrologTipText() {
    return "Whether to output the prolog with comments about software version, date/time of generation, etc.";
  }

  /**
   * Sets whether to output the classpath (in comments) or not.
   *
   * @param value	if true then the classpath is generated
   */
  public void setOutputClasspath(boolean value) {
    m_OutputClasspath = value;
  }

  /**
   * Returns whether the classpath (comments) is generated.
   *
   * @return		true if the classpath is generated
   */
  public boolean getOutputClasspath() {
    return m_OutputClasspath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputClasspathTipText() {
    return "Whether to output the classpath in the comments as well.";
  }

  /**
   * Sets whether to output the line numbers.
   *
   * @param value	if true then the lines get prefixed with line numbers
   */
  public void setOutputLineNumbers(boolean value) {
    m_OutputLineNumbers = value;
  }

  /**
   * Returns whether line numbers get output.
   *
   * @return		true if lines get prefixed with line numbers
   */
  public boolean getOutputLineNumbers() {
    return m_OutputLineNumbers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputLineNumbersTipText() {
    return "Whether to prefix each line with the line number.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when writing the file, use empty string for default.";
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected List initOutput() {
    return new ArrayList();
  }

  /**
   * Not used.
   *
   * @param option	the option
   * @return		null
   */
  @Override
  public List processOption(BooleanOption option) {
    return null;
  }

  /**
   * Not used.
   *
   * @param option	the option
   * @return		null
   */
  @Override
  public List processOption(ClassOption option) {
    return null;
  }

  /**
   * Not used.
   *
   * @param option	the option
   * @return		null
   */
  @Override
  public List processOption(AbstractArgumentOption option) {
    return null;
  }

  /**
   * Returns a stripped down version of the actor, i.e., for ActorHandlers,
   * a copy of the actor without any sub-actors gets returned.
   *
   * @param actor	the actor to strip down
   * @return		the stripped down actor
   * @see		ActorHandler
   */
  public Actor strip(Actor actor) {
    Actor		result;

    // create actor with only default sub-actors
    if (actor instanceof ActorHandler) {
      m_StripProducer.produce(actor);
      m_StripConsumer.setInput(m_StripProducer.getOutput());
      result = (Actor) m_StripConsumer.consume();
      m_StripProducer.cleanUp();
      m_StripConsumer.cleanUp();
      result.setParent(actor.getParent());
    }
    // create a shallow copy of actor
    else {
      result = actor;
    }

    return result;
  }

  /**
   * Hook method for performing checks on the input. May throw exceptions
   * if object doesn't pass test(s).
   *
   * @param object	the objec to check
   * @return		the checked object
   */
  protected OptionHandler checkInput(OptionHandler object) {
    OptionHandler	result;

    result = super.checkInput(object);

    if (!(object instanceof Actor))
      throw new IllegalStateException("Object is not an actor!");

    return result;
  }

  /**
   * Processes the actor.
   *
   * @param actor	the actor to process
   */
  protected void process(Actor actor) {
    Actor		stripped;
    ActorHandler	handler;
    int			i;
    ArrayList		list;
    Line		line;

    stripped = strip(actor);
    line     = new Line(stripped.toCommandLine());
    if (m_Nesting.empty())
      m_Output.add(line);
    else
      m_Nesting.peek().add(line);

    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;
      list    = new ArrayList();
      if (m_Nesting.empty())
        m_Output.add(list);
      else
        m_Nesting.peek().add(list);
      m_Nesting.push(list);
      for (i = 0; i < handler.size(); i++)
        process(handler.get(i));
      m_Nesting.pop();
    }
  }

  /**
   * Visits the option and obtains information from it.
   *
   * @param visitedObject	the option handler to visit
   * @return			the generated output
   */
  public List produce(OptionHandler visitedObject) {
    reset();

    if (isLoggingEnabled())
      getLogger().info("preVisit: " + m_Input.getClass().getName());
    preProduce();

    if (isLoggingEnabled())
      getLogger().info("doVisit: " + m_Input.getClass().getName());
    process((Actor) visitedObject);

    if (isLoggingEnabled())
      getLogger().info("postVisit: " + m_Input.getClass().getName());
    postProduce();

    return getOutput();
  }

  /**
   * For generating a string from the nested structure.
   *
   * @param buffer	the buffer to add to
   * @param current	the current list to process
   * @param depth	the nesting depth (used for indentation)
   * @param lineNo	the line number (1-based)
   * @return		the updated line number
   */
  protected int toString(StringBuilder buffer, List current, int depth, int lineNo) {
    StringBuilder	indent;
    int			i;
    Object		item;

    indent = new StringBuilder();
    for (i = 0; i < depth; i++)
      indent.append(" ");

    for (i = 0; i < current.size(); i++) {
      item = current.get(i);
      if (item instanceof List) {
	lineNo = toString(buffer, (List) item, depth + 1, lineNo);
      }
      else {
	if (m_OutputLineNumbers)
	  buffer.append(lineNo + ":");
	buffer.append(indent).append(item.toString()).append("\n");
      }
      lineNo++;
    }

    return lineNo;
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();

    if (m_OutputProlog) {
      result.append(COMMENT + " " + PROJECT + ": " + Environment.getInstance().getProject() + "\n");
      result.append(COMMENT + " " + DATE + ": " + m_DateFormat.format(new Date()) + "\n");
      result.append(COMMENT + " " + USER + ": " + System.getProperty("user.name") + "\n");
      result.append(COMMENT + " " + CHARSET + ": " + m_Encoding.charsetValue().name() + "\n");
      result.append(COMMENT + " " + MODULES + ": " + Utils.flatten(Modules.getSingleton().getModules(), ",") + "\n");
      if (m_OutputClasspath)
	result.append(COMMENT + " " + CLASS_PATH + ": " + Java.getClassPath(true) + "\n");
      result.append(COMMENT + "\n");
    }

    toString(result, m_Output, 0, 1);

    // remove trailing newline
    if (result.charAt(result.length() - 1) == '\n')
      result.deleteCharAt(result.length() - 1);

    return result.toString();
  }

  /**
   * Executes the producer from commandline.
   *
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(CompactFlowProducer.class, args);
  }
}
