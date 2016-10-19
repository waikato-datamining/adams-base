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
 * PDFExport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PdfFont;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.data.PageOrientation;
import adams.flow.transformer.pdfproclet.Image;
import adams.flow.transformer.pdfproclet.PDFGenerator;
import adams.flow.transformer.pdfproclet.PageSize;
import adams.flow.transformer.pdfproclet.PdfProclet;
import adams.flow.transformer.pdfproclet.PlainText;
import adams.flow.transformer.pdfproclet.SpreadSheet;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a PDF from the exported objects (first get turned into files).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFExport
  extends AbstractMultiObjectExportWithPreferredExtensions
  implements FileWriter {

  private static final long serialVersionUID = 360117990418986281L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /** the page size. */
  protected PageSize m_PageSize;

  /** the page orientation. */
  protected PageOrientation m_PageOrientation;

  /** the PDF processors. */
  protected PdfProclet[] m_Proclets;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a PDF from the exported objects (first get turned into files).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output", "outputFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "page-size", "pageSize",
      PageSize.A4);

    m_OptionManager.add(
      "page-orientation", "pageOrientation",
      PageOrientation.PORTRAIT);

    m_OptionManager.add(
      "proclet", "proclets",
      getDefaultProclets());
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return 		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The PDF file to generate.";
  }

  /**
   * Sets the page size.
   *
   * @param value	the size
   */
  public void setPageSize(PageSize value) {
    m_PageSize = value;
    reset();
  }

  /**
   * Returns the page size.
   *
   * @return 		the size
   */
  public PageSize getPageSize() {
    return m_PageSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pageSizeTipText() {
    return "The page size of the generated PDF.";
  }

  /**
   * Sets the page orientation.
   *
   * @param value	the orientation
   */
  public void setPageOrientation(PageOrientation value) {
    m_PageOrientation = value;
    reset();
  }

  /**
   * Returns the page orientation.
   *
   * @return 		the orientation
   */
  public PageOrientation getPageOrientation() {
    return m_PageOrientation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pageOrientationTipText() {
    return "The page orientation of the generated PDF.";
  }

  /**
   * Returns the default proclets to use.
   *
   * @return		the default
   */
  protected PdfProclet[] getDefaultProclets() {
    PlainText		plain;
    SpreadSheet		sheet;
    Image		image;

    plain = new PlainText();
    plain.setAddFilename(true);
    plain.setPageBreakAfter(true);
    plain.setFontContent(new PdfFont("Courier-Normal-12"));

    sheet = new SpreadSheet();
    sheet.setAddFilename(true);
    sheet.setPageBreakAfter(true);

    image = new Image();
    image.setAddFilename(true);
    image.setPageBreakAfter(true);
    image.setScale(0.75);

    return new PdfProclet[]{plain, sheet, image};
  }

  /**
   * Sets the processors for processing the files.
   *
   * @param value	the processors to use
   */
  public void setProclets(PdfProclet[] value) {
    m_Proclets = value;
    reset();
  }

  /**
   * Returns the processors in use.
   *
   * @return 		the processors in use
   */
  public PdfProclet[] getProclets() {
    return m_Proclets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String procletsTipText() {
    return "The processors for processing the files.";
  }

  /**
   * Performs the actual export of the objects using the given names.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(String[] names, Object[] objects) {
    MessageCollection 		errors;
    AbstractObjectExporter 	exporter;
    int				i;
    String			ext;
    PlaceholderFile		file;
    List<File> 			files;
    String			msg;
    File			outputDir;
    PDFGenerator		generator;

    for (i = 0; i < names.length; i++)
      names[i] = FileUtils.createFilename(names[i], "");
    names  = disambiguateNames(names);
    errors = new MessageCollection();

    // temp output dir
    outputDir = TempUtils.createTempFile("zipexport", "");
    if (!outputDir.mkdirs())
      errors.add("Failed to create temporary output directory: " + outputDir);

    files = new ArrayList<>();
    if (errors.isEmpty()) {
      for (i = 0; i < names.length; i++) {
	exporter = determineExporter(names[i], objects[i], errors);
	ext = determineExtension(exporter);
	file = new PlaceholderFile(outputDir.getAbsolutePath() + File.separator + names[i] + "." + ext);
	msg = exporter.export(objects[i], file);
	if (msg != null)
	  errors.add("Failed to find export '" + names[i] + "'/" + Utils.classToString(objects[i].getClass()) + "\n" + msg);
	else
	  files.add(file);
      }
    }

    // create PDF file
    if (errors.isEmpty()) {
      generator = new PDFGenerator();
      generator.setOutput(getOutputFile());
      generator.setPageSize(getPageSize());
      generator.setPageOrientation(getPageOrientation());
      generator.setProclets(getProclets());
      try {
	generator.open();
	for (File f: files) {
	  try {
	    generator.addFile(f);
	  }
	  catch (Exception e) {
	    errors.add("Problems adding file '" + f + "'!", e);
	  }
	}
      }
      catch (Exception e) {
	errors.add("Failed to create PDF: " + m_OutputFile, e);
      }
      finally {
	generator.close();
      }
    }

    if (outputDir.exists())
      FileUtils.delete(outputDir);

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }
}
