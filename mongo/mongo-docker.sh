# Create a container from the mongo image, 
#  run is as a daemon (-d), expose the port 27017 (-p),
#  set it to auto start (--restart)
#  and with mongo authentication (--auth)
# Image used is https://hub.docker.com/_/mongo/
docker pull mongo
docker run --name mongo_docker --restart=always -d -p 27017:27017 mongo mongod --auth

# Using the mongo "localhost exception" (https://docs.mongodb.org/v3.0/core/security-users/#localhost-exception) 
# add a root user

# bash into the container
sudo docker exec -i -t mongo_docker bash

# connect to local mongo
mongo

# create the first admin user
use admin
db.createUser({user:"r_user",pwd:"r_pass",roles:[{role:"root",db:"admin"}]})

# exit the mongo shell
exit
# exit the container
exit

sudo docker exec -i -t mongo_docker bash
# now you can connect with the admin user (from any mongo client >=3 )
#  remember to use --authenticationDatabase "admin"
mongo -u "r_user" -p "r_pass" localhost --authenticationDatabase "admin"

use book_db
db.createUser({user: "book_user", pwd: "pass", roles: [{ role: "readWrite", db: "book_db" }], passwordDigestor:"server"})

book = { title : "Tarzen", author : "Edger Rice", isbn : "5784-884-3893"}
db.book_db.insert(book)
db.book_db.find({})