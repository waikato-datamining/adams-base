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
 * iTextPDF.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.UnorderedRange;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.env.Environment;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Helper class for iTextPDF.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class iTextPDF {

  /**
   * Extracts a subset of pages from one PDF file and stores that in another PDF file.
   *
   * @param logging	for logging, can be null
   * @param input	the input PDF
   * @param range	the range of pages to extract
   * @param output	the output PDF
   * @return		null if successful, otherwise error message
   */
  public static String extractPages(LoggingSupporter logging, File input, UnorderedRange range, File output) {
    String		result;
    File		file;
    int			i;
    Document 		document;
    PdfCopy 		copy;
    PdfReader 		reader;
    int[]		pages;
    int			page;
    FileOutputStream 	fos;

    result = null;

    fos = null;
    try {
      if ((logging != null) && logging.isLoggingEnabled())
	logging.getLogger().info("Extracting pages from '" + input + "' into '" + output + "'");
      document = new Document();
      fos      = new FileOutputStream(output.getAbsolutePath());
      copy     = new PdfCopy(document, fos);
      document.open();
      document.addCreationDate();
      document.addCreator(Environment.getInstance().getProject());
      document.addAuthor(System.getProperty("user.name"));
      reader = new PdfReader(input.getAbsolutePath());
      if ((logging != null) && logging.isLoggingEnabled())
	logging.getLogger().info("- #pages: " + reader.getNumberOfPages());
      range.setMax(reader.getNumberOfPages());
      pages = range.getIntIndices();
      for (i = 0; i < pages.length; i++) {
	page = pages[i] + 1;
	copy.addPage(copy.getImportedPage(reader, page));
	if ((logging != null) && logging.isLoggingEnabled())
	  logging.getLogger().info("- adding page: " + page);
      }
      copy.freeReader(reader);
      document.close();
    }
    catch (Exception e) {
      if (logging != null)
	result = LoggingHelper.handleException(logging, "Failed to extract pages from " + input + " to " + output + ": ", e);
      else
        result = "Failed to extract pages from " + input + " to " + output + ":\n" + LoggingHelper.throwableToString(e);
    }
    finally {
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
