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
 * StringToCustomStringRepresentation.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.gui.goe.CustomStringRepresentationHandler;

/**
 <!-- globalinfo-start -->
 * Turns a String into an object of a class with an associated object editor that has implements adams.gui.goe.CustomStringRepresentationHandler. For instance, all adams.core.base.BaseObject dervied classes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-classname &lt;java.lang.String&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The class to convert the strings to.
 * &nbsp;&nbsp;&nbsp;default: adams.core.base.BaseString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToCustomStringRepresentation
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = -1819666048086043899L;
  
  /** the class to convert the string to. */
  protected String m_Classname;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Turns a String into an object of a class with an associated object "
	+ "editor that has implements " + CustomStringRepresentationHandler.class.getName() 
	+ ". For instance, all " + BaseObject.class.getName() + " dervied classes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classname", "classname",
	    BaseString.class.getName());
  }

  /**
   * Sets the class to convert the strings to.
   *
   * @param value	the class name
   */
  public void setClassname(String value) {
    try {
      Class.forName(value);
    }
    catch (Exception e) {
      return;
    }
    
    m_Classname = value;
    reset();
  }

  /**
   * Returns the class to convert the strings to.
   *
   * @return 		the class name
   */
  public String getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The class to convert the strings to.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  public Class generates() {
    return Object.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    Object		result;
    Class		cls;
    PropertyEditor	editor;
    
    cls    = Class.forName(m_Classname);
    editor = PropertyEditorManager.findEditor(cls);
    if (editor instanceof CustomStringRepresentationHandler) {
      editor.setValue(cls.newInstance());
      result = ((CustomStringRepresentationHandler) editor).fromCustomStringRepresentation((String) m_Input);
    }
    else {
      throw new IllegalStateException("Failed to obtain a " + CustomStringRepresentationHandler.class.getName() + " editor!");
    }
    
    return result;
  }
}
