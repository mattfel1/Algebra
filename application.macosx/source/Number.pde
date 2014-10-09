// Attraction
// Daniel Shiffman <http://www.shiffman.net>

// A class for a draggable attractive body in our world

class Number {
  color cc = color(255, 255, 255);
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
  color c;
  color colour;
  color dragcolor=color(100,100,100);
  color rollovercolor=color(200,200,200);
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
    nvalue = float(val);
    varflag=false;
    if (Float.isNaN(nvalue)) {
      varflag=true;
    }
    dragOffset = new PVector(0.0,0.0);
  }


  // Method to display
  void display() {
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
      text(int(nvalue),location.x-4*mass/5,location.y+mass/2);
    }
    else if (varflag==true) {
      text(wvalue,location.x-4*mass/5,location.y+mass/2);
    }
  }

  // The methods below are for mouse interaction
  void clicked(int mx, int my) {
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

  void hover(int mx, int my) {
    float d = dist(mx,my,location.x,location.y);
    if (d < mass && dragged==false) {
      rollover = true;
    } 
    else {
      rollover = false;
    }
  }

  void stopDragging() {
    dragging = false;
  }

  void collision() {
    float g = dist(400,100,location.x,location.y);
    if (g < mass) {
      collision = true;
    }
    else {
      collision = false;    
    }
  }
  
  
  void changePVector(float xx, float yy) {
    location = new PVector(xx,yy);
  }
  
  void changeSize(int shrinkfactor) {
    sizeoftext=sizeoftext-shrinkfactor;
  }
    
  
  void drag() {
    if (dragging) {
      location.x = mouseX + dragOffset.x;
      location.y = mouseY + dragOffset.y;
    }
  }

  void shrink(int siz) {
    sizeoftext=siz;
  } 


}


