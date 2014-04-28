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
 * ActorExecutionClassProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.ArrayList;
import java.util.List;

import adams.env.Environment;
import adams.flow.core.AbstractActor;

/**
 * Generates a wrapper class for executing an actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorExecutionClassProducer
  extends AbstractActorJavaCodeProducer {

  /** for serialization. */
  private static final long serialVersionUID = -4862444496437534455L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a wrapper class for executing an actor.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"simple-name", "simpleName",
	"Blah");

    m_OptionManager.add(
	"package", "package",
	"adams");
  }

  /**
   * Sets the simple name (without package) of the class to generate.
   *
   * @param value	the simple name
   */
  @Override
  public void setSimpleName(String value) {
    super.setSimpleName(value);
  }

  /**
   * Returns the simple name to use.
   *
   * @return		the simple name
   */
  @Override
  public String getSimpleName() {
    return super.getSimpleName();
  }

  /**
   * Sets the package name of the class to generate.
   *
   * @param value	the package name
   */
  @Override
  public void setPackage(String value) {
    super.setPackage(value);
  }

  /**
   * Returns the package name to use.
   *
   * @return		the package name
   */
  @Override
  public String getPackage() {
    return super.getPackage();
  }

  /**
   * Returns the indentation for code inside the try-catch-block.
   *
   * @return		the indentation string
   */
  @Override
  protected String getIndentation() {
    return "    ";
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

    result.add(AbstractActor.class.getName());

    return result;
  }

  /**
   * Adds the Javadoc for the class.
   */
  @Override
  protected void addClassJavadoc() {
    m_OutputBuffer.append("/**\n");
    m_OutputBuffer.append(" * Wrapper class for executing an actor." + "\n");
    m_OutputBuffer.append(" *\n");
    m_OutputBuffer.append(" * @author " + System.getProperty("user.name") + "\n");
    m_OutputBuffer.append(" * @author " + getClass().getName() + " (code generator)" + "\n");
    m_OutputBuffer.append(" * @version $" + "Revision" + "$\n");
    m_OutputBuffer.append(" */\n");
  }

  /**
   * Adds a method that gets executed just prior to the actors setup and
   * execution.
   */
  protected void addPreExecuteMethod() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Hook method before the actor is executed.\n");
    m_OutputBuffer.append("   * <p/>\n");
    m_OutputBuffer.append("   * Default implementation does nothing.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param actor 		the actor that will get executed.\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void preExecute(AbstractActor actor) throws Exception {\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds a method that executes the actor.
   */
  protected void addDoExecuteMethod() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Performs the actual execution of the actor.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param actor 		the actor that will get executed.\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void doExecute(AbstractActor actor) throws Exception {\n");
    m_OutputBuffer.append("    String result = actor.setUp();\n");
    m_OutputBuffer.append("    if (result != null)\n");
    m_OutputBuffer.append("      throw new Exception(\"Setting up of actor failed: \" + result);\n");
    m_OutputBuffer.append("    result = actor.execute();\n");
    m_OutputBuffer.append("    if (result != null)\n");
    m_OutputBuffer.append("      throw new Exception(\"Execution of actor failed: \" + result);\n");
    m_OutputBuffer.append("    actor.wrapUp();\n");
    m_OutputBuffer.append("    // the following call closes all graphical output (like dialogs and frames)\n");
    m_OutputBuffer.append("    // and frees up memory\n");
    m_OutputBuffer.append("    actor.cleanUp();\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds a method that gets executed just after to the actors execution
   * and cleanUp.
   */
  protected void addPostExecuteMethod() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Hook method after the actor was executed.\n");
    m_OutputBuffer.append("   * <p/>\n");
    m_OutputBuffer.append("   * Default implementation does nothing.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param actor 		the actor that got executed.\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void postExecute(AbstractActor actor) throws Exception {\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Hook-method for adding additional methods necessary to make the class
   * compile.
   */
  @Override
  protected void addAdditionalMethods() {
    // preExecute
    addPreExecuteMethod();
    // doExecute
    addDoExecuteMethod();
    // postExecute
    addPostExecuteMethod();
    // actual execute method
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Executes the actor.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public void execute() throws Exception {\n");
    m_OutputBuffer.append("    AbstractActor actor = getActor();\n");
    m_OutputBuffer.append("    preExecute(actor);\n");
    m_OutputBuffer.append("    doExecute(actor);\n");
    m_OutputBuffer.append("    postExecute(actor);\n");
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
    m_OutputBuffer.append("   * @throws Exception if set up fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public AbstractActor getActor() throws Exception {\n");
    m_OutputBuffer.append("    AbstractArgumentOption    argOption;\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    " + m_Input.getClass().getName() + " " + getOuterVariableName() + " = new " + m_Input.getClass().getName() + "();\n");
    m_OutputBuffer.append("    \n");
  }

  /**
   * Closes the method with the generated code.
   */
  @Override
  protected void addMethodEnd() {
    // getActor - end
    if (m_OutputBuffer.charAt(m_OutputBuffer.length() - 1) != '\n')
      m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    return " + getOuterVariableName() + ";\n");
    m_OutputBuffer.append("  }\n");
  }

  /**
   * Hook method for adding a main method.
   */
  @Override
  protected void addMainMethod() {
    String	inst;

    m_OutputBuffer.append("\n");
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Executes the actor, when started from commandline.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param args ignored\n");
    m_OutputBuffer.append("   * @throws Exception if execution fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  public static void main(String[] args) throws Exception {\n");
    m_OutputBuffer.append("    Environment.setEnvironmentClass(" + Environment.getEnvironmentClass().getName() + ".class);\n");
    inst = m_SimpleName.toLowerCase() + "Inst";
    m_OutputBuffer.append("    " + m_SimpleName + " " + inst + " = new " + m_SimpleName + "();\n");
    m_OutputBuffer.append("    " + inst + ".execute();\n");
    m_OutputBuffer.append("  }\n");
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(ActorExecutionClassProducer.class, args);
  }
}
