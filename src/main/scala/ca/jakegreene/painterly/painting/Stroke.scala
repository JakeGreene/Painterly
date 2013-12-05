package ca.jakegreene.painterly.painting

import java.awt.Color

case class Point(x: Int, y: Int)
case class Stroke(points: Seq[Point], width: Int, colour: Color)