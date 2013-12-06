package ca.jakegreene.painterly

import processing.core.PApplet
import processing.core.PConstants
import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.painting.Point
import java.awt.Color
import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.render.StrokeRenderers
import ca.jakegreene.painterly.render.LayerRenderers

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
  
  implicit val renderer = this
  
  val crossPoints = Seq(Point(50, 50), Point(300, 300), Point(300, 50), Point(50, 300))
  val cross = Stroke(crossPoints, 5, Color.ORANGE)
  val offsetPoints = crossPoints.map{case Point(x, y) => Point(x + 10, y + 5)}
  val offsetCross = Stroke(offsetPoints, 3, Color.BLUE)
  val layer = Layer(Seq(cross))
  val bottomLayer = Layer(Seq(offsetCross))
  
  val renderLayer = LayerRenderers.drawInOrder(StrokeRenderers.drawLinear) _
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * P3D (needed for depth) does not support these
     * functions
     */
    size(800, 600, PConstants.P3D);
  }
  
  override def draw() {
    background(255);
    renderLayer(layer, 1)
    renderLayer(bottomLayer, 0)
  }
}