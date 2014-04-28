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
 * SAXUtils.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.utils;

import weka.core.Statistics;

/**
 * A helper class for SAX
 *
 * @author  dale (dale at waikato dot ac dot nz)
 */

public class SAXUtils {
  /**
   * Piecewise Aggregate Approximation.
   * @param inarray	input array
   * @param numwindows	number of pieces
   * @return		PAA
   */
  public static double[] PAA(double[] inarray, int numwindows){  
    double width=(double)inarray.length/(double)numwindows;
    int whole=(int)Math.floor(width);
    
    double[] ret = new double[numwindows];
    
    int currpos=0;
    double remainder1=0;
    double remainder2=width-(remainder1+whole);
    for (int i=0;i<numwindows;i++){
      
      double sum=remainder1*inarray[currpos];
      if (remainder1 != 0){
	currpos++;
      }
      for (int j=currpos;j<currpos+whole;j++){
	sum+=inarray[j];
      }
      currpos+=whole;
      if (i != numwindows-1){
	sum+=inarray[currpos]*remainder2;
      }
      remainder1=1.0-remainder2;
      whole=(int)Math.floor(width-remainder1);
      remainder2=width-(remainder1+whole);
      ret[i]=sum/width;
    }
    return(ret);
  }
  
  /**
   * Convert a row in original space into SAX labels. Assumes original space has been x normalised (rownorm?).
   * 
   * @param inarray
   * @param numwindows
   * @param bps
   * @return
   */
  public static double[] toSAX(double[] inarray, int numwindows, double[] bps){
    double[] sax=PAA(inarray,numwindows);
    for (int x=0;x<sax.length;x++){
      int saxlabel=bps.length;
      for (int i=0;i<bps.length;i++){	
	if (sax[x] < bps[i] ){
	  saxlabel=i;
	  break;
	}
      }    
      sax[x]=saxlabel;
    }
    return(sax);
  }

  
  /**
   * Returns the maximum Z number for the given bin and breakpoints.
   * Assumes bins numbered from -infinity, starting from 0.
   * @param bin		bin number
   * @param bps		breakpoints
   * @return
   */
  private static double max(int bin, double[] bps){
    if (bin>=bps.length){
      return(Double.POSITIVE_INFINITY);
    }
    return(bps[bin]);
  }
  
  
  /**
   * Returns the minimum Z number for the given bin and breakpoints.
   * Assumes bins numbered from -infinity, starting from 0.
   * @param bin		bin number
   * @param bps		breakpoints
   * @return
   */
  private static double min(int bin, double[] bps){
    if (bin==0){
      return(Double.NEGATIVE_INFINITY);
    }
    return(bps[bin-1]);
  }
  
  /**
   * Calculate the distance matrix for use in the MINDIST function.
   * 
   * @param bps 	breakpoints
   * @return distance matrix
   */
  public static double[][] calcDistMatrix(double[] bps){
    double[][] ret=new double[bps.length+1][bps.length+1];
    for (int r=0;r<bps.length+1;r++){
      for (int c=0;c<bps.length+1;c++){
	if (Math.abs(r-c) <= 1){
	  ret[r][c]=0;
	} else {
	  ret[r][c]=bps[Math.max(r, c)-1] - bps[Math.min(r, c)];	
	}
      }
    }
    return(ret);
  }
  
  /**
   * Calculate the distance between 2 SAX vectors.
   * 
   * @param q		vector 1	
   * @param c		vector 2
   * @param distMatrix	distance matrix
   * @param orig_n	original vector length
   * @return		distance measure
   */
  public static double minDist(double[] q, double[] c, double[][] distMatrix, int orig_n){
    double ret=0;
    for (int i=0;i<c.length;i++){
      ret+=distMatrix[(int)q[i]][(int)c[i]] * distMatrix[(int)q[i]][(int)c[i]];
    }
    return(Math.sqrt(ret) * Math.sqrt((double)orig_n/(double)c.length));
  }
  
  /**
   * Calculate the break points for equal-frequency bins for a gaussian.
   * @param bins	number of bins
   * @return		break points. Z numbers.
   */
  public static double[] calcBreakPoints(int bins){
    double[] ret = new double[bins-1];
    double dbins=bins;
    if (bins % 2==0){ //even
      int num=(int)(dbins-2)/2;
      double width=0.5/(dbins/2.0);
      ret[(int)((dbins-2.0)/2.0)]=0;
      for (int i=1;i<=num;i++){
	ret[(int)((dbins-2.0)/2.0) + i]=Statistics.normalInverse(0.5+(i*width));
	ret[(int)((dbins-2.0)/2.0) - i]=-ret[(int)((dbins-2.0)/2.0) + i];
      }      
    } else { //odd
      int num=(int)(dbins-3)/2;
      double width=1/dbins;
      ret[(int)((dbins-1.0)/2.0)]=Statistics.normalInverse(0.5+(width/2.0));
      ret[(int)((dbins-1.0)/2.0)-1]=-ret[(int)((dbins-1.0)/2.0)];
      for (int i=1;i<=num;i++){
	ret[(int)((dbins-1.0)/2.0) + (i)]=Statistics.normalInverse(0.5+(width/2.0)+(i*width));
	ret[(int)((dbins-1.0)/2.0) - (i+1)]=-ret[(int)((dbins-1.0)/2.0) + i];
      }      
    }
    return(ret);
  }
  /**
   * Runs the algorithm from commandline.
   *
   * @param args	the options
   */
  public static void main(String[] args) {
    double[] get=calcBreakPoints(3);
    get=calcBreakPoints(4);
    //get=calcBreakPoints(7);
    //get=calcBreakPoints(9);
    double[][] get2=calcDistMatrix(get);
    get=calcBreakPoints(5);
  }
}
