# geohash_db

This project is a Java implementation of an in-memory database to  store 'GeoHash'es in a scalable and extensible fashion, using various software design patterns
and features of Java. I started this project as a graduate school assignment, and will incrementally add to it for practice as I get time.

The main features of this project include:

 1. Efficient DB operations
  - Inserting a single location data point to the DB in O(logN) time
  - Deleting a single location to the DB in O(logN)
  - Deleting all data between a range of latitude and longitude in O(logN) time
  - Search for a single data point in O(logN) time
  - Search for all data points in a specified lat/lon range. This operation is O(N), given the worst case that we search for every data point in the DB.
  
 2. Extensible storage of different kinds of data in the DB, along with aggregation of any attributes, regardless of data type.
  - 'DataAndPosition' interface used to encapsulate any 'data' Object with a Position. Anything stored in the DB needs a position (eg - location) but can have data
  of any type.
  - By passing an 'AttributeStrategy' to ProximityStreamDb based on what type of data is being stored, we are able to return max, min, average, and an
  attribute histogram for data between ranges of locations stored in the DB. 
 
 3. Query engine for operations on the database. Only queries supported so far are in the format: 
 (find
  (near \<lat\> \<lon\> <bitsOfPrecision>)
  (where
    (and 
      (> :<atrribute> <value>)
      (> :<attribute> <value>)
    )
  )

  This is implemented by parsing the original expression into an Abstract Syntax Tree, and then building an Expression Tree by running a Visitor through it.
  
Some fun additions I would like to make to this project for practice in the future include:
  1. Using a persistent data structure to store DB history, rather than rebuilding from scratch.
  2. Optimizing the expression parser to use the Builder pattern rather than my almost hard-coded implementation of the Visitor.
  3. After step 2), extend the query language to support a multitude of different commands for the different functionalities of this DB. This would include:
    - 'Less than' and 'equal to' should be options of the where expression
    - All find, delete, and insert operations. Delete should be able to support attribute conditions in a similar way to how we do it for 'find'.
    - History search
    - Streaming aggregations (eg - max, min, average, histogram)  
    
  4. Removing the dependency on DataAndPosition from CompositeTree. It should be able to just store positions
  5. Optimize some of the functional programming operations; maybe add concurrency.
  
 Thanks for reading! Please email ishankaul2000@gmail.com for any questions, suggestions or feedback.
  
