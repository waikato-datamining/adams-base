package optimization;

import java.io.BufferedReader;
//import corejava.Console;

/**
 *
 *This class tests the Fmin class.
 *<p>
 *Modified by Eric Eaton to use a new Console class as a substitute for corejava.Console.
 *
 *@author Steve Verrill
 *@version .5 --- March 25, 1998
 */


public class FminTest extends Object implements Fmin_methods {

  int id_f_to_min;
  double c,d,e;

  FminTest(int idtemp, double ctemp, double dtemp, double etemp) {

    id_f_to_min = idtemp;
    c = ctemp;
    d = dtemp;
    e = etemp;

  }

  private static class Console {

    public static int readInt(String prompt) {
      try {
	System.out.println(prompt);
	BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
	return Integer.parseInt(reader.readLine());
      } catch (Exception e) {
	System.out.println("Error:  invalid input");
	System.out.println();
	return readInt(prompt);
      }
    }

    public static double readDouble(String prompt) {
      try {
	System.out.println(prompt);
	BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
	return Double.parseDouble(reader.readLine());
      } catch (Exception e) {
	System.out.println("Error:  invalid input");
	System.out.println();
	return readDouble(prompt);
      }
    }
  }

  public static void main (String args[]) {

    int another;
    int idtemp;
    double ctemp,dtemp,etemp;
    double a,b,tol,xmin;

    ctemp = dtemp = etemp = 0.0;

    another = 1;

    while (another == 1) {

/*

   Console is a public domain class described in Cornell
   and Horstmann's Core Java (SunSoft Press, Prentice-Hall).

*/

      idtemp = Console.readInt("\nWhat function do you " +
	"want to minimize?\n\n" +
	"1 -- (x - c)(x - d)\n" +
	"2 -- (x - c)(x - d)(x - e)\n" +
	"3 -- sin(x)\n\n");

      if (idtemp == 1) {

	ctemp = Console.readDouble("\nWhat is the c value?  ");
	dtemp = Console.readDouble("\nWhat is the d value?  ");

      } else if (idtemp == 2) {

	ctemp = Console.readDouble("\nWhat is the c value?  ");
	dtemp = Console.readDouble("\nWhat is the d value?  ");
	etemp = Console.readDouble("\nWhat is the e value?  ");

      }

      FminTest fmintest = new FminTest(idtemp,ctemp,dtemp,etemp);

      a = Console.readDouble("\nWhat is the a value?  ");
      b = Console.readDouble("\nWhat is the b value?  ");
      tol = Console.readDouble("\nWhat is the tol value?  ");

      xmin = Fmin.fmin(a,b,fmintest,tol);

      System.out.print("\nThe xmin value is " + xmin + "\n");

      another = Console.readInt("\nAnother test?" +
	"   0 - no   1 - yes\n\n");

    }

    System.out.print("\n");

  }


  public double f_to_minimize(double x) {

    double f;

    if (id_f_to_min == 1) {

      f = (x - c)*(x - d);

    } else if (id_f_to_min == 2) {

      f = (x - c)*(x - d)*(x - e);

    } else {

      f = Math.sin(x);

    }

    return f;

  }


}
