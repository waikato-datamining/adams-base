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
 * WekaInstanceToMap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import weka.core.Attribute;
import weka.core.Instance;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns the Weka Instance into a Map, with the attribute names the keys.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaInstanceToMap
  extends AbstractConversion {

  private static final long serialVersionUID = 6188118988692631954L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the Weka Instance into a Map, with the attribute names the keys.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Instance.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Map<String,Object>	result;
    Instance		inst;
    Attribute 		att;
    int			i;

    result = new HashMap<>();
    inst   = (Instance) m_Input;

    for (i = 0; i < inst.numAttributes(); i++) {
      att = inst.attribute(i);
      if (inst.isMissing(i)) {
	result.put(att.name(), null);
      }
      else {
        switch (att.type()) {
	  case Attribute.NUMERIC:
	    result.put(att.name(), inst.value(i));
	    break;
	  case Attribute.NOMINAL:
	  case Attribute.STRING:
	  case Attribute.DATE:
	    result.put(att.name(), inst.stringValue(i));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled attribute type at #" + (i+1) + "/" + att.name() + ": " + Attribute.typeToString(att.type()));
	}
      }
    }

    return result;
  }
}
