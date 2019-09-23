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
 * ApplyActorProducer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option;

import adams.core.Utils;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a Java class that allows executing/applying an actor as a method.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ApplyActorProducer
  extends AbstractActorJavaCodeProducer {

  private static final long serialVersionUID = 2831286496587052076L;

  /**
   * The type of actor.
   */
  public enum ActorType {
    STANDALONE,
    SOURCE,
    TRANSFORMER,
    SINK
  }

  /** the type of actor. */
  protected ActorType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a Java class that allows executing/applying an actor as a method.";
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
    StringBuilder	result;
    int			i;

    result = new StringBuilder("    ");
    for (i = 0; i < m_Indentation; i++)
      result.append("  ");

    return result.toString();
  }

  /**
   * Hook method that gets called before nesting is increased.
   *
   * @param value	the current object
   */
  protected void preIndent(Object value) {
    m_OutputBuffer.append(getIndentation());
    m_OutputBuffer.append("{\n");
  }

  /**
   * Hook method that gets called just after the nesting got decreased.
   *
   * @param value	the current object
   */
  protected void postIndent(Object value) {
    m_OutputBuffer.append(getIndentation());
    m_OutputBuffer.append("}\n");
  }

  /**
   * Returns other necessary imports.
   *
   * @return		the class names
   */
  @Override
  protected List<String> getRequiredImports() {
    List<String>	result;

    result = new ArrayList<>(super.getRequiredImports());

    result.add(Actor.class.getName());
    result.add(AbstractActor.class.getName());
    if (m_Input instanceof InputConsumer)
      result.add(InputConsumer.class.getName());
    if (m_Input instanceof OutputProducer)
      result.add(OutputProducer.class.getName());
    if (m_Type != ActorType.STANDALONE)
      result.add(Token.class.getName());

    return result;
  }

  /**
   * Adds the start of the enclosing "class" statement.
   * <br><br>
   * Default implementation does not derive the class from another superclass.
   */
  protected void addClassStart() {
    super.addClassStart();
    m_OutputBuffer.append("  /** the actor instance. */\n");
    m_OutputBuffer.append("  protected Actor m_Actor;\n");
    m_OutputBuffer.append("\n");
  }

  /**
   * Adds the method that encloses the generated code.
   */
  @Override
  protected void addMethodStart() {
    // getActor - start
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Used to create an instance of the actor.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @return a suitably configured <code>Actor</code> value\n");
    m_OutputBuffer.append("   * @throws Exception if set up fails\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected Actor configure() throws Exception {\n");
    m_OutputBuffer.append("    AbstractArgumentOption    argOption;\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    if (Environment.getEnvironmentClass() == null)\n");
    m_OutputBuffer.append("      Environment.setEnvironmentClass(adams.env.Environment.class);\n");
    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    " + m_Input.getClass().getName() + " " + getOuterVariableName() + " = new " + m_Input.getClass().getName() + "();\n");
    m_OutputBuffer.append("    \n");
  }

  /**
   * Hook-method for adding additional methods necessary to make the class
   * compile.
   */
  @Override
  protected void addAdditionalMethods() {
    // javadoc
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Applies the actor.\n");
    m_OutputBuffer.append("   *\n");
    switch (m_Type) {
      case TRANSFORMER:
      case SINK:
	m_OutputBuffer.append("   * @param input 	the data for th actor to use as input\n");
        break;
    }
    switch (m_Type) {
      case SOURCE:
      case TRANSFORMER:
    m_OutputBuffer.append("   * @return 		the generated data\n");
        break;
    }
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");

    // method
    switch (m_Type) {
      case STANDALONE:
	m_OutputBuffer.append("  public void apply() throws Exception {\n");
        break;
      case SOURCE:
	m_OutputBuffer.append("  public Object[] apply() throws Exception {\n");
        break;
      case TRANSFORMER:
	m_OutputBuffer.append("  public Object[] apply(Object input) throws Exception {\n");
        break;
      case SINK:
	m_OutputBuffer.append("  public void apply(Object input) throws Exception {\n");
        break;
    }

    // variables
    m_OutputBuffer.append("    String msg;\n");
    switch (m_Type) {
      case SOURCE:
      case TRANSFORMER:
	m_OutputBuffer.append("    List<Object> result;\n");
	break;
    }
    m_OutputBuffer.append("    \n");

    // init actor
    m_OutputBuffer.append("    if (m_Actor == null) {\n");
    m_OutputBuffer.append("      m_Actor = configure();\n");
    m_OutputBuffer.append("      msg = m_Actor.setUp();\n");
    m_OutputBuffer.append("      if (msg != null)\n");
    m_OutputBuffer.append("        throw new Exception(\"Failed to setup actor: \" + msg);\n");
    m_OutputBuffer.append("    }\n");

    // input
    switch (m_Type) {
      case TRANSFORMER:
      case SINK:
	m_OutputBuffer.append("    \n");
	m_OutputBuffer.append("    ((InputConsumer) m_Actor).input(new Token(input));\n");
        break;
    }

    m_OutputBuffer.append("    \n");
    m_OutputBuffer.append("    msg = m_Actor.execute();\n");
    m_OutputBuffer.append("    if (msg != null)\n");
    m_OutputBuffer.append("      throw new Exception(\"Failed to execute actor: \" + msg);\n");

    // output
    switch (m_Type) {
      case SOURCE:
      case TRANSFORMER:
	m_OutputBuffer.append("    \n");
	m_OutputBuffer.append("    result = new ArrayList<>();\n");
	m_OutputBuffer.append("    while (((OutputProducer) m_Actor).hasPendingOutput())\n");
	m_OutputBuffer.append("      result.add(((OutputProducer) m_Actor).output().getPayload());\n");
	m_OutputBuffer.append("    \n");
	m_OutputBuffer.append("    return result.toArray(new Object[0]);\n");
        break;
    }

    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
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
   * Does not generate a main method.
   */
  protected void addMainMethod() {
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    m_Type = null;
    if (ActorUtils.isStandalone((Actor) m_Input))
      m_Type = ActorType.STANDALONE;
    else if (ActorUtils.isSource((Actor) m_Input))
      m_Type = ActorType.SOURCE;
    else if (ActorUtils.isTransformer((Actor) m_Input))
      m_Type = ActorType.TRANSFORMER;
    else if (ActorUtils.isSink((Actor) m_Input))
      m_Type = ActorType.SINK;
    if (m_Type == null)
      throw new IllegalStateException("Failed to determine type of actor: " + Utils.classToString(m_Input));

    super.preProduce();
  }

  /**
   * Executes the producer from commandline.
   *
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(ApplyActorProducer.class, args);
  }
}
