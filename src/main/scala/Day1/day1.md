<span style="color:cyan; font-weight:bold;">Day 1- 05-11-2025 </span>

There are two ways of declaring variables in the scala
 * var => **Mutable Variables**
 * val=> **Immutable Variables**

**📌NOTE📌**:- You can explicitly declare the type of the variable, scala support **type inference** so if
we don't mention the type scala compiler will automatically detect type of variable.

**📌NOTE📌**:- If we are try to reassign, val variables it will throw error **Reassignment to val**

<span style="color:cyan; font-weight:bold;">DataTypes in Scala </span>
  
| **Sr no** | **DataType** | **Description (Range)**                                                    | **Default Value** |
|-----------|--------------|----------------------------------------------------------------------------|--|
| 1         | Byte         | 8-bit numbers are represented -128 to 127                                  | 0 |
| 2         | Short        | 16-bit numbers are represented -32768 to 32767                             | 0 |
| 3         | Int          | 32-bit numbers are represented -2147483648 to 2147483647                   | 0 |
| 4         | Long         | 64-bit numbers are represented -9223372036854775808 to 9223372036854775807 | 0L |
| 5         | Float        | 32-bit decimal numbers are represented                                     | 0.0F |
| 6         | Double       | 64-bit decimal numbers are represented                                     | 0.0D |
| 7         | Char         | 16 bit unsigned unicode characters are represented.                        | '\u000' |
| 8         | String       | A sequence of characters                                                   | null |
| 9         | Boolean      | true or false value of literal are represented                             | False |
| 10        | Unit         | similar to void datatype in java, corresponds to no value                  | No default value |
| 11        | Null         | Null or empty reference                                                    | No default value |
| 12        | Nothing      | Subtype of every type                                                      | No default value |
| 13        | Any          | Supertype of any type                                                      | No default value |
| 14        | AnyRef       | Supertype of any reference type.                                           | No default value |

**📌NOTE📌**:- To initialize these variables with default values, you must use **var** for Initialization. Initialization with **val** will throw an error.
This is probable error, if you are going to use val **Unbound placeholder parameter; incorrect use of _**

<span style="color:cyan; font-weight:bold;">Different ways of String declarations</span>

 * Putting in ""
 * Scala Supports multiple line Strings you need to put """ String Content """ within triple quotes
 * You can use string Interpolation

**📌NOTE📌**:- Scala string concatenation is done with **+** and **concat** method str1.concat(str2)  
**📌NOTE📌**:- If you want your escape characters should be neglect you can write String as raw"String Content"  

<span style="color:cyan; font-weight:bold;">String Interpolation Specifiers </span>


| **Specifier** | **Type**          | **Meaning**                   | **Example**            |
|---------------|-------------------|-------------------------------|------------------------|
| %d           | Integer           | Decimal integer               | f"$x%d" → 42 |
| %f           | Float / Double    | Floating -point number        | f"$pi%1.2f" → 3.14 |
| %s           | String            | String value                  | f"$name%s" → "Alice" |
| %c           | Char              | Single character              | f"$ch%c" → 'A' |
| %b           | Boolean           | true / false                  | f"$flag%b" |
| %x           | Integer           | Hexadecimal                   | f"$num%x" → "2a" |
| %e           | Float / Double    | Scientific notation           | f"$pi%e" → "3.141593e+00" |
| %o           | Integer           | Octal representation          | f"$num%o" → "52" |
| %t           | Date / Time       | Date / time formatting        | f"$date%tY" → "2025" (year) |

**📌NOTE📌**:-    **%[width].[precision]f**, %f for floating number  
<span style="color:orange; font-weight:bold;">width (2)</span> → The minimum total number of characters to display (including digits, decimal point, and spaces for padding).
If the number is longer, it just expands — it’s not truncated.  
<span style="color:orange; font-weight:bold;">precision (.2)</span> → Number of digits after the decimal point.


<span style="color:cyan; font-weight:bold;">Taking Input from User </span>


You need to <span style="color:yellow; font-weight:bold;">import scala.io.StdIn.</span> library to take inputs from user.  

<span style="color:orange; font-weight:bold;">readLine()</span> => for reading String  
<span style="color:orange; font-weight:bold;">readInt()</span> => for reading Integer  
<span style="color:orange; font-weight:bold;">readDouble()</span> => for reading Double  
<span style="color:orange; font-weight:bold;">readBoolean()</span> => for reading Boolean

<span style="color:cyan; font-weight:bold;">Collections in Scala</span>

Scala has two type of collections:
1. Mutable Collections: - HashMap, ArrayBuffer, HashMap
2. Immutable collections :- List, Vector, set, Map 

| Collection     | Ordered | Mutable | Allows Duplicates | Example             |
| -------------- | ------- | ------- | ----------------- | ------------------- |
| **List**       | ✅       | ❌       | ✅                 | `List(1, 2, 2)`     |
| **Set**        | ❌       | ❌/✅     | ❌                 | `Set(1, 2)`         |
| **Map**        | ❌       | ❌/✅     | ❌ (keys)          | `Map("a"->1)`       |
| **Array**      | ✅       | ✅       | ✅                 | `Array(1, 2, 3)`    |
| **Vector**     | ✅       | ❌       | ✅                 | `Vector(1, 2, 3)`   |
| **ListBuffer** | ✅       | ✅       | ✅                 | `ListBuffer(1,2,3)` |

Map

| Feature                         | Immutable Map                | Mutable Map                |
| ------------------------------- | ---------------------------- | -------------------------- |
| Package                         | `scala.collection.immutable` | `scala.collection.mutable` |
| Default in Scala?               | ✅ Yes                        | ❌ No                       |
| Can modify in place?            | ❌ No                         | ✅ Yes                      |
| Safe for functional programming | ✅                            | ⚠️ No                      |
| Example                         | `Map("a"->1)`                | `mutable.Map("a"->1)`      |






