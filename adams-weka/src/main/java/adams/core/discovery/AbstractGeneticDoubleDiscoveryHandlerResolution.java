package adams.core.discovery;

/**
 * Created by dale on 13/08/2015.
 */
public abstract class AbstractGeneticDoubleDiscoveryHandlerResolution extends AbstractGeneticDoubleDiscoveryHandler{

  private static final long serialVersionUID = -4401650612139991644L;

  protected int m_Splits;

  /** numbits. */
  protected int m_numBits;
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "splits", "splits",
      getDefaultSplits());
  }

  public static double log2(int n) {
    return (Math.log(n) / Math.log(2));
  }

  protected int calcNumBits(){
    return((int)(Math.floor(log2(m_Splits))+1));
  }

  /**
   * Returns the default splits.
   *
   * @return		the default
   */
  protected abstract int getDefaultSplits();

  /**
   * Sets the splits.
   *
   * @param value	the splits
   */
  public void setSplits(int value) {
    if (getOptionManager().isValid("splits", value)) {
      m_Splits = value;
      m_numBits=calcNumBits();
      reset();
    }
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public int getSplits() {
    return m_Splits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitsTipText() {
    return "The number of doubles to use between max and min.";
  }

  public static double bitsToDouble(String bits, double min, double max, int splits){
    double j=0;

    for (int i=0;i<bits.length();i++) {
      if (bits.charAt(i)=='1') {
	j = j + Math.pow(2, bits.length()-i-1);
      }
    }
    j=Math.min(splits,j);
    return(min+((double)j)*((max-min)/(double)(splits-1)));
  }

  public double bitsToDouble(String bits){
    double j=0;
    for (int i=0;i<bits.length();i++) {
      if (bits.charAt(i)=='1') {
	j = j + Math.pow(2, bits.length()-i-1);
      }
    }
    j=Math.min(j,getSplits());
    return(getMinimum()+((double)j)*((getMaximum()-getMinimum())/(double)(getSplits()-1)));
  }

  protected static int calcNumBits(int num){
    return((int)(Math.floor(log2(num))+1));
  }

  public String doubleToBits(double in){
    double sdist=(getMaximum()-getMinimum())/((double)getSplits()-1);
    double dist=in-getMinimum();
    double rat=dist/sdist;
    int split=(int)Math.round(rat);

    String bits = Integer.toBinaryString(split);
    while (bits.length() < m_numBits){
      bits="0"+bits;
    }
    return(bits);
  }

  protected static int calcNumBits(int min, int max){
    int range=max-min;
    return((int)(Math.floor(log2(range))+1));
  }

  public static String doubleToBits(double in,double min, double max, int splits){
    double sdist=(max-min)/((double)splits-1);
    double dist=in-min;
    double rat=dist/sdist;
    int split=(int)Math.round(rat);

    String bits = Integer.toBinaryString(split);
    while (bits.length() < calcNumBits(splits)){
      bits="0"+bits;
    }
    return(bits);
  }


  public static void main(String[] args) {
    //runGeneticAlgorithm(Environment.class, DarkLord.class, args);
    double i= 12;
    String s=doubleToBits(i,1.0,128.0,25);
    System.err.println(s);
    i=bitsToDouble(s,1.0,128.0,25);
    System.err.println(i);

  }
}
