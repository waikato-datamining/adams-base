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
 * PublicPrincipalComponents.java
 * Copyright (C) 2014 Dutch Sprouts, Wageningen, NL
 */
package weka.filters.unsupervised.attribute;

import java.util.ArrayList;

import adams.core.License;
import adams.core.annotation.ThirdPartyCopyright;

/**
 * Class that is identical to the Principal components class except it contains a public method to get the coefficients
 * from the principal components model
 * 
 * @author michael.fowke
 * @version $Revision$
 */
@ThirdPartyCopyright(
    author = "Michael Fowke",
    license = License.GPL3,
    copyright = "2014 Dutch Sprouts, Wageningen, NL"
)
public class PublicPrincipalComponents 
  extends PrincipalComponents{

  /** for serialization*/
  private static final long serialVersionUID = -3256644040958902529L;

  /**
   * Get the components from the principal components model
   * 
   * @return		2D array containing the coefficients
   */
  public ArrayList<ArrayList<Double>> getCoefficients() {
    ArrayList<ArrayList<Double>> toReturn = new ArrayList<ArrayList<Double>>();

    double 		cumulative;
    int 		i;
    int 		j;
    double 		coeff_value;
    int			numAttsLowerBound;

    if (m_Eigenvalues == null)
      return null;

    if (m_MaxAttributes > 0)
      numAttsLowerBound = m_NumAttribs - m_MaxAttributes;
    else
      numAttsLowerBound = 0;

    if (numAttsLowerBound < 0)
      numAttsLowerBound = 0;

    //all the coefficients for a single principal component
    ArrayList<Double> onePC;
    cumulative = 0.0;
    //loop through each principle component
    for (i = m_NumAttribs - 1; i >= numAttsLowerBound; i--) {
      onePC = new ArrayList<Double>();

      for(j = 0; j < m_NumAttribs; j++) {	 
	coeff_value = m_Eigenvectors[j][m_SortedEigens[i]];
	onePC.add(coeff_value);
      }

      toReturn.add(onePC);
      cumulative += m_Eigenvalues[m_SortedEigens[i]];

      if ((cumulative / m_SumOfEigenValues) >= m_CoverVariance)
	break;
    }

    return toReturn;
  }
}
