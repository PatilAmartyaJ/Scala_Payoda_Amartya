package Services

import javax.inject._
import repositories.SensorReadingByZoneRepository
import models.SensorReadingByZone

@Singleton
class SensorReadingByZoneService @Inject()(repo: SensorReadingByZoneRepository) {

  def getByZone(zoneId: String, limit: Int = 50): List[SensorReadingByZone] =
    repo.findByZone(zoneId,limit)
}
