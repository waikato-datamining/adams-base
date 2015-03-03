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
 * ProvenanceNode.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.provenance;

import adams.core.CloneHandler;
import adams.core.net.HtmlUtils;
import adams.core.option.OptionUtils;
import adams.flow.provenance.ProvenanceInformation;
import adams.gui.core.BaseTreeNode;

/**
 * Specialized tree node for storing/displaying provenance information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProvenanceNode
  extends BaseTreeNode
  implements CloneHandler<ProvenanceNode> {

  /** for serialization. */
  private static final long serialVersionUID = 624983664888717132L;

  /** the owning tree. */
  protected ProvenanceTree m_Owner;

  /**
   * Initializes the tree node with the given provenance information.
   *
   * @param owner	thw owning tree
   */
  public ProvenanceNode(ProvenanceTree owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Initializes the tree node with the given provenance information.
   *
   * @param owner	thw owning tree
   * @param info	the provenance information to store
   */
  public ProvenanceNode(ProvenanceTree owner, ProvenanceInformation info) {
    super(info);

    m_Owner = owner;
  }

  /**
   * Sets the owning tree recursively.
   *
   * @param value	the owner
   */
  public void setOwner(ProvenanceTree value) {
    int		i;

    m_Owner = value;

    for (i = 0; i < getChildCount(); i++)
      ((ProvenanceNode) getChildAt(i)).setOwner(value);
  }

  /**
   * Returns the owning tree.
   *
   * @return		the owner
   */
  public ProvenanceTree getOwner() {
    return m_Owner;
  }

  /**
   * Checks whether provenance information is available.
   *
   * @return		true if provenance information is available
   */
  public boolean hasProvenanceInformation() {
    return (getUserObject() != null);
  }

  /**
   * Returns the provenance information.
   *
   * @return		the information
   */
  public ProvenanceInformation getProvenanceInformation() {
    return (ProvenanceInformation) getUserObject();
  }

  /**
   * Returns the classname from the stored provenance information.
   *
   * @return		the classname, null if no provenance information available
   */
  public String getClassname() {
    if (hasProvenanceInformation())
      return getProvenanceInformation().getClassname();
    else
      return null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public ProvenanceNode getClone() {
    ProvenanceNode	result;
    ProvenanceNode	child;
    int			i;

    result = new ProvenanceNode(getOwner(), getProvenanceInformation());

    for (i = 0; i < getChildCount(); i++) {
      child = ((ProvenanceNode) getChildAt(i)).getClone();
      result.add(child);
    }

    return result;
  }

  /**
   * Turns a class (from input/output) into a string.
   *
   * @param cls		the classes to turn into a list
   * @return		the generated string
   */
  protected String classToString(Class cls) {
    StringBuilder	result;
    int			n;
    String		name;
    String		prefix;
    Class		type;

    result = new StringBuilder();

    type = cls;
    if (type.isArray())
      type = type.getComponentType();
    name = type.getName();

    // remove common prefixes
    if (m_Owner != null) {
      for (n = 0; n < m_Owner.getInputOutputPrefixes().length; n++) {
	prefix = m_Owner.getInputOutputPrefixes()[n];
	if (name.startsWith(prefix))
	  name = name.replace(prefix, "");
      }
    }

    result.append(name);
    if (cls.isArray())
      result.append("[]");

    return result.toString();
  }

  /**
   * Returns an HTML representation of the provenance information.
   *
   * @return		the HTML representation
   */
  @Override
  public String toString() {
    StringBuilder		result;
    ProvenanceInformation	info;

    result = new StringBuilder();

    result.append("<html>");

    if (hasProvenanceInformation()) {
      info = getProvenanceInformation();

      // output
      if (info.hasOutputDataType()) {
	if (m_Owner == null)
	  result.append(
	      "Output: " + HtmlUtils.toHTML(classToString(info.getOutputDataType())));
	else
	  result.append(
	      "<font size='" + m_Owner.getInputOutputSize() + "' color='" + m_Owner.getInputOutputColor() + "'>"
	      + "Output: " + HtmlUtils.toHTML(classToString(info.getOutputDataType())) + "</font>");
	result.append("<br>");
      }

      // actor type
      if (m_Owner == null)
	result.append(
	    HtmlUtils.toHTML(info.getActorType().toString()));
      else
	result.append(
	    "<font size='" + m_Owner.getActorTypeSize() + "' color='" + m_Owner.getActorTypeColor() + "'>"
	    + HtmlUtils.toHTML(info.getActorType().toString()) + "</font>");
      result.append(" ");

      // options
      if (m_Owner == null)
	result.append(
	    HtmlUtils.toHTML(OptionUtils.joinOptions(info.getOptions())));
      else
	result.append(
	    "<font size='" + m_Owner.getOptionsSize() + "' color='" + m_Owner.getOptionsColor() + "'>"
	    + HtmlUtils.toHTML(OptionUtils.joinOptions(info.getOptions())) + "</font>");

      // input
      if (info.hasInputDataType()) {
	result.append("<br>");
	if (m_Owner == null)
	  result.append(
	      "Input: " + HtmlUtils.toHTML(classToString(info.getInputDataType())));
	else
	  result.append(
	      "<font size='" + m_Owner.getInputOutputSize() + "' color='" + m_Owner.getInputOutputColor() + "'>"
	      + "Input: " + HtmlUtils.toHTML(classToString(info.getInputDataType())) + "</font>");
      }
    }
    else {
      result.append("<b>[none]</b>");
    }

    result.append("</html>");

    return result.toString();
  }

  /**
   * Returns a plain text representation of the provenance information.
   *
   * @return		the plain text representation
   */
  @Override
  public String toPlainText() {
    StringBuilder		result;
    ProvenanceInformation	info;

    result = new StringBuilder();

    if (hasProvenanceInformation()) {
      info = getProvenanceInformation();

      // output
      if (info.hasOutputDataType()) {
	if (m_Owner == null)
	  result.append(
	      "Output: " + classToString(info.getOutputDataType()));
	else
	  result.append("Output: " + classToString(info.getOutputDataType()));
	result.append("\n");
      }

      // actor type
      result.append(info.getActorType().toString());
      result.append(" ");

      // options
      if (m_Owner == null)
	result.append(OptionUtils.joinOptions(info.getOptions()));
      else
	result.append(OptionUtils.joinOptions(info.getOptions()));

      // input
      if (info.hasInputDataType()) {
	result.append("\n");
	if (m_Owner == null)
	  result.append("Input: " + classToString(info.getInputDataType()));
	else
	  result.append("Input: " + classToString(info.getInputDataType()));
      }
    }
    else {
      result.append("[none]");
    }

    return result.toString();
  }
}
