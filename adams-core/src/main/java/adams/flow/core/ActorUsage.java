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
 * ActorUsage.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.ClassLister;
import adams.core.base.BaseRegExp;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Traverses directories and loads all flows that it can locate for examination. Generates a spreadsheet containing two columns:<br>
 * 1. actor class<br>
 * 2. all flow files that use this actor
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; [-dir ...] (property: directories)
 * &nbsp;&nbsp;&nbsp;The directories to traverse.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;If enabled, the directories are traversed recursively.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-path &lt;boolean&gt; (property: noPath)
 * &nbsp;&nbsp;&nbsp;If enabled, the path is omitted in the generated spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.SpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer for storing the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the spreadsheet to with the specified writer, ignored 
 * &nbsp;&nbsp;&nbsp;if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorUsage
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6655416366310701302L;

  /** the directories to traverse. */
  protected PlaceholderDirectory[] m_Directories;
  
  /** whether to traverse the directories recursively. */
  protected boolean m_Recursive;
  
  /** whether to omit the path from the flow name. */
  protected boolean m_NoPath;
  
  /** the spreadsheet writer for writing the output. */
  protected SpreadSheetWriter m_Writer;
  
  /** the output file. */
  protected PlaceholderFile m_Output;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Traverses directories and loads all flows that it can locate for "
	+ "examination. Generates a spreadsheet containing two columns:\n"
	+ "1. actor class\n"
	+ "2. all flow files that use this actor";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dir", "directories",
	    new PlaceholderDirectory[0]);

    m_OptionManager.add(
	    "recursive", "recursive",
	    false);

    m_OptionManager.add(
	    "no-path", "noPath",
	    false);

    m_OptionManager.add(
	    "writer", "writer",
	    getDefaultWriter());

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));
  }
  
  /**
   * Sets the directories to traverse.
   *
   * @param value	the directories
   */
  public void setDirectories(PlaceholderDirectory[] value) {
    m_Directories = value;
    reset();
  }

  /**
   * Returns the directories to traverse.
   *
   * @return		the directories
   */
  public PlaceholderDirectory[] getDirectories() {
    return m_Directories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoriesTipText() {
    return "The directories to traverse.";
  }
  
  /**
   * Sets whether to traverse the directories recursively.
   *
   * @param value	true if recursive
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
    reset();
  }

  /**
   * Returns whether to traverse the directories recursively.
   *
   * @return		true if recursive
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "If enabled, the directories are traversed recursively.";
  }
  
  /**
   * Sets whether to omit the path of the flow in the spreadsheet.
   *
   * @param value	true if to omit the path
   */
  public void setNoPath(boolean value) {
    m_NoPath = value;
    reset();
  }

  /**
   * Returns whether to omit the path of the flow in the spreadsheet.
   *
   * @return		true if to omit the path
   */
  public boolean getNoPath() {
    return m_NoPath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noPathTipText() {
    return "If enabled, the path is omitted in the generated spreadsheet.";
  }

  /**
   * Returns the default writer.
   * 
   * @return		the default writer
   */
  protected SpreadSheetWriter getDefaultWriter() {
    return new CsvSpreadSheetWriter();
  }
  
  /**
   * Sets the writer to use.
   *
   * @param value	the writer to use
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the writer in use
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer for storing the spreadsheet.";
  }
  
  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return 
	"The file to write the spreadsheet to with the specified writer, "
	+ "ignored if pointing to a directory.";
  }

  /**
   * Locates all the flows.
   * 
   * @return		the flow files
   */
  protected List<String> locateFlows() {
    List<String>	result;
    LocalDirectoryLister lister;
    int			i;
    
    result = new ArrayList<String>();
    
    for (i = 0; i < m_Directories.length; i++) {
      if (isLoggingEnabled())
	getLogger().info("Traversing '" + m_Directories[i] + "' (recursive=" + m_Recursive + ")...");
      lister = new LocalDirectoryLister();
      lister.setListFiles(true);
      lister.setRegExp(new BaseRegExp(".*\\.flow"));
      lister.setRecursive(m_Recursive);
      lister.setWatchDir(m_Directories[i].getAbsolutePath());
      result.addAll(Arrays.asList(lister.list()));
    }
    
    return result;
  }
  
  /**
   * Analyzes all the flows.
   * 
   * @param flows	the flow files to analyze
   * @return		the generated overview
   */
  protected SpreadSheet analyzeFlows(List<String> flows) {
    SpreadSheet				result;
    HashMap<String,List<String>>	relation;
    int					i;
    String[]				names;
    Row					row;
    List<String>			list;
    Actor				actor;
    List<Actor>				actors;
    int					count;
    String				flowAct;

    // initialize relation
    relation = new HashMap<String,List<String>>();
    names    = ClassLister.getSingleton().getClassnames(Actor.class);
    for (String name: names)
      relation.put(name, new ArrayList<String>());

    // analyze flows
    count = 0;
    for (String flow: flows) {
      count++;
      if (m_NoPath)
	flowAct = new PlaceholderFile(flow).getName();
      else
	flowAct = flow;
      actor = ActorUtils.read(flow);
      if (actor == null) {
	getLogger().warning("Failed to read: " + flow);
      }
      else {
	actors = ActorUtils.enumerate(actor);
	for (i = 0; i < actors.size(); i++) {
	  list = relation.get(actors.get(i).getClass().getName());
	  if (!list.contains(flowAct))
	    list.add(flowAct);
	}
      }
      if (isLoggingEnabled() && (count % 100 == 0))
	getLogger().info("Analyzing flows " + (count + "/" + flows.size()) + "...");
    }
    
    // sort
    for (String name: names)
      Collections.sort(relation.get(name));

    // generate spreadsheet
    if (isLoggingEnabled())
      getLogger().info("Generating spreadsheet...");
    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    row.addCell("C").setContent("Class");
    row.addCell("F").setContent("Flow");
    for (String name: names) {
      list = relation.get(name);
      if (list.size() == 0) {
	row = result.addRow();
	row.addCell("C").setContent(name);
	row.addCell("F").setContent("");
      }
      else {
	for (String flow: list) {
	  row = result.addRow();
	  row.addCell("C").setContent(name);
	  row.addCell("F").setContent(flow);
	}
      }
    }
    
    return result;
  }
  
  /**
   * Generates the actor usage spreadsheet and saves it if possible.
   * 
   * @return		the generated spreadsheet
   */
  public SpreadSheet execute() {
    SpreadSheet		result;
    List<String>	flows;
    
    flows  = locateFlows();
    result = analyzeFlows(flows);
    
    if (result == null) {
      getLogger().severe("Failed to generate spreadsheet!");
    }
    else {
      if (!m_Output.isDirectory()) {
	if (isLoggingEnabled())
	  getLogger().info("Writing spreadsheet to " + m_Output + "...");
	if (!m_Writer.write(result, m_Output))
	  getLogger().severe("Failed to write spreadsheet to: " + m_Output);
	else
	  getLogger().info("Finished writing spreadsheet!");
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No output file provided, not writing spreadsheet to disk.");
      }
    }
    
    return result;
  }
  
  /**
   * Executes the class.
   * 
   * @param args	the command-line options, use "-help" for listing the help
   * @throws Exception	if setting of environment class fails or other problem arises
   */
  public static void main(String[] args) throws Exception {
    if (OptionUtils.helpRequested(args)) {
      System.out.println();
      System.out.println("Usage: " + ActorUsage.class.getName() + " [-env <classname>] [-dir <flow dir> ...] [-recursive] [-no-path] [-writer <classname + options>] [-output <file>]");
      System.out.println();
      return;
    }

    // environment
    String env = OptionUtils.removeOption(args, "-env");
    if (env == null)
      env = Environment.class.getName();
    Class cls = Class.forName(env);
    Environment.setEnvironmentClass(cls);
    
    ActorUsage usage = (ActorUsage) OptionUtils.forName(ActorUsage.class, ActorUsage.class.getName(), args);
    if (usage.execute() == null)
      System.err.println("Failed to generate spreadsheet!");
  }
}
