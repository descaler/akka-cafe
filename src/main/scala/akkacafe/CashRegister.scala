package akkacafe

import akka.actor._
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import scala.concurrent.duration._

case class Transaction(item: Item)
case class Receipt(amount: Int)

class CashRegister extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(4.seconds)

  val prices = Map[Item, Int](Espresso -> 7, Cappuccino -> 9)
  val printer = context.actorOf(Props[ReceiptPrinter], "Printer")

  var takings = 0

  def receive = {
    case Transaction(item) =>
      val price = prices(item)
      log.info(s"Generating a receipt for $item at $price kuna")
      val requester = sender()
      (printer ? PrintJob(price)).map((requester, _)).pipeTo(self)

    case (requester: ActorRef, receipt: Receipt) =>
      takings += receipt.amount
      log.info(s"Takings increased to $takings kuna")
      requester ! receipt
  }

}
