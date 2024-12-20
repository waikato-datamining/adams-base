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
 * TesseractOCR.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.management.ProcessUtils;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.TesseractLanguage;
import adams.flow.core.TesseractPageSegmentation;
import adams.flow.core.Token;
import adams.flow.standalone.TesseractConfiguration;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Applies OCR to the incoming image file using Tesseract.<br>
 * In case of successful OCR, either the file names of the generated files are broadcast or the combined text of the files.<br>
 * NB: The actor deletes all files that have the same prefix as the specified output base. Something you need to be aware of when doing OCR in parallel or generate other files with the same prefix.<br>
 * <br>
 * For more information on tesseract see:<br>
 * https://github.com/tesseract-ocr/tesseract<br>
 * <br>
 * <br>
 * For more information on hOCR see:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;HOCR
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TesseractOCR
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-language &lt;ALBANIAN|ARABIC|AZERBAUIJANI|BULGARIAN|CATALAN|CHEROKEE|CROATION|CZECH|DANISH|DANISH_FRAKTUR|DUTCH|ENGLISH|ESPERANTO|ESTONIAN|FINNISH|FRENCH|GALICIAN|GERMAN|GREEK|HEBREW|HINDI|HUNGARIAN|INDONESIAN|ITALIAN|JAPANESE|KOREAN|LATVIAN|LITHUANIAN|NORWEGIAN|OLD_ENGLISH|OLD_FRENCH|POLISH|PORTUGUESE|ROMANIAN|RUSSIAN|SERBIAN|SIMPLIFIED_CHINESE|SLOVAKIAN|SLOVENIAN|SPANISH|SWEDISH|TAGALOG|TAMIL|TELUGU|THAI|TRADITIONAL_CHINESE|TURKISH|UKRAINIAN|VIETNAMESE&gt; (property: language)
 * &nbsp;&nbsp;&nbsp;The language to use for OCR (must be installed).
 * &nbsp;&nbsp;&nbsp;default: ENGLISH
 * </pre>
 * 
 * <pre>-page-segmentation &lt;OSD_ONLY|AUTO_WITH_OSD|AUTO_NO_OSD|FULL_AUTO_NO_OSD|SINGLE_COLUMN|SINGLE_VERTICAL_BLOCK|SINGLE_BLOCK|SINGLE_LINE|SINGLE_WORD|SINGLE_WORD_CIRCLE|SINGLE_CHARACTER&gt; (property: pageSegmentation)
 * &nbsp;&nbsp;&nbsp;The page segementation to use.
 * &nbsp;&nbsp;&nbsp;default: FULL_AUTO_NO_OSD
 * </pre>
 * 
 * <pre>-output-base &lt;adams.core.io.PlaceholderFile&gt; (property: outputBase)
 * &nbsp;&nbsp;&nbsp;The base name for the generated file(s).
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;outputbase
 * </pre>
 * 
 * <pre>-output-text &lt;boolean&gt; (property: outputText)
 * &nbsp;&nbsp;&nbsp;If enabled, text combined text of all generated files is output rather than 
 * &nbsp;&nbsp;&nbsp;the file names.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator used between the content of two files if text rather than 
 * &nbsp;&nbsp;&nbsp;the file names is forwarded; you can use special characters like \n and 
 * &nbsp;&nbsp;&nbsp;\t as well
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-hocr &lt;boolean&gt; (property: outputHOCR)
 * &nbsp;&nbsp;&nbsp;If enabled, HTML files using the hOCR format are generated rather than ASCII 
 * &nbsp;&nbsp;&nbsp;files.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TesseractOCR
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;
  
  /** the language to use. */
  protected TesseractLanguage m_Language;
  
  /** the page segmentation to use. */
  protected TesseractPageSegmentation m_PageSegmentation;

  /** the output base. */
  protected PlaceholderFile m_OutputBase;

  /** whether to output the OCRed text instead of the files. */
  protected boolean m_OutputText;
  
  /** the separator between multiple text files. */
  protected String m_Separator;
  
  /** whether to output hOCR instead of ASCII. */
  protected boolean m_OutputHOCR;
  
  /** the tesseract connection to use. */
  protected TesseractConfiguration m_Configuration;

  /** for executing tesseract. */
  protected transient CollectingProcessOutput m_ProcessOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies OCR to the incoming image file using Tesseract.\n"
	+ "In case of successful OCR, either the file names of the generated files "
	+ "are broadcast or the combined text of the files.\n"
	+ "NB: The actor deletes all files that have the same prefix as the "
	+ "specified output base. Something you need to be aware of when "
	+ "doing OCR in parallel or generate other files with the same prefix.\n"
	+ "\n"
	+ "For more information on tesseract see:\n"
	+ "https://github.com/tesseract-ocr/tesseract\n\n"
	+ "\n"
	+ "For more information on hOCR see:\n"
	+ "https://en.wikipedia.org/wiki/HOCR";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "language", "language",
	    TesseractLanguage.ENGLISH);

    m_OptionManager.add(
	    "page-segmentation", "pageSegmentation",
	    TesseractPageSegmentation.FULL_AUTO_NO_OSD);

    m_OptionManager.add(
	    "output-base", "outputBase",
	    new PlaceholderFile("${TMP}/outputbase"));

    m_OptionManager.add(
	    "output-text", "outputText",
	    false);

    m_OptionManager.add(
	    "separator", "separator",
	    "");

    m_OptionManager.add(
	    "output-hocr", "outputHOCR",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result  = QuickInfoHelper.toString(this, "language", m_Language, "lang: ");
    result += QuickInfoHelper.toString(this, "pageSegmentation", m_PageSegmentation, ", psm: ");
    result += QuickInfoHelper.toString(this, "outputBase", m_OutputBase, ", out: ");
    value   = QuickInfoHelper.toString(this, "outputText", m_OutputText, "output text", ", ");
    if (value.length() > 0) {
      result += value;
      value = QuickInfoHelper.toString(this, "separator", m_Separator, ", sep: ");
      if (value != null)
	result += value;
    }
    value = QuickInfoHelper.toString(this, "outputHOCR", m_OutputHOCR, "hOCR", ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Sets the language to use (needs to be installed).
   *
   * @param value	the language
   */
  public void setLanguage(TesseractLanguage value) {
    m_Language = value;
    reset();
  }

  /**
   * Returns the language to use.
   *
   * @return		the language
   */
  public TesseractLanguage getLanguage() {
    return m_Language;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String languageTipText() {
    return "The language to use for OCR (must be installed).";
  }

  /**
   * Sets the page segmentation to use.
   *
   * @param value	the page segmentation
   */
  public void setPageSegmentation(TesseractPageSegmentation value) {
    m_PageSegmentation = value;
    reset();
  }

  /**
   * Returns the page segmentation to use.
   *
   * @return		the page segmentation
   */
  public TesseractPageSegmentation getPageSegmentation() {
    return m_PageSegmentation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageSegmentationTipText() {
    return "The page segementation to use.";
  }

  /**
   * Sets the base name for the generated file(s).
   *
   * @param value	the base name
   */
  public void setOutputBase(PlaceholderFile value) {
    m_OutputBase = value;
    reset();
  }

  /**
   * Returns the base name for the generated file(s).
   *
   * @return		the base name
   */
  public PlaceholderFile getOutputBase() {
    return m_OutputBase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputBaseTipText() {
    return "The base name for the generated file(s).";
  }

  /**
   * Sets whether to output the content of all files rather than the files.
   *
   * @param value	true if to output the text
   */
  public void setOutputText(boolean value) {
    m_OutputText = value;
    reset();
  }

  /**
   * Returns whether to output the content of all files rather than the files.
   *
   * @return		true if text is output
   */
  public boolean getOutputText() {
    return m_OutputText;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTextTipText() {
    return 
	"If enabled, text combined text of all generated files is output "
	+ "rather than the file names.";
  }

  /**
   * Sets the separator between text files, in case text is being output rather
   * than file names.
   *
   * @param value	the backquoted separator
   */
  public void setSeparator(String value) {
    m_Separator = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the separator between text files, in case text is being output rather
   * than file names.
   *
   * @return		the backquoted separator
   */
  public String getSeparator() {
    return Utils.backQuoteChars(m_Separator);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return 
	"The separator used between the content of two files if text rather "
	+ "than the file names is forwarded; you can use special characters "
	+ "like \\n and \\t as well";
  }

  /**
   * Sets whether to use hOCR format instead of ASCII.
   *
   * @param value	true if to output hOCR
   */
  public void setOutputHOCR(boolean value) {
    m_OutputHOCR = value;
    reset();
  }

  /**
   * Returns whether to use hOCR format as output instead of ASCII.
   *
   * @return		true if to output hOCR
   */
  public boolean getOutputHOCR() {
    return m_OutputHOCR;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputHOCRTipText() {
    return 
	"If enabled, HTML files using the hOCR format are generated rather "
	+ "than ASCII files.";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Configuration = (TesseractConfiguration) ActorUtils.findClosestType(this, TesseractConfiguration.class);
      if (m_Configuration == null)
	result = "No " + TesseractConfiguration.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_OutputText)
      return new Class[]{String.class};
    else
      return new Class[]{String[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		fileStr;
    File		file;
    String[]		cmd;
    LocalDirectoryLister lister;
    String[]		files;
    StringBuilder	content;
    boolean		first;
    BufferedImage	img;

    result = null;

    fileStr = null;
    file    = null;
    if (m_InputToken.getPayload() instanceof File) {
      fileStr = ((File) m_InputToken.getPayload()).getAbsolutePath();
    }
    else if (m_InputToken.getPayload() instanceof String) {
      fileStr = new PlaceholderFile((String) m_InputToken.getPayload()).getAbsolutePath();
    }
    else {
      try {
	file = TempUtils.createTempFile(getClass().getSimpleName(), ".png");
	fileStr = file.getAbsolutePath();
	img = ((AbstractImageContainer) m_InputToken.getPayload()).toBufferedImage();
    	result = BufferedImageHelper.write(img, file);
      }
      catch (Exception e) {
	result = handleException("Failed to save image to temporary file!", e);
      }
    }

    if (result == null) {
      // delete all files that match the output base
      lister = new LocalDirectoryLister();
      lister.setWatchDir(m_OutputBase.getParentFile().getAbsolutePath());
      lister.setRegExp(new BaseRegExp(m_OutputBase.getName() + ".*"));
      lister.setListFiles(true);
      lister.setListDirs(false);
      lister.setRecursive(false);
      files = lister.list();
      for (String f: files)
	FileUtils.delete(new PlaceholderFile(f));

      cmd = m_Configuration.getCommand(fileStr, m_OutputBase.getAbsolutePath(), m_Language, m_PageSegmentation, m_OutputHOCR);
      try {
	m_ProcessOutput = ProcessUtils.execute(cmd);
	if (m_ProcessOutput.getExitCode() != 0) {
	  result = 
	      "tesseract exited with " + m_ProcessOutput.getExitCode() + "\n"
		  + "cmd: " + OptionUtils.joinOptions(cmd) + "\n"
		  + "stderr:\n" + m_ProcessOutput.getStdErr();
	}
	else {
	  files = lister.list();
	  if (m_OutputText) {
	    content = new StringBuilder();
	    first   = true;
	    for (String f: files) {
	      if (!first) {
		content.append(m_Separator);
		first = false;
	      }
	      content.append(Utils.flatten(FileUtils.loadFromFile(new PlaceholderFile(f)), "\n"));
	    }
	    m_OutputToken = new Token(content.toString());
	  }
	  else {
	    m_OutputToken = new Token(files);
	  }
	}
      }
      catch (Exception e) {
	result = handleException("Failed to execute tesseract: " + OptionUtils.joinOptions(cmd), e);
      }
      m_ProcessOutput = null;
      
      lister = null;
    }
    
    if ((file != null) && file.exists())
      file.delete();

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ProcessOutput != null)
      m_ProcessOutput.destroy();
    super.stopExecution();
  }
}
