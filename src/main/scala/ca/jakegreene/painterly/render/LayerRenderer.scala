package ca.jakegreene.painterly.render

import ca.jakegreene.painterly.painting.Layer

trait LayerRenderer {
  def draw(layer: Layer, depth: Int)
}

class InOrderRenderer(strokeRenderer: StrokeRenderer) extends LayerRenderer {
  override def draw(layer: Layer, depth: Int) {
    layer.strokes.foreach(strokeRenderer.draw(_, depth))
  }
}