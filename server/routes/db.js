'use strict';

var express = require('express');
var router = express.Router();
var app = express();

var mongodb = require('mongodb');
var ObjectId = require('mongodb').ObjectID;

var axios = require('axios');

var Promise = require("bluebird");

var mongoose = require('mongoose')
  , Schema = mongoose.Schema

var mongodb = "mongodb://localhost:27017/local";
mongoose.connect(mongodb);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var wimsSchema = Schema(
  {
    waterbodyId: String,
    loc:         [Number],
    lastActive:  String
  }
);

var wimsModel = mongoose.model('wimsModel', wimsSchema);

axios.get('http://environment.data.gov.uk/water-quality/id/sampling-point.json?lat=54.483784&long=-2.114319&dist=750&_limit=50000&samplingPointStatus=open').then(function (response) {
    var result = response.data
    var wimsPoints = []

    result["items"].forEach(function(element) {
        var wimsPoint = {
            url: `http://environment.data.gov.uk/water-quality/data/measurement.json?samplingPoint=${element["notation"]}&_sort=-sample&_limit=1`,
            data: {
                waterbodyId: element["notation"],
                loc: [element["lat"], element["long"]]
            }
        }

        wimsPoints.push(wimsPoint)

    })

    Promise.map(wimsPoints, function(wimsPoint){
        return axios.get(wimsPoint.url).then(function (response) {
            var res = response.data
            var items = res["items"];
            if (items != null){
                var firstItem = items[0];
                if (firstItem != null) {
                    var sample = firstItem["sample"];
                    if (sample != null) {
                        wimsPoint.data.lastActive = sample["sampleDateTime"]; 
                    }
                }
            }
            wimsModel.create(wimsPoint.data, function (err, entry) {
                if (err) return handleError(err);
                // saved!
                console.log(`waterbodyId: ${wimsPoint.data.waterbodyId}; loc: ${wimsPoint.data.loc}; lastActive: ${wimsPoint.data.lastActive}`);
            });
        }).catch(function (error) {
            console.log('error with:' + promise);
        })
    }, {concurrency: 10}).then(() => {
        process.exit()
    })
}).catch(function (error) {
    console.log('error in 2');
});


    // wimsPoints.find({}).forEach(function(doc){
    //     var xhr = new XMLHttpRequest();
    //     xhr.open("GET", `http://environment.data.gov.uk/water-quality/data/measurement.json?samplingPoint=${doc.waterbodyId}&_sort=-sample&_limit=1`, false);
    //     xhr.setRequestHeader("Accept", "application/json");
    //     xhr.send();
        
    //     var result = JSON.parse(xhr.responseText);
    //     var lastActiveEntry = null;
    //     var items = result["items"];
    //     if (items != null){
    //         var firstItem = items[0];
    //         if (firstItem != null) {
    //             var sample = firstItem["sample"];
    //             if (sample != null) {
    //                 lastActiveEntry = sample["sampleDateTime"]; 
    //             }
    //         }
    //     }
    //     wimsPoints.updateOne({ waterbodyId : doc.waterbodyId}, { $set : { "lastActive" : lastActiveEntry } });
    //     console.log(lastActiveEntry);
    // }); 

    // var xhr = new XMLHttpRequest();
    // xhr.open("GET", "http://environment.data.gov.uk/water-quality/id/sampling-point.json?lat=54.483784&long=-2.114319&dist=750&_limit=50000&samplingPointStatus=open", false);
    // xhr.setRequestHeader("Accept", "application/json");
    // xhr.send();

    // console.log("Status: " + xhr.status);

    // var result = JSON.parse(xhr.responseText);
    // console.log("Result: " + result["items"].length);

    // var wimsPoints = db.collection("wimsPoints");  

    // for(var i = 0; i < result["items"].length; i++){
    //     var item = result["items"][i];
        
    //     var entry = {}
    //     entry.id = item["notation"];
    //     var latitude = item["lat"];
    //     var longitude = item["long"];
    //     entry.loc = [latitude, longitude]

    //     var xhr1 = new XMLHttpRequest();
    //     xhr1.open("GET", "http://environment.data.gov.uk/water-quality/data/measurement.json?samplingPoint=" + entry.id + "&_sort=-sample&_limit=1", false);
    //     xhr1.setRequestHeader("Accept", "application/json");
    //     xhr1.send();
        
    //     var result1 = JSON.parse(xhr1.responseText);
    //     var lastActiveEntry = null;
    //     var items = result1["items"];
    //     if (items != null){
    //         var firstItem = items[0];
    //         if (firstItem != null) {
    //             var sample = firstItem["sample"];
    //             if (sample != null) {
    //                 lastActiveEntry = sample["sampleDateTime"]; 
    //             }
    //         }
    //     }
    //     entry.lastActive = lastActiveEntry

    //     console.log(i);
    //     wimsPoints.insert(entry); // add to database
    // }
// });

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
