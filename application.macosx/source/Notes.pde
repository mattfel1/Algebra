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
