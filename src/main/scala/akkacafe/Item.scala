package akkacafe

sealed trait Item
case object Espresso extends Item
case object Cappuccino extends Item
