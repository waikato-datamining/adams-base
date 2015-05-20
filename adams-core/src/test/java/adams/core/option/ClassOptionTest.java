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
 * ClassOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.EnumWithCustomDisplay;
import adams.core.base.BaseString;
import adams.core.option.IntegerOptionTest.IntegerOptionClass;
import adams.env.Environment;


/**
 * Test class for all class options. Run from the command line with: <br><br>
 * java adams.core.option.ClassOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassOptionTest
  extends AbstractArgumentOptionTest {

  /**
   * Enum for testing purposes.
   */
  public enum SimpleEnum {
    FIRST,
    SECOND,
    THIRD
  }

  /**
   * Enum for testing purposes.
   */
  public enum EnumCustomDisplay
    implements EnumWithCustomDisplay<EnumCustomDisplay> {

    FIRST("1st"),
    SECOND("2nd"),
    THIRD("3rd");

    private String m_Display;

    private String m_Raw;

    private EnumCustomDisplay(String display) {
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toString() {
      return toDisplay();
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public EnumCustomDisplay parse(String s) {
      EnumCustomDisplay	result;

      result = null;

      // default parsing
      try {
        result = valueOf(s);
      }
      catch (Exception e) {
        // ignored
      }

      // try display
      if (result == null) {
        for (EnumCustomDisplay ecd: values()) {
  	if (ecd.toDisplay().equals(s)) {
  	  result = ecd;
  	  break;
  	}
        }
      }

      return result;
    }
  }

  /**
   * Dummy class for testing class options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ClassOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 5310369882567819648L;

    protected SimpleEnum m_SimpleEnum;

    protected SimpleEnum[] m_SimpleEnumArray;

    protected EnumCustomDisplay m_EnumCustomDisplay;

    protected EnumCustomDisplay[] m_EnumCustomDisplayArray;

    protected BaseString m_BaseString;

    protected BaseString[] m_BaseStringArray;

    protected IntegerOptionClass m_IntegerOptionClass;

    protected IntegerOptionClass[] m_IntegerOptionClassArray;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "simple-enum", "simpleEnum",
	  SimpleEnum.FIRST);

      m_OptionManager.add(
	  "simple-enum-array", "simpleEnumArray",
	  new SimpleEnum[]{SimpleEnum.FIRST, SimpleEnum.SECOND});

      m_OptionManager.add(
	  "enum-custom", "enumCustomDisplay",
	  EnumCustomDisplay.FIRST);

      m_OptionManager.add(
	  "enum-custom-array", "enumCustomDisplayArray",
	  new EnumCustomDisplay[]{EnumCustomDisplay.FIRST, EnumCustomDisplay.SECOND});

      m_OptionManager.add(
	  "base-string", "baseString",
	  new BaseString("hello"));

      m_OptionManager.add(
	  "base-string-array", "baseStringArray",
	  new BaseString[]{new BaseString("hello"), new BaseString("world")});

      m_OptionManager.add(
	  "integer-option", "integerOptionClass",
	  new IntegerOptionClass());

      m_OptionManager.add(
	  "integer-option-array", "integerOptionClassArray",
	  new IntegerOptionClass[]{new IntegerOptionClass(), new IntegerOptionClass()});
    }

    public void setSimpleEnum(SimpleEnum value) {
      m_SimpleEnum = value;
    }

    public SimpleEnum getSimpleEnum() {
      return m_SimpleEnum;
    }

    public String simpleEnumTipText() {
      return "simpleEnum";
    }

    public void setSimpleEnumArray(SimpleEnum[] value) {
      m_SimpleEnumArray = value;
    }

    public SimpleEnum[] getSimpleEnumArray() {
      return m_SimpleEnumArray;
    }

    public String simpleEnumArrayTipText() {
      return "simpleEnumArray";
    }

    public void setEnumCustomDisplay(EnumCustomDisplay value) {
      m_EnumCustomDisplay = value;
    }

    public EnumCustomDisplay getEnumCustomDisplay() {
      return m_EnumCustomDisplay;
    }

    public String enumCustomDisplayTipText() {
      return "enumCustomDisplay";
    }

    public void setEnumCustomDisplayArray(EnumCustomDisplay[] value) {
      m_EnumCustomDisplayArray = value;
    }

    public EnumCustomDisplay[] getEnumCustomDisplayArray() {
      return m_EnumCustomDisplayArray;
    }

    public String enumCustomDisplayArrayTipText() {
      return "enumCustomDisplayArray";
    }

    public void setBaseString(BaseString value) {
      m_BaseString = value;
    }

    public BaseString getBaseString() {
      return m_BaseString;
    }

    public String baseStringTipText() {
      return "baseString";
    }

    public void setBaseStringArray(BaseString[] value) {
      m_BaseStringArray = value;
    }

    public BaseString[] getBaseStringArray() {
      return m_BaseStringArray;
    }

    public String baseStringArrayTipText() {
      return "baseStringArray";
    }

    public void setIntegerOptionClass(IntegerOptionClass value) {
      m_IntegerOptionClass = value;
    }

    public IntegerOptionClass getIntegerOptionClass() {
      return m_IntegerOptionClass;
    }

    public String integerOptionClassTipText() {
      return "integerOptionClass";
    }

    public void setIntegerOptionClassArray(IntegerOptionClass[] value) {
      m_IntegerOptionClassArray = value;
    }

    public IntegerOptionClass[] getIntegerOptionClassArray() {
      return m_IntegerOptionClassArray;
    }

    public String integerOptionClassArrayTipText() {
      return "integerOptionClassArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ClassOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new ClassOptionClass();
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ClassOptionTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
