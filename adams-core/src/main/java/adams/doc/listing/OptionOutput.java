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
 * OptionOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.HtmlHelpProducer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionProducer;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Creates a sub-directory for each module and stores the generated output
 * in there, one file per class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OptionOutput
  extends AbstractListingOutput
  implements EncodingSupporter {

  private static final long serialVersionUID = -3904476399937843340L;

  /** the option producer to use. */
  protected OptionProducer m_Producer;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /** the extension to use for the files. */
  protected String m_Extension;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a sub-directory for each module and stores the generated "
      + "output in there, one file per class.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-dir", "outputDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "producer", "producer",
      new HtmlHelpProducer());

    m_OptionManager.add(
      "extension", "extension",
      ".html");

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the output directory.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory.
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
    return "The top-level directory for storing the generated output.";
  }

  /**
   * Sets the producer.
   *
   * @param value	the producer
   */
  public void setProducer(OptionProducer value) {
    m_Producer = value;
    reset();
  }

  /**
   * Returns the producer.
   *
   * @return		the producer
   */
  public OptionProducer getProducer() {
    return m_Producer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String producerTipText() {
    return "The producer to use for generating the output for each class.";
  }

  /**
   * Sets the extension to use.
   *
   * @param value	the extension (incl dot)
   */
  public void setExtension(String value) {
    m_Extension = value;
    reset();
  }

  /**
   * Returns the extension to use.
   *
   * @return		the extension (incl dot)
   */
  public String getExtension() {
    return m_Extension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionTipText() {
    return "The extension to use for generated output files (incl dot).";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "outputDir", m_OutputDir, "dir: ");
    result += QuickInfoHelper.toString(this, "producer", m_Producer, ", producer: ");
    result += QuickInfoHelper.toString(this, "extension", m_Extension, ", ext: ");
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");

    return result;
  }

  /**
   * Outputs the supplied listing.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to output (module -> classnames)
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doGenerate(Class superclass, Map<String, List<String>> listing) {
    String  			result;
    PlaceholderDirectory	subdir;
    PlaceholderFile		outfile;
    OptionHandler		handler;

    result = null;

    if (!m_OutputDir.exists()) {
      if (!m_OutputDir.mkdirs())
        result = "Failed to create output directory: " + m_OutputDir;
    }

    if (result == null) {
      for (String module: getModules(listing)) {
        subdir = new PlaceholderDirectory(m_OutputDir.getAbsolutePath() + File.separator + module);
        if (!subdir.exists()) {
	  if (!subdir.mkdirs()) {
	    result = "Failed to create output directory for module '" + module + "': " + subdir;
	    break;
	  }
	}
	for (String classname: listing.get(module)) {
          outfile = new PlaceholderFile(subdir.getAbsolutePath() + File.separator + classname + m_Extension);
          try {
            handler = (OptionHandler) Class.forName(classname).newInstance();
	    m_Producer.produce(handler);
	    result = FileUtils.writeToFileMsg(outfile.getAbsolutePath(), m_Producer.toString(), false, m_Encoding.getValue());
	    if (result != null)
	      break;
	  }
	  catch (Exception e) {
            result = LoggingHelper.handleException(this, "Failed to generate output for class '" + classname + "'!", e);
	  }
	}
	if (result != null)
	  break;
      }
    }

    return result;
  }
}
