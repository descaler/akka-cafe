package akkacafe

import akka.actor._
import akka.routing.{Broadcast, RoundRobinRouter}
import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration._

object Cafe extends App {
  implicit val ec = Implicits.global

  val system = ActorSystem("Cafe")

  val cashRegister = system.actorOf(Props[CashRegister], "Register")

  val baristas = system.actorOf(Props(classOf[Barista], cashRegister).
    withRouter(RoundRobinRouter(nrOfInstances = 5)), "Barista")

  val customerBob = system.actorOf(Props(classOf[Customer], baristas, Espresso), "Bob")
  val customerSteve = system.actorOf(Props(classOf[Customer], baristas, Cappuccino), "Steve")

  customerBob ! CaffeineWithdrawal
  customerSteve ! CaffeineWithdrawal

  system.scheduler.scheduleOnce(15.seconds) { baristas ! Broadcast(ClosingTime) }
}