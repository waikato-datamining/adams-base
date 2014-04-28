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
 * ListParameter.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.setupgenerator;

import weka.core.Option;
import weka.core.Utils;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Container class for search parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListParameter
  extends AbstractParameter {
  
  /** for serialization. */
  private static final long serialVersionUID = 1415901739037349037L;
  
  /** the explicit list of values to use. */
  protected String[] m_List = new String[0];
  
  /**
   * Returns a string describing the object.
   * 
   * @return 		a description suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String globalInfo() {
    return 
        "Container class defining the search parameters for a particular "
      + "property.\n"
      + "Only the specified list values are used.";
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector	result;
    Enumeration	enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm);

    result.addElement(new Option(
        "\tThe list of explicit values to use (blank-separated list).\n"
        + "\t(default: none)",
        "list", 1, "-list <values>"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return		the current options
   */
  public String[] getOptions() {
    Vector<String>	result;
    String[]		options;
    int			i;

    result = new Vector<String>();

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    result.add("-list");
    result.add("" + getList());

    return result.toArray(new String[result.size()]);	  
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    
    tmpStr = Utils.getOption("list", options);
    if (tmpStr.length() != 0)
      setList(tmpStr);
    else
      setList("");

    super.setOptions(options);
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String listTipText() {
    return "The blank-separated list of values to use.";
  }

  /**
   * Get the blank-separated list of values.
   *
   * @return 		the list.
   */
  public String getList() {
    return Utils.joinOptions(m_List);
  }
  
  /**
   * Set the blank-separated list of values.
   *
   * @param value 	the list of values.
   */
  public void setList(String value) {
    if (value.length() > 0) {
      try {
	m_List = Utils.splitOptions(value);
      }
      catch (Exception e) {
	e.printStackTrace();
	m_List = new String[0];
      }
    }
    else {
      m_List = new String[0];
    }
  }
  
  /**
   * Returns a string representation of the search parameter.
   * 
   * @return		a string representation
   */
  public String toString() {
    String	result;

    result = super.toString();
    result += ", list: " + getList();
    
    return result;
  }
}