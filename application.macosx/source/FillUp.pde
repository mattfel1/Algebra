class FillUp {
  
  Number[] nleft= new Number[0];
  Number[] nright= new Number[0];
  Operator[] oleft= new Operator[0];
  Operator[] oright= new Operator[0];
  float neq;
  Operator eq;
  
  
  
  FillUp(String[] meatleft, String[] meatright, String[] potatoesleft, String[] potatoesright) {
    for (int i=0; i<meatleft.length; i++) {
      nleft=(Number[]) append(nleft,new Number(SLOTSIZE*(2*i+1),YVAL,meatleft[i]));
      if (i!=0) {
        oleft=(Operator[]) append(oleft,new Operator(2*SLOTSIZE*(i),YVAL,potatoesleft[i-1]));
      }
    }
    //set equals sign position (for redraw)
    neq = (EQSPACER + SLOTSIZE*(nleft.length+oleft.length+1));
    //set equals sign object (for initialize)
    eq = new Operator(EQSPACER + SLOTSIZE*(nleft.length+oleft.length+1),100,"0");
    
    for (int i=0; i<meatright.length; i++) {
      nright=(Number[]) append(nright,new Number(EQSPACER + eq.locx+SLOTSIZE*(2*i+1),YVAL,meatright[i]));
      if (i!=0) {
        oright=(Operator[]) append(oright,new Operator(EQSPACER + eq.locx+2*SLOTSIZE*(i),YVAL,potatoesright[i-1]));
      }
    }
  }
  
  
  
  
  

  
  
}

