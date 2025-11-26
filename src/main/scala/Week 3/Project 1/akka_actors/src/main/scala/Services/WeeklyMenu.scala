package Services


object WeeklyMenu {

  val weeklyMenu: Map[String, Map[String, Seq[String]]] = Map(

    "MONDAY" -> Map(
      "Veg" -> Seq(
        "Tomato Basil Soup",
        "Paneer Tikka",
        "Paneer Butter Masala",
        "Dal Tadka",
        "Veg Biryani",
        "Gulab Jamun"
      ),
      "NonVeg" -> Seq(
        "Chicken Clear Soup",
        "Chicken Tikka",
        "Butter Chicken",
        "Chicken Curry",
        "Chicken Biryani",
        "Caramel Custard"
      )
    ),

    "TUESDAY" -> Map(
      "Veg" -> Seq(
        "Sweet Corn Soup",
        "Crispy Corn",
        "Kadai Paneer",
        "Aloo Gobi Masala",
        "Veg Pulao",
        "Rasmalai"
      ),
      "NonVeg" -> Seq(
        "Hot & Sour Chicken Soup",
        "Chicken 65",
        "Mutton Rogan Josh",
        "Egg Curry",
        "Egg Biryani",
        "Brownie with Ice Cream"
      )
    ),

    "WEDNESDAY" -> Map(
      "Veg" -> Seq(
        "Veg Clear Soup",
        "Hara Bhara Kebab",
        "Veg Kofta Curry",
        "Dal Fry",
        "Jeera Rice",
        "Fruit Custard"
      ),
      "NonVeg" -> Seq(
        "Chicken Pepper Soup",
        "Fish Fingers",
        "Home Style Chicken Curry",
        "Prawn Masala",
        "Fish Biryani",
        "Red Velvet Cake Slice"
      )
    ),

    "THURSDAY" -> Map(
      "Veg" -> Seq(
        "Hot & Sour Veg Soup",
        "Veg Spring Rolls",
        "Palak Paneer",
        "Veg Handi",
        "Tandoori Roti + Rice",
        "Ice Cream (Vanilla)"
      ),
      "NonVeg" -> Seq(
        "Chicken Clear Soup",
        "Tandoori Chicken (Quarter)",
        "Mutton Keema Masala",
        "Egg Masala",
        "Chicken Fried Rice",
        "Ice Cream (Chocolate)"
      )
    ),

    "FRIDAY" -> Map(
      "Veg" -> Seq(
        "Lemon Coriander Soup",
        "Paneer Pakora",
        "Paneer Lababdar",
        "Chana Masala",
        "Veg Noodles",
        "Kheer"
      ),
      "NonVeg" -> Seq(
        "Chicken Soup",
        "BBQ Chicken Wings",
        "Chicken Korma",
        "Fish Curry (Konkani)",
        "Chicken Noodles",
        "Caramel Pudding"
      )
    ),

    "SATURDAY" -> Map(
      "Veg" -> Seq(
        "Manchow Veg Soup",
        "Veg Momos",
        "Shahi Paneer",
        "Dal Makhani",
        "Veg Fried Rice",
        "Chocolate Mousse"
      ),
      "NonVeg" -> Seq(
        "Chicken Manchow Soup",
        "Chicken Momos",
        "Prawn Curry",
        "Mutton Handi",
        "Chicken Fried Rice",
        "Blueberry Cheesecake"
      )
    ),

    "SUNDAY" -> Map(
      "Veg" -> Seq(
        "Veg Hot Pot Soup",
        "Paneer Malai Tikka",
        "Paneer Pasanda",
        "Mix Veg Curry",
        "Butter Naan + Rice",
        "Kulfi"
      ),
      "NonVeg" -> Seq(
        "Chicken Hot Pot Soup",
        "Prawn Koliwada",
        "Chicken Tawa Masala",
        "Fish Tikka",
        "Mutton Biryani",
        "Rabdi"
      )
    )
  )

  /** Generate menu for a specific day */
  def menuFor(day: String): String = {
    weeklyMenu.get(day).map { m =>
      s"""
         |-----------------------------------------
         | 🍽 *$day Menu*
         |-----------------------------------------
         |
         | 🥗 **VEG DISHES**
         | ${m("Veg").mkString("\n | • ", "\n | • ", "")}
         |
         | 🍗 **NON-VEG DISHES**
         | ${m("NonVeg").mkString("\n | • ", "\n | • ", "")}
         |
         |-----------------------------------------
         |""".stripMargin
    }.getOrElse("No menu found for this day.")
  }

}

