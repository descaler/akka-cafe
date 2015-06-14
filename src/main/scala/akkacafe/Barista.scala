package akkacafe

import akka.actor._
import akka.pattern.AskTimeoutException
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import scala.concurrent.duration._

case object ComeBackLater
case object ClosingTime

class Barista(cashRegister: ActorRef) extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(4.seconds)

  def receive = {
    case CoffeeOrder(item) =>
      log.info(s"$item request received")

      val receipt = cashRegister ? Transaction(item)

      receipt.map((Cup(item,Filled), _)).recover {
        case _: AskTimeoutException => ComeBackLater
      }.pipeTo(sender())

    case ClosingTime =>
      log.info("Closing time!")
      context.stop(self)
  }

}

