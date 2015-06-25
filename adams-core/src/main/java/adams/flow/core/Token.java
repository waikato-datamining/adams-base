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
 * Token.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;


import adams.core.CloneHandler;
import adams.core.Utils;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.gui.flow.provenance.ProvenanceNode;

import java.io.Serializable;

/**
 * A wrapper object for passing data through the flow.
 * <br><br>
 * If enabled, provenance information can be stored as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Provenance#isEnabled()
 * @see ProvenanceSupporter
 */
public class Token
  implements Serializable, CloneHandler<Token>, ProvenanceContainer {

  /** for serialization. */
  private static final long serialVersionUID = -619575965753741493L;

  /** the payload. */
  protected Object m_Payload;

  /** the provenance trace. */
  protected ProvenanceNode m_Provenance;

  /**
   * Initializes the token.
   */
  public Token() {
    this(null);
  }

  /**
   * Initializes the token.
   *
   * @param payload	the payload for this token
   */
  public Token(Object payload) {
    super();

    setPayload(payload);
    setProvenance(null);
  }

  /**
   * Sets the payload.
   *
   * @param value	the new payload
   */
  public void setPayload(Object value) {
    m_Payload = value;
  }

  /**
   * Returns the payload.
   *
   * @return		the payload
   */
  public Object getPayload() {
    return m_Payload;
  }

  /**
   * Checks whether the payload is null.
   *
   * @return		true if payload is null
   */
  public boolean isNull() {
    return (m_Payload == null);
  }

  /**
   * Returns whether provenance is available.
   *
   * @return		true if provenance is available
   */
  public boolean hasProvenance() {
    return (m_Provenance != null);
  }

  /**
   * Sets the provenance to use.
   *
   * @param value	the provenance
   */
  public void setProvenance(ProvenanceNode value) {
    m_Provenance = value;
  }

  /**
   * Returns the current provenance.
   *
   * @return		the provenance, null if none available
   */
  public ProvenanceNode getProvenance() {
    return m_Provenance;
  }

  /**
   * Adds the provenance information to the internal structure.
   *
   * @param info	the info to add (the new root node)
   */
  public void addProvenance(ProvenanceInformation info) {
    ProvenanceNode	node;
    ProvenanceNode	child;

    if (!Provenance.getSingleton().isEnabled())
      return;

    node = new ProvenanceNode(null, info);
    if (m_Provenance != null) {
      child = m_Provenance;
      node.add(child);
    }
    m_Provenance = node;
  }

  /**
   * Sets the provenance information. The "info" provenance information will
   * be the result of the parent nodes in the provenance tree.
   *
   * @param info	the provenance information to add (the new root node)
   * @param parents	the parent provenance information of the new node
   */
  public void mergeProvenance(ProvenanceInformation info, ProvenanceNode[] parents) {
    if (!Provenance.getSingleton().isEnabled())
      return;

    m_Provenance = new ProvenanceNode(null, info);
    for (ProvenanceNode parent: parents)
      m_Provenance.add(parent);
  }

  /**
   * Returns a clone of itself. Returns a deep copy of the payload if that is
   * serializable.
   *
   * @return		the clone
   */
  public Token getClone() {
    Token	result;

    result = new Token();

    if (!isNull() && (m_Payload instanceof CloneHandler))
      result.setPayload(((CloneHandler) m_Payload).getClone());
    else if (!isNull() && (m_Payload instanceof Serializable))
      result.setPayload(Utils.deepCopy(m_Payload));
    else
      result.setPayload(m_Payload);

    if (Provenance.getSingleton().isEnabled() && (m_Provenance != null))
      result.setProvenance(m_Provenance.getClone());

    return result;
  }

  /**
   * Returns the hashcode for the object.
   *
   * @return		the hash code
   */
  @Override
  public int hashCode() {
    if (isNull())
      return "".hashCode();
    else
      return m_Payload.hashCode();
  }

  /**
   * Returns a string representation of the payload.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "Token #" + hashCode() + ": " + (isNull() ? "null" : m_Payload.toString());
  }
}
