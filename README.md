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

# Quickstart
Run the `quickstart.sh` script for a working demo of the below. 

# Usage

Build the project using the command

    mvn clean package
    
then run the utility

```
java -jar target/gtfs-neo4j-import.jar <input> <CSV output dir> <Script output dir>
```

with the paramters

| Name | Description | Example |
| -------- | ----------- | ------- |
| input | GTFS input file (path or URL) | http://host/gtfs.zip |
| CSV output dir | Output folder for CSV files (which fill be imported into Neo4j) | /tmp/neo4j/csv | 
| Script output dir | Output folder for helper scripts: | /tmp/neo4j/script|
| | `import.sh` | Script for `neo4j-admin import` command line. |
| | `initialize.cypher` | Cypher initialization script. For creating indexes after import. |

### Creating the database
Run the generated `import.sh` script.

```
./import.sh <neo4j home> <CSV input dir> <database name>
```

with the paramters

| Name | Description | Example |
| -------- | ----------- | ------- |
| neo4j home | Neo4j home directory| /opt/neo4j |
| CSV input dir | Input folder for CSV files (same as CSV output dir above). | /tmp/neo4j/csv |
| database name | Name of the imported database | graph.db |

Database name is usually `graph.db`. If there is a preexisting database, remove it using the command

```
rm -rf <Neo4j home dir>/data/databases/graph.db
```

### Initialize the database
Run the `initialize.sh` or `setup.sh` script found under `src/main/script`.

```
./initialize.sh <neo4j home> <cypher script> <password>
```

with the paramters

| Name | Description | Example |
| -------- | ----------- | ------- |
| neo4j home | Neo4j home directory| /opt/neo4j |
| cypher script | Path to cypher script used for initialization, typically to `initialize.cypher` | /tmp/neo4j/script/initialize.cypher |
| password | Password for neo4j user on the Neo4j runtime | abcdef |

The `setup.sh` script performs the same function but additionally sets the default password on a fresh Neo4j installation.

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

# See also
Related links

  * [hamburg-gtfs-neo4j](https://github.com/aamalik/hamburg-gtfs-neo4j)
  * [neo4j-gtfs](https://github.com/tguless/neo4j-gtfs)
  * [Loading General Transport Feed Spec (GTFS) files into Neo4j - part 1/2](http://blog.bruggen.com/2015/11/loading-general-transport-feed-spec.html)
  * [Querying GTFS data - using Neo4j 2.3 - part 2/2](http://blog.bruggen.com/2015/11/querying-gtfs-data-using-neo4j-23-part.html)
  


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
[download]:         https://neo4j.com/download/