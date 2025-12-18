case class PollutionThreshold(
                               zoneId: String,
                               pm25Limit: BigDecimal,
                               pm10Limit: BigDecimal,
                               co2Limit: BigDecimal,
                               alertLevel: String
                             )
