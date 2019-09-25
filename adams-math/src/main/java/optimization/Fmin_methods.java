package optimization;

/** Interface method to define a function to minimize.
 */
public interface Fmin_methods {

   /** Defines a function f : double -> double to minimize.
    * @param x the input x
    * @return the value f(x) of the function applied to x.
    */
   double f_to_minimize(double x);

}
