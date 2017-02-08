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
 * XhtmlProducer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.AdditionalInformationHandler;
import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.doc.xhtml.Anchor;
import adams.doc.xhtml.Body;
import adams.doc.xhtml.Code;
import adams.doc.xhtml.Document;
import adams.doc.xhtml.Heading;
import adams.doc.xhtml.ListItem;
import adams.doc.xhtml.OrderedList;
import adams.doc.xhtml.Paragraph;
import adams.doc.xhtml.Pre;
import adams.doc.xhtml.Table;
import adams.doc.xhtml.Title;
import adams.doc.xhtml.UnorderedList;
import adams.doc.xml.AbstractTag;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.core.BaseTreeNode;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.tree.TreeNode;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates documentation in XHTML.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XhtmlProducer
  extends AbstractDocumentationProducer<String>
  implements RecursiveOptionProducer, FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1354998807180416601L;

  public static class ActorOptionTraverser
    implements OptionTraverser {

    /** for adding the latex code. */
    protected UnorderedList m_Details;
    
    /** whether to output variable values. */
    protected boolean m_OutputVariableValues;
    
    /** whether to output default values. */
    protected boolean m_OutputDefaultValues;

    /**
     * Initializes the traverser.
     * 
     * @param details	for adding the latex code
     * @param varValues	whether to output variable values
     * @param defValues	whether to output default values
     */
    public ActorOptionTraverser(UnorderedList details, boolean varValues, boolean defValues) {
      m_Details              = details;
      m_OutputVariableValues = varValues;
      m_OutputDefaultValues  = defValues;
    }

    /**
     * Adds the description of the option as row.
     *
     * @param parent	the table to add to
     * @param option	the option to use
     */
    protected void addDescription(Table parent, AbstractOption option) {
      String		description;
      Method		method;

      try {
	method      = option.getToolTipMethod();
	description = (String) method.invoke(option.getOptionHandler(), new Object[0]);
      }
      catch (Exception e) {
	description = "-none-";
      }

      parent.addRow(new String[]{
	  "Description",
	  description
      });
    }

    /**
     * Adds static information, like property, command-line flag and description.
     * 
     * @param option	the option to get the static information from
     */
    protected Table addStaticInfo(AbstractOption option) {
      ListItem	listitem;
      Table	table;
      Paragraph	para;

      listitem = new ListItem();
      m_Details.add(listitem);

      para = new Paragraph();
      para.setContent(option.getProperty() + "/" + "-" + option.getCommandline());
      listitem.add(para);
      
      table = new Table(2);
      table.setAttribute("border", "1");
      table.setAttribute("cellspacing", "0");
      listitem.add(table);
      addDescription(table, option);
      
      return table;
    }
    
    @Override
    public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
      boolean	different;
      Table	table;

      different = ((Boolean) option.getCurrentValue()).equals((Boolean) option.getDefaultValue());

      if (m_OutputDefaultValues || different) {
	table = addStaticInfo(option);
	table.addRow(
	    new String[]{
		"value",
		"" + option.getCurrentValue()
	    });
      }
    }

    @Override
    public void handleClassOption(ClassOption option, OptionTraversalPath path) {
      handleArgumentOption(option, path);
    }

    @Override
    public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
      Object	currValue;
      int	i;
      Table	table;

      if (option.isVariableAttached() && !m_OutputVariableValues) {
	table = addStaticInfo(option);
	table.addRow(
	    new String[]{
		"variable",
		option.getVariable()
	    });
      }
      else {
	currValue = option.getCurrentValue();

	if (m_OutputDefaultValues || !option.isDefaultValue(currValue)) {
	  if (currValue != null) {
	    table = addStaticInfo(option);

	    if (!option.isMultiple()) {
	      table.addRow(
		  new String[]{
		      "value",
		      option.toString(currValue)
		  });
	    }
	    else {
	      for (i = 0; i < Array.getLength(currValue); i++) {
		table.addRow(
		    new String[]{
			"value[" + i + "]",
			option.toString(Array.get(currValue, i))
		    });
	      }
	    }
	  }
	}
      }
    }

    @Override
    public boolean canHandle(AbstractOption option) {
      AbstractArgumentOption	opt;

      if (option instanceof AbstractArgumentOption) {
	opt = (AbstractArgumentOption) option;
	return ((opt.getBaseClass() != Actor.class) && (opt.getBaseClass() != AbstractActor.class));
      }

      return false;
    }

    @Override
    public boolean canRecurse(Class cls) {
      return !(ClassLocator.hasInterface(Actor.class, cls));
    }

    @Override
    public boolean canRecurse(Object obj) {
      if (obj == null)
	return false;
      else
	return canRecurse(obj.getClass());
    }
  }

  /** the output vector. */
  protected Document m_Document;

  /** whether to output default values as well. */
  protected boolean m_OutputDefaultValues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates comprehensive output in DocBook format, including the "
      + "descriptions for the options and (optionally) the default values.\n\n"
      + "For more information on the DocBook XML format, see:\n"
      + "http://www.docbook.org/";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"output-default-values", "outputDefaultValues",
	false);
  }

  /**
   * Initializes the visitor.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Document = new Document();
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
   * Sets whether to output default values as well.
   *
   * @param value	if true then default values are output as well
   */
  public void setOutputDefaultValues(boolean value) {
    m_OutputDefaultValues = value;
    reset();
  }

  /**
   * Returns whyether default values are output as well.
   *
   * @return		true if default values are output as well
   */
  public boolean getOutputDefaultValues() {
    return m_OutputDefaultValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDefaultValuesTipText() {
    return "Whether to output or suppress default values.";
  }
  
  /**
   * Generates a string representation of the tree in plain text.
   * 
   * @param tree	the flow tree to turn into plain text
   * @return		the string representation
   */
  protected String toPlainText(Tree tree) {
    StringBuilder	result;
    Object		root;
    List<Boolean>	more;

    result = new StringBuilder();

    root = tree.getModel().getRoot();
    if (root instanceof BaseTreeNode) {
      more = new ArrayList<Boolean>();
      more.add(false);
      toPlainText(result, 0, (BaseTreeNode) root, more);
    }
    else {
      result.append("Cannot generate tree!");
    }

    return result.toString();
  }
  
  /**
   * Returns the node in plain text.
   *
   * @param node	the node to turn into plain text
   * @return		the plain text representation
   */
  protected String toPlainText(Node node) {
    StringBuilder	result;
    Actor		actor;
    ActorPosition	pos;

    result = new StringBuilder();

    actor = node.getActor();
    if (actor == null) {
      result.append("[none]");
    }
    else {
      result.append(AbstractTag.toEntities(actor.getName()));
      // skip this actor?
      if (actor.getSkip()) {
	result.insert(0, "---");
	result.append("---");
      }
    }

    if (m_NamePosition.containsKey(actor.getFullName())) {
      pos = m_NamePosition.get(actor.getFullName());
      result.append(" <a href=\"#" + pos.index + "\">[" + pos.index + "]</a>");
    }

    return result.toString();
  }

  /**
   * Adds the node (and its potentional children) to the StringBuilder.
   *
   * @param builder	for adding the tree structure to
   * @param level	the current level (for indentation)
   * @param node	the node to process
   * @param more	for keeping track whether more siblings come after the
   * 			node
   */
  protected void toPlainText(StringBuilder builder, int level, BaseTreeNode node, List<Boolean> more) {
    StringBuilder	indentStr;
    int			i;
    TreeNode		child;
    String[]		lines;

    // generate indentation string
    indentStr = new StringBuilder();
    for (i = 0; i < level; i++) {
      if (more.get(i))
	indentStr.append("| ");
      else
	indentStr.append("  ");
    }

    // add node
    if (level > 0) {
      builder.append(indentStr);
      builder.append("|\n");
    }
    lines = toPlainText((Node) node).split("\n");
    for (i = 0; i < lines.length; i++) {
      builder.append(indentStr);
      if (i == 0)
	builder.append("+ ");
      else
	builder.append("  ");
      builder.append(lines[i] + "\n");
    }

    // add children
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      more.add(i < node.getChildCount() - 1);
      toPlainText(builder, level + 1, (BaseTreeNode) child, more);
      more.remove(more.size() - 1);
    }
  }

  /**
   * Adds an {@link Table} with the global info an potential
   * additional information.
   *
   * @param parent	the parent to add the elements to
   * @param obj		the object to process
   */
  protected void addSynopsis(AbstractTag parent, Object obj) {
    Table	table;
    Method		method;
    String		globalInfo;
    String		addInfo;

    table = new Table(2);
    table.setAttribute("border", "1");
    table.setAttribute("cellspacing", "0");
    parent.add(table);

    try {
      method = obj.getClass().getMethod("globalInfo", new Class[0]);
      if (method != null) {
	globalInfo = (String) method.invoke(obj, new Object[0]);
	table.addRow(
	    new String[]{
		"Synopsis",
		globalInfo
	    });
      }
    }
    catch (Exception e) {
      // ignored
    }

    if (obj instanceof AdditionalInformationHandler) {
      addInfo = ((AdditionalInformationHandler) getInput()).getAdditionalInformation();
      if ((addInfo != null) && (addInfo.length() > 0)) {
	table.addRow(new AbstractTag[]{
	    new Paragraph("Additional information"), 
	    new Pre(addInfo)
	});
      }
    }
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    super.preProduce();

    m_Output = null;

    m_Document = new Document();
    m_Document.getRoot().getHead().add(new Title("Setup documentation"));
  }

  /**
   * Generates the documentation.
   */
  protected void generateDocument() {
    Body			body;
    Heading			heading;
    Code			listing;
    Tree			tree;
    Paragraph			para;
    Actor			actor;
    OrderedList			olist;
    ListItem			listitem;
    ActorOptionTraverser	traverser;
    UnorderedList		ulist;
    String[]			lines;

    actor = null;
    if (m_Input instanceof Actor);
      actor = (Actor) m_Input;

    body    = m_Document.getRoot().getBody();
    heading = new Heading(1, "Structure");
    body.add(heading);
    if (actor != null) {
      para = new Paragraph();
      body.add(para);
      para.setContent(actor.getName());
      if (!actor.getAnnotations().isEmpty()) {
	para = new Paragraph();
	body.add(para);
	para.setContent(actor.getAnnotations().getValue());
      }
    }
    tree = new Tree(null, (Actor) getInput());
    listing = new Code();
    lines = Utils.split(toPlainText(tree), "\n");
    listing.setContent(Utils.flatten(lines, "<br>\n"));
    listing.setConvertEntities(false);
    body.add(listing);
    
    heading = new Heading(1, "Details");
    body.add(heading);
    olist = new OrderedList();
    body.add(olist);
    for (ActorPosition item: m_Positions) {
      listitem = new ListItem();
      olist.add(listitem);
      para = new Paragraph();
      para.add(new Anchor("" + item.index, null, item.actor.getName()));
      listitem.add(para);
      addSynopsis(listitem, item.actor);
      ulist = new UnorderedList();
      traverser = new ActorOptionTraverser(ulist, m_OutputVariableValues, m_OutputDefaultValues);
      item.actor.getOptionManager().traverse(traverser);
      if (ulist.getChildCount() > 0) {
	para = new Paragraph();
	para.setContent("Option" + (ulist.getChildCount() > 0 ? "s" : "") + ":");
	listitem.add(para);
	listitem.add(ulist);
      }
    }
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   */
  @Override
  public String getOutput() {
    StringBuilder	buffer;

    if (m_Output == null) {
      generateDocument();
      buffer = new StringBuilder();
      m_Document.toXML(buffer);
      m_Output = buffer.toString();
    }

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
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Document = null;
  }

  /**
   * Returns the description of the file format.
   *
   * @return		the description
   */
  public String getFormatDescription() {
    return "XHTML";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "html";
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
    runProducer(XhtmlProducer.class, args);
  }
}
