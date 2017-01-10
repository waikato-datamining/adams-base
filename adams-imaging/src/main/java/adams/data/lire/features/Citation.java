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
 * Citation.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.lire.features;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;

/**
 * Centralized citation for LIRE.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Citation {

  /**
   * Returns the generic citation for LIRe.
   *
   * @return		the citation information
   */
  public static TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;
    TechnicalInformation additional;

    result = new TechnicalInformation(Type.INPROCEEDINGS);
    result.setValue(Field.AUTHOR, "Mathias Lux and Savvas A. Chatzichristofis");
    result.setValue(Field.TITLE, "LIRE: Lucene Image Retrieval - An Extensible Java CBIR Library");
    result.setValue(Field.BOOKTITLE, "16th ACM International Conference on Multimedia");
    result.setValue(Field.YEAR, "2008");
    result.setValue(Field.PAGES, "1085-1088");
    result.setValue(Field.PUBLISHER, "ACM");
    result.setValue(Field.URL, "http://doi.acm.org/10.1145/1459359.1459577");

    additional = new TechnicalInformation(Type.INPROCEEDINGS);
    additional.setValue(Field.AUTHOR, "Lux, Mathias");
    additional.setValue(Field.TITLE, "Content Based Image Retrieval with LIRe");
    additional.setValue(Field.BOOKTITLE, "19th ACM International Conference on Multimedia");
    additional.setValue(Field.YEAR, "2011");
    additional.setValue(Field.PAGES, "735-738");
    additional.setValue(Field.PUBLISHER, "ACM");
    additional.setValue(Field.URL, "http://doi.acm.org/10.1145/2072298.2072432");
    result.add(additional);

    additional = new TechnicalInformation(Type.BOOK);
    additional.setValue(Field.AUTHOR, "Mathias Lux and Oge Marques");
    additional.setValue(Field.BOOKTITLE, "Visual Information Retrieval using Java and LIRE");
    additional.setValue(Field.YEAR, "2013");
    additional.setValue(Field.PUBLISHER, "Morgan Claypool");
    additional.setValue(Field.ISBN, "9781608459186");
    result.add(additional);

    return result;
  }
}
