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
 * PDFBox.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

/**
 * Helper class for PDFBox library.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFBox {

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
      return PDDocument.load(file.getAbsoluteFile());
    }
    catch (Exception e) {
      System.err.println("Failed to load PDF: " + file);
      e.printStackTrace();
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
      System.err.println("Failed to save PDF document to file '" + file + "':");
      e.printStackTrace();
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
      document.print();
      return true;
    }
    catch (Exception e) {
      System.err.println("Failed to print PDF document!");
      e.printStackTrace();
      return false;
    }
  }
}
