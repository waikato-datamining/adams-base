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
 * ActorSuggestion.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;
import adams.parser.actorsuggestion.Parser;
import adams.parser.actorsuggestion.Scanner;
import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Evaluates rules for suggesting actors.<br>
 * <br>
 * It uses the following grammar:<br>
 * <br>
 *  expr_list ::= expr_list expr_part | expr_part ;<br>
 *  expr_part ::= IF boolexpr THEN cmdexpr ;<br>
 * <br>
 *  boolexpr ::=    ( boolean )<br>
 *                | boolean<br>
 *                | boolexpr AND boolexpr<br>
 *                | boolexpr OR boolexpr<br>
 *                | TRUE<br>
 *                | FALSE<br>
 *                | NOT boolexpr<br>
 *                | ISFIRST<br>
 *                | ISLAST<br>
 *                | PARENT IS classexpr<br>
 *                | PARENT ALLOWS STANDALONE<br>
 *                | PARENT ALLOWS SOURCE<br>
 *                | PRECEDING GENERATES classexpr<br>
 *                | FOLLOWING ACCEPTS classexpr<br>
 *                | BEFORE STANDALONE<br>
 *                | AFTER STANDALONE<br>
 *                | BEFORE SOURCE<br>
 *                | AFTER SOURCE<br>
 *                | BEFORE TRANSFORMER<br>
 *                | AFTER TRANSFORMER<br>
 *                | BEFORE SINK<br>
 *                | AFTER SINK<br>
 *                | BEFORE classexpr<br>
 *                | AFTER classexpr<br>
 *                ;<br>
 *  classexpr ::=  "classname (interface or class)"<br>
 *                | FULLNAME ( PARENT )<br>
 *                | FULLNAME ( PRECEDING )<br>
 *                | FULLNAME ( FOLLOWING )<br>
 *                ;<br>
 *  cmdexpr ::=     classname<br>
 *                | "classname + options"<br>
 *                ;<br>
 * <br>
 * Notes:<br>
 * - A 'cmdexpr' string surrounded by double quotes can also contain placeholders:<br>
 *   classname: ${PARENT.CLASS}, ${PRECEDING.CLASS}, ${FOLLOWING.CLASS}<br>
 *   actor's name: ${PARENT.NAME}, ${PRECEDING.NAME}, ${FOLLOWING.NAME}<br>
 *   actor's fullname: ${PARENT.FULL}, ${PRECEDING.FULL}, ${FOLLOWING.FULL}<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 * &nbsp;&nbsp;&nbsp;The class to use for determining the environment.
 * &nbsp;&nbsp;&nbsp;default: adams.env.Environment
 * </pre>
 *
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The rule for determining the actor to propose (result is null if rul does
 * &nbsp;&nbsp;&nbsp;not apply).
 * &nbsp;&nbsp;&nbsp;default: ISFIRST: adams.flow.standalone.CallableActors
 * </pre>
 *
 * <pre>-parent &lt;adams.flow.core.Actor&gt; (property: parent)
 * &nbsp;&nbsp;&nbsp;The parent actor to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Flow -flow-execution-listener adams.flow.execution.NullListener
 * </pre>
 *
 * <pre>-position &lt;int&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position to add the proposed actor at.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to insert the proposed actor in.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * In order to get access to the full name, the {@link Node}s from the
 * tree for parent and actors must be set programmatically.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

