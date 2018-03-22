# gtfs-neo4j-import
Tool for [General Transit Feed Specification] import to the [Neo4j] graph database. Converts GTFS ZIP files to CSV files importable to Neo4j.

Projects using this library will benefit from:

  * high-performance import / database creation
  * command-line tool 
  * helper scripts for import and database post-processing (index creation)

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Obtain
The project is implemented in Java and built using [Maven]. The project is available on the central Maven repository.

Example dependency config:

```xml
<dependency>
    <groupId>com.github.skjolber.gtfs-neo4j-import</groupId>
    <artifactId>gtfs-neo4j-import</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# Usage
Build the project, download Neo4j and some GTFS file.

```
java -jar target/gtfs-neo4j-import.jar <input zip file or HTTP URL> <CSV output dir> <script output dir>
```

This will create a set of CSV files, and two additional helper files; `import.sh` and `initialize.cypher`.

### Creating the database
Run the `import.sh` script.

```
./import.sh <Neo4j home dir> <CSV output dir> <database name>
```

Database name is usually `graph.db`. If there is a preexisting database, remove it using the command

```
rm -rf <Neo4j home dir>/data/databases/graph.db
```

### Initialize the database
Run the `initialize.sh` or `setup.sh` script found under `src/main/script`.

```
./initialize.sh <Neo4j home dir> <script output dir>/initalize.cypher <password>
```

where `password` the login password for Neo4j.

The `setup.sh` script has the same syntax but additionally sets the default password on a fresh Neo4j installation.

Then start Neo4j using the command

```
./bin/neo4j console
```

and open a browser at [http://localhost:7474/browser/](http://localhost:7474/browser/). Try the command

```
match (s:Stop) return s limit 25
```

or

```
call db.schema()
```

for the following kind of presentation:

![alt text][db.png]

## Details
The transformation is limited to the files 

| File | Contents |
| -----| - |
| trips.txt | Trips |
| stops.txt | Stops |
| routes.txt | Routes |
| calendar_dates.txt | Dates |
| agency.txt | Agencies |
| stop_times.txt | Stop times |

and converts stop times to `seconds from midnight`.

# Contact
If you have any questions or comments, please email me at thomas.skjolberg@gmail.com.

Feel free to connect with me on [LinkedIn], see also my [Github page].

## License
[Apache 2.0]

# History
 - [1.0.0]: Initial release.

[Apache 2.0]: 			http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:		https://github.com/skjolber/gtfs-neo4j-import/issues
[Maven]:				http://maven.apache.org/
[LinkedIn]:				http://lnkd.in/r7PWDz
[Github page]:			https://skjolber.github.io
[1.0.0]:				https://github.com/skjolber/gtfs-neo4j-import/releases
[Entur GTFS]:           http://www.entur.org/dev/rutedata/
[General Transit Feed Specification]:			https://en.wikipedia.org/wiki/General_Transit_Feed_Specification
[Neo4j]:    			https://neo4j.com
[db.png]: 	https://raw.githubusercontent.com/skjolber/gtfs-neo4j-import/master/docs/images/db.png "Neo4j db schema"