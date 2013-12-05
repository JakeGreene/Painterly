package ca.jakegreene.painterly

import processing.core.PApplet
import processing.core.PConstants
import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.painting.Point
import ca.jakegreene.painterly.render.LinearStrokeRenderer

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Test")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(800, 600)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  
  val shortStroke = Stroke(Seq(Point(10,20), Point(60, 10)), 1)
  val cross = Stroke(Seq(Point(50, 50), Point(70, 70), Point(70, 50), Point(50, 70)), 5)
  val renderer = new LinearStrokeRenderer(this)
  
  override def setup() {
    size(800, 550, PConstants.P3D);
  }
  
  override def draw() {
    background(255);
    renderer.draw(shortStroke)
    renderer.draw(cross)
  }
}