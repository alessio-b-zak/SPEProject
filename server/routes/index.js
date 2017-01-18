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

/*
router.get('/getSamplingPoints/:lat/:lon', function(req, res) {
	var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
	var wims = new XMLHttpRequest();

	wims.open("GET", "http://environment.data.gov.uk/water-quality/id/sampling-point?lat=" + req.params.lat + "&long=" + req.params.lon + "&dist=10&samplingPointStatus=open", false);
	wims.send();

	// status 200 = OK
	console.log(wims.status);
	console.log(wims.statusText);

	var json = JSON.parse(wims.responseText);
	delete json["@context"];
	delete json["meta"];
	for (var i = 0; i < json.items.length; i++){
		delete json.items[i].area;
		delete json.items[i].comment;
		delete json.items[i].easting;
		delete json.items[i].northing;
		delete json.items[i].notation;
		delete json.items[i].label;
		delete json.items[i].samplingPointStatus;
		delete json.items[i].subArea;
		json.items[i].samplingPointType = json.items[i].samplingPointType.label;
	}
	//console.log(json);
	//var count = Object.keys(json.items).length;
	//console.log(count);
	res.send(json);
});
*/

module.exports = router;
