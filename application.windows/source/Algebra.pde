//v0.4
// FIX THE INSERTION OF THINGS MOVING LEFT/RIGHT.  

Operator eq;
float neq;

//PREFERENCE CONTROLS
int YVAL=100;
int TEXTSIZE=75;
int OPTEXTSIZE=60;
int EQTEXTSIZE=75;
int CIRCLEWEIGHT=30;
int SLOTSIZE=75;
int BUBBLES=5;
int DELAYFORSIMPLIFY=100;
float maxOpJump=-250; //maximum aount by which moving operators jump
int stepsize=20; //number of steps for animation
int stepsize2=30; //number of steps for simplification animation
color SIMPOPCOLOR=color(255,100,0);
color SIMPNUMCOLOR=color(255,0,0);
int showNewNumMax=10;
int DROPPREVIEWOFFSET=20; //how far to the right of a number to begin drawing rectangle
int DROPPREVIEWWIDTH=30; //how wide to make rectangle
int DROPPREVIEWCOLOR=color(255,220,220);

//skin and bones variables
int inc2=999; //incrementer for the shifting of something simplifying
int simpInc=999; //incrementer for the color highlights of something simplifying
int switched; //this is 1 for left->right, -1 for left<-right, and 2 for release but no movement.
float ori_locx; //this checks if we moved to other side of =
float ori_locy; //original y location at first click
float EQSPACER=20;
String NEG="-";
int inc=800; //arbitrarily large number
float distanceEq=0;
float distanceNum=0;
float distanceOp=0;
int Lmissing=0;
int Rmissing=0;
int moveallright=1; //Flag for if a left item moved
int infect=0; //flag for when animation finishes and we want to use our left/right arrays
float interesty; //y location of dropped object
float interestx; //x location of dropped object
float distanceInteresty; //y distance that dropped object needs to move
float distanceInterestx; //x distance that dropped object needs to move
float opJump; //amount by which to jump moved operators
int simplifyLeftAt=-1;
int simplifyRightAt=-1;
float distanceOpInterestx; //x distance that operator must move
float distanceOpInteresty; //y distance that operator must move
int rightstomove; //index of element on right side to quit moving at
FillUp filler;
FillUp initialfiller;
boolean snapback=false;
int simplify=0;
boolean getSimpDistances=false;
int incShowNewNum=999;
boolean simpCleanUp=false;
boolean isPrompt=true;  //LOOKY HERE FOR TURNING ON THE PROMPT SCREEN
String input="";
boolean allowInteraction=true;
boolean someoneismoving=false;
int insertion=-1; //keep track of where we want to insert number
int insertionSide=0; //keep track of which side we insert (left=-1 right=1)
int temp;
boolean insertionFlag=false; //how else to jump out of a for loop with 2 nested ifs?
tacticalInsertion drawTactic = new tacticalInsertion(); //for draw loop
tacticalInsertion dropTactic = new tacticalInsertion(); //for mouseRelease loop
measuringTape tape;
int rememberSLeft=-1; //these are just alternative location to store simplify locations for the tape measure after a simplify, duplicated because changing simplifyLeft/RightAt may cause bugs
int rememberSRight=-1; //
CookFromScratch initialize;


//meat and potatoes (numbers and operators)
String[] meatleft = new String[3];
String[] potatoesleft = new String[2];
String[] meatright = new String[3];
String[] potatoesright = new String[2];

Number[] arrayNumbersLeft = new Number[0];
Operator[] arrayOperatorsLeft = new Operator[0];
Number[] arrayNumbersRight = new Number[0];
Operator[] arrayOperatorsRight = new Operator[0];

