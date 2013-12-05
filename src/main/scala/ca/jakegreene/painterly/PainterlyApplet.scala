package ca.jakegreene.painterly

import processing.core.PApplet
import processing.core.PConstants
import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.painting.Point
import ca.jakegreene.painterly.render.LinearStrokeRenderer

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Painterly")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(800, 600)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  
  val shortStroke = Stroke(Seq(Point(10,20), Point(60, 10)), 1)
  val cross = Stroke(Seq(Point(50, 50), Point(300, 300), Point(300, 50), Point(50, 300)), 5)
  val renderer = new LinearStrokeRenderer(this)
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * Consider using P2D or P3D only if speed is
     * becoming a larger issue than smooth strokes
     */
    size(800, 600);
  }
  
  override def draw() {
    background(255);
    renderer.draw(shortStroke)
    renderer.draw(cross)
  }
}