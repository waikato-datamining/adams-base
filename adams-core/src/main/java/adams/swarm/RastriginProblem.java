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
 * RastriginProblem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.swarm;

import adams.env.Environment;
import org.jblas.DoubleMatrix;

/**
 * Concrete example for Cat Swarm Optimizations (CSO): Rastrigin problem
 *
 * See <a href="https://en.wikipedia.org/wiki/Rastrigin_function" target="_blank">here</a>
 *
 * @author Mike Mayo (mmayo at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RastriginProblem extends CSO {

  /*
   * Problem-Specific Parameters
   *
   */
  protected int probDimensions = 30;
  protected double probMaxValue = 5.12;
  protected double probMinValue = -5.12;


  /*
   * Implementation of random particle generator
   *
   */
  @Override
  public DoubleMatrix randomParticle() {
    DoubleMatrix particle=DoubleMatrix.rand(1,probDimensions);
    particle.muli(probMaxValue-probMinValue);
    particle.addi(probMinValue);
    return particle;
  }

  /*
   * Implementation of fitness function for the rastrigin problem
   *
   */
  @Override
  public double particleFitness(DoubleMatrix particle){
    double result=10*probDimensions;
    for (int i=0;i<probDimensions;i++) {
      double x=particle.get(0,i);
      result += x*x;
      result -= 10*Math.cos(2*Math.PI*x);
    }
    return result;
  }

  /*
   * Stringifier
   *
   */
  @Override
  public String toString(){
    String result=super.toString();
    result += "\nprobDimensions  = "+probDimensions;
    result += "\nprobMaxValue    = "+probMaxValue;
    result += "\nprobMinValue    = "+probMinValue;
    return result;
  }

  /*
   * Launcher method
   * -- load and set parameters and then call run()
   *
   */
  public static void main(String[] args){
    Environment.setEnvironmentClass(Environment.class);
    new RastriginProblem().run();
  }
}