public class ActorSuggestion
  extends AbstractExpressionEvaluator<Actor>
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -2060968616326323959L;

  /**
   * Container class for data required for making suggestions.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SuggestionData {
    /** the parent. */
    public Actor parent;

    /** the parent node. */
    public Node parentNode;

    /** the position. */
    public int position;

    /** the actors on the same level. */
    public Actor[] actors;

    /** the actor nodes on the same level. */
    public Node[] actorNodes;
  }

  /** the parent of the proposed actor. */
  protected Actor m_Parent;

  /** the parent node. */
  protected transient Node m_ParentNode;

  /** the position the actor is to be inserted at. */
  protected int m_Position;

  /** the actors in which the proposed actor gets inserted. */
  protected Actor[] m_Actors;

  /** the nodes in which the proposed actor gets inserted. */
  protected transient Node[] m_ActorNodes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates rules for suggesting actors.\n\n"
      + "It uses the following grammar:\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return
        " expr_list ::= expr_list expr_part | expr_part ;\n"
      + " expr_part ::= IF boolexpr THEN cmdexpr ;\n"
      + "\n"
      + " boolexpr ::=    ( boolean )\n"
      + "               | boolean\n"
      + "               | boolexpr AND boolexpr\n"
      + "               | boolexpr OR boolexpr\n"
      + "               | TRUE\n"
      + "               | FALSE\n"
      + "               | NOT boolexpr\n"
      + "               | ISFIRST\n"
      + "               | ISLAST\n"
      + "               | PARENT IS classexpr\n"
      + "               | PARENT ALLOWS STANDALONE\n"
      + "               | PARENT ALLOWS SOURCE\n"
      + "               | PRECEDING GENERATES classexpr\n"
      + "               | FOLLOWING ACCEPTS classexpr\n"
      + "               | BEFORE STANDALONE\n"
      + "               | AFTER STANDALONE\n"
      + "               | BEFORE SOURCE\n"
      + "               | AFTER SOURCE\n"
      + "               | BEFORE TRANSFORMER\n"
      + "               | AFTER TRANSFORMER\n"
      + "               | BEFORE SINK\n"
      + "               | AFTER SINK\n"
      + "               | BEFORE classexpr\n"
      + "               | AFTER classexpr\n"
      + "               ;\n"
      + " classexpr ::=  \"classname (interface or class)\"\n"
      + "               | FULLNAME ( PARENT )\n"
      + "               | FULLNAME ( PRECEDING )\n"
      + "               | FULLNAME ( FOLLOWING )\n"
      + "               ;\n"
      + " cmdexpr ::=     classname\n"
      + "               | \"classname + options\"\n"
      + "               ;\n"
      + "\n"
      + "Notes:\n"
      + "- A 'cmdexpr' string surrounded by double quotes can also contain placeholders:\n"
      + "  classname: ${PARENT.CLASS}, ${PRECEDING.CLASS}, ${FOLLOWING.CLASS}\n"
      + "  actor's name: ${PARENT.NAME}, ${PRECEDING.NAME}, ${FOLLOWING.NAME}\n"
      + "  actor's fullname: ${PARENT.FULL}, ${PRECEDING.FULL}, ${FOLLOWING.FULL}\n"
      ;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    // dummy option, is queried manually in runEvaluator method
    m_OptionManager.add(
	    "parent", "parent",
	    new Flow());

    m_OptionManager.add(
	    "position", "position",
	    0, 0, null);

    m_OptionManager.add(
	    "actor", "actors",
	    new Actor[]{});
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "ISFIRST: adams.flow.standalone.CallableActors";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The rule for determining the actor to propose (result is null if rul does not apply).";
  }

  /**
   * Sets the parent to use.
   *
   * @param value	the parent
   */
  public void setParent(Actor value) {
    m_Parent = value;
  }

  /**
   * Returns the current parent in use.
   *
   * @return		the parent
   */
  public Actor getParent() {
    return m_Parent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String parentTipText() {
    return "The parent actor to use.";
  }

  /**
   * Sets the parent node to use.
   *
   * @param value	the parent
   */
  public void setParentNode(Node value) {
    m_ParentNode = value;
  }

  /**
   * Returns the current parent node in use.
   *
   * @return		the parent
   */
  public Node getParentNode() {
    return m_ParentNode;
  }

  /**
   * The position to add the proposed actor at.
   *
   * @param value	the position
   */
  public void setPosition(int value) {
    m_Position = value;
  }

  /**
   * Returns the position to add the proposed actor at.
   *
   * @return		the position
   */
  public int getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return "The position to add the proposed actor at.";
  }

  /**
   * Sets the actors to insert the proposed actor in.
   *
   * @param value	the actors
   */
  public void setActors(Actor[] value) {
    m_Actors = value;
  }

  /**
   * Returns the actors to insert the proposed actor in.
   *
   * @return		the actors
   */
  public Actor[] getActors() {
    return m_Actors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors to insert the proposed actor in.";
  }

  /**
   * Sets the nodes to insert the proposed actor in.
   *
   * @param value	the nodes
   */
  public void setActorNodes(Node[] value) {
    m_ActorNodes = value;
  }

  /**
   * Returns the nodes to insert the proposed actor in.
   *
   * @return		the nodes
   */
  public Node[] getActorNodes() {
    return m_ActorNodes;
  }

  /**
   * Performs the evaluation.
   *
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  public Actor evaluate() throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;

    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(m_Expression.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setParent(getParent());
    parser.setParentNode(getParentNode());
    parser.setPosition(getPosition());
    parser.setActors(getActors());
    parser.setActorNodes(getActorNodes());
    parser.parse();

    return parser.getResult();
  }

  /**
   * Performs the evaluation.
   *
   * @param expr	the expression/rule to evaluate
   * @param data	the suggestion data
   * @return		the proposed classname in case of a match, otherwise null
   * @throws Exception	if evaluation fails
   */
  public static Actor evaluate(String expr, SuggestionData data) throws Exception {
    Actor[]	result;

    result = evaluate(new String[]{expr}, data);

    return result[0];
  }

  /**
   * Performs the evaluation.
   *
   * @param expr	the expressions/rules to evaluate
   * @param data	the suggestion data
   * @return		array with proposed classnames (cells are null if rule wasn't a match)
   * @throws Exception	if evaluation fails
   */
  public static Actor[] evaluate(String[] expr, SuggestionData data) throws Exception {
    ArrayList<Actor>	result;
    ActorSuggestion	suggestion;
    int			i;
    Actor		actor;

    suggestion = new ActorSuggestion();
    suggestion.setParent(data.parent);
    suggestion.setParentNode(data.parentNode);
    suggestion.setPosition(data.position);
    suggestion.setActors(data.actors);
    suggestion.setActorNodes(data.actorNodes);

    result = new ArrayList<>();
    for (i = 0; i < expr.length; i++) {
      suggestion.setExpression(expr[i]);
      actor = suggestion.evaluate();
      if ((actor == null) || (result.contains(actor)))
	continue;
      result.add(actor);
    }

    return result.toArray(new Actor[result.size()]);
  }
}
