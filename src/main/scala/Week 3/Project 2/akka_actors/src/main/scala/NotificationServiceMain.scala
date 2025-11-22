//import Actors.{RoomServiceActor, WiFiServiceActor}
//import Actors.{NotificationCoordinatorActor, RestaurantServiceActor}
//import akka.actor.typed.ActorSystem
//import akka.actor.typed.scaladsl.Behaviors
//import kafka.KafkaBookingConsumer
//
//object NotificationServiceMain extends App {
//
//  val system = ActorSystem[Nothing](Behaviors.setup[Nothing] { ctx =>
//
//    val roomService       = ctx.spawn(RoomServiceActor(), "RoomServiceActor")
//    val wifiService       = ctx.spawn(WiFiServiceActor(), "WiFiServiceActor")
//    val restaurantService = ctx.spawn(RestaurantServiceActor(), "RestaurantServiceActor")
//
//    val coordinator = ctx.spawn(
//      NotificationCoordinatorActor(roomService, wifiService, restaurantService),
//      "CoordinatorActor"
//    )
//
//    KafkaBookingConsumer.start(coordinator)(ctx.system)
//
//    Behaviors.empty
//  }, "NotificationServiceSystem")
//}
//
