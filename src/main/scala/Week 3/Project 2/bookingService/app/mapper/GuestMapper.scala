package mapper

import Summary.GuestSummary
import models.Guest

object GuestMapper {
  def toSummary(guest: Guest): GuestSummary =
    GuestSummary(
      guest_Id = guest.guest_id,
      full_name = guest.full_name,
      email = guest.email
    )

}
