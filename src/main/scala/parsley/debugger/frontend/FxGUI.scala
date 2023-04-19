package parsley.debugger.frontend

import javafx.embed.swing.JFXPanel
import parsley.debugger.{DebugTree, ParseAttempt}
import parsley.debugger.frontend.internal.{
  defaultFont,
  simpleInsets,
  AttemptList,
  DefaultBGColour,
  DefaultBackground,
  InputHighlighter,
  ThreeSplitPane,
  TreeControls,
  TreeDisplay
}
import scalafx.application.Platform
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.Priority
import scalafx.scene.text.{FontWeight, Text}
import scalafx.stage.Stage

/** ScalaFX (on JavaFX) renderer for debug trees. This frontend provides interactive visuals to
  * explore the parse / debug tree of a parser.
  *
  * Nodes can be expanded for information or moved around in order to re-organise them, and a live
  * input display will show what part of the input a parser has tried to parse when its respective
  * node is selected.
  */
class FxGUI private[parsley] () extends DebugGUI {
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

          private val sc      = this
          private val attList = new ScrollPane {
            outer =>
            prefWidth  <== outer.width
            prefHeight <== outer.height

            hbarPolicy = ScrollBarPolicy.Never
            vbarPolicy = ScrollBarPolicy.Never

            content <== Bindings.createObjectBinding(
              () =>
                selectedTree() match {
                  case None       =>
                    new Text {
                      text = "No Tree Selected"
                      font = defaultFont(2, FontWeight.Bold)

                      hgrow = Priority.Always
                      vgrow = Priority.Always

                      alignmentInParent = Pos.Center
                    }.delegate
                  case Some(tree) =>
                    selectedAtt() = None
                    new AttemptList(sc, tree, selectedAtt).delegate
                },
              selectedTree
            )
          }

          content = new ThreeSplitPane(
            this,
            new TreeControls(this, new TreeDisplay(this, tree, selectedTree)),
            attList,
            inputDisplay
          )
        }
      }

      rendered.showAndWait()
      // No need to exit.
    }
  }
}

/** Instance manager for FxGUI instances. */
object FxGUI {
  /** Create a new FxGUI instance for rendering debug trees. These may be re-used by calling
    * [[FxGUI.render]] again.
    *
    * It is recommended that you assign this to an implicit value before calling
    * [[parsley.debugger.attachDebuggerGUI]].
    *
    * @return
    *   A fresh instance of [[FxGUI]] for displaying debug trees.
    */
  def newInstance: FxGUI = new FxGUI
}
