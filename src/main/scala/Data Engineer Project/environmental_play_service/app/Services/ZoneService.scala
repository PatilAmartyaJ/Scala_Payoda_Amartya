package Services

import jakarta.inject.Inject
import models.Zone
import repositories.ZoneRepository

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ZoneService @Inject()(zoneRepo: ZoneRepository)(implicit ec: ExecutionContext) {
  def createZone(
                  name: String,
                  city: String,
                  latitude: Double,
                  longitude: Double
                 ): Future[Zone] = {


    val zone = Zone.create(name, city, latitude, longitude)

    zoneRepo.create(zone)
  }

  def getAllZones(): Future[Seq[Zone]] = {
    zoneRepo.findAll()
  }

}
