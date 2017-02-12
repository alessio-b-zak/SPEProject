'use strict';

var express = require('express');
var fs = require('file-system');
var path = require('path');
var router = express.Router();

var mongodb = require('mongodb');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});


//:lat1/:lon1/:lat3/:lon3
router.get('/getImages/:lat1/:lon1/:lat3/:lon3', function(req, res) {
	// Get a Mongo client to work with the Mongo server
  var MongoClient = mongodb.MongoClient;

  // Define where the MongoDB server is
  var url = 'mongodb:///image_database';

  // Connect to the server
  MongoClient.connect(url, function (err, db) {
    if (err) {
      console.log('Unable to connect to the Database Server', err);
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
        var images = [];
        for (var i = 0; i < result.length; i++) {
          images[i] = {};
          images[i].comment = result[i].comment;
          images[i].loc = result[i].loc;
          images[i].image = fs.readFileSync(path.join(__dirname, result[i].path));
        }
        res.status(200).send(images);
        res.end();
        }
      });

      //Close the database connection
      db.close();
    }
  });

});


router.post('/uploadImage', function(req, res) {
  // Get a Mongo client to work with the Mongo server
  var MongoClient = mongodb.MongoClient;

  // Define where the MongoDB server is
  var url = 'mongodb:///image_database';

  // Connect to the server
  MongoClient.connect(url, function (err, db) {
  if (err) {
    console.log('Unable to connect to the Database Server', err);
  } else {
      // We are connected
      console.log('Connection established to', url);
      var images = db.collection("images");
      images.count({}, function( err, count){
        var number = count.toString();
        var location = path.join('uploads','image' + number + '.bmp');
        var imagepath = path.join(__dirname, location);
        var entry = {};
        entry.comment = req.headers.comment;
        entry.loc = [parseFloat(req.headers.latitude),parseFloat(req.headers.longitude)];
        entry.path = location;
        images.insert(entry);

        if (req.method == "POST") {
          var data = "";
          req.on("data", function(chunk) {
            data += chunk;
          });
          req.on("end", function() {
            console.log("Received posted data: " + data);
            fs.writeFile(imagepath, data, function (err) {
              if(err){
                console.log("Problem saving image");
              }else {
                console.log("Image Saved on server");
              }
            });

          });
        } else {
          console.dir(request);
        }

        db.close();
      });
    }
  });
});
    // console.log(req.files.image.originalFilename);
    // console.log(req.files.image.path);
    //     fs.readFile(req.files.image.path, function (err, data){
    //     var dirname = "/home/rajamalw/Node/file-upload";
    //     var newPath = dirname + "/uploads/" +     req.files.image.originalFilename;
    //     fs.writeFile(newPath, data, function (err) {
    //     if(err){
    //     res.json({'response':"Error"});
    //     }else {
    //     res.json({'response':"Saved"});


/*
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
*/

module.exports = router;
