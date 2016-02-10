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
 * DefaultFlowWriter.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.FileEncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.option.NestedProducer;
import adams.data.io.input.DefaultFlowReader;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Writes flows in the default format (nested).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFlowWriter
  extends AbstractFlowWriter
  implements FileEncodingSupporter {

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
    return OutputType.FILE;
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
    return false;
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
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(Node content, File file) {
    List<String>	lines;

    if (getUseCompact()) {
      lines = TreeHelper.getCommandLines(content);
      lines.add(0, NestedProducer.COMMENT + " " + NestedProducer.CHARSET + ": " + m_Encoding.charsetValue().name());
      lines.add(0, NestedProducer.COMMENT + " " + NestedProducer.USER + ": " + System.getProperty("user.name"));
      lines.add(0, NestedProducer.COMMENT + " " + NestedProducer.DATE + ": " + m_DateFormat.format(new Date()));
      lines.add(0, NestedProducer.COMMENT + " " + NestedProducer.PROJECT + ": " + Environment.getInstance().getProject());
      return FileUtils.writeToFile(file.getAbsolutePath(), Utils.flatten(lines, "\n"), false, m_Encoding.getValue());
    }
    else {
      return write(content.getFullActor(), file);
    }
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
    NestedProducer	producer;

    if (getUseCompact()) {
      return write(TreeHelper.buildTree((AbstractActor) content), file);
    }
    else {
      producer = new NestedProducer();
      producer.setOutputClasspath(false);
      producer.setEncoding(m_Encoding);
      producer.produce(content);
      return producer.write(file.getAbsolutePath());
    }
  }
}
