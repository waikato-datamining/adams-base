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
 * DefaultFlowReader.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads flows in the default format (nested).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFlowReader
  extends AbstractFlowReader
  implements EncodingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4618819455357416453L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads flows in the default format (nested).";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Flow file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"flow"};
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when reading the file, use empty string for default.";
  }

  /**
   * Determines whether the file is in compact format or not.
   *
   * @param file	the file to check
   * @param data	for transferring the data
   * @return		true if in compact format
   */
  protected boolean isCompact(File file, List<String> data) {
    boolean		result;
    List<String>	lines;

    lines  = FileUtils.loadFromFile(file, m_Encoding.getValue());
    result = isCompact(lines);

    // transfer data?
    data.clear();
    if (lines != null)
      data.addAll(lines);

    return result;
  }

  /**
   * Determines whether the lines represent compact format or not.
   *
   * @param lines	the flow
   * @return		true if in compact format
   */
  protected boolean isCompact(List<String> lines) {
    boolean		result;
    int			i;
    int			count;

    result = false;
    count  = 0;
    if (lines != null) {
      for (i = 0; i < lines.size(); i++) {
	if (lines.get(i).startsWith(NestedProducer.COMMENT))
	  continue;
	count++;
	if (lines.get(i).startsWith(" ")) {
	  result = true;
	  break;
	}
      }
    }

    // single actor
    if (count == 1)
      result = true;

    return result;
  }

  /**
   * Reads the actor from the non-compact format.
   *
   * @param lines	the lines to read the actor from
   * @return		the actor, null if failed to read
   */
  protected Actor readNonCompact(List<String> lines) {
    Actor		result;
    NestedConsumer	consumer;

    consumer = new NestedConsumer();
    consumer.setEncoding(m_Encoding);
    result = (Actor) consumer.fromString(Utils.flatten(lines, "\n"));

    // transfer errors/warnings
    m_Errors.addAll(consumer.getErrors());
    m_Warnings.addAll(consumer.getWarnings());

    return result;
  }

  /**
   * Performs the actual reading.
   *
   * @param lines	the flow data
   * @return		the flow or null in case of an error
   */
  protected Node readNode(List<String> lines) {
    Node		result;
    MessageCollection errors;

    if (isCompact(lines)) {
      Utils.removeComments(lines, NestedProducer.COMMENT);
      errors = new MessageCollection();
      result = TreeHelper.buildTree(lines, errors);
      if (!errors.isEmpty())
	m_Errors.addAll(errors.toList());
    }
    else {
      result = TreeHelper.buildTree(readNonCompact(lines));
    }

    return result;
  }

  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   */
  @Override
  protected Node doReadNode(File file) {
    Node		result;
    List<String>	lines;

    lines = new ArrayList<>();
    if (isCompact(file, lines))
      result = readNode(lines);
    else
      result = TreeHelper.buildTree(readNonCompact(lines));

    return result;
  }

  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Node doReadNode(Reader r) {
    List<String>	lines;
    String		line;
    BufferedReader 	reader;

    lines = new ArrayList<>();
    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    try {
      while ((line = reader.readLine()) != null)
	lines.add(line);
    }
    catch (Exception e) {
      m_Errors.add("Failed to read node data from reader:\n" + Utils.throwableToString(e));
      return null;
    }

    return readNode(lines);
  }

  /**
   * Performs the actual reading.
   *
   * @param in		the input stream to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Node doReadNode(InputStream in) {
    return doReadNode(new InputStreamReader(in));
  }

  /**
   * Performs the actual reading.
   *
   * @param lines	the flow data
   * @return		the flow or null in case of an error
   */
  protected Actor readActor(List<String> lines) {
    Actor		result;
    Node		node;
    MessageCollection	errors;

    result = null;
    if (isCompact(lines)) {
      Utils.removeComments(lines, NestedProducer.COMMENT);
      errors = new MessageCollection();
      node   = TreeHelper.buildTree(lines, errors);
      if (!errors.isEmpty())
	m_Errors.addAll(errors.toList());
      if (node != null)
	result = node.getFullActor();
    }
    else {
      result = readNonCompact(lines);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Actor doReadActor(Reader r) {
    List<String>	lines;
    String		line;
    BufferedReader 	reader;

    lines = new ArrayList<>();
    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    try {
      while ((line = reader.readLine()) != null)
	lines.add(line);
    }
    catch (Exception e) {
      m_Errors.add("Failed to read data from reader:\n" + Utils.throwableToString(e));
      return null;
    }

    return readActor(lines);
  }

  /**
   * Performs the actual reading.
   *
   * @param in		the input stream to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Actor doReadActor(InputStream in) {
    return doReadActor(new InputStreamReader(in));
  }

  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   */
  @Override
  protected Actor doReadActor(File file) {
    Actor		result;
    List<String>	lines;

    lines = new ArrayList<>();
    if (isCompact(file, lines))
      result = readActor(lines);
    else
      result = readNonCompact(lines);

    return result;
  }

  /**
   * Returns the corresponding writer, if available.
   *
   * @return		the writer, null if none available
   */
  @Override
  public FlowWriter getCorrespondingWriter() {
    DefaultFlowWriter	result;

    result = new DefaultFlowWriter();
    result.setEncoding(getEncoding());

    return result;
  }
}
