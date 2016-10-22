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
 * BIMoment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.moments;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class BICentralMoment extends AbstractBufferedMoment {

  protected int m_P;
  protected int m_Q;

  public int getP() {
    return m_P;
  }

  public void setP(int m_P) {
    this.m_P = m_P;
  }

  public int getQ() {
    return m_Q;
  }

  public void setQ(int m_Q) {
    this.m_Q = m_Q;
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("p", "p", 0);
    m_OptionManager.add("q", "q", 0);
  }

  @Override
  protected double doCalculate(boolean[][] img) {
    return MomentHelper.centralMoment(img, m_P, m_Q);
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
