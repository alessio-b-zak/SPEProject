'use strict';

var express = require('express');
var router = express.Router();
var app = express();

var mongodb = require('mongodb');
var ObjectId = require('mongodb').ObjectID;

var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;

var xhr = new XMLHttpRequest();
xhr.open("GET", "http://environment.data.gov.uk/water-quality/id/sampling-point.json?lat=54.483784&long=-2.114319&dist=750&_limit=5&samplingPointStatus=open", false);
xhr.setRequestHeader("Accept", "application/json");
xhr.send();

console.log("Status: " + xhr.status);
console.log("Response: " + xhr.responseText);

var result = JSON.parse(xhr.responseText);
console.log("Result: " + result["items"].length);

var wimsPoints = db.collection("wimsPoints");

for(i = 0; i < result["items"].length; i++){
  var item = result["items"][i];
  
  var entry = {}
  entry.id = item["notation"];
  entry.latitude = item["lat"];
  entry.longitude = item["long"];
  
  wimsPoints.insert(entry);
  console.log("id: " + entry.id + ", latitude: " + entry.latitude + ", longitude: " + entry.longitude);
}



// db.open(function(err, db) {
//   if(err) {
//     console.log('Unable to connect to the mongoDB server. Error:', err);
//   } else {
//     console.log("We are connected");
// 	console.log(server.port);
// 	console.log(app.port);
//   }
// });

// app.get('/getImages/:lat1/:lon1/:lat3/:lon3', function(req, res) {
// 	var lat2 = req.params.lat1;
// 	var lon2 = req.params.lon3;
// 	var lat4 = req.params.lat3;
// 	var lon4 = req.params.lon1;
// 	var images = db.collection("images");	  
// 	var image_1 = {comment: 'image1', loc: [51.449208, -2.574137]}
// 	var image_2 = {comment: 'image2', loc: [52.448787, -3.611741]}
// 	images.insert([image_1, image_2]);
// 	images.find(
// 	   {
// 		 loc: {
// 		   $geoWithin: {
// 			  $polygon: [ [ req.params.lat1, req.params.lon1 ], 
// 						  [ lat2, lon2 ],
// 						  [ req.params.lat3, req.params.lon3 ],
// 						  [ lat4, lon4 ],
// 						  [ req.params.lat1, req.params.lon1 ] ]
// 			}
// 		 }
// 	   }
// 	).toArray(function (err, result) {
// 		if (err) {
//         console.log(err);
//       } else {
//         console.log('Found:', result);
//       }
// 	});
// 	res.send(result);
// });