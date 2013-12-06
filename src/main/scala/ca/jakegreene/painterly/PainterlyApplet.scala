

package ca.jakegreene.painterly

import java.awt.Color
import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.painting.Point
import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.render.LayerRenderers
import processing.core.PApplet
import ca.jakegreene.painterly.render.StrokeRenderers
import processing.core.PConstants
import processing.core.PImage
import ca.jakegreene.painterly.util.Blur
import ca.jakegreene.processing.AdvancedPImage._

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Painterly")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(1800, 900)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  implicit val applet = this
  
  val image = loadImage("Domo_lizard_smaller.png")
  val blurredImage = Blur.gaussian(image, 6)
  var differenceImage: PImage = null
  
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * P3D (needed for depth) does not support these
     * functions
     */
    size(1800, 900, PConstants.P3D);
    frameRate(30)
    differenceImage = image.difference(blurredImage)
  }
  
  override def draw() {
    background(255);
    
    image(image, 0, 0)
    image(blurredImage, 600, 0)
    image(differenceImage, 1200, 0)
  }
}