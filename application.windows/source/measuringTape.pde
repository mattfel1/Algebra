class measuringTape {
  float[] leftNumDistx;
  float[] leftNumDisty;
  float[] rightNumDistx;
  float[] rightNumDisty;
  float[] leftOpDistx;
  float[] leftOpDisty;
  float[] rightOpDistx;
  float[] rightOpDisty;
  int amountOnLeft;
  int amountOnRight;
  float opJump; //probably a vestigial property
  
  measuringTape(int amountOnLef, int amountOnRigh , float opJ) {
    leftNumDistx=new float[0];
    leftNumDisty=new float[0];
    rightNumDistx=new float[0];
    rightNumDisty=new float[0];
    leftOpDistx=new float[0];
    leftOpDisty=new float[0];
    rightOpDistx=new float[0];
    rightOpDisty=new float[0];
    opJump=opJ;
    
    amountOnLeft=amountOnLef;
    amountOnRight=amountOnRigh;
  }
  
  // WE CAN PROBABLY TURN THE ELSE IF STATEMENTS INTO CASE STATEMENTS IN THIS METHOD!!!!!!!!!!!!!!!!!!!!!!!!
  void measure(int switched , int insertionSide , int insertion , int Rmissing , int Lmissing , float interestx , float interesty , int rememberSLeft , int rememberSRight , float opJump, Number[] arrayNumbersLeft , Number[] arrayNumbersLeft2 , Number[] arrayNumbersRight , Number[] arrayNumbersRight2 , Operator[] arrayOperatorsLeft , Operator[] arrayOperatorsLeft2 , Operator[] arrayOperatorsRight , Operator[] arrayOperatorsRight2) {
    if (switched!=2) {
      // L <- R
      if (switched==-1 && insertionSide==-1) {
        for (int l=0 ; l < amountOnLeft ; l++) {
          if (l<insertion) {
            leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x-arrayNumbersLeft[l].location.x); 
            leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y-arrayNumbersLeft[l].location.y);
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l==insertion) {
            leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x-arrayNumbersLeft[l].location.x); 
            leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y-arrayNumbersLeft[l].location.y);
            if (l<amountOnLeft-1) {
              leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l+1].location.x - arrayOperatorsLeft[l].location.x);
              leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l+1].location.y - arrayOperatorsLeft[l].location.y);
            }
          }
          else if ( l>insertion) {
            leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l+1].location.x-arrayNumbersLeft[l].location.x); 
            leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l+1].location.y-arrayNumbersLeft[l].location.y);
            if (l<amountOnLeft-1) {
              leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l+1].location.x - arrayOperatorsLeft[l].location.x);
              leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l+1].location.y - arrayOperatorsLeft[l].location.y);
            }
          }
        }
        for (int r=0 ; r < amountOnRight ; r++) {
          if (r<Rmissing) {
            rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x); 
            rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
          }
          else if (r==Rmissing) {
            rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersLeft2[insertion+1].location.x - interestx);
            rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersLeft2[insertion+1].location.y - interesty);
          }
          else if (r>Rmissing) {
            rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r-1].location.x - arrayNumbersRight[r].location.x); 
            rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r-1].location.y - arrayNumbersRight[r].location.y);
          }
        }
        for (int r=0 ; r < amountOnRight-1 ; r++) { //missing side operators
          if (r<Rmissing-1) {
              rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
              rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r==Rmissing-1) {
              if (insertion!=-1) {rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsLeft2[insertion].location.x - arrayOperatorsRight[r].location.x);}
              else {rightOpDistx = (float[]) append(rightOpDistx , arrayNumbersLeft2[0].location.x - arrayOperatorsRight[r].location.x);}
              rightOpDisty = (float[]) append(rightOpDisty , opJump); 
          }
          else if (r>Rmissing-1 && Rmissing!=0) { //FOR WHEN WE DO NOT REMOVE FIRST NUMBER
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r-1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r-1].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r>Rmissing-1 && Rmissing==0) { //FOR WHEN WE REMOVE FIRST NUMBER
            if (r==0) {
              rightOpDistx = (float[]) append(rightOpDistx , arrayNumbersRight2[0].location.x - arrayOperatorsRight[0].location.x); //????? probably incorrect...  This handles sign abosrption
              rightOpDisty = (float[]) append(rightOpDisty , 0);
            }
            else if (r>0) {
              rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r-1].location.x - arrayOperatorsRight[r].location.x);
              rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r-1].location.y - arrayOperatorsRight[r].location.y);
            }
          }
        }
     }
     
     // L -> R
     else if (switched==1 && insertionSide==1) {
       for (int l=0 ; l < amountOnLeft ; l++) {
         if (l<Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x-arrayNumbersLeft[l].location.x); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y-arrayNumbersLeft[l].location.y);
         }
         else if (l==Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersRight2[insertion+1].location.x - interestx); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersRight2[insertion+1].location.y - interesty);
         }
         else if (l>Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l-1].location.x-arrayNumbersLeft[l].location.x); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l-1].location.y-arrayNumbersLeft[l].location.y);
         }
       }
       for (int r=0 ; r < amountOnRight ; r++) {
         if (r<insertion) {
           rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x); 
           rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
           rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
           rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
         }
         else if (r==insertion) {
           rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
           rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
           if (r<amountOnRight-1) {
             rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r+1].location.x - arrayOperatorsRight[r].location.x);
             rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r+1].location.y - arrayOperatorsRight[r].location.y);
           }
         }
         else if (r>insertion) {
           rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r+1].location.x - arrayNumbersRight[r].location.x); 
           rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r+1].location.y - arrayNumbersRight[r].location.y);
           if (r<amountOnRight-1) {
             rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r+1].location.x - arrayOperatorsRight[r].location.x);
             rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r+1].location.y - arrayOperatorsRight[r].location.y);
           }
         }
       }
       for (int l=0 ; l < amountOnLeft-1 ; l++) { //missing side operators
         if (l<Lmissing-1) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
         }
         else if (l==Lmissing-1) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsRight2[insertion].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , opJump); 
         }
         else if (l>Lmissing-1 && Lmissing!=0) { //for when we do not remove first number
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
         }
         else if (l>Lmissing-1 && Lmissing==0) { //for when we do remove first number
           if (l==0) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayNumbersLeft2[0].location.x - arrayOperatorsLeft[0].location.x); //probably incorrect???? handles sign abosrption animation
             leftOpDisty = (float[]) append(leftOpDisty , 0);
           }
           else if (l>0) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
           }
         }
       }
    }
   
    else if (switched==1 && insertionSide==0) { //this is left to immediately right of equals sign
       for (int l=0 ; l < amountOnLeft ; l++) {
         if (l<Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x-arrayNumbersLeft[l].location.x); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y-arrayNumbersLeft[l].location.y);
         }
         else if (l==Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersRight2[0].location.x - interestx); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersRight2[0].location.y - interesty);
         }
         else if (l>Lmissing) {
           leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l-1].location.x-arrayNumbersLeft[l].location.x); 
           leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l-1].location.y-arrayNumbersLeft[l].location.y);
         }
       }
       for (int r=0 ; r < amountOnRight ; r++) {
         rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r+1].location.x - arrayNumbersRight[r].location.x); 
         rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r+1].location.y - arrayNumbersRight[r].location.y);
         if (r!=0) {
           rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r-1].location.x);
           rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r-1].location.y);
         }
       }
       for (int l=0 ; l < amountOnLeft-1 ; l++) { //missing side operators
         if (l<Lmissing-1) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
         }
         else if (l==Lmissing-1) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayNumbersRight2[0].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , opJump); 
         }
         else if (l>Lmissing-1 && Lmissing!=0) { //for when we do not remove first number
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
         }
         else if (l>Lmissing-1 && Lmissing==0) { //for when we do remove first number
           if (l==0) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayNumbersLeft2[0].location.x - arrayOperatorsLeft[0].location.x); //probably incorrect???? handles sign abosrption animation
             leftOpDisty = (float[]) append(leftOpDisty , 0);
           }
           else if (l>0) {
             leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
             leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
           }
         }
       }
    }





    else if (switched==1 && insertionSide==-1) {//this is left to left
      for (int r=0 ; r<amountOnRight ; r++) { //right sides don't change their association
        rightNumDistx=(float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
        rightNumDisty=(float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
        if (r>0) {
          rightOpDistx=(float[]) append(rightOpDistx , arrayOperatorsRight2[r-1].location.x - arrayOperatorsRight[r-1].location.x);
          rightOpDisty=(float[]) append(rightOpDisty , arrayOperatorsRight2[r-1].location.y - arrayOperatorsRight[r-1].location.y);
        }
      }
      for (int l=0 ; l<amountOnLeft ; l++) { //handle left side numbres
        if (l<Lmissing && l<=insertion) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x - arrayNumbersLeft[l].location.x);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y - arrayNumbersLeft[l].location.y);
        }
        else if (Lmissing==0 && l==0) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[insertion].location.x - interestx);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[insertion].location.y - interesty);
        }
        else if (l==Lmissing && insertion>=0 && Lmissing>insertion) { ///////////////////////////////???????????????????????????? is this right?
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[insertion+1].location.x - interestx);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[insertion+1].location.y - interesty);
        }
        else if (l==Lmissing && insertion>=0 && Lmissing<insertion) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[insertion].location.x - interestx);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[insertion].location.y - interesty);
        }
        else if (l==Lmissing && insertion==-1) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[0].location.x - interestx);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[0].location.y - interesty);
        }
        else if (l>Lmissing && l<=insertion) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l-1].location.x - arrayNumbersLeft[l].location.x);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l-1].location.y - arrayNumbersLeft[l].location.y);
        }
        else if (l>Lmissing && l>insertion) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x - arrayNumbersLeft[l].location.x);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y - arrayNumbersLeft[l].location.y);
        }          
        else if (l>insertion && l<Lmissing) {
          leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l+1].location.x - arrayNumbersLeft[l].location.x);
          leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l+1].location.y - arrayNumbersLeft[l].location.y);
        }
      }
      for (int l=0 ; l<amountOnLeft-1 ; l++) {
        if (Lmissing==0) { //if we move first number
          if (l==0) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayNumbersLeft2[0].location.x - arrayOperatorsLeft[0].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayNumbersLeft2[0].location.y - arrayOperatorsLeft[0].location.y);
          }
          else if (l<insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l>=insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
        }
        else if (insertion==-1) { //if we insert into first position
          if (l<Lmissing-1) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l==Lmissing-1) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayNumbersLeft2[0].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , opJump);
          }
          else if (l>Lmissing-1) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
        }
        else { //typical case, not involving first position transitions
          if (l<Lmissing-1 && l<insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l<Lmissing-1 && l>=insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l+1].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l+1].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l==Lmissing-1 && l<insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[insertion-1].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , opJump);
          }
          else if (l==Lmissing-1 && l >insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[insertion].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , opJump);
          }
          else if (l>Lmissing-1 && l<insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l].location.y);
          }
          else if (l>Lmissing-1 && l>=insertion) {
            leftOpDistx = (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
            leftOpDisty = (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
          }
        }
            
          
          
          
      } 
    }
    
    else if (switched==-1 && insertionSide>=0) {//this is right to right
      for (int l=0 ; l<amountOnLeft ; l++) { //nums on left don't do anything unusual
        leftNumDistx=(float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x - arrayNumbersLeft[l].location.x);
        leftNumDisty=(float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y - arrayNumbersLeft[l].location.y);
        if (l>0) {
          leftOpDistx=(float[]) append(leftOpDistx , arrayOperatorsLeft2[l-1].location.x - arrayOperatorsLeft[l-1].location.x);
          leftOpDisty=(float[]) append(leftOpDisty , arrayOperatorsLeft2[l-1].location.y - arrayOperatorsLeft[l-1].location.y);
        }
      }
      for (int r=0 ; r<amountOnRight ; r++) { // LOTS of cases for numbers on right
        if (r<Rmissing && insertionSide==1 && r<=insertion) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
        }
        else if (Rmissing==0 && r==0) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[insertion].location.x - interestx);        
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[insertion].location.y - interesty);
        }
        else if (r==Rmissing && insertionSide==1 && Rmissing>insertion) { ////////////////////////////?????????????? is this right?
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[insertion+1].location.x - interestx);
          rightNumDisty = (float[]) append(rightNumDisty, arrayNumbersRight2[insertion+1].location.y - interesty);
        }
        else if (r==Rmissing && insertionSide==1 && Rmissing<insertion) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[insertion].location.x - interestx);
          rightNumDisty = (float[]) append(rightNumDisty, arrayNumbersRight2[insertion].location.y - interesty);
        }
        else if (r<Rmissing && insertionSide==0) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r+1].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r+1].location.y - arrayNumbersRight[r].location.y);
        }
        else if (r==Rmissing && insertionSide==0) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[0].location.x - interestx);
          rightNumDisty = (float[]) append(rightNumDisty, arrayNumbersRight2[0].location.y - interesty);
        }
        else if (r>Rmissing && insertionSide==0) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
        }
        else if (r>Rmissing && r<=insertion && insertionSide==1) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r-1].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r-1].location.y - arrayNumbersRight[r].location.y);
        }
        else if (r>Rmissing && r>insertion && insertionSide==1) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
        }
        else if (r>insertion && r<Rmissing && insertionSide==1) {
          rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r+1].location.x - arrayNumbersRight[r].location.x);
          rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r+1].location.y - arrayNumbersRight[r].location.y);
        }
      }
      for (int r=0 ; r<amountOnRight-1 ; r++) {
        if (Rmissing==0) { // if we move first number
          if (r==0) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayNumbersRight2[0].location.x - arrayOperatorsRight[0].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayNumbersRight2[0].location.y - arrayOperatorsRight[0].location.y);
          }
          else if (r<insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r-1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r-1].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r>=insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
          }
        }
        else if (insertionSide==0) { //if we insert into first position
          if (r<Rmissing-1) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r+1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r+1].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r==Rmissing-1) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayNumbersRight2[0].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , opJump);
          }
          else if (r>Rmissing-1) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
          }
        }
        else { //typical case, not involving first position transitions
          if (r<Rmissing-1 && r<insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r<Rmissing-1 && r>=insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r+1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r+1].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r==Rmissing-1 && r<insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[insertion-1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , opJump);
          }
          else if (r==Rmissing-1 && r>insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[insertion].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , opJump);
          }
          else if (r>Rmissing-1 && r<insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r-1].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r-1].location.y - arrayOperatorsRight[r].location.y);
          }
          else if (r>Rmissing-1 && r>=insertion) {
            rightOpDistx = (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
            rightOpDisty = (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
          }
        }
      }         
    } 
    
    else if (switched==0 && insertionSide==0) {   //is this vestigial?
      for (int l=0 ; l<amountOnLeft ; l++) {
        leftNumDistx = (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x-arrayNumbersLeft[l].location.x); 
        leftNumDisty = (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y-arrayNumbersLeft[l].location.y);        
      }
      for (int r=0 ; r<amountOnRight ; r++) {
        rightNumDistx = (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x); 
        rightNumDisty = (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
      }
    }
  }
  
  
  
  
  else if (switched==2) { //simplify phase condition
    for (int l=0 ; l<amountOnLeft ; l++) {
      leftNumDistx= (float[]) append(leftNumDistx , arrayNumbersLeft2[l].location.x - arrayNumbersLeft[l].location.x);
      leftNumDisty= (float[]) append(leftNumDisty , arrayNumbersLeft2[l].location.y - arrayNumbersLeft[l].location.y);
    }
    for (int r=0 ; r<amountOnRight ; r++) {
      rightNumDistx= (float[]) append(rightNumDistx , arrayNumbersRight2[r].location.x - arrayNumbersRight[r].location.x);
      rightNumDisty= (float[]) append(rightNumDisty , arrayNumbersRight2[r].location.y - arrayNumbersRight[r].location.y);
    }
    for (int l=0 ; l<amountOnLeft - 1 ; l++) {
      leftOpDistx= (float[]) append(leftOpDistx , arrayOperatorsLeft2[l].location.x - arrayOperatorsLeft[l].location.x);
      leftOpDisty= (float[]) append(leftOpDisty , arrayOperatorsLeft2[l].location.y - arrayOperatorsLeft[l].location.y);
    }
    for (int r=0 ; r<amountOnRight - 1 ; r++) {
      rightOpDistx= (float[]) append(rightOpDistx , arrayOperatorsRight2[r].location.x - arrayOperatorsRight[r].location.x);
      rightOpDisty= (float[]) append(rightOpDisty , arrayOperatorsRight2[r].location.y - arrayOperatorsRight[r].location.y);
    }
  }
  
  
  
}
  
  
void nextFunction() {}
  
  
  
  
  
  
  
}
