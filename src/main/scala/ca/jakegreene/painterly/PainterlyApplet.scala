

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
    
    frame.setSize(600, 450)
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
    size(600, 450, PConstants.P3D);
    frameRate(30)
    noLoop()
    noStroke()
  }
  
  override def draw() {
    background(255);
    image(image, 0, 0)
    val strokePoints = painter.findStrokePoints(image, 8)
    strokePoints.foreach {
      case (x, y, size) => {
        val colour = image.pixels(x + (y*image.width))
        fill(colour)
        ellipse(x, y, size, size)
      }
    }
  }
}