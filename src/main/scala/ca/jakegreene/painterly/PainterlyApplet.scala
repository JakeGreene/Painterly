

package ca.jakegreene.painterly

import ca.jakegreene.painterly.painting.PainterlyCreator
import ca.jakegreene.painterly.util.Blur
import ca.jakegreene.processing.AdvancedPImage.PImage2AdvancedPImage
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Painterly")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(1000, 700)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  implicit val applet = this
  
  val image = loadImage("Domo_lizard_smaller.png")  
  val painter = new PainterlyCreator()
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * P3D (needed for depth) does not support these
     * functions
     */
    size(1000, 700, PConstants.P3D);
    frameRate(30)
    noLoop()
    noStroke()
  }
  
  override def draw() {
    background(255);
    image(image, 0, 0)
    drawLayer(20)
    drawLayer(10)
    drawLayer(5)
  }
  
  private def drawLayer(strokeSize: Int) {
    val strokePoints = painter.findStrokePoints(image, strokeSize)
    strokePoints.foreach {
      case (x, y, size) => {
        val colour = image.pixels(x + (y*image.width))
        fill(colour)
        ellipse(x, y, size, size)
      }
    }
  }
}