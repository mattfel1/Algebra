import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Algebra extends PApplet {

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
int SIMPOPCOLOR=color(255,100,0);
int SIMPNUMCOLOR=color(255,0,0);
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
public void setup() {
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
public void draw() {
  
  
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
          arrayNumbersLeft[simplifyLeftAt].nvalue=PApplet.parseFloat(meatleft[simplifyLeftAt]);
          arrayOperatorsLeft=popOpOut(arrayOperatorsLeft , simplifyLeftAt);
        }
        else if (simplifyRightAt>=0) {
          arrayNumbersRight=popNumOut(arrayNumbersRight , simplifyRightAt+1);
          arrayNumbersRight[simplifyRightAt].nvalue=PApplet.parseFloat(meatright[simplifyRightAt]);
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
            meatleft[i]=Operate(potatoesleft[i] , PApplet.parseFloat(meatleft[i]) , PApplet.parseFloat(meatleft[i+1])); //combine terms behind the scenes   
            
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
            meatright[i]=Operate(potatoesright[i] , PApplet.parseFloat(meatright[i]) , PApplet.parseFloat(meatright[i+1]) ); //combine terms behind the scenes    
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
public void keyPressed() {
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
public void mousePressed() {
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
public void mouseReleased() {
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


















public Number[] KillNumbers(Number[] all) {
  int s=all.length;
  for (int i=0;i<s;i++) {
    all=(Number[]) shorten(all);
  }
  return all;
}

public Operator[] KillOperators(Operator[] all) {
  int s=all.length;
  for (int i=0;i<s;i++) {
    all=(Operator[]) shorten(all);
  }
  return all;
}



public void slideEq(Operator thingy, float stepperx, float steppery) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
}

public void slideNum(Number thingy, float stepperx, float steppery , int shrinkfactor) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
  thingy.changeSize(shrinkfactor);
}

public void slideOp(Operator thingy, float stepperx, float steppery , int shrinkfactor) {
  thingy.changePVector(thingy.location.x+stepperx, thingy.location.y+steppery);
  thingy.changeSize(shrinkfactor);
}

// 0th element is x distance to slide, 1st element is y distance to slide
public float[] slideNumDist(Number[] nums, float intx, float inty, int index) {
  float[] answer=new float[2];
  answer[0]=nums[index].location.x-intx;
  answer[1]=nums[index].location.y-inty;
  return answer;
}

public float[] slideOpDist(Operator[] ops, float intx, float inty, int index) {
  float[] answer=new float[2];
  answer[0]=ops[index].location.x-intx;
  answer[1]=ops[index].location.y-inty;
  return answer;
}


public String Operate(String operation,float num1, float num2) {
  Float answer=0.0f;
  if (operation=="-1") {answer=num1-num2;}
  else if (operation=="1") {answer=num1+num2;}
  else if (operation=="2") {answer=num1/num2;}
  else if (operation=="-2") {answer=num1*num2;}
  return answer.toString();
}


public String[] popIn(String[] subject, String article, int index) {
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

public String[] popOut(String[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (String[]) shorten(subject);
  }
  return subject;
}

public Number[] popNumOut(Number[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (Number[]) shorten(subject);
  }
  else if (subject.length==1) {
    subject = (Number[]) shorten(subject);
  }
  return subject;
}

public Operator[] popOpOut(Operator[] subject, int index) {
  if (subject.length>1) {
    arrayCopy(subject,index+1,subject,index,subject.length-1-index);
    subject = (Operator[]) shorten(subject);
  }
  else if (subject.length==1) {
    subject = (Operator[]) shorten(subject);
  }
  return subject;
}

public String complement(String operator) {
  String comp="0";
  if (operator=="1") {comp="-1";}
  else if (operator=="-1") {comp="1";}
  else if (operator=="2") {comp="-2";}
  else if (operator=="-2") {comp="2";}
  return comp;
}

public String[] deleteFrom(String[] subject, int index) {
  arrayCopy(subject,index+1,subject,index,subject.length-index-1);
  subject = (String[]) shorten(subject);
  return subject;
}

public String signAbsorb(String subject , String absorbed) {
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


public int moveVector(int missing , int insert) {
  if (missing<insert) {return 1;}
  else if (missing>insert) {return -1;}
  else {return 0;}
}
class Button {
  String name;
  PVector location;   // Location
  float locx;
  float locy;
  float mass;
  int cc;
  int colour;
  int sizeoftext;
}
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


class Mover {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;
  int c = color(255, 204, 0);

  Mover() {
    location = new PVector(400,50);
    velocity = new PVector(1,0);
    acceleration = new PVector(0,0);
    mass = 1;
  }
  
  public void applyForce(PVector force) {
    PVector f = PVector.div(force,mass);
    acceleration.add(f);
  }
  
  public void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0);
  }

  public void display() {
    stroke(0);
    strokeWeight(2);
    fill(c);
    ellipse(location.x,location.y,16,16);
  }

  public void checkEdges() {

    if (location.x > width) {
      location.x = 0;
    } else if (location.x < 0) {
      location.x = width;
    }

    if (location.y > height) {
      velocity.y *= -1;
      location.y = height;
    }

  }

}



/*

v0.15 - Bug with moves from right to left sliding too far in the animation fixed.  The "slideNum" function was running twice; once for moving the object of interest and
then again for the standard module that slides ALL the characters over.
 - Bug with signs occasionally not changing fixed because last sign was not getting truncated on swithces that involved first-term moves.  I.e. the potatoes array was not fully
 getting moved to the arrayOperators because there were too many potatoes.
 - Fixed simplification animation annoyance by reversing the order that items get drawn :)
 
v0.20 - Totally functional drag/drop for addition/subtraction.  Future versions will fix minor bugs and improve.

v0.21 - Fixed bug with trying to drag another item while equation is still being simplified with "allowInteraction" flag.
 
 
v0.25 - Functionized the insertion checker and fixed all associated, known bugs

v0.26 - Changed potatoes/operators to handle strings rather than integers to determine optype.  
 - Revamped side-switch animation to include tacticalInsertion
 
v0.30 - Totally function addition/subtraction, where terms can be inserted and taken from ANYWHERE
  *** BUG FOUND.  SIGN ABSORBPTION WHEN GOING TO THE LEFTMOST POSITION OR JUST RIGHT OF = SIGN IS REVERSED!  - 20 BECOMES
    -20 RATHER THAN JUST 20 ***
    
    
    
    
    
 */
// Attraction
// Daniel Shiffman <http://www.shiffman.net>

// A class for a draggable attractive body in our world

class Number {
  int cc = color(255, 255, 255);
  float mass;    // Mass, tied to size
  PVector location;   // Location
  boolean dragging = false; // Is the object being dragged?
  boolean rollover = false; // Is the mouse over the ellipse?
  boolean collision = false;
  boolean dragged = false; //remember if we just dragged it
  PVector dragOffset;  // holds the offset for when object is clicked on
  float locx;
  float locy;
  String wvalue;
  float nvalue;  //
  boolean varflag;
  int c;
  int colour;
  int dragcolor=color(100,100,100);
  int rollovercolor=color(200,200,200);
  int sizeoftext;

  Number(float x, float y, String val) {
    locx = x;
    locy = y;
    colour=color(0,0,0);
    c = color(255, 255, 255);
    location = new PVector(locx,locy);
    sizeoftext=TEXTSIZE/2;
    mass = CIRCLEWEIGHT;
    wvalue=val;
    nvalue = PApplet.parseFloat(val);
    varflag=false;
    if (Float.isNaN(nvalue)) {
      varflag=true;
    }
    dragOffset = new PVector(0.0f,0.0f);
  }


  // Method to display
  public void display() {
    /*    DRAW ELLIPSE
    ellipseMode(CENTER);
    strokeWeight(1);
    stroke(0);

    if (dragging) {
      fill (50);
       if (collision) fill(255,0,0);
    }
    else if (rollover) fill(cc);
   
    else fill(c ,200);
    ellipse(location.x,location.y,mass*2,mass*2);
    */
    
    textSize(sizeoftext);
    if (dragging) {fill(dragcolor);}
    else if (rollover) {fill(rollovercolor);}
    else {fill(colour);}
    if (varflag==false) {
      text(PApplet.parseInt(nvalue),location.x-4*mass/5,location.y+mass/2);
    }
    else if (varflag==true) {
      text(wvalue,location.x-4*mass/5,location.y+mass/2);
    }
  }

  // The methods below are for mouse interaction
  public void clicked(int mx, int my) {
    float d = dist(mx,my,location.x,location.y);
    if (d < mass) {
      ori_locx = location.x;
      ori_locy = location.y;
      dragging = true;
      dragged = true;
      dragOffset.x = location.x-mx;
      dragOffset.y = location.y-my;
    }
  }

  public void hover(int mx, int my) {
    float d = dist(mx,my,location.x,location.y);
    if (d < mass && dragged==false) {
      rollover = true;
    } 
    else {
      rollover = false;
    }
  }

  public void stopDragging() {
    dragging = false;
  }

  public void collision() {
    float g = dist(400,100,location.x,location.y);
    if (g < mass) {
      collision = true;
    }
    else {
      collision = false;    
    }
  }
  
  
  public void changePVector(float xx, float yy) {
    location = new PVector(xx,yy);
  }
  
  public void changeSize(int shrinkfactor) {
    sizeoftext=sizeoftext-shrinkfactor;
  }
    
  
  public void drag() {
    if (dragging) {
      location.x = mouseX + dragOffset.x;
      location.y = mouseY + dragOffset.y;
    }
  }

  public void shrink(int siz) {
    sizeoftext=siz;
  } 


}


class Operator {
  int optype; //which operator it is
  String soptype;
  PVector location;   // Location
  float locx;
  float locy;
  float mass;
  int cc;
  int colour;
  int sizeoftext;

  Operator(float x, float y, String val) {
    locx = x;
    locy = y;
    colour = color(0,0,0);
    sizeoftext=TEXTSIZE*3/4;
    cc = color(255, 255, 255);
    location = new PVector(locx,locy);
    mass = CIRCLEWEIGHT;
    soptype = val;
    if (soptype=="0") {optype=0;}
    else if (soptype=="1") {optype=1;}
    else if (soptype=="-1") {optype=-1;}
    else if (soptype=="2") {optype=2;}
    else if (soptype=="-2") {optype=-2;}

  }

  // Method to display
  public void display() {
//    ellipseMode(CENTER);
//    strokeWeight(1);
//    stroke(0);
//    fill(cc);
//    ellipse(location.x,location.y,mass*2,mass*2);
    textSize(sizeoftext);
    fill(colour);  
    pushMatrix();
    translate(-mass,3*mass/4);
    switch(optype) {
      case 0:
        textSize(TEXTSIZE);
        text("=",location.x,location.y);
        break;
      case 1:
        text("+",location.x,location.y);
        break;
      case -1:
        text("-",location.x,location.y);
        break;
      case 2:
        text("*",location.x,location.y);
        break;
      case -2:
        text("/",location.x,location.y);
        break;
    }
    popMatrix();
  }
  
  public void changePVector(float xx, float yy) {
    location = new PVector(xx,yy);
  }
  
  public void changeSize(int shrinkfactor) {
    sizeoftext=sizeoftext-shrinkfactor;
  }
    
  
  public void shrink(int siz) {
    sizeoftext=siz;
  }




}


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
  public void measure(int switched , int insertionSide , int insertion , int Rmissing , int Lmissing , float interestx , float interesty , int rememberSLeft , int rememberSRight , float opJump, Number[] arrayNumbersLeft , Number[] arrayNumbersLeft2 , Number[] arrayNumbersRight , Number[] arrayNumbersRight2 , Operator[] arrayOperatorsLeft , Operator[] arrayOperatorsLeft2 , Operator[] arrayOperatorsRight , Operator[] arrayOperatorsRight2) {
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
  
  
public void nextFunction() {}
  
  
  
  
  
  
  
}
class tacticalInsertion {
  int insertion;
  int insertionSide;   //-1 is left, 1 is right, 0 is equals-sign, 9 is nowhere
  boolean insertionFlag;
  
  
  tacticalInsertion() {
    insertion=1;
    insertionSide=9;
    insertionFlag=false;
  }
  
  
  
  public void scan(float xmouse, Number[] spots, int side, Operator eq) {
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
    
    
    
    
  public void reset(boolean previous) {
    if (previous==false) {
      insertion=-1;
      insertionSide=9;
    }
  }
  
  
  public void clone(tacticalInsertion udder) {
    insertion=udder.insertion;
    insertionSide=udder.insertionSide;
    insertionFlag=udder.insertionFlag;
  }
    
}

        
        
        

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "Algebra" });
  }
}
