#!/usr/bin/env bash
set -x

curl -s -X POST localhost:8080/birthdays -H 'Content-type:application/json' -d '{"birthDate": "2000-06-22", "name": "John McClane"}'
echo
curl -s -X POST localhost:8080/birthdays -H 'Content-type:application/json' -d '{"birthDate": "2002-06-22", "name": "Jim McClane"}'
echo
curl -s -X POST localhost:8080/birthdays -H 'Content-type:application/json' -d '{"birthDate": "2010-06-22", "name": "Tom McClane"}'
echo
curl -s localhost:8080/birthdays
echo
curl -s 'localhost:8080/birthdays/byName/John%20McClane'
echo
curl -s 'localhost:8080/birthdays/byBirthDate/2000-06-22'
echo
curl -s -X PUT localhost:8080/birthdays/3 -H 'Content-type:application/json' -d '{"birthDate": "2020-06-22", "name": "Tim McClane"}'
echo
curl -s 'localhost:8080/birthdays/3'
echo
curl -s -X DELETE 'localhost:8080/birthdays/3'
echo
curl -s 'localhost:8080/birthdays/3'
echo
curl -s 'localhost:8080/birthdays'
echo
curl -s -X DELETE 'localhost:8080/birthdays/1'
echo
curl -s -X DELETE 'localhost:8080/birthdays/2'
echo
curl -s 'localhost:8080/birthdays'
