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
 * DefaultFlowWriter.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionProducer;
import adams.core.option.CompactFlowProducer;
import adams.core.option.NestedProducer;
import adams.data.io.input.DefaultFlowReader;
import adams.data.io.input.FlowReader;
import adams.flow.core.Actor;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;

/**
 * Writes flows in the default format (nested).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFlowWriter
  extends AbstractNestedFlowWriter
  implements EncodingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2888696337173444983L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to use compact format. */
  protected boolean m_UseCompact;

  /** for formatting dates. */
  protected static DateFormat m_DateFormat;
  static {
    m_DateFormat = new DateFormat("yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes flows in the default format (nested).";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new DefaultFlowReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new DefaultFlowReader().getFormatExtensions();
  }

  /**
   * Returns how to write the data, from a file, stream or writer.
   *
   * @return		how to write the data
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.WRITER;
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

    m_OptionManager.add(
      "use-compact", "useCompact",
      getDefaultUseCompact());
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
    return "The type of encoding to use when writing the file, use empty string for default.";
  }

  /**
   * Returns the default missing value.
   *
   * @return		the default for missing values
   */
  protected boolean getDefaultUseCompact() {
    return true;
  }

  /**
   * Sets whether to use compact format.
   *
   * @param value	true if to use compact format
   */
  public void setUseCompact(boolean value) {
    m_UseCompact = value;
    reset();
  }

  /**
   * Returns whether to use compact format.
   *
   * @return		true if compact format used
   */
  public boolean getUseCompact() {
    return m_UseCompact;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String useCompactTipText() {
    return "If enabled, the compact format is used.";
  }

  /**
   * Turns the nested format into a string.
   *
   * @param nested	the nested format to convert
   * @return		the generated string
   */
  protected String toCompactString(List nested) {
    return new CompactFlowProducer().toString(nested);
  }

  /**
   * Turns the nested format into a string.
   *
   * @param nested	the nested format to convert
   * @return		the generated string
   */
  protected String toNestedString(List nested) {
    return new NestedProducer().toString(nested);
  }

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(List content, File file) {
    String	contentStr;

    if (getUseCompact())
      contentStr = toCompactString(content);
    else
      contentStr = toNestedString(content);

    return FileUtils.writeToFile(file.getAbsolutePath(), contentStr, false, m_Encoding.getValue());
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(List content, Writer writer) {
    String	contentStr;

    if (getUseCompact())
      contentStr = toCompactString(content);
    else
      contentStr = toNestedString(content);

    try {
      writer.write(contentStr);
      writer.flush();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write nested format to writer!", e);
      return false;
    }
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   *
   * @param content	the content to write
   * @param out		the output stream to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(List content, OutputStream out) {
    String	contentStr;

    if (getUseCompact())
      contentStr = toCompactString(content);
    else
      contentStr = toNestedString(content);

    try {
      out.write(contentStr.getBytes());
      out.flush();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write nested format to output stream!", e);
      return false;
    }
  }

  /**
   * Turns the actor into a compact string.
   *
   * @param actor	the actor to convert
   * @return		the
   */
  protected String toCompactString(Actor actor) {
    return AbstractOptionProducer.toString(CompactFlowProducer.class, actor);
  }

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(Actor content, File file) {
    CompactFlowProducer	compact;
    NestedProducer 	nested;

    if (getUseCompact()) {
      compact = new CompactFlowProducer();
      compact.setOutputClasspath(false);
      compact.setEncoding(m_Encoding);
      compact.produce(content);
      return compact.write(file.getAbsolutePath());
    }
    else {
      nested = new NestedProducer();
      nested.setOutputClasspath(false);
      nested.setEncoding(m_Encoding);
      nested.produce(content);
      return nested.write(file.getAbsolutePath());
    }
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the content to write
   * @param writer	the writer to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(Actor content, Writer writer) {
    CompactFlowProducer	compact;
    NestedProducer 	nested;
    String		contentStr;

    if (getUseCompact()) {
      compact = new CompactFlowProducer();
      compact.setOutputClasspath(false);
      compact.setEncoding(m_Encoding);
      compact.produce(content);
      contentStr = compact.toString();
    }
    else {
      nested = new NestedProducer();
      nested.setOutputClasspath(false);
      nested.setEncoding(m_Encoding);
      nested.produce(content);
      contentStr = nested.toString();
    }

    try {
      writer.write(contentStr);
      writer.flush();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write actor to writer!", e);
      return false;
    }
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   *
   * @param content	the content to write
   * @param out		the output stream to write the content to
   * @return		true if successfully written
   */
  protected boolean doWrite(Actor content, OutputStream out) {
    CompactFlowProducer	compact;
    NestedProducer 	nested;
    String		contentStr;

    if (getUseCompact()) {
      compact = new CompactFlowProducer();
      compact.setOutputClasspath(false);
      compact.setEncoding(m_Encoding);
      compact.produce(content);
      contentStr = compact.toString();
    }
    else {
      nested = new NestedProducer();
      nested.setOutputClasspath(false);
      nested.setEncoding(m_Encoding);
      nested.produce(content);
      contentStr = nested.toString();
    }

    try {
      out.write(contentStr.getBytes());
      out.flush();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to writer actor to output stream!", e);
      return false;
    }
  }

  /**
   * Returns the corresponding reader, if available.
   *
   * @return		the reader, null if none available
   */
  @Override
  public FlowReader getCorrespondingReader() {
    DefaultFlowReader	result;

    result = new DefaultFlowReader();
    result.setEncoding(getEncoding());

    return result;
  }
}