Number[] arrayNumbersLeft2 = new Number[0];
Operator[] arrayOperatorsLeft2 = new Operator[0];
Number[] arrayNumbersRight2 = new Number[0];
Operator[] arrayOperatorsRight2 = new Operator[0];

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void setup() {
// ORIGINAL SETUP LOOP FOR WHEN WE DO NOT LET USER TYPE IN EQUATION
//  //meat defines our original numbers
//  meatleft[0] = "3";
//  meatleft[1] = "x";
//  meatleft[2] = "7";
//  meatright[0] = "5";
//  meatright[1] = "y";
//  meatright[2] = "9";
//  //potatoes defines our original operators.
//  potatoesleft[0]="1";
//  potatoesleft[1]="-1";
//  potatoesright[0]="1";
//  potatoesright[1]="1";
//  //write left side on screen
//
//  initialfiller=new FillUp(meatleft, meatright, potatoesleft, potatoesright);
//  arrayNumbersLeft=initialfiller.nleft;
//  arrayNumbersRight=initialfiller.nright;
//  arrayOperatorsLeft=initialfiller.oleft;
//  arrayOperatorsRight=initialfiller.oright;
//  eq=initialfiller.eq;

  size(1200, 300);
  smooth();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void draw() {
  
  
  /*********
  * PROMPT *
  **********/
  //display prompt screen
  if( isPrompt ) {
    background(0);
    textSize(20);
    fill(255,50,50);
    text("Please type in your equation, then hit <enter>.  \n    (Only use numbers, variables, +, -, and =)",20,20);
    textSize(30);
    fill(255);
    text(input,100,150);
    fill(50,50,255);
    textSize(10);
    text("Algebra Manipulator       v0.4-alpha",1000,260);
  }
  
  
  
  
  
  else {
    /**********************
    *FIND INSERTION POINTS*
    **********************/    
    if (someoneismoving) {
      drawTactic.scan(mouseX , arrayNumbersLeft, -1, eq);
      drawTactic.scan(mouseX , arrayNumbersRight, 1, eq);
      drawTactic.reset(drawTactic.insertionFlag); //resets if insertion flag is false at this point, which happens if we start draw loop again and are no longer at an insertion point
    }
    
    
    /*************************************
     *REFILL ARRAYS THAT HOLD DRAWN ITEMS*
     ************************************/
    //redraw when something switches sides. switched==-1 is L<-R, +1 is L->R, -2 is a simplification on first move, +2 is simplification on second phase (first phase handled separately)
    if (switched==1 || switched==-1 || snapback==true || switched==2) {
      allowInteraction=false;
      if (snapback==false) {
        inc=0; //initialize animation incrementor
      }
      else if (snapback==true) {
        infect=1;
      } //override end of animation if we just want snapback
      
      //kill array
      arrayNumbersLeft2=KillNumbers(arrayNumbersLeft2);
      arrayNumbersRight2=KillNumbers(arrayNumbersRight2);
      arrayOperatorsLeft2=KillOperators(arrayOperatorsLeft2);
      arrayOperatorsRight2=KillOperators(arrayOperatorsRight2);

      //refill arrays
      filler=new FillUp(meatleft, meatright, potatoesleft, potatoesright);
      arrayNumbersLeft2=filler.nleft;
      arrayNumbersRight2=filler.nright;
      arrayOperatorsLeft2=filler.oleft;
      arrayOperatorsRight2=filler.oright;

      //reset snapback flag, since we just refilled arrays
      snapback=false;
      //calculate distance to move equals sign. filler's (new's) eq minus the old eq location
      distanceEq=filler.neq-eq.location.x;
      if (switched!=2) {
        tape = new measuringTape(arrayNumbersLeft.length , arrayNumbersRight.length, maxOpJump);
        tape.measure(switched , dropTactic.insertionSide , dropTactic.insertion , Rmissing , Lmissing , interestx , interesty , 0 , 0 , maxOpJump , arrayNumbersLeft , arrayNumbersLeft2 , arrayNumbersRight , arrayNumbersRight2 , arrayOperatorsLeft , arrayOperatorsLeft2 , arrayOperatorsRight , arrayOperatorsRight2);
      }
      else if (switched==2) {  //remember that simplify==2 means we are on second stage of a simplification
        tape = new measuringTape(arrayNumbersLeft.length , arrayNumbersRight.length , opJump);
        tape.measure(switched , 0 , 0 , 0 , 0 , 0 , 0 , rememberSLeft , rememberSRight , maxOpJump, arrayNumbersLeft , arrayNumbersLeft2 , arrayNumbersRight , arrayNumbersRight2 , arrayOperatorsLeft, arrayOperatorsLeft2 , arrayOperatorsRight , arrayOperatorsRight2);
        rememberSLeft=-1;
        rememberSRight=-1;
      }

     /////////////////////////////////////////////////////////////////////////////  
        
      opJump=maxOpJump/(2*stepsize);
      //reset switched flag
      if (switched==2) {simpCleanUp=true;}
      switched=0;
    }
    
    // for simplification switch, set interest distances to slide
    if (getSimpDistances==true) {
      if (simplifyLeftAt>=0) {
        distanceInterestx=slideNumDist(arrayNumbersLeft,arrayNumbersLeft[simplifyLeftAt+1].location.x , arrayNumbersLeft[simplifyLeftAt+1].location.y , simplifyLeftAt)[0];
        distanceInteresty=slideNumDist(arrayNumbersLeft,arrayNumbersLeft[simplifyLeftAt+1].location.x , arrayNumbersLeft[simplifyLeftAt+1].location.y , simplifyLeftAt)[1];
        distanceOpInterestx=arrayNumbersLeft[simplifyLeftAt].location.x-arrayOperatorsLeft[simplifyLeftAt].location.x;
        distanceOpInteresty=arrayNumbersLeft[simplifyLeftAt].location.y-arrayOperatorsLeft[simplifyLeftAt].location.y;
      }
      if (simplifyRightAt>=0) {
        distanceInterestx=slideNumDist(arrayNumbersRight,arrayNumbersRight[simplifyRightAt+1].location.x , arrayNumbersRight[simplifyRightAt+1].location.y , simplifyRightAt)[0];
        distanceInteresty=slideNumDist(arrayNumbersRight,arrayNumbersRight[simplifyRightAt+1].location.x , arrayNumbersRight[simplifyRightAt+1].location.y , simplifyRightAt)[1];
        distanceOpInterestx=arrayNumbersRight[simplifyRightAt].location.x-arrayOperatorsRight[simplifyRightAt].location.x;
        distanceOpInteresty=arrayNumbersRight[simplifyRightAt].location.y-arrayOperatorsRight[simplifyRightAt].location.y;
      }
      getSimpDistances=false; //reset flag, since we just got our distances
    }
  
    /****************
     *ANIMATE THINGS*
     ****************/
     
    //for simplification animations, phase 2 (slide).
    if (inc2<stepsize2+1) {
      if (simplifyLeftAt>=0) {
        slideNum(arrayNumbersLeft[simplifyLeftAt+1] , distanceInterestx/stepsize2 , distanceInteresty/stepsize2 , 1);
        slideOp(arrayOperatorsLeft[simplifyLeftAt], distanceOpInterestx/stepsize2 , distanceOpInteresty/stepsize2 , 1);
      }
      else if (simplifyRightAt>=0) {
        slideNum(arrayNumbersRight[simplifyRightAt+1] , distanceInterestx/stepsize2 , distanceInteresty/stepsize2 , 1);
        slideOp(arrayOperatorsRight[simplifyRightAt], distanceOpInterestx/stepsize2 , distanceOpInteresty/stepsize2 , 1);
      }
  
      inc2++;
      if (inc2==stepsize2+1) {
        if (simplifyLeftAt>=0) {
          arrayNumbersLeft=popNumOut(arrayNumbersLeft , simplifyLeftAt+1);
          arrayNumbersLeft[simplifyLeftAt].nvalue=float(meatleft[simplifyLeftAt]);
          arrayOperatorsLeft=popOpOut(arrayOperatorsLeft , simplifyLeftAt);
        }
        else if (simplifyRightAt>=0) {
          arrayNumbersRight=popNumOut(arrayNumbersRight , simplifyRightAt+1);
          arrayNumbersRight[simplifyRightAt].nvalue=float(meatright[simplifyRightAt]);
          arrayOperatorsRight=popOpOut(arrayOperatorsRight , simplifyRightAt);
        }
        incShowNewNum=0;
        rememberSLeft=simplifyLeftAt;
        rememberSRight=simplifyRightAt;
        simplifyLeftAt=-1;
        simplifyRightAt=-1;
      }
    }
    
    if (incShowNewNum<showNewNumMax) {
      incShowNewNum++;
      if (incShowNewNum==showNewNumMax) {switched=2;}
    }
      
    //Reverse dy for the operator halfway through the move
    if (inc==stepsize/2) {
      if (Rmissing>0) {
        tape.rightOpDisty[Rmissing-1]=-tape.rightOpDisty[Rmissing-1];
      }
      if (Lmissing>0) {
        tape.leftOpDisty[Lmissing-1]=-tape.leftOpDisty[Lmissing-1];
      }
    }
    //As long we have not stepped all the way through..
    if (inc<stepsize+1) {
      //slide the equals sign
      slideEq(eq, distanceEq/stepsize, 0);

      //Animate everybody who is currently on the screen
      for (int l=0 ; l < arrayNumbersLeft.length ; l++ ) {
        slideNum(arrayNumbersLeft[l], tape.leftNumDistx[l]/stepsize, tape.leftNumDisty[l]/stepsize, 0);
      }
      for (int l=0 ; l < arrayOperatorsLeft.length ; l++) {
        slideOp(arrayOperatorsLeft[l], tape.leftOpDistx[l]/stepsize, tape.leftOpDisty[l]/stepsize, 0);
      }
      for (int r=0 ; r < arrayNumbersRight.length ; r++) {
        slideNum(arrayNumbersRight[r], tape.rightNumDistx[r]/stepsize, tape.rightNumDisty[r]/stepsize, 0);
      }
      for (int r=0 ; r < arrayOperatorsRight.length ; r++) {
        slideOp(arrayOperatorsRight[r], tape.rightOpDistx[r]/stepsize, tape.rightOpDisty[r]/stepsize, 0);
      }
  
      inc++;
      if (inc==stepsize+1) {
        infect=1;
      }
    }
  
    if (infect==1) {
      arrayNumbersLeft=arrayNumbersLeft2;
      arrayNumbersRight=arrayNumbersRight2;
      arrayOperatorsLeft=arrayOperatorsLeft2;
      arrayOperatorsRight=arrayOperatorsRight2;
      Rmissing=-1;
      Lmissing=-1;
      infect=0;
      simplify=1;
      simpCleanUp=false;
    }
    
    
    /*****************************
    *CHECK FOR THINGS TO SIMPLIFY*
    ******************************/
    //when simplify flag is raised, do simplification check and indicate by resetting simpInc and storing index of simplifying numbers
    if (simplify!=0) {
      if (simplifyLeftAt==-1 && simplifyRightAt==-1) {
        for (int i=0;i<arrayNumbersLeft.length-1;i++) {
          if (arrayNumbersLeft[i].varflag==false && arrayNumbersLeft[i+1].varflag==false) {
            simplifyLeftAt=i;
            meatleft[i]=Operate(potatoesleft[i] , float(meatleft[i]) , float(meatleft[i+1])); //combine terms behind the scenes   
            
            //fix up numbers
            if (simplifyLeftAt<meatleft.length-2) {arrayCopy(meatleft, i+2, meatleft, i+1, meatleft.length-i-2);}
            meatleft = (String[]) shorten(meatleft);
            
            //fix up operators
            potatoesleft = popOut(potatoesleft,i);
            simpInc=0;
            break;
          }
        }
      }
      if (simplifyLeftAt==-1 && simplifyRightAt==-1) {
        for (int i=0;i<arrayNumbersRight.length-1;i++) {
          if (arrayNumbersRight[i].varflag==false && arrayNumbersRight[i+1].varflag==false) {
            simplifyRightAt=i;
            meatright[i]=Operate(potatoesright[i] , float(meatright[i]) , float(meatright[i+1]) ); //combine terms behind the scenes    
            //fix up numbers
            if (simplifyRightAt<meatright.length-2) {arrayCopy(meatright, i+2, meatright, i+1, meatright.length-i-2);}
            meatright = (String[]) shorten(meatright);
            
            //fix up operators
            potatoesright = popOut(potatoesright , i);
            
            simpInc=0;
            break;
          }
        }
      }
      
      //if nothing got simplified, just reset simplify flag and release screen for another interaction
      if (simplifyLeftAt==-1 && simplifyRightAt==-1) {
        simplify=0;
        allowInteraction=true;
      }
    }
    
    //color in the things that are about to be simplified
    if (simpInc<DELAYFORSIMPLIFY) { 
      if (simplifyLeftAt>=0) {
        arrayNumbersLeft[simplifyLeftAt].colour=SIMPNUMCOLOR;
        arrayNumbersLeft[simplifyLeftAt+1].colour=SIMPNUMCOLOR;
        arrayOperatorsLeft[simplifyLeftAt].colour=SIMPOPCOLOR;
      }
      else if (simplifyRightAt>=0) {
        arrayNumbersRight[simplifyRightAt].colour=SIMPNUMCOLOR;
        arrayNumbersRight[simplifyRightAt+1].colour=SIMPNUMCOLOR;
        arrayOperatorsRight[simplifyRightAt].colour=SIMPOPCOLOR;
      }
      simpInc++;
    }
    
    if (simpInc>=DELAYFORSIMPLIFY && simplify!=0) {
      getSimpDistances=true; //indicate we need to redraw due to simplification
      inc2=0; //incrementor for simplification animations
      simplify=0; //reset simplify flag
    } 
    
          
  
  
  
  
  
    /************
    *DRAW THINGS*
    *************/
    //wipe background
    background(255);
  
    //Draw items
    for (int n=arrayOperatorsLeft.length-1 ; n>=0 ; n--) {
      arrayOperatorsLeft[n].display();
    }
    for (int n=arrayOperatorsRight.length-1 ; n>=0 ;n--) {
      arrayOperatorsRight[n].display();
    }
    for (int n=arrayNumbersLeft.length-1; n>=0 ; n--) {
      arrayNumbersLeft[n].display();
      arrayNumbersLeft[n].drag();
      if (someoneismoving==false && allowInteraction==true) {arrayNumbersLeft[n].hover(mouseX, mouseY);}
    }
    for (int n=arrayNumbersRight.length-1 ; n>=0 ; n--) {
      arrayNumbersRight[n].display();
      arrayNumbersRight[n].drag();
      if (someoneismoving==false && allowInteraction==true) {arrayNumbersRight[n].hover(mouseX, mouseY);}
    }
    if (insertionSide==-1 && someoneismoving==true) { //left side
      fill(DROPPREVIEWCOLOR);
      rect(arrayNumbersLeft[insertion].location.x + DROPPREVIEWOFFSET, YVAL - 20 , DROPPREVIEWWIDTH , 40);
    }
    if (drawTactic.insertionFlag==true) {
      fill(DROPPREVIEWCOLOR);
      switch(drawTactic.insertionSide) {
        case -1:
          if (drawTactic.insertion>=0) {rect(arrayNumbersLeft[drawTactic.insertion].location.x + DROPPREVIEWOFFSET, YVAL - 20 , DROPPREVIEWWIDTH , 40);} //this is for not very first
          else if (drawTactic.insertion==-1) {rect(10, YVAL - 20 , DROPPREVIEWWIDTH , 40);}
          break;
        case 1:
          rect(arrayNumbersRight[drawTactic.insertion].location.x + DROPPREVIEWOFFSET , YVAL - 20 , DROPPREVIEWWIDTH , 40);
          break;
        case 0:
          rect(eq.location.x + DROPPREVIEWOFFSET , YVAL - 20 , DROPPREVIEWWIDTH , 40);
          break;
      }
    }      
  
  
    //Throw down equals sign
    eq.display();
  }
  
  dropTactic.clone(drawTactic);
  drawTactic.insertionFlag=false;
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void keyPressed() {
  //display what user types on screen
  if ( isPrompt) {
    if (key==BACKSPACE && input.length()>0) {
      input=input.substring(0,input.length()-1 );
    }
    else if (key==CODED) {}
    else if (key!=BACKSPACE) {
      input=input+key;
    }
    
    
    //parse what user typed and feed into meat/potatoes
    if (key==ENTER || key==RETURN) {
      initialize = new CookFromScratch(input);
      
      //transfer class to main variables
      meatleft=initialize.meatleft;
      meatright=initialize.meatright;
      potatoesleft=initialize.potatoesleft;
      potatoesright=initialize.potatoesright;
    
      //fill up the actual screen-visible arrays
      initialfiller=new FillUp(meatleft, meatright, potatoesleft, potatoesright);
      arrayNumbersLeft=initialfiller.nleft;
      arrayNumbersRight=initialfiller.nright;
      arrayOperatorsLeft=initialfiller.oleft;
      arrayOperatorsRight=initialfiller.oright;
      eq=initialfiller.eq;
      isPrompt=false;
    }
  }
}



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void mousePressed() {
  if (allowInteraction) {
    for (int n=0;n<arrayNumbersLeft.length;n++) {
      arrayNumbersLeft[n].clicked(mouseX, mouseY);
      if (arrayNumbersLeft[n].dragging==true) {someoneismoving=true;}
    }
    for (int n=0;n<arrayNumbersRight.length;n++) {
      arrayNumbersRight[n].clicked(mouseX, mouseY);
      if (arrayNumbersRight[n].dragging==true) {someoneismoving=true;}
    }
  }
  
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// on release click (remember that this includes when things
//  aren't actually dragged
void mouseReleased() {
  if (allowInteraction) {
    someoneismoving=false;
    switched=0;
    
    int ntimes=arrayNumbersLeft.length; //because the amount changes when you swap
    Lmissing=-1; //none missing from left unless otherwise stated
    for (int n=0;n<ntimes;n++) {
      //plop it down
      arrayNumbersLeft[n].stopDragging();
      //handling left->right swaps
      if ( (switched!=1 && switched!=-1)/*other moves?*/
      && arrayNumbersLeft[n].dragged==true/*something actually dragged and not just a mouse click?*/
      && dropTactic.insertionFlag==true
//      && ((ori_locx-eq.location.x)*(mouseX-eq.location.x))<0/*released on other side of equals?*/
      && meatleft.length>1 /*not the last term?*/) {
      
  
        interesty=mouseY;
        interestx=mouseX;
        switched=1;
        arrayNumbersLeft[n].dragged=false;
        Lmissing=n;
  
        // handle number insertion
        if (dropTactic.insertionSide==1) {meatright=popIn(meatright,meatleft[n],dropTactic.insertion+1); meatleft=popOut(meatleft,n);}
        else if (dropTactic.insertionSide==-1 && n==0) {meatleft=popIn(meatleft,meatleft[n],dropTactic.insertion+1); meatleft=popOut(meatleft,n);}
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion>=0 && n>0 && moveVector(n,dropTactic.insertion)==1) {meatleft=popIn(meatleft,meatleft[n],dropTactic.insertion+1); meatleft=popOut(meatleft,n);}
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion>=0 && n>0 && n>dropTactic.insertion) {meatleft=popIn(meatleft,meatleft[n],dropTactic.insertion+1); meatleft=popOut(meatleft,n+1);}
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion==-1) {meatleft = popIn(meatleft, signAbsorb(meatleft[n],potatoesleft[n-1]) , 0 ); meatleft=popOut(meatleft,n+1);}
        else if (dropTactic.insertionSide==0 && n>0) {meatright=popIn(meatright , signAbsorb(meatleft[n],complement(potatoesleft[n-1])) , 0 ); meatleft=popOut(meatleft,n); }
        else if (dropTactic.insertionSide==0 && n==0) {meatright=popIn(meatright , signAbsorb(meatleft[0],"-1") , 0 ); meatleft=popOut(meatleft,n); }
  
        // handle operator switch
        //    First consider if we move the first term
        if (dropTactic.insertionSide==1) {
          if (n==0) {
            //handle move-to right side sign
            potatoesright=popIn(potatoesright,"-1",dropTactic.insertion);
    
            //handle move-from left side sign absorption.
            // FUTURE: ANIMATE THE ABSORPTION?
            meatleft[0]=signAbsorb(meatleft[0] , potatoesleft[0]);
           
            //shift operators
            potatoesleft = deleteFrom(potatoesleft,n);
          }
          //   Otherwise just shift all operators back
          else {
            potatoesright=popIn(potatoesright , complement(potatoesleft[n-1]) , dropTactic.insertion);
            potatoesleft=popOut(potatoesleft,n-1);
          }
        }
        else if (dropTactic.insertionSide==0) {
          if (n==0) {
            potatoesright=popIn(potatoesright,"1",0);
    
            //handle move-from left side sign absorption.
            // FUTURE: ANIMATE THE ABSORPTION?
            meatleft[0]=signAbsorb(meatleft[0] , potatoesleft[0]);
          }
          else {
            potatoesright=popIn(potatoesright,"1",0);
            potatoesleft=popOut(potatoesleft,n-1);
          }
        }
        else if (dropTactic.insertionSide==-1) {
          if (n==0) {
            potatoesleft=popIn(potatoesleft,"1",dropTactic.insertion);
            meatleft[0]=signAbsorb(meatleft[0],potatoesleft[0]);
            potatoesleft=deleteFrom(potatoesleft,0);
          }
          else if (n>0 && dropTactic.insertion<n && dropTactic.insertion!=-1) {
            potatoesleft=popIn(potatoesleft,potatoesleft[n-1] , dropTactic.insertion);
            potatoesleft=popOut(potatoesleft,n);
          }
          else if (n>0 && dropTactic.insertion>n && dropTactic.insertion!=-1) {
            potatoesleft=popIn(potatoesleft,potatoesleft[n-1] , dropTactic.insertion);
            potatoesleft=popOut(potatoesleft,n-1);
          }
          else if (n>0 && dropTactic.insertion==-1) {
            potatoesleft=popOut(potatoesleft,n-1);
            potatoesleft=popIn(potatoesleft,"1",0);
          }
        }
          
          
      }//end left->right swaps
    } //end left side release-click checker
    

  
  
    ntimes=arrayNumbersRight.length; //because the amount changes when you swap
    Rmissing=-1;
    if (switched!=0) {
      return;
    }
    for (int n=0;n<ntimes;n++) {
      //plop it down
      arrayNumbersRight[n/*-abs(switched) REMOVING abs(switched) MAY CAUSE A BUG????*/].stopDragging();
  
      //handles left<-right swaps
      if ( (switched!=1 && switched!=-1)/*other moves?*/ 
      && arrayNumbersRight[n].dragged==true/*something actually dragged and not just a mouse click?*/ 
      && dropTactic.insertionFlag==true
//      && ((ori_locx-eq.location.x)*(mouseX-eq.location.x))<0/*released on other side of equals?*/
      && meatright.length>1 /*not last term?*/) {

        interesty=mouseY;
        interestx=mouseX;
        switched=-1;
        arrayNumbersRight[n].dragged=false;
        Rmissing=n;
  
        // handle number insertion
        if (dropTactic.insertionSide==-1 && dropTactic.insertion>=0) {meatleft=popIn(meatleft,meatright[n],dropTactic.insertion+1); meatright=popOut(meatright,n); }
        else if (dropTactic.insertionSide==1 && n==0) {meatright=popIn(meatright,meatright[n],dropTactic.insertion+1); meatright=popOut(meatright,0);}
        else if (dropTactic.insertionSide==1 && dropTactic.insertion>=0 && n>0 && moveVector(n,dropTactic.insertion)==1) {meatright=popIn(meatright,meatright[n],dropTactic.insertion+1); meatright=popOut(meatright,n);}
        else if (dropTactic.insertionSide==1 && dropTactic.insertion>=0 && n>0 && moveVector(n,dropTactic.insertion)==-1) {meatright=popIn(meatright,meatright[n],dropTactic.insertion+1); meatright=popOut(meatright,n+1);}
        else if (dropTactic.insertionSide==0) {meatright = popIn(meatright, signAbsorb(meatright[n],potatoesright[n-1]),0); meatright=popOut(meatright,n+1);}
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion==-1 && n>0) {meatleft=popIn(meatleft , signAbsorb(meatright[n],complement(potatoesright[n-1])) , 0); meatright=popOut(meatright,n); }
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion==-1 && n==0) {meatleft=popIn(meatleft , signAbsorb(meatright[0],"-1") , 0); meatright=popOut(meatright,n); }

  
        // handle operator switch
        //    First consider if we move the first term
        if (dropTactic.insertionSide==-1 && dropTactic.insertion>=0) {
          if (n==0) {
            //handle move-to left side sign
            potatoesleft=popIn(potatoesleft,"-1",dropTactic.insertion);
    
            //handle move-from right side sign absorption.
            // FUTURE: ANIMATE THE ABSORPTION?
            meatright[0]=signAbsorb(meatright[0] , potatoesright[0]);
    
            //shift operators
            potatoesright = deleteFrom(potatoesright,n);
          }
          //   Otherwise just shift all operators back
          else {
            potatoesleft=popIn(potatoesleft , complement(potatoesright[n-1]) , dropTactic.insertion);
            potatoesright=popOut(potatoesright,n-1);
          }
        }
        else if (dropTactic.insertionSide==-1 && dropTactic.insertion==-1) {
          if (n==0) {
            potatoesleft=popIn(potatoesleft,"1",0);
            meatright[0]=signAbsorb(meatright[0] , potatoesright[0]);
            potatoesright=deleteFrom(potatoesright,n);
          }
          else {
            potatoesleft=popIn(potatoesleft, "1",0);
            potatoesright=popOut(potatoesright,n-1);
          }
        }  
        else if (dropTactic.insertionSide>=0) {
          if (n==0) {
            meatright[0]=signAbsorb(meatright[0],potatoesleft[0]);
            potatoesright=popIn(potatoesright,"1",dropTactic.insertion);
            potatoesright=deleteFrom(potatoesright,0);
          }
          else if (n>0 && dropTactic.insertion<n && dropTactic.insertionSide==1) {
            potatoesright=popIn(potatoesright,potatoesright[n-1],dropTactic.insertion);
            potatoesright=popOut(potatoesright,n);
          }
          else if (n>0 && dropTactic.insertion>n && dropTactic.insertionSide==1) {
            potatoesright=popIn(potatoesright,potatoesright[n-1],dropTactic.insertion);
            potatoesright=popOut(potatoesright,n-1);
          }
          else if (n>0 && dropTactic.insertionSide==0) {
            potatoesright=popOut(potatoesright,n-1);
            potatoesright=popIn(potatoesright,"1",0);
          }
        }
      }//end left<-right swaps
    }//end right side loop mouse-release checker
    //flag for dragging something but no switch
    if (switched==0) {
      snapback=true;
    }
  }
}





