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
 * Moment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.moments.AbstractBufferedMoment;
import adams.data.image.moments.BIMoment;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class Moment extends AbstractBufferedImageFeatureGenerator {

  protected AbstractBufferedMoment m_BufferedMoment;

  public AbstractBufferedMoment getBufferedMoment() {
    return m_BufferedMoment;
  }

  public void setBufferedMoment(AbstractBufferedMoment m_BufferedMoment) {
    this.m_BufferedMoment = m_BufferedMoment;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("moment", "bufferedMoment", new BIMoment());
  }

  /**
   * Creates the header from a template image.
   *
   * @param img the image to act as a template
   * @return the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;

    result = new HeaderDefinition();
    result.add(m_BufferedMoment.toCommandLine().replace(" ", "").replace(m_BufferedMoment.getClass().getPackage().getName() + ".", ""), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature genration.
   *
   * @param img the image to process
   * @return the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[] result = new List[1];
    result[0] = new ArrayList<>();
    result[0].add(m_BufferedMoment.calculate(img));
    return result;
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return null;
  }
}
