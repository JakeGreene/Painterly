package ca.jakegreene.painterly.render

import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.painting.Stroke

object LayerRenderer {
  def drawInOrder(renderStroke: (Stroke, Int) => Unit)(layer: Layer, depth: Int) {
    layer.strokes.foreach(renderStroke(_, depth))
  }
}
