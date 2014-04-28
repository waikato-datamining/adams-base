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
 * FlowStructureDotProducer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.flow.control.AbstractTee;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.IfThenElse;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorUser;

/**
 * Outputs the flow structure in DOT (GraphViz) format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowStructureDotProducer
  extends AbstractRecursiveOptionProducerWithOptionHandling<String,String>
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5441506605408584791L;

  /** whether to display the class name as well in the label. */
  protected boolean m_OutputClassname;

  /** whether to link to callable actors. */
  protected boolean m_LinkCallableActors;

  /** for storing the generating string. */
  protected StringBuilder m_OutputBuffer;

  /** the counter for the nodes. */
  protected int m_Counter;

  /** the actor name - ID relation. */
  protected Hashtable<String,String> m_NameIDRelation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the flow structure in DOT (GraphViz) format.\n\n"
      + "For more information on the DOT format, see:\n"
      + "http://www.graphviz.org/content/dot-language";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_UsePropertyNames = true;
    m_NameIDRelation   = new Hashtable<String,String>();
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"output-classname", "outputClassname",
	false);

    m_OptionManager.add(
	"link-callable-actors", "linkCallableActors",
	false);
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected String initOutput() {
    return "";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputVariableValuesTipText() {
    return "This option is ignored, since only the structure is output.";
  }

  /**
   * Sets whether to output the classnames in the labels.
   *
   * @param value	if true then the classnames are added to the labels
   */
  public void setOutputClassname(boolean value) {
    m_OutputClassname = value;
    reset();
  }

  /**
   * Returns whether to add the classnames to the labels.
   *
   * @return		true if the classnames are added
   */
  public boolean getOutputClassname() {
    return m_OutputClassname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputClassnameTipText() {
    return "Whether to output the classnames in the labels as well.";
  }

  /**
   * Sets whether to output the links to callable actors as well.
   *
   * @param value	if true then the links to callable actors are output as well
   */
  public void setLinkCallableActors(boolean value) {
    m_LinkCallableActors = value;
    reset();
  }

  /**
   * Returns whether to output the links to callable actors as well.
   *
   * @return		true if the links to callable actors are output as well
   */
  public boolean getLinkCallableActors() {
    return m_LinkCallableActors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String linkCallableActorsTipText() {
    return "Whether to output the links to callable actors as well.";
  }

  /**
   * Visits a boolean option. Does nothing.
   *
   * @param option	the boolean option
   * @return		always null
   */
  @Override
  public String processOption(BooleanOption option) {
    return null;
  }

  /**
   * Returns the next node ID.
   *
   * @return		the node ID
   */
  protected String nextNodeID() {
    m_Counter++;
    return "N" + m_Counter;
  }

  /**
   * Adds an edge.
   *
   * @param from	the "from" ID
   * @param to		the "to" ID
   */
  protected void addEdge(String from, String to) {
    m_OutputBuffer.append("  ");
    m_OutputBuffer.append(sanitize(from) + " -> " + sanitize(to));
    m_OutputBuffer.append("\n");
  }

  /**
   * Sanitizes the label.
   *
   * @param label	the label to process
   * @return		the processed label
   */
  protected String sanitize(String label) {
    StringBuilder	result;
    int			i;
    char		c;

    result = new StringBuilder();
    for (i = 0; i < label.length(); i++) {
      c = label.charAt(i);
      if (i == 0) {
	if ((c >= 'a') && (c <= 'z'))
	  result.append(c);
	else if ((c >= 'A') && (c <= 'Z'))
	  result.append(c);
	else
	  result.append("_");
      }
      else {
	if ((c >= '0') && (c <= '9'))
	  result.append(c);
	else if ((c >= 'a') && (c <= 'z'))
	  result.append(c);
	else if ((c >= 'A') && (c <= 'Z'))
	  result.append(c);
	else
	  result.append("_");
      }
    }

    return result.toString();
  }

  /**
   * Adds a node.
   *
   * @param id		the ID of the node
   * @param label	the option label of the node, use null to omit
   */
  protected void addNode(String id, String label) {
    addNode(id, label, null, null);
  }

  /**
   * Adds a node.
   *
   * @param id		the ID of the node
   * @param label	the option label of the node, use null to omit
   * @param shape	the shape of the node
   * @param style	the style of the shape, null to omit
   */
  protected void addNode(String id, String label, String shape, String style) {
    List<String>	attrs;
    int			i;

    m_OutputBuffer.append("  ");
    m_OutputBuffer.append(sanitize(id));

    attrs = new ArrayList<String>();
    if (label != null)
      attrs.add("label=\"" + Utils.backQuoteChars(label) + "\"");
    if (shape != null)
      attrs.add("shape=" + shape);
    if (style != null)
      attrs.add("style=\"" + style + "\"");
    if (attrs.size() > 0) {
      m_OutputBuffer.append(" [");
      for (i = 0; i < attrs.size(); i++) {
	if (i > 0)
	  m_OutputBuffer.append(" ");
	m_OutputBuffer.append(attrs.get(i));
      }
      m_OutputBuffer.append("]");
    }

    m_OutputBuffer.append("\n");
  }

  /**
   * Adds the actor as node.
   *
   * @param actor	the actor to add as node
   * @return		the ID used for the node
   */
  protected String addNode(AbstractActor actor) {
    String	shape;
    String	style;

    // default for transformer
    shape = "box";
    style = null;

    // general
    if (ActorUtils.isSource(actor))
      shape = "parallelogram";

    // custom
    if (actor instanceof Flow)
      shape = "ellipse";

    if (actor instanceof AbstractTee)
      shape = "triangle";

    if (actor instanceof Trigger)
      shape = "ellipse";

    if (actor instanceof Branch)
      shape = "triangle";

    if (actor instanceof IfThenElse)
      shape = "diamond";

    return addNode(actor, shape, style);
  }

  /**
   * Adds the actor as node.
   *
   * @param actor	the actor to add as node
   * @param shape	the shape of the node, null to ignore
   * @param style	the style of the node, null to ignore
   * @return		the ID used for the node
   */
  protected String addNode(AbstractActor actor, String shape, String style) {
    String		result;
    String		label;
    String		globalID;
    AbstractActor	globalActor;

    result = nextNodeID();

    // edge?
    if (m_Nesting.size() > 0)
      addEdge(m_Nesting.peek(), result);

    label = actor.getName();
    if (m_OutputClassname)
      label += "\n" + "[" + actor.getClass().getName().replaceFirst("^.*\\.flow\\.", "") + "]";

    // global actor?
    if (actor instanceof CallableActorUser) {
      if (m_LinkCallableActors) {
	globalActor = ((CallableActorUser) actor).getCallableActor();
	if (globalActor != null) {
	  globalID = m_NameIDRelation.get(globalActor.getFullName());
	  if (globalID != null)
	    addEdge(result, globalID);
	}
      }
      else {
	label += ": " + ((AbstractCallableActor) actor).getCallableName();
      }
    }

    addNode(result, label, shape, style);

    m_NameIDRelation.put(actor.getFullName(), result);

    return result;
  }

  /**
   * Adds the actor as node (actor is represented by the variable).
   *
   * @param actor	the actor to add as node
   * @param variable	the variable representing the acto
   * @return		the ID used for the node
   */
  protected String addVariableNode(String variable) {
    String	result;

    result = nextNodeID();

    // edge?
    if (m_Nesting.size() > 0)
      addEdge(m_Nesting.peek(), result);

    addNode(result, variable, "box", "dotted");

    return result;
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		always null
   */
  @Override
  public String processOption(ClassOption option) {
    Object		currValue;
    Object		currValues;
    Object		value;
    int			i;
    AbstractActor	actor;
    String		id;
    int			size;

    if (!AbstractActor.class.isAssignableFrom(option.getBaseClass()))
      return null;

    if (option.isVariableAttached()) {
      addVariableNode(option.getVariable());
    }
    else {
      currValue  = getCurrentValue(option);
      currValues = null;

      if (currValue != null) {
	if (!option.isMultiple()) {
	  value  = currValue;
	  actor  = (AbstractActor) value;
	  id     = addNode(actor);
	  m_Nesting.push(id);
	  doProduce(((OptionHandler) value).getOptionManager());
	}
	else {
	  currValues = currValue;
	  size = m_Nesting.size();
	  for (i = 0; i < Array.getLength(currValues); i++) {
	    value  = Array.get(currValues, i);
	    actor  = (AbstractActor) value;
	    id     = addNode(actor);
	    m_Nesting.push(id);
	    doProduce(((OptionHandler) value).getOptionManager());
	    m_Nesting.pop();
	  }
	  while (m_Nesting.size() > size)
	    m_Nesting.pop();
	}
      }
    }

    return null;
  }

  /**
   * Visits an argument option. Does nothing.
   *
   * @param option	the argument option
   * @return		always null
   */
  @Override
  public String processOption(AbstractArgumentOption option) {
    return null;
  }

  /**
   * Makes sure that the input is an AbstractActor object.
   *
   * @param object	the objec to check
   * @return		the checked object
   */
  @Override
  protected OptionHandler checkInput(OptionHandler object) {
    OptionHandler	result;

    result = super.checkInput(object);

    if (!(result instanceof AbstractActor))
      throw new IllegalArgumentException(
	  "Input object must be derived from " + AbstractActor.class.getName());

    return result;
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    String		id;
    AbstractActor	actor;

    super.preProduce();

    m_Counter      = 0;
    m_Output       = null;
    m_OutputBuffer = new StringBuilder();
    m_OutputBuffer.append("digraph " + sanitize(getInput().getClass().getName()) + " {\n");

    actor = (AbstractActor) getInput();
    actor.setUp();

    id = addNode(actor);
    m_Nesting.push(id);
  }

  /**
   * Hook-method after visiting options.
   */
  @Override
  protected void postProduce() {
    AbstractActor	actor;

    super.postProduce();

    m_OutputBuffer.append("}\n");

    actor = (AbstractActor) getInput();
    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   */
  @Override
  public String getOutput() {
    if (m_Output == null)
      m_Output = m_OutputBuffer.toString();

    return m_Output;
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    return getOutput();
  }

  /**
   * Returns the description of the file format.
   *
   * @return		the description
   */
  @Override
  public String getFormatDescription() {
    return "GraphViz DOT format";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  @Override
  public String getDefaultFormatExtension() {
    return "dot";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(FlowStructureDotProducer.class, args);
  }
}
