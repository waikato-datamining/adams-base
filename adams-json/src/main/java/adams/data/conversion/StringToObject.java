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
 * StringToObject.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.LenientModeSupporter;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseClassname;
import adams.flow.core.Unknown;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 <!-- globalinfo-start -->
 * Converts the JSON string into an object of the specified class.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-classname &lt;adams.core.base.BaseClassname&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The classname to converting the incoming JSON string to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.Unknown
 * </pre>
 *
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, unknown properties won't cause an error.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringToObject
  extends AbstractConversionFromString
  implements LenientModeSupporter {

  private static final long serialVersionUID = -3505835696008632787L;

  /** the class to convert to. */
  protected BaseClassname m_Classname;

  /** whether to use lenient mode. */
  protected boolean m_Lenient;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the JSON string into an object of the specified class using Jackson's Databind functionality (https://github.com/FasterXML/jackson-databind).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classname", "classname",
      new BaseClassname(Unknown.class.getName()));

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "classname", m_Classname);
    result += QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient", ", ");

    return result;
  }

  /**
   * Sets the classname.
   *
   * @param value	the classname
   */
  public void setClassname(BaseClassname value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the classname.
   *
   * @return		the classname
   */
  public BaseClassname getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The classname to converting the incoming JSON string to.";
  }

  /**
   * Sets whether to parse JSON in lenient mode or not.
   * Latter will raise an exception if unknown properties are encountered.
   *
   * @param value 	true if lenient
   */
  @Override
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to parse JSON in lenient mode or not.
   * Latter will raise an exception if unknown properties are encountered.
   *
   * @return 		true if lenient
   */
  @Override
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String lenientTipText() {
    return "If enabled, unknown properties won't cause an error.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    if (m_Classname == null)
      return Object.class;
    else
      return m_Classname.classValue();
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    return new ObjectMapper()
	     .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !m_Lenient)
	     .readerFor(m_Classname.classValue()).readValue((String) m_Input);
  }
}
