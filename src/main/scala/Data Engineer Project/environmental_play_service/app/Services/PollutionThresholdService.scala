package Services

import jakarta.inject.Inject
import models.PollutionThreshold
import repositories.PollutionThresholdRepository

import scala.concurrent.{ExecutionContext, Future}

class PollutionThresholdService @Inject()(pollutionThreshRepo: PollutionThresholdRepository)(implicit ec: ExecutionContext) {

  def createPollutionThreshold(
                                       zone_id: String,
                                       pm2_5_limit: Double,
                                       pm10_limit: Double,
                                       co2_limit: Double,
                                       alert_level: String
                  ): Future[PollutionThreshold] = {


    val pollutionThreshold = PollutionThreshold.create(zone_id,pm2_5_limit,pm10_limit, co2_limit, alert_level)

    pollutionThreshRepo.create(pollutionThreshold)
  }

  def getAllSensors(): Future[Seq[PollutionThreshold]] = {
    pollutionThreshRepo.findAll()
  }

}
/*
| City      | Zone ID         | PM2.5 | PM10 | CO2  | Alert    |
| --------- | --------------- | ----- | ---- | ---- | -------- |
| Delhi     | ZN-qOO9POi55dOh | 55    | 120  | 1200 | CRITICAL |
| Mumbai    | ZN-hgOhcvR3W1xc | 45    | 100  | 1100 | HIGH     |
| Kolkata   | ZN-V3Vd9d65x0K4 | 50    | 110  | 1150 | HIGH     |
| Chennai   | ZN-41HR9VatcUzl | 35    | 80   | 1000 | MODERATE |
| Ahmedabad | ZN-8bcEkmLdy6cM | 40    | 90   | 1050 | MODERATE |
| Hyderabad | ZN-THpTeOHdvRy3 | 38    | 85   | 1020 | MODERATE |
| Pune      | ZN-9vOmGAJuWYYA | 32    | 70   | 950  | LOW      |
| Bengaluru | ZN-vlBb3LdjmPcS | 28    | 60   | 900  | LOW      |

*/