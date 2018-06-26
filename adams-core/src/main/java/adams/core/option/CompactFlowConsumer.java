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
 * CompactFlowConsumer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.option;

import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.option.NestedFormatHelper.Line;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.MutableActorHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Reads nested, compact actor flow format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompactFlowConsumer
  extends AbstractRecursiveOptionConsumer<List,List>
  implements EncodingSupporter {

  private static final long serialVersionUID = 990201642552033905L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the internal consumer in use for parsing commandlines. */
  protected ArrayConsumer m_Consumer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the compact flow format (one actor per line, single blank for each depth level, comment block at start using '" + CompactFlowProducer.COMMENT + "').";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Encoding = new BaseCharset();
    m_Consumer = new ArrayConsumer();
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
    return "The type of encoding to use when reading the file, use empty string for default.";
  }

  /**
   * Converts the input string into the internal format.
   *
   * @param s		the string to process
     * @return		the internal format, null in case of an error
   */
  @Override
  protected List convertToInput(String s) {
    ArrayList<String> lines;
    String		msg;
    int			offset;

    try {
      // split into separate lines
      lines   = new ArrayList<>(Arrays.asList(s.split("\n")));
      // check modules
      msg = NestedFormatHelper.checkModules(NestedFormatHelper.getModules(lines));
      if (msg != null)
        logWarning(msg);
      offset  = NestedFormatHelper.removeComments(lines);
      // convert into nested format
      return NestedFormatHelper.linesToNested(lines, offset, ' ');
    }
    catch (Exception e) {
      msg = "Failed to convert to input:";
      logError(msg + " " + e);
      getLogger().log(Level.SEVERE, msg, e);
      return null;
    }
  }

  /**
   * Creates the empty option handler from the internal data structure and
   * returns it. This option handler will then be "visited".
   *
   * @return		the generated option handler, null in case of an error
   */
  @Override
  protected OptionHandler initOutput() {
    OptionHandler	result;
    Line line;
    String		msg;

    try {
      line   = (Line) m_Input.get(0);
      result = OptionUtils.forCommandLine(Actor.class, line.getContent());
      checkDeprecation(result);
      m_Input.remove(0);
      if (m_Input.size() > 0) {
	if (m_Input.get(0) instanceof ArrayList)
	  m_Input = (List) m_Input.get(0);
	else
	  m_Input = new ArrayList();
      }
      else {
	m_Input = new ArrayList();
      }
    }
    catch (Exception e) {
      msg = "Failed to initialize output:";
      logError(msg + " " + e);
      getLogger().log(Level.SEVERE, msg, e);
      result = null;
    }

    return result;
  }

  /**
   * Unused.
   *
   * @param option	the boolean option to process
   * @param values	the value for the boolean option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(BooleanOption option, List values) throws Exception {
  }

  /**
   * Unused.
   *
   * @param option	the boolean option to process
   * @param values	the value for the boolean option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(ClassOption option, List values) throws Exception {
  }

  /**
   * Unused.
   *
   * @param option	the boolean option to process
   * @param values	the value for the boolean option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(AbstractArgumentOption option, List values) throws Exception {
  }

  /**
   * Visits the options.
   *
   * @param manager	the manager to visit
   * @param input	the input data to use
   */
  @Override
  protected void doConsume(OptionManager manager, List input) {
    ActorHandler	handler;
    boolean		mutable;
    Actor		actor;
    int			i;
    int	 		a;
    Line		line;
    String		msg;

    if (manager.getOwner() instanceof ActorHandler) {
      handler = (ActorHandler) manager.getOwner();
      mutable = (handler instanceof MutableActorHandler);
      if (mutable)
        ((MutableActorHandler) handler).removeAll();
      i = 0;
      a = 0;
      while (i < input.size()) {
        try {
	  line  = (Line) input.get(i);
	  actor = (Actor) m_Consumer.fromString(line.getContent());
	  if (m_Consumer.hasErrors()) {
	    for (String error: m_Consumer.getErrors())
	      logError(error);
	  }
	  if (mutable)
	    ((MutableActorHandler) handler).add(actor);
	  else
	    handler.set(a, actor);
	  a++;
	  i++;
	  // nested actors?
	  if ((i < input.size()) && (input.get(i) instanceof List)) {
	    doConsume(actor.getOptionManager(), (List) input.get(i));
	    i++;
	  }
	}
	catch (Exception e) {
          msg = "Failed to instantiate: " + input.get(i);
          logError(msg + "\n" + e);
          getLogger().log(Level.SEVERE, msg, e);
          return;
	}
      }
    }
  }
}
