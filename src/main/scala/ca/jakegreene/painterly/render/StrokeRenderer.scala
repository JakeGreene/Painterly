package ca.jakegreene.painterly.render

import ca.jakegreene.painterly.painting.Stroke
import processing.core.PApplet
import processing.core.PConstants


trait StrokeRenderer {
  def draw(stroke: Stroke)
}

class LinearStrokeRenderer(renderer: PApplet) extends StrokeRenderer {
  def draw(stroke: Stroke) {
    renderer.strokeWeight(stroke.width)
    renderer.strokeCap(PConstants.ROUND)
    renderer.stroke(stroke.colour.getRed(), stroke.colour.getGreen(), stroke.colour.getBlue())
    // [ (p0, p1), (p1, p2), ... (p_n-2, p_n-1) ]
    val segments = stroke.points.init zip stroke.points.tail
    segments.foreach {case (start, end) => renderer.line(start.x, start.y, end.x, end.y)}
  }
}