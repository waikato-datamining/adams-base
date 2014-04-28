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
 * Capability.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Enumeration of all capabilities.
 * <p/>
 * Replication of the Weka capabilities, as these cannot be edited in the
 * GOE. Needs to be updated whenever the Weka ones changes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum Capability {
  // attributes
  /** can handle nominal attributes. */
  NOMINAL_ATTRIBUTES,
  /** can handle binary attributes. */
  BINARY_ATTRIBUTES,
  /** can handle unary attributes. */
  UNARY_ATTRIBUTES,
  /** can handle empty nominal attributes. */
  EMPTY_NOMINAL_ATTRIBUTES,
  /** can handle numeric attributes. */
  NUMERIC_ATTRIBUTES,
  /** can handle date attributes. */
  DATE_ATTRIBUTES,
  /** can handle string attributes. */
  STRING_ATTRIBUTES,
  /** can handle relational attributes. */
  RELATIONAL_ATTRIBUTES,
  /** can handle missing values in attributes. */
  MISSING_VALUES,
  // class
  /** can handle data without class attribute, eg clusterers. */
  NO_CLASS,
  /** can handle nominal classes. */
  NOMINAL_CLASS,
  /** can handle binary classes. */
  BINARY_CLASS,
  /** can handle unary classes. */
  UNARY_CLASS,
  /** can handle empty nominal classes. */
  EMPTY_NOMINAL_CLASS,
  /** can handle numeric classes. */
  NUMERIC_CLASS,
  /** can handle date classes. */
  DATE_CLASS,
  /** can handle string classes. */
  STRING_CLASS,
  /** can handle relational classes. */
  RELATIONAL_CLASS,
  /** can handle missing values in class attribute. */
  MISSING_CLASS_VALUES,
  // other
  /** can handle multi-instance data. */
  ONLY_MULTIINSTANCE;

  /**
   * Turns the ADAMS capability into a WEKA one.
   *
   * @param c		the capability to convert
   * @return		the corresponding WEKA capability
   */
  public static weka.core.Capabilities.Capability toWeka(Capability c) {
    switch (c) {
      case NOMINAL_ATTRIBUTES:
        return weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES;
      case BINARY_ATTRIBUTES:
        return weka.core.Capabilities.Capability.BINARY_ATTRIBUTES;
      case UNARY_ATTRIBUTES:
        return weka.core.Capabilities.Capability.UNARY_ATTRIBUTES;
      case EMPTY_NOMINAL_ATTRIBUTES:
        return weka.core.Capabilities.Capability.EMPTY_NOMINAL_ATTRIBUTES;
      case NUMERIC_ATTRIBUTES:
        return weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES;
      case DATE_ATTRIBUTES:
        return weka.core.Capabilities.Capability.DATE_ATTRIBUTES;
      case STRING_ATTRIBUTES:
        return weka.core.Capabilities.Capability.STRING_ATTRIBUTES;
      case RELATIONAL_ATTRIBUTES:
        return weka.core.Capabilities.Capability.RELATIONAL_ATTRIBUTES;
      case MISSING_VALUES:
        return weka.core.Capabilities.Capability.MISSING_VALUES;
      case NO_CLASS:
        return weka.core.Capabilities.Capability.NO_CLASS;
      case NOMINAL_CLASS:
        return weka.core.Capabilities.Capability.NOMINAL_CLASS;
      case BINARY_CLASS:
        return weka.core.Capabilities.Capability.BINARY_CLASS;
      case UNARY_CLASS:
        return weka.core.Capabilities.Capability.UNARY_CLASS;
      case EMPTY_NOMINAL_CLASS:
        return weka.core.Capabilities.Capability.EMPTY_NOMINAL_CLASS;
      case NUMERIC_CLASS:
        return weka.core.Capabilities.Capability.NUMERIC_CLASS;
      case DATE_CLASS:
        return weka.core.Capabilities.Capability.DATE_CLASS;
      case STRING_CLASS:
        return weka.core.Capabilities.Capability.STRING_CLASS;
      case RELATIONAL_CLASS:
        return weka.core.Capabilities.Capability.RELATIONAL_CLASS;
      case MISSING_CLASS_VALUES:
        return weka.core.Capabilities.Capability.MISSING_CLASS_VALUES;
      case ONLY_MULTIINSTANCE:
        return weka.core.Capabilities.Capability.ONLY_MULTIINSTANCE;
      default:
          throw new IllegalStateException("Unhandled capability: " + c);
    }
  }

  /**
   * Turns the WEKA capability into an ADAMS one.
   *
   * @param c		the capability to convert
   * @return		the corresponding ADAMS capability
   */
  public static Capability toAdams(weka.core.Capabilities.Capability c) {
    switch (c) {
      case NOMINAL_ATTRIBUTES:
        return Capability.NOMINAL_ATTRIBUTES;
      case BINARY_ATTRIBUTES:
        return Capability.BINARY_ATTRIBUTES;
      case UNARY_ATTRIBUTES:
        return Capability.UNARY_ATTRIBUTES;
      case EMPTY_NOMINAL_ATTRIBUTES:
        return Capability.EMPTY_NOMINAL_ATTRIBUTES;
      case NUMERIC_ATTRIBUTES:
        return Capability.NUMERIC_ATTRIBUTES;
      case DATE_ATTRIBUTES:
        return Capability.DATE_ATTRIBUTES;
      case STRING_ATTRIBUTES:
        return Capability.STRING_ATTRIBUTES;
      case RELATIONAL_ATTRIBUTES:
        return Capability.RELATIONAL_ATTRIBUTES;
      case MISSING_VALUES:
        return Capability.MISSING_VALUES;
      case NO_CLASS:
        return Capability.NO_CLASS;
      case NOMINAL_CLASS:
        return Capability.NOMINAL_CLASS;
      case BINARY_CLASS:
        return Capability.BINARY_CLASS;
      case UNARY_CLASS:
        return Capability.UNARY_CLASS;
      case EMPTY_NOMINAL_CLASS:
        return Capability.EMPTY_NOMINAL_CLASS;
      case NUMERIC_CLASS:
        return Capability.NUMERIC_CLASS;
      case DATE_CLASS:
        return Capability.DATE_CLASS;
      case STRING_CLASS:
        return Capability.STRING_CLASS;
      case RELATIONAL_CLASS:
        return Capability.RELATIONAL_CLASS;
      case MISSING_CLASS_VALUES:
        return Capability.MISSING_CLASS_VALUES;
      case ONLY_MULTIINSTANCE:
        return Capability.ONLY_MULTIINSTANCE;
      default:
          throw new IllegalStateException("Unhandled capability: " + c);
    }
  }
}