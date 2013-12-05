package ca.jakegreene.painterly.painting

case class Point(x: Int, y: Int)
case class Stroke(points: Seq[Point])