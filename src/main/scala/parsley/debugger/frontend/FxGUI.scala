package parsley.debugger.frontend

import javafx.embed.swing.JFXPanel
import parsley.debugger.DebugTree
import parsley.debugger.frontend.internal._
import scalafx.application.Platform
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.stage.Stage

/** ScalaFX (on JavaFX) renderer for debug trees. This frontend provides interactive visuals to
  * explore the parse / debug tree of a parser.
  *
  * Nodes can be expanded for information or moved around in order to re-organise them, and a live
  * input display will show what part of the input a parser has tried to parse when its respective
  * node is selected.
  */
case object FxGUI extends DebugGUI {
  override def render(input: => String, tree: => DebugTree): Unit = {
    // This forces initialisation of JavaFX's internals.
    // We don't actually need this for anything other than that.
    new JFXPanel()

    val selectedTree: ObjectProperty[Option[DebugTree]] = ObjectProperty(None)
    val zoomLevel: DoubleProperty                       = DoubleProperty(1.0)

    val inputDisplay = new InputHighlighter(input, selectedTree)

    Platform.runLater {
      val rendered = new Stage {
        title = "Parsley Tree Visualisation"
        scene = new Scene(960, 600) {
          fill = DefaultBGColour

          content = new ThreeSplitPane(
            this,
            new TreeControls(new TreeDisplay(this, tree, selectedTree, zoomLevel), zoomLevel),
            new AttemptInfo(selectedTree),
            new HBox(inputDisplay)
          )
        }
      }

      rendered.showAndWait()
      // No need to exit.
    }
  }
}
