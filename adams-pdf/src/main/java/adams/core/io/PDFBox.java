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
 * PDFBox.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.gui.core.ImageManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Helper class for PDFBox library.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PDFBox {

  /** the logger in use. */
  private static Logger LOGGER = LoggingHelper.getLogger(PDFBox.class);

  /**
   * Loads the PDF file.
   *
   * @param filename	the filename of the PDF
   * @return		the PDF document, null if failed to load
   */
  public static PDDocument load(String filename) {
    return load(new PlaceholderFile(filename));
  }

  /**
   * Loads the PDF file.
   *
   * @param file	the PDF file
   * @return		the PDF document, null if failed to load
   */
  public static PDDocument load(File file) {
    try {
      return Loader.loadPDF(new RandomAccessReadBufferedFile((file.getAbsoluteFile())));
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to load PDF: " + file, e);
      return null;
    }
  }

  /**
   * Closes the document again.
   *
   * @param document	the document to close, can be null
   */
  public static void close(PDDocument document) {
    if (document != null) {
      try {
	document.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Saves the PDF document under the specified filename.
   *
   * @param doc		the document to save
   * @param file	the file to save the document to
   * @return		true if successfully saved
   */
  public static boolean save(PDDocument doc, String file) {
    return save(doc, new PlaceholderFile(file));
  }

  /**
   * Saves the PDF document under the specified filename.
   *
   * @param doc		the document to save
   * @param file	the file to save the document to
   * @return		true if successfully saved
   */
  public static boolean save(PDDocument doc, File file) {
    boolean		result;

    try {
      doc.save(file.getAbsoluteFile());
      result = true;
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to save PDF document to file '" + file + "':", e);
      result = false;
    }

    return result;
  }

  /**
   * Prints the PDF document.
   *
   * @param filename	the filename of the PDF to print
   * @return		true if successfully printed
   */
  public static boolean print(String filename) {
    return print(new PlaceholderFile(filename));
  }

  /**
   * Prints the PDF document.
   *
   * @param file	the PDF file print
   * @return		true if successfully printed
   */
  public static boolean print(File file) {
    boolean	result;
    PDDocument	document;

    result   = false;
    document = load(file);
    if (document != null) {
      result = print(document);
      close(document);
    }

    return result;
  }

  /**
   * Prints the PDF document.
   *
   * @param document	the PDF document to print
   * @return		true if successfully printed
   */
  public static boolean print(PDDocument document) {
    try {
      PrinterJob job = PrinterJob.getPrinterJob();
      job.setPageable(new PDFPageable(document));
      job.print();
      return true;
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to print PDF document!", e);
      return false;
    }
  }

  /**
   * Prints the PDF document, bringing up a dialog.
   *
   * @param file	the PDF file print
   * @return		true if successfully printed
   */
  public static boolean printWithDialog(File file) {
    boolean	result;
    PDDocument	document;

    result   = false;
    document = load(file);
    if (document != null) {
      result = printWithDialog(document);
      close(document);
    }

    return result;
  }

  /**
   * Prints the PDF document, bringing up a dialog.
   *
   * @param document	the PDF document to print
   * @return		true if successfully printed
   */
  public static boolean printWithDialog(PDDocument document) {
    try {
      PrinterJob job = PrinterJob.getPrinterJob();
      job.setPageable(new PDFPageable(document));
      if (job.printDialog()) {
	job.print();
	return true;
      }
      else {
	return false;
      }
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to print PDF document!", e);
      return false;
    }
  }

  /**
   * Returns the number of pages in the PDF document.
   *
   * @param file	the PDF file to get the page count for
   * @return		the page count, -1 if failed to determine
   */
  public static int getPageCount(File file) {
    int		result;
    PDDocument 	doc;

    try {
      doc    = Loader.loadPDF(new RandomAccessReadBufferedFile((file.getAbsoluteFile())));
      result = doc.getNumberOfPages();
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to determine page count of: " + file, e);
      result = -1;
    }

    return result;
  }

  /**
   * Extracts the text from the PDF.
   *
   * @param file	the PDF to get the text from
   * @return		the text, null if failed to extract
   */
  public static String extractText(File file) {
    String 		result;
    PDDocument 		doc;
    PDFTextStripper 	stripper;

    doc = null;
    try {
      doc      = Loader.loadPDF(new RandomAccessReadBufferedFile((file.getAbsoluteFile())));
      stripper = new PDFTextStripper();
      result   = stripper.getText(doc);
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to extract text from: " + file, e);
      result = null;
    }
    finally {
      if (doc != null) {
	try {
	  doc.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Extracts the text from the PDF.
   *
   * @param data	the PDF (binary data) to get the text from
   * @return		the text, null if failed to extract
   */
  public static String extractText(byte[] data) {
    String 		result;
    PDDocument 		doc;
    PDFTextStripper 	stripper;

    doc = null;
    try {
      doc      = Loader.loadPDF(new RandomAccessReadBuffer(data));
      stripper = new PDFTextStripper();
      result   = stripper.getText(doc);
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to extract text byte array!", e);
      result = null;
    }
    finally {
      if (doc != null) {
	try {
	  doc.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Extracts images from PDF resources.
   *
   * @param resources		the resources to extract the images from
   * @return			the list of extracted images
   * @throws IOException	if extraction fails
   */
  @MixedCopyright(
      author = "https://stackoverflow.com/users/156669/matt",
      url = "https://stackoverflow.com/a/37664125/4698227",
      license = License.CC_BY_SA_3
  )
  public static List<RenderedImage> extractImagesFromResources(PDResources resources) throws IOException {
    List<RenderedImage> 	result;
    PDXObject 			xObject;

    result = new ArrayList<>();

    if (resources == null)
      return result;

    for (COSName xObjectName : resources.getXObjectNames()) {
      xObject = resources.getXObject(xObjectName);
      if (xObject instanceof PDFormXObject)
	result.addAll(extractImagesFromResources(((PDFormXObject) xObject).getResources()));
      else if (xObject instanceof PDImageXObject)
	result.add(((PDImageXObject) xObject).getImage());
    }

    return result;
  }

  /**
   * Extracts images from a PDF document.
   *
   * @param document		the document to extract the images from
   * @return			the list of extracted images
   * @throws IOException	if extraction fails
   */
  @MixedCopyright(
      author = "https://stackoverflow.com/users/156669/matt",
      url = "https://stackoverflow.com/a/37664125/4698227",
      license = License.CC_BY_SA_3
  )
  public static List<BufferedImage> extractImages(PDDocument document) throws IOException {
    List<BufferedImage> 	result;
    List<RenderedImage> 	images;

    images = new ArrayList<>();
    for (PDPage page : document.getPages()) {
      for (RenderedImage image: extractImagesFromResources(page.getResources())) {
	if (!images.contains(image))
	  images.add(image);
      }
    }

    // convert to BufferedImage
    result = new ArrayList<>();
    for (RenderedImage image : images)
      result.add(ImageManager.toBufferedImage(image));

    return result;
  }

  /**
   * Extracts images from a PDF document.
   *
   * @param file		the file to extract the images from
   * @return			the list of extracted images
   * @throws IOException	if extraction fails
   */
  public static List<BufferedImage> extractImages(File file) throws IOException {
    PDDocument		doc;

    doc = null;
    try {
      doc = Loader.loadPDF(new RandomAccessReadBufferedFile(file.getAbsoluteFile()));
      return extractImages(doc);
    }
    finally {
      try {
        if (doc != null)
          doc.close();
      }
      catch (Exception e) {
        // ignored
      }
    }
  }

  /**
   * Extracts images from a PDF document.
   *
   * @param data		the PDF data to extract the images from
   * @return			the list of extracted images
   * @throws IOException	if extraction fails
   */
  public static List<BufferedImage> extractImages(byte[] data) throws IOException {
    PDDocument		doc;

    doc = null;
    try {
      doc = Loader.loadPDF(new RandomAccessReadBuffer(data));
      return extractImages(doc);
    }
    finally {
      try {
	if (doc != null)
	  doc.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }
}
