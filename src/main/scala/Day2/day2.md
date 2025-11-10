
Priority - Immutability
Functional Programming language
Also Supports Object oriented Programming
No Multiple inheritance
No Interface(instead we have trait)
No Concept of static keyword
Instead we have concept called Companion Object
Class are of two types in scala - Normal class and Case class






| Feature                | Normal Class                | Case Class                        |
    |------------------------|----------------------------|-----------------------------------|
| Syntax                 | `class MyClass {}`         | `case class MyClass()`            |
| Immutability           | Mutable by default         | Immutable by default              |
| Equality               | Reference equality         | Structural equality               |
| Pattern Matching       | Not supported              | Supported                         |
| Copy Method            | Not available              | Available (`copy()`)              |
| Serialization          | Not serializable by default| Serializable by default           |
| Companion Object       | Not auto-generated         | Auto-generated                    |
| Useful for             | General purpose classes    | Modeling immutable data           |










| Use of `static`         | Description                       | Common Use Case        |
| ----------------------- | --------------------------------- | ---------------------- |
| **Static variable**     | Shared by all objects             | Counters, constants    |
| **Static method**       | Belongs to the class              | Utility/helper methods |
| **Static block**        | Runs once when class loads        | Initialize static data |
| **Static nested class** | Nested class independent of outer | Logical grouping       |
| **Static import**       | Import static members             | Cleaner syntax         |



hashcode() is not about correctness of equality itself, it’s about efficiency in hash-based collections like:
Set
Map / HashMap
HashSet
How it works:
When you insert an object into a HashSet, the hash code decides which "bucket" the object goes into.
Only objects in the same bucket are compared using equals.
Without hashCode, the collection would have to check every single element with equals, which is very slow.
Even if equals compares all fields, without a proper hashCode: