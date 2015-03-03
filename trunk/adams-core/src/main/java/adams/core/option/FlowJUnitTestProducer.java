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
 * FlowJUnitTestProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adams.core.base.BaseString;
import adams.env.Environment;

/**
 * Generates a JUnit test case for flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowJUnitTestProducer
  extends AbstractFlowJavaCodeProducer {

  /** for serialization. */
  private static final long serialVersionUID = -422414504795720518L;

  /** the class for which the test is generated. */
  protected Class m_ActorClass;

  /** whether to create a regression test. */
  protected boolean m_HasRegressionTest;

  /** the files to use in the regression test. */
  protected BaseString[] m_RegressionFiles;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a JUnit test case for flows.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"actor-class", "actorClass",
	getDefaultActorClass());

    m_OptionManager.add(
	"has-regression-test", "hasRegressionTest",
	false);

    m_OptionManager.add(
	"regression-file", "regressionFiles",
	new BaseString[]{new BaseString("dumpfile.txt")});
  }

  /**
   * Returns the default actor class.
   * 
   * @return		the default class
   */
  protected String getDefaultActorClass() {
    return adams.flow.sink.Display.class.getName();
  }
  
  /**
   * Sets the class to generate the test for.
   *
   * @param value	the class to generate the test for
   */
  public void setActorClass(String value) {
    try {
      m_ActorClass = Class.forName(value);
      setSimpleName(m_ActorClass.getSimpleName());
      setPackage(m_ActorClass.getPackage().getName());
      reset();
    }
    catch (Exception e) {
      m_ActorClass = adams.flow.sink.Display.class;
      getLogger().severe("Error instantiating class: " + e);
    }
  }

  /**
   * Returns the name of the class to generate the test for.
   *
   * @return		the class
   */
  public String getActorClass() {
    return m_ActorClass.getName();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorClassTipText() {
    return "The name of the actor class that this test is generated for.";
  }

  /**
   * Sets whether to create a regression test as well.
   *
   * @param value	if true then a regression test is generated as well
   */
  public void setHasRegressionTest(boolean value) {
    m_HasRegressionTest = value;
    reset();
  }

  /**
   * Returns whether to generate a regression test as well.
   *
   * @return		true if to create a regression test as well
   */
  public boolean getHasRegressionTest() {
    return m_HasRegressionTest;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hasRegressionTestTipText() {
    return "Whether a method for a regression test should be added.";
  }

  /**
   * Sets the regression files to use.
   *
   * @param value	the files (no path)
   */
  public void setRegressionFiles(BaseString[] value) {
    m_RegressionFiles = value;
    reset();
  }

  /**
   * Returns the regression files to use.
   *
   * @return		the files (no path)
   */
  public BaseString[] getRegressionFiles() {
    return m_RegressionFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regressionFilesTipText() {
    return "The files to use in the regression test (if one is generated).";
  }

  /**
   * Returns other necessary imports.
   *
   * @return		the class names
   */
  @Override
  protected List<String> getRequiredImports() {
    List<String>	result;

    result = new ArrayList<String>(super.getRequiredImports());

    result.add("junit.framework.Test");
    result.add("junit.framework.TestSuite");
    result.add("adams.flow.core.AbstractActor");
    result.add("adams.flow.control.Flow");
    result.add("adams.flow.AbstractFlowTest");
    if (m_HasRegressionTest)
      result.add("adams.test.TmpFile");

    return result;
  }

  /**
   * Returns the indentation for code inside the try-catch-block.
   *
   * @return		the indentation string
   */
  @Override
  protected String getIndentation() {
    return "      ";
  }

  /**
   * Adds the copyright notice.
   */
  @Override
  protected void addCopyright() {
    SimpleDateFormat	yearFormatter;

    m_OutputBuffer.append("/*" + "\n");
    m_OutputBuffer.append(" * " + m_SimpleName + "Test.java" + "\n");
    if (m_Copyright.length() > 0) {
      yearFormatter = new SimpleDateFormat("yyyy");
      m_OutputBuffer.append(" * Copyright (C) " + yearFormatter.format(new Date()) + " " + m_Copyright + "\n");
    }
    m_OutputBuffer.append(" */" + "\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds the Javadoc for the class.
   */
  @Override
  protected void addClassJavadoc() {
    m_OutputBuffer.append("/**\n");
    m_OutputBuffer.append(" * Test for " + m_SimpleName + " actor." + "\n");
    m_OutputBuffer.append(" *\n");
    m_OutputBuffer.append(" * @author " + System.getProperty("user.name") + "\n");
    m_OutputBuffer.append(" * @author " + getClass().getName() + " (code generator)" + "\n");
    m_OutputBuffer.append(" * @version $" + "Revision" + "$\n");
    m_OutputBuffer.append(" */\n");
  }

  /**
   * Adds the start of the enclosing "class" statement.
   */
  @Override
  protected void addClassStart() {
    m_OutputBuffer.append("public class " + m_SimpleName + "Test" + "\n");
    m_OutputBuffer.append("  extends AbstractFlowTest {\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds the constructor.
   */
  @Override
  protected void addConstructor() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Initializes the test.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param name	the name of the test\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public " + m_SimpleName + "Test(String name) {\n");
    m_OutputBuffer.append("    super(name);\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Hook-method for adding additional methods necessary to make the class
   * compile.
   */
  @Override
  protected void addAdditionalMethods() {
    int		i;

    // setUp
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Called by JUnit before each test method.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void setUp() throws Exception {\n");
    m_OutputBuffer.append("    super.setUp();\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    //m_TestHelper.copyResourceToTmp(\"some.csv\");\n");
    if (m_HasRegressionTest) {
      for (i = 0; i < m_RegressionFiles.length; i++)
	m_OutputBuffer.append("    m_TestHelper.deleteFileFromTmp(\"" + m_RegressionFiles[i].getValue() + "\");\n");
    }
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
    // tearDown
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Called by JUnit after each test method.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @throws Exception	if tear-down fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void tearDown() throws Exception {\n");
    m_OutputBuffer.append("    //m_TestHelper.deleteFileFromTmp(\"some.csv\");\n");
    if (m_HasRegressionTest) {
      for (i = 0; i < m_RegressionFiles.length; i++)
	m_OutputBuffer.append("    m_TestHelper.deleteFileFromTmp(\"" + m_RegressionFiles[i].getValue() + "\");\n");
    }
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    super.tearDown();\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
    // testRegression
    if (m_HasRegressionTest) {
      m_OutputBuffer.append("  /**\n");
      m_OutputBuffer.append("   * Performs a regression test, comparing against previously generated output.\n");
      m_OutputBuffer.append("   */\n");
      m_OutputBuffer.append("  public void testRegression() {\n");
      m_OutputBuffer.append("    performRegressionTest(\n");
      m_OutputBuffer.append("        new TmpFile[]{\n");
      for (i = 0; i < m_RegressionFiles.length; i++) {
	m_OutputBuffer.append("          new TmpFile(\"" + m_RegressionFiles[i].getValue() + "\")");
	if (i < m_RegressionFiles.length - 1)
	  m_OutputBuffer.append(",");
	m_OutputBuffer.append("\n");
      }
      m_OutputBuffer.append("        });\n");
      m_OutputBuffer.append("  }\n");
      m_OutputBuffer.append("\n");
    }
    // suite
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * \n");
    m_OutputBuffer.append("   * Returns a test suite.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @return		the test suite\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public static Test suite() {\n");
    m_OutputBuffer.append("    return new TestSuite(" + m_SimpleName + "Test" + ".class);\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds the method that encloses the generated code.
   */
  @Override
  protected void addMethodStart() {
    // getActor - start
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Used to create an instance of a specific actor.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @return a suitably configured <code>AbstractActor</code> value\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public AbstractActor getActor() {\n");
    m_OutputBuffer.append("    AbstractArgumentOption    argOption;\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    Flow " + getOuterVariableName() + " = new Flow();\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    try {\n");
  }

  /**
   * Closes the method with the generated code.
   */
  @Override
  protected void addMethodEnd() {
    // getActor - end
    m_OutputBuffer.append("    }\n");
    m_OutputBuffer.append("    catch (Exception e) {\n");
    m_OutputBuffer.append("      fail(\"Failed to set up actor: \" + e);\n");
    m_OutputBuffer.append("    }\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    return " + getOuterVariableName() + ";\n");
    m_OutputBuffer.append("  }\n");
  }

  /**
   * Hook method for adding a main method.
   */
  @Override
  protected void addMainMethod() {
    m_OutputBuffer.append("\n");
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Runs the test from commandline.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param args	ignored\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public static void main(String[] args) {\n");
    m_OutputBuffer.append("    Environment.setEnvironmentClass(" + Environment.getEnvironmentClass().getName() + ".class);\n");
    m_OutputBuffer.append("    runTest(suite());\n");
    m_OutputBuffer.append("  }\n");
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    m_SimpleName = m_ActorClass.getSimpleName();
    m_Package    = m_ActorClass.getPackage().getName();

    super.preProduce();
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(FlowJUnitTestProducer.class, args);
  }
}
