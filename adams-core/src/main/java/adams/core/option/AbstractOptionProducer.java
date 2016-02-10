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
 * AbstractOptionProducer.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import JSci.maths.wavelet.IllegalScalingException;
import adams.core.io.FileEncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.core.management.CharsetHelper;
import adams.env.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Generates output from visiting the options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public abstract class AbstractOptionProducer<O,I>
  extends LoggingObject
  implements OptionProducer<O,I> {

  /** for serialization. */
  private static final long serialVersionUID = 4502704821224667069L;

  /** the top-level visited object. */
  protected OptionHandler m_Input;

  /** whether to use command-line flags or property names. */
  protected boolean m_UsePropertyNames;

  /** the output data. */
  protected transient O m_Output;

  /** the last data structure that was generated. */
  protected I m_LastGenerated;

  /** whether to output the current value instead of variable placeholders. */
  protected boolean m_OutputVariableValues;

  /** top-level properties to skip. */
  protected HashSet<String> m_SkippedProperties;
  
  /**
   * Initializes the visitor.
   */
  public AbstractOptionProducer() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_LoggingLevel         = LoggingLevel.OFF;
    m_UsePropertyNames     = false;
    m_Output               = initOutput();
    m_OutputVariableValues = (this instanceof DebugOptionProducer);
    m_SkippedProperties    = new HashSet<String>();
  }

  /**
   * Resets the members.
   */
  protected void reset() {
    m_Input         = null;
    m_Output        = initOutput();
    m_LastGenerated = null;
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelTipText() {
    return "The level of debugging output (0 = no output).";
  }

  /**
   * Generates a debug string, e.g., based on the method name.
   * <br><br>
   * Default implementation merely returns the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String generateLoggingString(String s) {
    return s;
  }

  /**
   * Sets whether to output the values of options instead of variable
   * placeholders.
   *
   * @param value	if true then the values are output instead of variable placeholders
   */
  public void setOutputVariableValues(boolean value) {
    m_OutputVariableValues = value;
    reset();
  }

  /**
   * Returns whether the values of options are output instead of variable
   * placeholders.
   *
   * @return		true if values are output instead of variable placeholders
   */
  public boolean getOutputVariableValues() {
    return m_OutputVariableValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputVariableValuesTipText() {
    return "Whether to output the values of variables instead of the variable placeholders.";
  }

  /**
   * Sets the top-level properties to skip.
   * 
   * @param value	the properties
   */
  public void setSkippedProperties(HashSet<String> value) {
    m_SkippedProperties = value;
    reset();
  }
  
  /**
   * Returns the skipped top-level properties.
   * 
   * @return		the properties
   */
  public HashSet<String> getSkippedProperties() {
    return m_SkippedProperties;
  }
  
  /**
   * Returns whether property names are used or just the command-line flags.
   *
   * @return		true if property names are used
   */
  public boolean getUsePropertyNames() {
    return m_UsePropertyNames;
  }

  /**
   * Returns the visited top-level object.
   *
   * @return		the visited object
   */
  public OptionHandler getInput() {
    return m_Input;
  }

  /**
   * Returns either the property name or the commandline flag, depending
   * on whether property names are to be used or not.
   *
   * @param option	the option to return the identifier for
   * @return		the identifier
   * @see		#getUsePropertyNames()
   */
  protected String getOptionIdentifier(AbstractOption option) {
    if (getUsePropertyNames())
      return option.getProperty();
    else
      return "-" + option.getCommandline();
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  protected abstract O initOutput();

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   * @see		#initOutput()
   */
  public O getOutput() {
    if (m_Output == null)
      m_Output = initOutput();

    return m_Output;
  }

  /**
   * Returns the current value for the option.
   *
   * @param option	the option to get the current value for
   * @return		the current value (can be array)
   */
  protected Object getCurrentValue(AbstractOption option) {
    return option.getCurrentValue();
  }

  /**
   * Checks whether the value represents the default value for the option.
   *
   * @param option	the option to check the default value for
   * @param value	the (potential) default value
   * @return		true if the value represents the default value
   */
  protected boolean isDefaultValue(AbstractArgumentOption option, Object value) {
    return option.isDefaultValue(value);
  }

  /**
   * Hook method that gets called just before an option gets produced.
   * <br><br>
   * Default implementation does nothing
   *
   * @param manager	the option manager
   * @param index	the index of the option
   */
  protected void preProduce(OptionManager manager, int index) {
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  public abstract I processOption(BooleanOption option);

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  public abstract I processOption(ClassOption option);

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  public abstract I processOption(AbstractArgumentOption option);

  /**
   * Visits the option and obtains information from it.
   *
   * @param option	the current option
   * @return		the last internal data structure that was generated
   */
  public I doProduce(AbstractOption option) {
    if (option instanceof BooleanOption) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("produce/boolean") + ": " + option);
      return processOption((BooleanOption) option);
    }
    else if (option instanceof ClassOption) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("produce/class") + ": " + option);
      return processOption((ClassOption) option);
    }
    else if (option instanceof AbstractArgumentOption){
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("produce/argument") + ": " + option);
      return processOption((AbstractArgumentOption) option);
    }
    else {
      throw new IllegalScalingException("Unhandled type of option: " + option.getClass().getName());
    }
  }

  /**
   * Hook method that gets called just after an option was produced.
   * <br><br>
   * Default implementation does nothing
   *
   * @param manager	the option manager
   * @param index	the index of the option
   */
  protected void postProduce(OptionManager manager, int index) {
  }

  /**
   * Hook method for performing checks on the input. May throw exceptions
   * if object doesn't pass test(s).
   * <br><br>
   * Default implementation does nothing.
   *
   * @param object	the objec to check
   * @return		the checked object
   */
  protected OptionHandler checkInput(OptionHandler object) {
    if (object == null)
      throw new IllegalStateException("Input is null!");
    return object;
  }

  /**
   * Hook-method before starting visiting options.
   * <br><br>
   * Default implementation merely initializes m_Output.
   *
   * @see		#initOutput()
   */
  protected void preProduce() {
    if (m_Output == null)
      m_Output = initOutput();
  }

  /**
   * Visits the options and obtains information from them.
   *
   * @param manager	the manager to traverse
   */
  protected void doProduce(OptionManager manager) {
    List<AbstractOption>	options;
    int				i;

    options = manager.getOptionsList();
    for (i = 0; i < options.size(); i++) {
      // skipped property?
      if (m_SkippedProperties.contains(options.get(i).getProperty()))
	continue;
      
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("preProduce") + ": " + manager.getOwner().getClass().getName());
      preProduce(manager, i);

      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("produce") + ": " + manager.getOwner().getClass().getName());
      m_LastGenerated = doProduce(options.get(i));
      if (isLoggingEnabled())
	getLogger().fine(generateLoggingString("produce") + " --> " + m_LastGenerated);

      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("postProduce") + ": " + manager.getOwner().getClass().getName());
      postProduce(manager, i);
    }
  }

  /**
   * Hook-method after visiting options.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void postProduce() {
  }

  /**
   * Visits the option and obtains information from it.
   *
   * @param visitedObject	the option handler to visit
   * @return			the generated output
   */
  public O produce(OptionHandler visitedObject) {
    reset();
    m_Input = checkInput(visitedObject);

    if (isLoggingEnabled())
      getLogger().info("preVisit: " + m_Input.getClass().getName());
    preProduce();

    if (isLoggingEnabled())
      getLogger().info("doVisit: " + m_Input.getClass().getName());
    doProduce(getInput().getOptionManager());

    if (isLoggingEnabled())
      getLogger().info("postVisit: " + m_Input.getClass().getName());
    postProduce();

    return getOutput();
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public abstract String toString();

  /**
   * Writes the generated content to the specified file.
   *
   * @param filename	the file to write to
   * @return		true if successfully written
   */
  public boolean write(String filename) {
    boolean		result;
    BufferedWriter	writer;
    FileOutputStream    fos;
    Charset		charset;

    charset = null;
    if (this instanceof FileEncodingSupporter)
      charset = ((FileEncodingSupporter) this).getEncoding().charsetValue();
    if (charset == null)
      charset = CharsetHelper.getSingleton().getCharset();

    writer = null;
    fos    = null;
    try {
      fos = new FileOutputStream(filename);
      if (filename.toLowerCase().endsWith(".gz"))
	writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(fos), charset));
      else
	writer = new BufferedWriter(new OutputStreamWriter(fos, charset));
      writer.write(toString());
      writer.newLine();
      writer.flush();
      result = true;
    }
    catch (Exception e) {
      result = false;
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Input = null;
    m_Output        = null;
    m_LastGenerated = null;
  }

  /**
   * Uses the specified producer to generate a string representation of the
   * given option handler.
   *
   * @param cls		the producer class to use
   * @param handler	the option handler to turn into a string representation
   * @return		the string representation, null in case of an error
   */
  public static String toString(Class<? extends OptionProducer> cls, OptionHandler handler) {
    String		result;
    OptionProducer	producer;

    result = null;

    try {
      producer = (OptionProducer) cls.newInstance();
      producer.produce(handler);
      result   = producer.toString();
      producer.cleanUp();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Uses the specified producer to process the option handler and return the
   * generated data structure.
   *
   * @param cls		the producer class to use
   * @param handler	the handler to process
   * @return		the generated data structure
   */
  public static Object produce(Class<? extends OptionProducer> cls, OptionHandler handler) {
    Object		result;
    OptionProducer	producer;

    result = null;

    try {
      producer = (OptionProducer) cls.newInstance();
      producer.produce(handler);
      result = producer.getOutput();
      producer.cleanUp();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Runs an option producer from commandline.
   *
   * @param producer	the producer class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runProducer(Class producer, String[] args) {
    OptionProducer	producerInst;
    OptionHandler	handler;
    String		input;
    String		output;

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	System.out.println("\n");
	System.out.println("-env <environment class>\n");
	System.out.println("-input <file in nested format>\n");
	System.out.println("-output <for storing generated output>\n");
	System.out.println(" if no output file provided, output gets printed on stdout\n");
	System.out.println("\n");
      }
      else {
	Environment.setEnvironmentClass(Class.forName(OptionUtils.removeOption(args, "-env")));
	input = OptionUtils.removeOption(args, "-input");
	if (input == null)
	  throw new IllegalArgumentException("No input file specified!");
	output = OptionUtils.removeOption(args, "-output");
	producerInst = (OptionProducer) OptionUtils.forName(OptionProducer.class, producer.getName(), args);
	handler      = AbstractOptionConsumer.fromFile(NestedConsumer.class, new File(input));
	if (handler == null)
	  throw new IllegalStateException("Failed to read input file: " + input);
	producerInst.produce(handler);
	if (output == null) {
	  System.out.println(producerInst.toString());
	}
	else {
	  if (FileUtils.writeToFile(new File(output).getAbsolutePath(), producerInst.toString(), false))
	    System.out.println("Output written to: " + output);
	  else
	    System.err.println("Failed!");
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * For testing option producers:
   * <ul>
   * 	<li>-input &lt;file in nested format&gt;</li>
   * 	<li>-output &lt;file for generated output&gt;</li>
   * 	<li>-producer &lt;classname (plus options if applicable)&gt;</li>
   * </ul>
   */
  public static void main(String[] args) {
    
  }
}
