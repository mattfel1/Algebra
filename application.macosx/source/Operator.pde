class Operator {
  int optype; //which operator it is
  String soptype;
  PVector location;   // Location
  float locx;
  float locy;
  float mass;
  color cc;
  color colour;
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
  void display() {
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
  
  void changePVector(float xx, float yy) {
    location = new PVector(xx,yy);
  }
  
  void changeSize(int shrinkfactor) {
    sizeoftext=sizeoftext-shrinkfactor;
  }
    
  
  void shrink(int siz) {
    sizeoftext=siz;
  }




}


