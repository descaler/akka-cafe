package akkacafe

sealed trait Order
case class CoffeeOrder(item:Item) extends Order

