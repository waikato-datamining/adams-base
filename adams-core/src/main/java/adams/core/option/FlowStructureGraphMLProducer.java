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
 * FlowStructureGraphMLProducer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.util.Hashtable;

import adams.core.io.FileFormatHandler;
import adams.core.net.HtmlUtils;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.CallableActorUser;

/**
 * Outputs the flow structure in GraphML XML format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowStructureGraphMLProducer
  extends AbstractRecursiveOptionProducerWithOptionHandling<String,String>
  implements FileFormatHandler {

  /** blah. */
  private static final long serialVersionUID = -1493427064082222688L;

  /** for serialization. */

  /** whether to display the class name as well in the label. */
  protected boolean m_OutputClassname;

  /** whether to link to callable actors. */
  protected boolean m_LinkCallableActors;

  /** whether to output yEd GraphML. */
  protected boolean m_OutputYEdGraphML;

  /** for storing the generating string. */
  protected StringBuilder m_OutputBuffer;

  /** the counter for the nodes. */
  protected int m_NodeCounter;

  /** the counter for the edges. */
  protected int m_EdgeCounter;

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
        "Outputs the flow structure in GraphML XML format.\n\n"
      + "For more information on the GraphML format, see:\n"
      + "http://en.wikipedia.org/wiki/GraphML";
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

    m_OptionManager.add(
	"output-yed-graphml", "outputYEdGraphML",
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
   * Returns whether to output the links to global actors as well.
   *
   * @return		true if the links to global actors are output as well
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
    return "Whether to output the links to global actors as well.";
  }

  /**
   * Sets whether to output yEd GraphML.
   *
   * @param value	if true then yEd Graphml is produced
   */
  public void setOutputYEdGraphML(boolean value) {
    m_OutputYEdGraphML = value;
    reset();
  }

  /**
   * Returns whether to output yEd GraphML.
   *
   * @return		true if to output yEd GraphML
   */
  public boolean getOutputYEdGraphML() {
    return m_OutputYEdGraphML;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputYEdGraphMLTipText() {
    return "Whether to output yEd GraphML.";
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
    m_NodeCounter++;
    return "n" + m_NodeCounter;
  }

  /**
   * Returns the next edge ID.
   *
   * @return		the edge ID
   */
  protected String nextEdgeID() {
    m_EdgeCounter++;
    return "e" + m_EdgeCounter;
  }

  /**
   * Adds an edge.
   *
   * @param from	the "from" ID
   * @param to		the "to" ID
   */
  protected void addEdge(String from, String to) {
    m_OutputBuffer.append("  <edge");
    m_OutputBuffer.append(" id=\"" + nextEdgeID() + "\"");
    m_OutputBuffer.append(" directed=\"true\"");
    m_OutputBuffer.append(" source=\"" + sanitize(from) + "\"");
    m_OutputBuffer.append(" target=\"" + sanitize(to) + "\"");
    m_OutputBuffer.append("/>\n");
  }

  /**
   * Sanitizes the label.
   *
   * @param label	the label to process
   * @return		the processed label
   */
  protected String sanitize(String label) {
    return HtmlUtils.toHTML(label);
  }

  /**
   * Adds a node.
   *
   * @param id		the ID of the node
   * @param label	the option label of the node, use null to omit
   */
  protected void addNode(String id, String label) {
    m_OutputBuffer.append("  <node id=\"" + sanitize(id) + "\">\n");
    if (m_OutputYEdGraphML) {
      m_OutputBuffer.append("    <data key=\"d0\">\n");
      m_OutputBuffer.append("      <y:ShapeNode>\n");
      m_OutputBuffer.append("        <y:NodeLabel>" + sanitize(label) + "</y:NodeLabel>\n");
      m_OutputBuffer.append("      </y:ShapeNode>\n");
      m_OutputBuffer.append("    </data>\n");
    }
    else {
      m_OutputBuffer.append("    <data key=\"d0\">" + sanitize(label) + "</data>\n");
    }
    m_OutputBuffer.append("  </node>\n");
  }

  /**
   * Adds the actor as node.
   *
   * @param actor	the actor to add as node
   * @param shape	the shape of the node, null to ignore
   * @param style	the style of the node, null to ignore
   * @return		the ID used for the node
   */
  protected String addNode(AbstractActor actor) {
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

    addNode(result, label);

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

    addNode(result, variable);

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

    m_NodeCounter  = 0;
    m_EdgeCounter  = 0;
    m_Output       = null;
    m_OutputBuffer = new StringBuilder();
    m_OutputBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    m_OutputBuffer.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");
    m_OutputBuffer.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
    if (m_OutputYEdGraphML)
      m_OutputBuffer.append("    xmlns:y=\"http://www.yworks.com/xml/graphml\"\n");
    m_OutputBuffer.append("    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
    if (m_OutputYEdGraphML)
      m_OutputBuffer.append("  <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\n");
    else
      m_OutputBuffer.append("  <key id=\"d0\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");
    m_OutputBuffer.append("  <graph id=\"" + Environment.getInstance().getProject() + "\" edgedefault=\"directed\">\n");
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

    m_OutputBuffer.append("  </graph>\n");
    m_OutputBuffer.append("</graphml>\n");

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
  public String getFormatDescription() {
    return "GraphML XML format";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "graphml";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(FlowStructureGraphMLProducer.class, args);
  }
}
