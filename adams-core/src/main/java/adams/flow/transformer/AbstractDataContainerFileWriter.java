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
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.CompressionSupporter;
import adams.data.DataUtils;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.MetaFileWriter;
import adams.flow.core.Token;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Abstract ancestor for transformer actors that write data containers
 * to disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to write to disk
 */
public abstract class AbstractDataContainerFileWriter<T extends DataContainer>
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2589436559371405252L;

  public enum FileNameGeneration {
    /** Use either ID or database ID. */
    AUTOMATIC,
    /** Use the database ID. */
    DATABASE_ID,
    /** use the container's ID (but ensures that it is a valid filename). */
    ID,
    /** use the specified name (without path). */
    SUPPLIED
  }

  /** the writer to use. */
  protected AbstractDataContainerWriter<T> m_Writer;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /** how to generate the filename. */
  protected FileNameGeneration m_FileNameGeneration;

  /** the supplied filename. */
  protected String m_SuppliedFileName;

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

    m_OptionManager.add(
      "file-name-generation", "fileNameGeneration",
      FileNameGeneration.AUTOMATIC);

    m_OptionManager.add(
      "supplied-file-name", "suppliedFileName",
      getDefaultSuppliedFileName());
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
   * Sets how to generate the filename.
   *
   * @param value	the generation
   */
  public void setFileNameGeneration(FileNameGeneration value) {
    m_FileNameGeneration = value;
    reset();
  }

  /**
   * Returns how to generate the file name.
   *
   * @return		the generation
   */
  public FileNameGeneration getFileNameGeneration() {
    return m_FileNameGeneration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileNameGenerationTipText() {
    return "Defines how to generate the file name.";
  }

  /**
   * The default for the supplied file name.
   *
   * @return		the default
   */
  protected String getDefaultSuppliedFileName() {
    return "out." + getDefaultExtension();
  }

  /**
   * Sets the filename to use when set to {@link FileNameGeneration#SUPPLIED}.
   * Extension included.
   *
   * @param value	the generation
   */
  public void setSuppliedFileName(String value) {
    m_SuppliedFileName = value;
    reset();
  }

  /**
   * Returns the filename to use when set to {@link FileNameGeneration#SUPPLIED}.
   * Extension included.
   *
   * @return		the generation
   */
  public String getSuppliedFileName() {
    return m_SuppliedFileName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppliedFileNameTipText() {
    return "The file name (without path) to use when using " + FileNameGeneration.SUPPLIED + " (including extension).";
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
  @Override
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
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Returns the default extension to use if none can be found (excl dot).
   * <br>
   * Default implementation returns the default extension of the default writer.
   */
  protected String getDefaultExtension() {
    return getDefaultWriter().getDefaultFormatExtension();
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
    boolean		useCompression;

    result = null;

    // extensions
    if (m_Writer instanceof MetaFileWriter)
      ext = ((MetaFileWriter) m_Writer).getActualFormatExtensions();
    else
      ext = m_Writer.getFormatExtensions();

    // determine extension to use
    actualExt = getDefaultExtension();
    if (actualExt.endsWith("*"))
      actualExt = "";
    for (String e: ext) {
      if (e.equals("*"))
        continue;
      actualExt = e;
      break;
    }
    if (!actualExt.isEmpty() && !actualExt.startsWith("."))
      actualExt = "." + actualExt;
    
    // gzip compression?
    useCompression = ((m_Writer instanceof CompressionSupporter) && ((CompressionSupporter) m_Writer).getUseCompression());
    if (useCompression)
      actualExt += ".gz";

    // filename
    switch (m_FileNameGeneration) {
      case AUTOMATIC:
	if (m_Writer.isOutputFile())
	  file = new PlaceholderFile(DataUtils.createFilename(m_OutputDir, (Object) cont, actualExt));
	else
	  file = new PlaceholderFile(DataUtils.createFilename(m_OutputDir, (Object) cont, null));
	break;
      case DATABASE_ID:
	if (m_Writer.isOutputFile())
	  file = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + ((DatabaseIDHandler) cont).getDatabaseID() + actualExt);
	else
	  file = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + ((DatabaseIDHandler) cont).getDatabaseID());
	break;
      case ID:
	if (m_Writer.isOutputFile())
	  file = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + FileUtils.createFilename(cont.getID(), "_") + actualExt);
	else
	  file = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + FileUtils.createFilename(cont.getID(), "_"));
	break;
      case SUPPLIED:
	file = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + m_SuppliedFileName);
	break;
      default:
	throw new IllegalStateException("Unhandled file name generation: " + m_FileNameGeneration);
    }

    // setup writer
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

    if (m_InputToken.isArray()) {
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