//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


















Number[] KillNumbers(Number[] all) {
  int s=all.length;
  for (int i=0;i<s;i++) {
    all=(Number[]) shorten(all);
  }
  return all;
}

Operator[] KillOperators(Operator[] all) {
  int s=all.length;
  for (int i=0;i<s;i++) {
    all=(Operator[]) shorten(all);
  }
  return all;
}



void slideEq(Operator thingy, float stepperx, float steppery) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
}

void slideNum(Number thingy, float stepperx, float steppery , int shrinkfactor) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
  thingy.changeSize(shrinkfactor);
}

void slideOp(Operator thingy, float stepperx, float steppery , int shrinkfactor) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
  thingy.changeSize(shrinkfactor);
}

// 0th element is x distance to slide, 1st element is y distance to slide
float[] slideNumDist(Number[] nums, float intx, float inty, int index) {
  float[] answer=new float[2];
  answer[0]=nums[index].location.x-intx;
  answer[1]=nums[index].location.y-inty;
  return answer;
}

float[] slideOpDist(Operator[] ops, float intx, float inty, int index) {
  float[] answer=new float[2];
  answer[0]=ops[index].location.x-intx;
  answer[1]=ops[index].location.y-inty;
  return answer;
}


String Operate(String operation,float num1, float num2) {
  Float answer=0.0;
  if (operation=="-1") {answer=num1-num2;}
  else if (operation=="1") {answer=num1+num2;}
  else if (operation=="2") {answer=num1/num2;}
  else if (operation=="-2") {answer=num1*num2;}
  return answer.toString();
}


