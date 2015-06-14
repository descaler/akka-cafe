package akkacafe

import akka.actor._
import scala.concurrent.duration._

case object CaffeineWithdrawal

class Customer(coffeeSource:ActorRef, favourite:Item) extends Actor with ActorLogging {

  implicit val ec = context.dispatcher

  context.watch(coffeeSource)

  def receive = {
    case CaffeineWithdrawal =>
      log.info(s"Caffeine withdrawal for $self!")
      coffeeSource ! CoffeeOrder(favourite)

    case (Cup(item, filled), Receipt(amount)) =>
      log.info(s"Yes, got a $filled cup of $item for $self!")

    case ComeBackLater =>
      log.info("Come on, I need my caffeine fix!!!")
      context.system.scheduler.scheduleOnce(300.millis) {
        coffeeSource ! CoffeeOrder(favourite)
      }

    case Terminated(actorRef) =>
      log.info("Closing?  Oh well, I'll go somewhere else for my coffee...")
  }
}