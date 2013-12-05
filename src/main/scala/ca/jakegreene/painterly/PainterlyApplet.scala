package ca.jakegreene.painterly

import processing.core.PApplet
import processing.core.PConstants

class PainterlyApplet extends PApplet {
  override def setup() {
    size(800, 600, PConstants.P3D);
  }
  
  override def draw() {
    background(0);
  }
}