String[] popIn(String[] subject, String article, int index) {
  if (index<=subject.length-1) {
    subject = expand(subject, subject.length+1);
    arrayCopy(subject, index, subject, index+1, subject.length-index-1);
    subject[index]=article;
  }
  else if (index==subject.length) {
    subject= (String[]) append(subject,article);
  }
  return subject;
}

String[] popOut(String[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (String[]) shorten(subject);
  }
  return subject;
}

Number[] popNumOut(Number[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (Number[]) shorten(subject);
  }
  else if (subject.length==1) {
    subject = (Number[]) shorten(subject);
  }
  return subject;
}

Operator[] popOpOut(Operator[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (Operator[]) shorten(subject);
  }
  else if (subject.length==1) {
    subject = (Operator[]) shorten(subject);
  }
  return subject;
}

String complement(String operator) {
  String comp="0";
  if (operator=="1") {comp="-1";}
  else if (operator=="-1") {comp="1";}
  else if (operator=="2") {comp="-2";}
  else if (operator=="-2") {comp="2";}
  return comp;
}

String[] deleteFrom(String[] subject, int index) {
  arrayCopy(subject,index+1,subject,index,subject.length-index-1);
  subject = (String[]) shorten(subject);
  return subject;
}

String signAbsorb(String subject , String absorbed) {
  if (absorbed == "-1") {
    if (subject.substring(0,1).equals(NEG)) { //truncate if double negative
      subject=subject.substring(1,subject.length());
    }
    else {
      subject="-"+subject;
    }
  }
  return subject;
}


int moveVector(int missing , int insert) {
  if (missing<insert) {return 1;}
  else if (missing>insert) {return -1;}
  else {return 0;}
}
