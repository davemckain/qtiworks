#!/bin/sh
dropdb qtiworks
createdb -O qtiworks qtiworks
mvn -DSchemaSetup exec:exec
mvn -DUserImporter exec:exec
