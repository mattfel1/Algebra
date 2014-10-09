class tacticalInsertion {
  int insertion;
  int insertionSide;   //-1 is left, 1 is right, 0 is equals-sign, 9 is nowhere
  boolean insertionFlag;
  
  
  tacticalInsertion() {
    insertion=1;
    insertionSide=9;
    insertionFlag=false;
  }
  
  
  
  void scan(float xmouse, Number[] spots, int side, Operator eq) {
    int temp=spots.length-1;
    for (int g=0 ; g<temp ; g++) {
      if (xmouse>spots[g].location.x && xmouse<=spots[g+1].location.x && spots[g].dragging==false && spots[g+1].dragging==false) {
        insertion=g;
        insertionSide=side;
        insertionFlag=true;
        break;
      }
    }
    if (insertionFlag==false && side==-1 && xmouse>spots[temp].location.x && xmouse <= eq.location.x && spots[temp].dragging==false) {
      insertion=temp;
      insertionSide=side;
      insertionFlag=true;
    }
    else if (insertionFlag==false && side==1 && xmouse>eq.location.x && xmouse <= spots[0].location.x && spots[0].dragging==false) { //after equals sign
      insertion=0;
      insertionSide=0;
      insertionFlag=true;
    }
    else if (insertionFlag==false && side==1 && xmouse>spots[temp].location.x && spots[temp].dragging==false) { //after last term
      insertion=temp;
      insertionSide=side;
      insertionFlag=true;
    }
    else if (insertionFlag==false && side==-1 && xmouse<spots[0].location.x && spots[0].dragging==false) { //before first term on left side
      insertion=-1;
      insertionSide=-1;
      insertionFlag=true;
    }
  }
    
    
    
    
  void reset(boolean previous) {
    if (previous==false) {
      insertion=-1;
      insertionSide=9;
    }
  }
  
  
  void clone(tacticalInsertion udder) {
    insertion=udder.insertion;
    insertionSide=udder.insertionSide;
    insertionFlag=udder.insertionFlag;
  }
    
}
