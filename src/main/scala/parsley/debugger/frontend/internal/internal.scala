package parsley.debugger.frontend

import scalafx.geometry.Insets
import scalafx.scene.layout.{
  Background,
  BackgroundFill,
  Border,
  BorderStroke,
  BorderStrokeStyle,
  BorderWidths,
  CornerRadii
}
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.text.{Font, FontWeight}

// This will contain common constants and methods.
package object internal {
  // Default background; #f4f4f4.
  private[frontend] val DefaultBGColour: Color = Color.rgb(244, 244, 244)

  private[frontend] val DefaultBackground: Background = new Background(
    Array(
      new BackgroundFill(
        DefaultBGColour,
        CornerRadii.Empty,
        Insets.Empty
      )
    )
  )

  private[frontend] val SuccessColour: Color = Color.LightGreen

  private[frontend] val FailureColour: Color = Color.rgb(201, 115, 109)

  private[this] def lerp(v0: Double, v1: Double, t: Double): Double =
    (1 - t) * v0 + t * v1

  private[frontend] def lerpColour(c1: Color, c2: Color, t: Double): Color =
    Color.color(
      lerp(c1.red, c2.red, t),
      lerp(c1.green, c2.green, t),
      lerp(c1.blue, c2.blue, t),
      lerp(c1.opacity, c2.opacity, t)
    )

  private[frontend] def simpleBackground(fill: Paint): Background = new Background(
    Array(
      new BackgroundFill(
        fill,
        CornerRadii.Empty,
        Insets.Empty
      )
    )
  )

  private[frontend] def simpleBorder(style: BorderStrokeStyle): Border = new Border(
    new BorderStroke(
      Color.Black,
      style,
      CornerRadii.Empty,
      new BorderWidths(relativeSize(0.2))
    )
  )

  private[frontend] def simpleInsets(multiplier: Double): Insets =
    Insets(relativeSize(multiplier))

  // For easy use, all sizes will be relative to the default font size.
  private[frontend] def relativeSize(multiplier: Double): Double =
    Font.default.size * multiplier

  private[frontend] def defaultFont(
    multiplier: Double,
    weight: FontWeight = FontWeight.Normal
  ): Font =
    Font(Font.default.family, weight, relativeSize(multiplier))

  private[frontend] def monoFont(
    multiplier: Double,
    weight: FontWeight = FontWeight.Normal
  ): Font =
    Font("monospace", weight, relativeSize(multiplier))
}
