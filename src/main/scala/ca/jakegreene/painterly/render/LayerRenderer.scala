package ca.jakegreene.painterly.render

import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.painting.Stroke

trait LayerRenderer {
  def draw(layer: Layer, depth: Int)
}

class InOrderRenderer(renderStroke: (Stroke, Int) => Unit) extends LayerRenderer {
  override def draw(layer: Layer, depth: Int) {
    layer.strokes.foreach(renderStroke(_, depth))
  }
}