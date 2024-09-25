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
 * AbstractSpreadSheetWriter.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.AdditionalInformationHandler;
import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 * Ancestor for classes that can write spreadsheet objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpreadSheetWriter
  extends AbstractOptionHandler
  implements SpreadSheetWriter, EncodingSupporter, AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3547064795252689769L;

  /**
   * How to write the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    /** write to a file. */
    FILE,
    /** write using a writer. */
    WRITER,
    /** write to a stream. */
    STREAM
  }

  /** whether the read process was stopped through an external source. */
  protected boolean m_Stopped;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Encoding = new BaseCharset();
  }

  /**
   * Resets the writer.
   */
  @Override
  public void reset() {
    super.reset();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("Supported file extensions: ").append(Utils.flatten(getFormatExtensions(), ", "));
    result.append("\n");
    result.append("Default file extension: ").append(getDefaultFormatExtension());

    return result.toString();
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  @Override
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  @Override
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String encodingTipText() {
    return "The type of encoding to use when writing using a writer, use empty string for default.";
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  protected abstract OutputType getOutputType();

  /**
   * Returns whether to automatically compress.
   * <br><br>
   * Default implementation returns false.
   *
   * @return		true if to automatically decompress
   */
  protected boolean supportsCompressedOutput() {
    return false;
  }

  /**
   * Returns whether the file should get compressed, i.e.,
   * {@link #supportsCompressedOutput()} returns true and the filename ends
   * with ".gz".
   *
   * @param filename	the filename to check
   * @return		true if decompression should occur
   */
  protected boolean canCompress(String filename) {
    return supportsCompressedOutput() && filename.toLowerCase().endsWith(".gz");
  }

  /**
   * Hook method before writing to a file.
   * <br>
   * Default implementation does nothing.
   *
   * @param filename	the filename to check
   */
  protected void preWriteFile(String filename) {
  }

  /**
   * Writes the given content to the specified file.
   * Handles compression automatically, if the filename ends with ".gz",
   * {@link #supportsCompressedOutput()} returns true and file is not
   * being appended.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   * @see		#supportsCompressedOutput()
   * @see		AppendableSpreadSheetWriter
   */
  @Override
  public boolean write(SpreadSheet content, File file) {
    return write(content, file.getAbsolutePath());
  }

  /**
   * Writes the spreadsheet in CSV format to the given file.
   * Handles compression automatically, if the filename ends with ".gz",
   * {@link #supportsCompressedOutput()} returns true and file is not
   * being appended.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   * @see		#supportsCompressedOutput()
   * @see		AppendableSpreadSheetWriter
   */
  @Override
  public boolean write(SpreadSheet content, String filename) {
    boolean			result;
    BufferedWriter		writer;
    OutputStream		output;
    boolean			append;
    AppendableSpreadSheetWriter	appendable;

    preWriteFile(filename);

    append = false;
    if (this instanceof AppendableSpreadSheetWriter) {
      appendable = (AppendableSpreadSheetWriter) this;
      append     = appendable.isAppending();
      if (append) {
        appendable.setFileExists(new File(filename).exists());
        append = appendable.canAppend(content);
      }
    }

    writer = null;
    output = null;
    try {
      switch (getOutputType()) {
        case FILE:
          result = doWrite(content, filename);
          break;
        case STREAM:
          output = new FileOutputStream(filename, append);
          if (!append && canCompress(filename))
            output = new GZIPOutputStream(output);
          result = doWrite(content, output);
          output.flush();
          break;
        case WRITER:
          output = new FileOutputStream(filename, append);
          if (!append && canCompress(filename))
            output = new GZIPOutputStream(output);
          if (m_Encoding != null)
            writer = new BufferedWriter(new OutputStreamWriter(output, m_Encoding.charsetValue()));
          else
            writer = new BufferedWriter(new OutputStreamWriter(output));
          result = doWrite(content, writer);
          writer.flush();
          break;
        default:
          throw new IllegalStateException("Unhandled output type: " + getOutputType());
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write to: " + filename, e);
      result = false;
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(output);
    }

    return result;
  }

  /**
   * Writes the spreadsheet in CSV format to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet content, OutputStream stream) {
    switch (getOutputType()) {
      case FILE:
        throw new IllegalStateException("Only supports writing to files, not output streams!");
      case STREAM:
        return doWrite(content, stream);
      case WRITER:
        return doWrite(content, new OutputStreamWriter(stream));
      default:
        throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }

  /**
   * Writes the spreadsheet in CSV format to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet content, Writer writer) {
    switch (getOutputType()) {
      case FILE:
        throw new IllegalStateException("Only supports writing to files, not output streams!");
      case STREAM:
	try {
	  return doWrite(content, WriterOutputStream.builder().setWriter(writer).get());
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to write to stream!", e);
	  return false;
	}
      case WRITER:
        return doWrite(content, writer);
      default:
        throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }

  /**
   * Performs the actual writing.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet content, String filename) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param out		the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet content, OutputStream out) {
    return false;
  }

  /**
   * Stops the reading (might not be immediate, depending on reader).
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Returns whether the reading was stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(SpreadSheetWriter.class);
  }
}
