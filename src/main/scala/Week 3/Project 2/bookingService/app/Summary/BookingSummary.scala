package Summary

import java.time.LocalDate

case class BookingSummary( booking_Id:String,
                           hotel_Id: String,
                           room_Id: String,
                           guest_Id: String,
                           check_In_Date: LocalDate,
                           check_Out_Date: LocalDate,
                           booking_Status: String,
                           number_Of_Guests: Int,
                           rate_Per_Night: BigDecimal,
                           total_Amount: Option[BigDecimal],
                           payment_Status: String,
                           payment_Method: Option[String],

                         )
