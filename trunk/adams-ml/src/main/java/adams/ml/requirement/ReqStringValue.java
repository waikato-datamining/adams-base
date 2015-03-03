package adams.ml.requirement;

import adams.ml.BaseData;

public class ReqStringValue extends ReqString {

  protected String[] m_regex_arr;


  public ReqStringValue(String regex){
    m_regex_arr=new String[1];
    m_regex_arr[0]=regex;
  }
  
  /**
   * Match any one of
   * @param regex
   */
  public ReqStringValue(String[] regex){
    m_regex_arr=regex;
  }

  @Override
  /**
   * Does not need to be present.
   * 
   */
  public String checkRequirement(BaseData bd) {
    if (bd == null || bd.getData() == null){
      return(null);
    }
    String res=super.checkRequirement(bd);
    if (res != null){
      return(res);
    }
    if (m_regex_arr == null){
      return(null);
    }
    
    String val=(String)bd.getData();
    for (String regex:m_regex_arr){
      if (val.matches(regex)){
	return(null);
      }
    }
    return("Failed regex");
  }   

}
