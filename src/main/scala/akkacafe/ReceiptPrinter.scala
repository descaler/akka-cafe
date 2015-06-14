package akkacafe

import akka.actor._
import scala.util.Random

case class PrintJob(amount: Int)

class PaperJamException(msg: String) extends Exception(msg)

class ReceiptPrinter extends Actor with ActorLogging {

  var paperJam = false

  def receive = {
    case PrintJob(amount) => sender ! createReceipt(amount)
  }

  def createReceipt(price: Int): Receipt = {
    if (Random.nextBoolean()) paperJam = true
    if (paperJam) throw new PaperJamException("Oh no, the printer is jammed again!")
    Receipt(price)
  }

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    log.info(s"Restarted, jammed status = $paperJam")
  }

}
