package parsley.debugger.frontend

import javafx.embed.swing.JFXPanel
import parsley.debugger.frontend.internal._
import parsley.debugger.{DebugTree, ParseAttempt}
import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
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

    val selectedTree: ObjectProperty[Option[DebugTree]]   = ObjectProperty(None)
    val selectedAtt: ObjectProperty[Option[ParseAttempt]] = ObjectProperty(None)

    val inputDisplay = new InputHighlighter(input, selectedAtt)

    Platform.runLater {
      val rendered = new Stage {
        title = "Parsley Tree Visualisation"
        scene = new Scene(960, 600) {
          fill = DefaultBGColour

          content = new ThreeSplitPane(
            this,
            new TreeControls(new TreeDisplay(this, tree, selectedTree)),
            new HBox(new AttemptList(selectedTree, selectedAtt)),
            new HBox(inputDisplay)
          )
        }
      }

      rendered.showAndWait()
      // No need to exit.
    }
  }

  /** Get a FxGUI instance for rendering debug trees. These may be re-used by calling
    * [[FxGUI.render]] again.
    *
    * It is recommended that you assign this to an implicit value before calling
    * [[parsley.debugger.attachDebuggerGUI]].
    *
    * @return
    *   A fresh instance of [[FxGUI]] for displaying debug trees.
    */
  def newInstance: FxGUI.type = this
}
