package Services

import jakarta.inject.Inject
import models.{DailyPollutionSummary, Zone}
import repositories.{DailyPollutionSummaryRepository, PollutionThresholdRepository}

import java.sql.Date
import scala.concurrent.{ExecutionContext, Future}

class DailyPollutionSummaryService @Inject()(dailyPollutionSumRepo: DailyPollutionSummaryRepository)(implicit ec: ExecutionContext) {

  def getAllSummaries(): Future[Seq[DailyPollutionSummary]] = {
    dailyPollutionSumRepo.findAll()
  }

  def getByDateAndZone(date:Date,zone_id:String): Future[Option[DailyPollutionSummary]]={
    dailyPollutionSumRepo.findByZoneAndDate(date,zone_id)
  }



}
