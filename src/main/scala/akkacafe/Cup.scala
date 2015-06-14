package akkacafe

sealed trait CupState
case object Clean extends CupState
case object Filled extends CupState
case object Dirty extends CupState

case class Cup(item:Item, state: CupState)
