

package ca.jakegreene.painterly

import java.awt.Color
import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.painting.Point
import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.render.LayerRenderers
import processing.core.PApplet
import ca.jakegreene.painterly.render.StrokeRenderers
import processing.core.PConstants
import ca.jakegreene.painterly.util.Convolver
import processing.core.PImage
import ca.jakegreene.painterly.util.Blur

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Painterly")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(1440, 900)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  val image = loadImage("Domo_lizard_smaller.jpg")
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * P3D (needed for depth) does not support these
     * functions
     */
    size(1440, 900, PConstants.P3D);
    frameRate(30)
  }
  
  override def draw() {
    background(255);
    val blurredImage = Blur.gaussian(image, mouseY >>> 6)
    image(blurredImage, 0, 0)
  }
}