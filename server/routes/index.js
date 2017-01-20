'use strict';

var express = require('express');
var router = express.Router();

var mongodb = require('mongodb');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/getImages/:lat1/:lon1/:lat3/:lon3', function(req, res) {
	// Get a Mongo client to work with the Mongo server
  var MongoClient = mongodb.MongoClient;

  // Define where the MongoDB server is
  var url = 'mongodb://localhost:27017/example';

  // Connect to the server
	MongoClient.connect(url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the Server', err);
		} else {
			// We are connected
			console.log('Connection established to', url);

			// Cast all parametters into integers
			var lat1 = parseFloat(req.params.lat1);
			var lon1 = parseFloat(req.params.lon1);
			var lat3 = parseFloat(req.params.lat3);
			var lon3 = parseFloat(req.params.lon3);
			var lat2 = lat1;
			var lon2 = lon3;
			var lat4 = lat3;
			var lon4 = lon1;

			// Get the documents collection
			var images = db.collection("images");

			// Find all images within the area
			images.find({
				loc: {
					$geoWithin: {
						$polygon: [ [ lat1, lon1 ],
												[ lat2, lon2 ],
												[ lat3, lon3 ],
												[ lat4, lon4 ],
												[ lat1, lon1 ] ]
					}
				}
			}).toArray(function (err, result) {
			if (err) {
					console.log(err);
					res.send([]);
				} else {
					console.log('Found:', result);
					res.send(result);
				}
			});

			//Close the database connection
			db.close();
		}
	});
});

router.get('/addImage/:comment/:lat/:lon', function(req, res) {
	// Get a Mongo client to work with the Mongo server
  var MongoClient = mongodb.MongoClient;

  // Define where the MongoDB server is
  var url = 'mongodb://localhost:27017/example';

  // Connect to the server
	MongoClient.connect(url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the Server', err);
		} else {
			// We are connected
			console.log('Connection established to', url);

			// Cast all parametters into integers
      var comm = req.params.comment;
			var lat = parseFloat(req.params.lat);
			var lon = parseFloat(req.params.lon);
			// Get the documents collection
			var images = db.collection("images");
      var image = {comment: comm, loc: [lat, lon]}
      images.insert([image], function(err,result) {
        if(err) {
          console.log(err);
        } else {
          res.send("Upload successful");
        }
      });

			//Close the database connection
			db.close();
		}
	});
});

module.exports = router;
