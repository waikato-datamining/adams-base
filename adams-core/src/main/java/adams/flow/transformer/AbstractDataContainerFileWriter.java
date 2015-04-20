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
 * AbstractDataContainerFileWriter.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.CompressionSupporter;
import adams.data.DataUtils;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.MetaFileWriter;
import adams.flow.core.Token;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Abstract ancestor for transformer actors that write data containers
 * to disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to write to disk
 */
public abstract class AbstractDataContainerFileWriter<T extends DataContainer>
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2589436559371405252L;

  /** the writer to use. */
  protected AbstractDataContainerWriter<T> m_Writer;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    getDefaultWriter());

    m_OptionManager.add(
	    "dir", "outputDir",
	    new PlaceholderDirectory("."));
  }

  /**
   * Returns the default writer to use.
   *
   * @return		the default writer
   */
  protected abstract AbstractDataContainerWriter<T> getDefaultWriter();

  /**
   * Sets the writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(AbstractDataContainerWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the writer
   */
  public AbstractDataContainerWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for saving the data.";
  }

  /**
   * Sets the output directory for the spectrums.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the current output directory for the spectrums.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The output directory for the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  protected abstract Class getDataContainerClass();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the data type
   */
  public Class[] accepts() {
    Class	cls;

    cls = Array.newInstance(getDataContainerClass(), 0).getClass();

    return new Class[]{cls.getComponentType(), cls};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Returns the default extension to use if none can be found (excl dot).
   * <br>
   * Default implementation returns empty string.
   */
  protected String getDefaultExtension() {
    return "";
  }

  /**
   * Performs the actual writing.
   * 
   * @param conts	the container array (if an array was received)
   * @param cont	the container (if a single container was received)
   * @return		null if everything is fine, otherwise error message
   */
  protected String doWrite(T[] conts, T cont) {
    String		result;
    PlaceholderFile	file;
    boolean		success;
    String[]		ext;
    String              actualExt;

    result = null;

    // extensions
    if (m_Writer instanceof MetaFileWriter)
      ext = ((MetaFileWriter) m_Writer).getActualFormatExtensions();
    else
      ext = m_Writer.getFormatExtensions();

    // determine extension to use
    actualExt = getDefaultExtension();
    for (String e: ext) {
      if (e.equals("*"))
        continue;
      actualExt = e;
      break;
    }
    if (!actualExt.isEmpty() && !actualExt.startsWith("."))
      actualExt = "." + actualExt;
    
    // setup writer
    if (m_Writer.isOutputFile()) {
      // gzip compression?
      if ((m_Writer instanceof CompressionSupporter) && ((CompressionSupporter) m_Writer).getUseCompression())
	file = new PlaceholderFile(DataUtils.createFilename(m_OutputDir, (Object) cont, actualExt + ".gz"));
      else
	file = new PlaceholderFile(DataUtils.createFilename(m_OutputDir, (Object) cont, actualExt));
    }
    else {
      file = new PlaceholderFile(DataUtils.createFilename(m_OutputDir, (Object) cont, null));
    }
    m_Writer.setOutput(file);

    // write data
    try {
      if (conts != null)
	success = m_Writer.write(Arrays.asList(conts));
      else
	success = m_Writer.write(cont);

      if (!success)
	result = "Failed to write data to '" + file + "'!";

      if (isLoggingEnabled()) {
	if (conts != null) {
	  getLogger().info(conts.length + " containers written to " + file);
	}
	else {
	  if (cont instanceof DatabaseIDHandler)
	    getLogger().info("Container #" + ((DatabaseIDHandler) cont).getDatabaseID() + " written to " + file);
	  else
	    getLogger().info("Container " + cont + " written to " + file);
	}
      }
      m_Writer.cleanUp();
      if (success)
	m_OutputToken = new Token(file.getAbsolutePath());
    }
    catch (Exception e) {
      result = handleException("Error writing container(s) to '" + file + "': ", e);
      return result;
    }

    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    T			cont;
    T[]			conts;

    if (m_InputToken.getPayload().getClass().isArray()) {
      conts = (T[]) m_InputToken.getPayload();
      cont  = conts[0];
    }
    else {
      conts = null;
      cont  = (T) m_InputToken.getPayload();
    }
    
    result = doWrite(conts, cont);

    return result;
  }
}
