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
 * JPod.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.Range;
import de.intarsys.pdf.content.CSDeviceBasedInterpreter;
import de.intarsys.pdf.content.text.CSTextExtractor;
import de.intarsys.pdf.cos.COSObjectWalkerDeep;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSVisitorException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDObject;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDPageTree;
import de.intarsys.pdf.platform.cwt.image.awt.ImageConverterPdf2Awt;
import de.intarsys.pdf.tools.kernel.PDFGeometryTools;
import de.intarsys.tools.locator.FileLocator;

/**
 * Helper class for jPod (PDF handling).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JPod {

  /**
   * Loads the specified PDF document.
   *
   * @param file	the file to load
   * @return		the PDF document, null in case of an error
   */
  public static PDDocument load(String file) {
    return load(new PlaceholderFile(file));
  }

  /**
   * Loads the specified PDF document.
   *
   * @param file	the file to load
   * @return		the PDF document, null in case of an error
   */
  public static PDDocument load(File file) {
    PDDocument		result;
    FileLocator 	locator;

    locator = new FileLocator(file.getAbsolutePath());
    try {
      result = PDDocument.createFromLocator(locator);
    }
    catch (Exception e) {
      System.err.println("Failed to open PDF file '" + file + "':");
      e.printStackTrace();
      result = null;
    }

    return result;
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
    FileLocator 	locator;

    try {
      locator = new FileLocator(file.getAbsolutePath());
      doc.save(locator, null);
      result = true;
    }
    catch (Exception e) {
      System.err.println("Failed to save PDF document to file '" + file + "':");
      e.printStackTrace();
      result = false;
    }

    return result;
  }

  /**
   * Extracts all text from the specified PDF file.
   *
   * @param file	the PDF file to extract the content from
   * @return		the extracted text, null in case of an error
   */
  public static String extract(File file) {
    return extract(file, new Range(Range.ALL));
  }

  /**
   * Extracts all text from the specified PDF file.
   *
   * @param file	the PDF file to extract the content from
   * @param range	the range of pages to extract
   * @return		the extracted text, null in case of an error
   */
  public static String extract(File file, Range range) {
    int[]			pages;
    PDDocument 			document;
    StringBuilder		result;
    int				i;
    PDPageTree			tree;
    CSTextExtractor 		extractor;
    PDPage 			page;
    AffineTransform 		pageTx;
    CSDeviceBasedInterpreter 	interpreter;

    result = new StringBuilder();

    document = load(file);
    if (document != null) {
      try {
	tree = document.getPageTree();
	range.setMax(tree.getCount());
	pages = range.getIntIndices();
	for (i = 0; i < pages.length; i++) {
	  extractor = new CSTextExtractor();
	  page      = tree.getPageAt(pages[i]);
	  pageTx    = new AffineTransform();
	  PDFGeometryTools.adjustTransform(pageTx, page);
	  extractor.setDeviceTransform(pageTx);
	  interpreter = new CSDeviceBasedInterpreter(null, extractor);
	  interpreter.process(page.getContentStream(), page.getResources());
	  result.append(extractor.getContent());
	  result.append("\n");
	}
      }
      catch (Exception e) {
	result = null;
	System.err.println("Failed to extract text from '" + file + "': ");
	e.printStackTrace();
      }
      close(document);
    }

    if (result != null)
      return result.toString();
    else
      return null;
  }

  /**
   * Extracts all images from the specified PDF file.
   *
   * @param file	the PDF file to extract the images from
   * @return		the extracted images, null in case of an error
   */
  public static List<BufferedImage> extractImages(File file) {
    final List<BufferedImage>	result;
    PDDocument 			document;
    COSObjectWalkerDeep 	walker;
    boolean			failed;

    result   = new ArrayList<BufferedImage>();
    failed   = false;
    document = load(file);
    if (document != null) {
      try {
	walker = new COSObjectWalkerDeep() {
	  @Override
	  public Object visitFromStream(COSStream stream) throws COSVisitorException {
	    if (!PDImage.CN_Subtype_Image.equals(stream.getDict().get(PDObject.DK_Subtype)))
	      return null;
	    PDImage image = (PDImage) PDImage.META.createFromCos(stream);
	    if (image == null)
	      return null;
	    try {
	      ImageConverterPdf2Awt converter = new ImageConverterPdf2Awt(image);
	      BufferedImage bi = converter.getBufferedImage();
	      result.add(bi);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
	};
	document.cosGetDoc().getCatalog().cosGetObject().accept(walker);
      }
      catch (Exception e) {
	System.err.println("Failed to extract text from '" + file + "': ");
	e.printStackTrace();
	failed = true;
      }
      close(document);
    }

    if (failed)
      return null;
    else
      return result;
  }

  /**
   * Determines the page count of a PDF file.
   *
   * @param file	the PDF file to extract the content from
   * @return		the page count, -1 in case of an error
   */
  public static int getPageCount(File file) {
    PDDocument 	document;
    int		result;

    result   = -1;
    document = load(file);
    if (document != null)
      result = document.getPageTree().getCount();
    close(document);

    return result;
  }
}
