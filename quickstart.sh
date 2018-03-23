if [ "$#" -ne 3 ]; then
    echo "Downloads and installs Neo4j in a target directory with imported GTFS data"
    echo "Usage: <input GTFS zip file> <target directory> <password>"
    exit 1
fi

set -e

NEO4J_URI=http://dist.neo4j.org/neo4j-community-3.3.4-unix.tar.gz

mkdir -p $2/import
cp ./src/main/script/*.sh $2
chmod +x $2/*.sh
mvn clean package

java -jar target/gtfs-neo4j-import.jar $1 $2/import $2

cd $2

echo "Downloading Neo4j.."
curl --fail --show-error --location $NEO4J_URI -o ./neo4j.tar

tar --extract --file ./neo4j.tar

pattern="neo4j-community"
for _dir in *"${pattern}"*; do
    [ -d "${_dir}" ] && dir="${_dir}" && break
done

rm -rf ./$dir/data/databases/graph.db
chmod +x ./import.sh

echo "Importing CSV files to Neo4j.."

./import.sh ./$dir ./import graph.db

echo $dir

echo "Running post configuration.."

./setup.sh $2/$dir $2/initialize.cypher $3

echo "Starting Neo4j.."

./$dir/bin/neo4j console



