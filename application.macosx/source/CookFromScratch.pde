class CookFromScratch {
  String dinner;
  String[] meatleft;
  String[] meatright;
  String[] potatoesleft;
  String[] potatoesright;
  
 CookFromScratch(String dinner){
    dinner = dinner;
    String veggieleft = split(dinner, "=")[0];
    String veggieright = split(dinner, "=")[1];
    potatoesleft = new String[0];
    potatoesright = new String[0];
    meatleft = splitTokens( veggieleft , "+-");
    meatleft = trim(meatleft);
    meatright = splitTokens( veggieright , "+-");
    meatright = trim(meatright);
    for (int m=0; m<veggieleft.length() ; m++) {
      if (veggieleft.substring(m,m+1).equals("+")) {potatoesleft = (String[]) append(potatoesleft,"1");}
      else if (veggieleft.substring(m,m+1).equals("-")) { potatoesleft = (String[]) append(potatoesleft,"-1");}
    }
    for (int m=0; m<veggieright.length() ; m++) {
      if (veggieright.substring(m,m+1).equals("+")) {potatoesright = (String[]) append(potatoesright,"1");}
      else if (veggieright.substring(m,m+1).equals("-")) {potatoesright = (String[]) append(potatoesright,"-1");}
    }

    
  }
  
  
  
 
}
