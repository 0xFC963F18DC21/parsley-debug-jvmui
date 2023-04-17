package parsley.debugger.frontend

import parsley.debugger.DebugTree

/** ScalaFX (on JavaFX) renderer for debug trees. This frontend provides interactive visuals to
  * explore the parse / debug tree of a parser.
  *
  * Nodes can be expanded for information or moved around in order to re-organise them, and a live
  * input display will show what part of the input a parser has tried to parse when its respective
  * node is selected.
  */
class FxGUI private[parsley] () extends DebugGUI {
  override def render(input: => String, tree: => DebugTree): Unit =
    throw new NotImplementedError("Need to figure out ScalaFX first.")
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
