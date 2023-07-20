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
 * XML.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.filecomplete;

import adams.core.io.FileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Checks whether the XML can be parsed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class XML
  extends AbstractFileCompleteCheck {

  private static final long serialVersionUID = 8742612165238773767L;

  /** whether the parser is validating or not. */
  protected boolean m_Validating;

  /** whether the parser is namespace aware. */
  protected boolean m_NameSpaceAware;

  /** Set state of XInclude processing.*/
  protected boolean m_XIncludeAware;

  /** Specifies that the parser produced by this code will expand entity reference nodes. */
  protected boolean m_ExpandEntityReferences;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the XML can be parsed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "validating", "validating",
      false);

    m_OptionManager.add(
      "name-space-aware", "nameSpaceAware",
      false);

    m_OptionManager.add(
      "x-include-aware", "XIncludeAware",
      false);

    m_OptionManager.add(
      "expand-entity-references", "expandEntityReferences",
      false);
  }

  /**
   * Sets whether to use a validating parser.
   *
   * @param value	true if to use validating parser
   */
  public void setValidating(boolean value) {
    m_Validating = value;
    reset();
  }

  /**
   * Returns whether a validating parser is used.
   *
   * @return 		true if validating parser
   */
  public boolean getValidating() {
    return m_Validating;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String validatingTipText() {
    return "If enabled, the parser will validate the XML.";
  }

  /**
   * Sets whether to use a namespace aware parser.
   *
   * @param value	true if to use namespace aware parser
   */
  public void setNameSpaceAware(boolean value) {
    m_NameSpaceAware = value;
    reset();
  }

  /**
   * Returns whether a namespace aware parser used.
   *
   * @return 		true if namespace aware
   */
  public boolean getNameSpaceAware() {
    return m_NameSpaceAware;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameSpaceAwareTipText() {
    return "If enabled, the parser will be namespace aware.";
  }

  /**
   * Sets whether to use a X-include aware parser.
   *
   * @param value	true if to use X-include aware parser
   */
  public void setXIncludeAware(boolean value) {
    m_XIncludeAware = value;
    reset();
  }

  /**
   * Returns whether a X-include aware parser is used.
   *
   * @return 		true if X-include aware parser
   */
  public boolean getXIncludeAware() {
    return m_XIncludeAware;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XIncludeAwareTipText() {
    return "If enabled, the parser will be X-include aware.";
  }

  /**
   * Sets whether to expand entity references.
   *
   * @param value	true if to expand entity references
   */
  public void setExpandEntityReferences(boolean value) {
    m_ExpandEntityReferences = value;
    reset();
  }

  /**
   * Returns whether a parser expands entity references.
   *
   * @return 		true if parser expands entity references
   */
  public boolean getExpandEntityReferences() {
    return m_ExpandEntityReferences;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandEntityReferencesTipText() {
    return "If enabled, the parser will expand entity references.";
  }

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer the buffer to check
   * @return true if complete
   */
  @Override
  public boolean isComplete(byte[] buffer) {
    DocumentBuilderFactory 	factory;
    DocumentBuilder 		builder;

    try {
      factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(m_Validating);
      factory.setNamespaceAware(m_NameSpaceAware);
      factory.setXIncludeAware(m_XIncludeAware);
      factory.setExpandEntityReferences(m_ExpandEntityReferences);
      builder = factory.newDocumentBuilder();
      builder.parse(new ByteArrayInputStream(buffer));
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks whether the file is complete.
   *
   * @param file the file to check
   * @return true if complete
   */
  @Override
  public boolean isComplete(File file) {
    return isComplete(FileUtils.loadFromBinaryFile(file));
  }
}
