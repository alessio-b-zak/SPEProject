'use strict';

var express = require('express');
var fs = require('file-system');
var path = require('path');
var router = express.Router();
var Jimp = require("jimp");
var Baby = require("babyparse");
var sharp = require("sharp");

var mongodb = require('mongodb');
var ObjectId = require('mongodb').ObjectID;

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('index', { title: 'Express' });    
});

router.get('/getClassification/:easting/:northing', function(req, res){

    var csv = "bristol_water_classification.csv";
    var parsed = Baby.parseFiles(csv, {
        header: true
    });

    var data_length = Object.keys(parsed.data).length;
    console.log(data_length);

    var easting = req.params.easting;
    var northing = req.params.northing;

    var minimum_index = 0;
    var minimum_value = Infinity;

    for (var i = 0; i < data_length; i++) {
        var easting_dif = Math.pow(easting - parseInt(parsed.data[i].easting), 2);
        var northing_dif = Math.pow(northing - parseInt(parsed.data[i].northing), 2);
        var euclidean = Math.sqrt(easting_dif + northing_dif);

        if (euclidean < minimum_value) {
            minimum_value = euclidean;
            minimum_index = i;
        }
    }

    var classification1 = parsed.data[minimum_index].classification_item;
    var classification2 = parsed.data[minimum_index + 1].classification_item;

    var rating1 = parsed.data[minimum_index].cycle;
    var rating2 = parsed.data[minimum_index + 1].cycle;
    var result = {}
    result[classification1] = rating1;
    result[classification2] = rating2;
    res.send(result);

});
//:lat1/:lon1/:lat3/:lon3
router.get('/getImage/:id', function(req, res) {
    // Cast all parametters into integers
    var id = req.params.id;

    // Get the documents collection
    var images = db.collection("images");

    // Find all images within the area
    images.findOne({'_id': new ObjectId(id)}, {} , function (err, result) {
        if (err) {
            console.log(err);
            res.send([]);
        } else {
            console.log('Found:', result);
            var image = {};
            image._id = result._id;
            image.comment = result.comment;
            image.loc = result.loc;
            image.tags = result.tags;
            image.date = result.date;
            image.image = fs.readFileSync(path.join(__dirname, result.imagepath));
            res.status(200).send(image);
            res.end();
        }
    });
});

//:lat1/:lon1/:lat3/:lon3
router.get('/getThumbnails/:lat1/:lon1/:lat3/:lon3', function(req, res) {

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
                images[i]._id = result[i]._id;
                images[i].comment = result[i].comment;
                images[i].loc = result[i].loc;
                images[i].tags = result[i].tags;
                images[i].date = result[i].date;
                images[i].image = fs.readFileSync(path.join(__dirname, result[i].thumbnailpath));
            }
            res.status(200).send(images);
            res.end();
        }
    });
});

//:lat1/:lon1/:lat3/:lon3
router.get('/getImagesLocation/:lat1/:lon1/:lat3/:lon3', function(req, res) {
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
                images[i]._id = result[i]._id;
                images[i].loc = result[i].loc;
                images[i].tag = result[i].tag;
            }
            res.status(200).send(images);
            res.end();
        }
    });
});

//:lat1/:lon1/:lat3/:lon3/:lastActive
router.get('/getWIMSPoints/:lat1/:lon1/:lat3/:lon3/:lastActive', function(req, res) {

    // Cast all parametters into integers
    var lat1 = parseFloat(req.params.lat1);
    var lon1 = parseFloat(req.params.lon1);
    var lat3 = parseFloat(req.params.lat3);
    var lon3 = parseFloat(req.params.lon3);
    var lat2 = lat1;
    var lon2 = lon3;
    var lat4 = lat3;
    var lon4 = lon1;
    var lastActiveYear = req.params.lastActive;

    // Get the documents collection
    var wimsPoints = db.collection("wimsmodels");

    // Find all wimsPoints within the area which are last active on a given year
    wimsPoints.find({
        loc: {
            $geoWithin: {
                $polygon: [ [ lat1, lon1 ],
                            [ lat2, lon2 ],
                            [ lat3, lon3 ],
                            [ lat4, lon4 ],
                            [ lat1, lon1 ] ]
            }
        },
        lastActive: {
            $regex : lastActiveYear
        }
    }).toArray(function (err, result) {
        if (err) {
            console.log(err);
            res.send([]);
        } else {
            console.log('Found:', result);
            var points = [];
            for (var i = 0; i < result.length; i++) {
                points[i] = {};
                points[i].id        = result[i].waterbodyId;
                points[i].latitude  = result[i].loc[0];
                points[i].longitude = result[i].loc[1];
            }
        res.status(200).send(points);
        res.end();
        }
    });
});



router.post('/uploadImage', function(req, res) {
    var images = db.collection("images");
    images.count({}, function( err, count){
        count = count + 1;
        var number = count.toString();

        var database_image_location = path.join('uploads','image' + number + '.png');
        var imagepath = path.join(__dirname, database_image_location);
        console.log(imagepath);
        var database_thumbnail_location = path.join('thumbnails','image' + number + '.jpeg');
        var thumbnailpath = path.join(__dirname, database_thumbnail_location);

        var entry = {};
        entry.comment = req.headers.comment;
        entry.tags = req.headers.tags.split(",");
        entry.loc = [parseFloat(req.headers.latitude),parseFloat(req.headers.longitude)];
        entry.imagepath = database_image_location;
        entry.thumbnailpath = database_thumbnail_location;

        var date = new Date();
        entry.date = date.getDate() + "/" +  (date.getMonth() + 1) + "/" + date.getFullYear() + " " + new Date(new Date().getTime() + 60*60*1000).toLocaleTimeString();

        images.insert(entry);

        if (req.method == "POST") {
            var data = [];
            req.on("data", function(chunk) {
                data.push(chunk);
                });
                req.on("end", function() {
                var bytes = Buffer.concat(data);
                fs.writeFile(imagepath, bytes, function (err) {
                    if(err){
                        console.log("Problem saving image");
                    } else {
                        console.log("Image Saved on server");
                        sharp(imagepath)
                        .resize(250,250)
                        .toFormat(sharp.format.jpeg)
                        .toFile(thumbnailpath, function(err){
                            if(err) {
                                console.log("Problem saving thumbnail");
                                res.send([]);
                            } else {
                                console.log("Thumbnail saved.");
                                res.status(200).send(["It worked"]);
                            }
                        });
                    }

                });
            });
        } else {
            console.dir(request);
        }
    });
});

module.exports = router;
