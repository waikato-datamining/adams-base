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
 * PDFCloseDocumentTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.source.PDFNewDocument;
import adams.flow.transformer.PDFAppendDocument;
import adams.flow.transformer.pdfproclet.PdfProclet;
import adams.flow.transformer.pdfproclet.SpreadSheet;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for PDFCloseDocument actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class PDFCloseDocumentTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PDFCloseDocumentTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("out.pdf");
    m_TestHelper.copyResourceToTmp("bolts_small.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("out.pdf");
    m_TestHelper.deleteFileFromTmp("bolts_small.csv");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PDFCloseDocumentTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.PDFNewDocument
      PDFNewDocument pdfnewdocument = new PDFNewDocument();
      argOption = (AbstractArgumentOption) pdfnewdocument.getOptionManager().findByProperty("outputFile");
      pdfnewdocument.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/out.pdf"));
      actors.add(pdfnewdocument);

      // Flow.PDFAppendDocument
      PDFAppendDocument pdfappenddocument = new PDFAppendDocument();
      List<PdfProclet> proclets = new ArrayList<>();
      SpreadSheet spreadsheet = new SpreadSheet();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(defaultspreadsheet);

      spreadsheet.setReader(csvspreadsheetreader);

      proclets.add(spreadsheet);
      pdfappenddocument.setProclets(proclets.toArray(new PdfProclet[0]));

      argOption = (AbstractArgumentOption) pdfappenddocument.getOptionManager().findByProperty("inputFile");
      pdfappenddocument.setInputFile((PlaceholderFile) argOption.valueOf("${TMP}/bolts_small.csv"));
      actors.add(pdfappenddocument);

      // Flow.PDFCloseDocument
      PDFCloseDocument pdfclosedocument = new PDFCloseDocument();
      actors.add(pdfclosedocument);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

