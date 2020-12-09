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
 * IterateFilesWithProgressBar.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderDirectory;
import adams.flow.control.ArrayProcess;
import adams.flow.control.CloseCallableDisplay;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.sink.ProgressBar;
import adams.flow.source.FileSystemSearch;
import adams.flow.source.Start;
import adams.flow.source.filesystemsearch.LocalFileSearch;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.ArrayLength;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.IncVariable;
import adams.flow.transformer.PassThrough;
import adams.flow.transformer.SelectArraySubset;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that looks for files in a directory and then iterates those. The progress is displayed using a ProgressBar.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default: process
 * </pre>
 *
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: dir)
 * &nbsp;&nbsp;&nbsp;The directory to search for files matching the regular expression.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;If enabled, the file search is performed recursively.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-select-subset &lt;boolean&gt; (property: selectSubset)
 * &nbsp;&nbsp;&nbsp;If enabled, the user can select a subset of the files.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-files &lt;adams.core.VariableName&gt; (property: numFiles)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the total number of files located.
 * &nbsp;&nbsp;&nbsp;default: num_files
 * </pre>
 *
 * <pre>-file-counter &lt;adams.core.VariableName&gt; (property: fileCounter)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the current count of files iterated.
 * &nbsp;&nbsp;&nbsp;default: file_counter
 * </pre>
 *
 * <pre>-unfold-file-array &lt;boolean&gt; (property: unfoldFileArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the file array gets flattened using the adams.flow.transformer.ArrayToSequence
 * &nbsp;&nbsp;&nbsp;transformer instead of processing files within the adams.flow.control.ArrayProcess
 * &nbsp;&nbsp;&nbsp;control actor.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class IterateFilesWithProgressBar
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the directory to search. */
  protected PlaceholderDirectory m_Dir;

  /** the regular expression to apply. */
  protected BaseRegExp m_RegExp;

  /** whether to search recursively. */
  protected boolean m_Recursive;

  /** whether to let the user select a subset. */
  protected boolean m_SelectSubset;

  /** the number of files variable. */
  protected VariableName m_NumFiles;

  /** the counter variable. */
  protected VariableName m_FileCounter;

  /** whether to unfold the array (ArrayToSequence) or not (ArrayProcess). */
  protected boolean m_UnfoldFileArray;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a sub-flow that looks for files in a directory and then "
        + "iterates those. The progress is displayed using a ProgressBar.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dir", "dir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "recursive", "recursive",
      false);

    m_OptionManager.add(
      "select-subset", "selectSubset",
      false);

    m_OptionManager.add(
      "num-files", "numFiles",
      new VariableName("num_files"));

    m_OptionManager.add(
      "file-counter", "fileCounter",
      new VariableName("file_counter"));

    m_OptionManager.add(
      "unfold-file-array", "unfoldFileArray",
      true);
  }

  /**
   * Returns the default name.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultName() {
    return "process";
  }

  /**
   * Sets the directory to search for files.
   *
   * @param value	the directory
   */
  public void setDir(PlaceholderDirectory value) {
    m_Dir = value;
    reset();
  }

  /**
   * Returns the directory to search for files.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getDir() {
    return m_Dir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String dirTipText() {
    return "The directory to search for files matching the regular expression.";
  }

  /**
   * Sets the regular expressions that the files must match.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the files must match.
   *
   * @return 		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the files must match.";
  }

  /**
   * Sets whether to let the user select a subset.
   *
   * @param value	true if user can select
   */
  public void setSelectSubset(boolean value) {
    m_SelectSubset = value;
    reset();
  }

  /**
   * Returns whether to let the user select a subset.
   *
   * @return 		true if user can select
   */
  public boolean getSelectSubset() {
    return m_SelectSubset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String selectSubsetTipText() {
    return "If enabled, the user can select a subset of the files.";
  }

  /**
   * Sets whether to search for files recursively.
   *
   * @param value	true if recursive
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
    reset();
  }

  /**
   * Returns whether to search for files recursively.
   *
   * @return 		true if recursive
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "If enabled, the file search is performed recursively.";
  }

  /**
   * Sets the variable name to use for the total number of files found.
   *
   * @param value	the variable name
   */
  public void setNumFiles(VariableName value) {
    m_NumFiles = value;
    reset();
  }

  /**
   * Returns the variable name to use for the total number of files found.
   *
   * @return 		the variable name
   */
  public VariableName getNumFiles() {
    return m_NumFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numFilesTipText() {
    return "The name of the variable to use for storing the total number of files located.";
  }

  /**
   * Sets the variable name to use for storing the current count of files iterated.
   *
   * @param value	the variable name
   */
  public void setFileCounter(VariableName value) {
    m_FileCounter = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the current count of files iterated.
   *
   * @return 		the variable name
   */
  public VariableName getFileCounter() {
    return m_FileCounter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String fileCounterTipText() {
    return "The name of the variable to use for storing the current count of files iterated.";
  }

  /**
   * Sets whether to flatten the file array.
   *
   * @param value	true if to unfold
   */
  public void setUnfoldFileArray(boolean value) {
    m_UnfoldFileArray = value;
    reset();
  }

  /**
   * Returns whether to flatten the file array.
   *
   * @return 		true if to unfold
   */
  public boolean getUnfoldFileArray() {
    return m_UnfoldFileArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String unfoldFileArrayTipText() {
    return "If enabled, the file array gets flattened using the "
      + Utils.classToString(ArrayToSequence.class) + " transformer instead of "
      + "processing files within the " + Utils.classToString(ArrayProcess.class)
      + " control actor.";
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return false;
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected Actor doGenerate() {
    Trigger		result;

    result = new Trigger();
    result.setName(m_Name);
    {
      CallableActors callable = new CallableActors();
      result.add(callable);
      {
	ProgressBar progress = new ProgressBar();
	progress.getOptionManager().setVariableForProperty("maximum", m_NumFiles.getValue());
	progress.setName("Progress");
	callable.add(progress);
      }

      result.add(new Start());

      Trigger iterate = new Trigger();
      iterate.setName("iterate");
      result.add(iterate);
      {
	SetVariable reset = new SetVariable();
	reset.setName("reset");
	reset.setVariableValue(new BaseText("0"));
	reset.setVariableName(new VariableName(m_FileCounter.getValue()));
	iterate.add(reset);

	LocalFileSearch local = new LocalFileSearch();
	local.setDirectory(new PlaceholderDirectory(m_Dir));
	local.setRecursive(m_Recursive);
	local.setRegExp(new BaseRegExp(m_RegExp.getValue()));
	FileSystemSearch search = new FileSystemSearch();
	search.setSearch(local);
	search.setOutputArray(true);
	search.setUseForwardSlashes(true);
	iterate.add(search);

	if (m_SelectSubset) {
	  SelectArraySubset select = new SelectArraySubset();
	  select.setAllowSearch(true);
	  iterate.add(select);
	}

	Tee teeNumFiles = new Tee();
	teeNumFiles.setName("# files");
	iterate.add(teeNumFiles);
	{
	  teeNumFiles.add(new ArrayLength());

	  adams.flow.transformer.SetVariable set = new adams.flow.transformer.SetVariable();
	  set.setVariableName(new VariableName(m_NumFiles.getValue()));
	  teeNumFiles.add(set);
	}

	if (m_UnfoldFileArray) {
	  iterate.add(new ArrayToSequence());

	  Tee teeProgress = new Tee();
	  teeProgress.setName("progress");
	  iterate.add(teeProgress);
	  {
	    IncVariable inc = new IncVariable();
	    inc.setVariableName(new VariableName(m_FileCounter.getValue()));
	    inc.setOutputVariableValue(true);
	    teeProgress.add(inc);

	    CallableSink sink = new CallableSink();
	    sink.setCallableName(new CallableActorReference("Progress"));
	    teeProgress.add(sink);
	  }

	  PassThrough pass = new PassThrough();
	  pass.setName("TODO process file");
	  iterate.add(pass);
	}
	else {
	  ArrayProcess process = new ArrayProcess();
	  process.setName("process files");
	  iterate.add(process);
	  {
	    Tee teeProgress = new Tee();
	    teeProgress.setName("progress");
	    process.add(teeProgress);
	    {
	      IncVariable inc = new IncVariable();
	      inc.setVariableName(new VariableName(m_FileCounter.getValue()));
	      inc.setOutputVariableValue(true);
	      teeProgress.add(inc);

	      CallableSink sink = new CallableSink();
	      sink.setCallableName(new CallableActorReference("Progress"));
	      teeProgress.add(sink);
	    }

	    PassThrough pass = new PassThrough();
	    pass.setName("TODO process file");
	    process.add(pass);
	  }
	}
      }

      CloseCallableDisplay close = new CloseCallableDisplay();
      close.setCallableName(new CallableActorReference("Progress"));
      result.add(close);
    }

    return result;
  }
}
