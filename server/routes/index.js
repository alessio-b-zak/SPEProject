'use strict';

var express = require('express');
var fs = require('file-system');
var path = require('path');
var router = express.Router();
var Jimp = require("jimp");
var Baby = require("babyparse");
var sharp = require("sharp");
var schedule = require('node-schedule');

var mongodb = require('mongodb');
var ObjectId = require('mongodb').ObjectID;

var axios = require('axios');
var Promise = require("bluebird");
var mongoose = require('mongoose')
  , Schema = mongoose.Schema

mongoose.connect('mongodb://localhost:27017/local', {
    useMongoClient: true,
});

const Proj4js = require('proj4');

Proj4js.defs([
    [
        'WGS84',
        '+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs'
    ],[
        'EPSG:27700',
        '+proj=tmerc +lat_0=49 +lon_0=-2 +k=0.9996012717 +x_0=400000 +y_0=-100000 +ellps=airy +towgs84=446.448,-125.157,542.060,0.1502, 0.2470,0.8421,-20.4894 +units=m +no_defs'
    ]
])

// Converts degrees to radians
function degTorad(deg) {
  return deg * (Math.PI / 180);
}

// Calculates the distance between two lat/lon points using Haversine Formula:
// https://en.wikipedia.org/wiki/Haversine_formula
function getDistanceBetweenTwoLatLonInKm(lat1, lon1, lat2, lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = degTorad(lat2 - lat1);  
    var dLon = degTorad(lon2 - lon1); 
    var a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(degTorad(lat1)) * Math.cos(degTorad(lat2)) * 
        Math.sin(dLon/2) * Math.sin(dLon/2); 
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    var d = R * c; // Distance in km
    return d;
}

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

//:lat/:lon/:lastActive
router.get('/getNearestWIMSPoint/:lat/:lon/:lastActive', function(req, res) {

    // Cast all parametters
    var lat = parseFloat(req.params.lat);
    var lon = parseFloat(req.params.lon);
    var lastActiveYear = req.params.lastActive;

    // Get the documents collection
    var wimsPoints = db.collection("wimsmodels");

    // Find nearest wimsPoints which was last active on a given year
    wimsPoints.find({
        loc: {
            $near: [ lat, lon ]
        },
        lastActive: {
            $regex : lastActiveYear
        }
    }).toArray(function (err, result) {
        if (err) {
            console.log(err);
            res.send([]);
        } else {
            var point = {};
            point.id        = result[0].waterbodyId;
            point.latitude  = result[0].loc[0];
            point.longitude = result[0].loc[1];
            point.distance  = getDistanceBetweenTwoLatLonInKm(lat, lon, point.latitude, point.longitude);
        
	    console.log(point)

            res.status(200).send(point);
            res.end();
        }
    });
});

//:lat1/:lon1/:lat3/:lon3
router.get('/getPermits/:lat1/:lon1/:lat3/:lon3', function(req, res) {

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
    var eprTable = db.collection("eprmodels");

    // Find all permits within the area which are last active on a given year
    eprTable.find({
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
            var points = [];
            for (var i = 0; i < result.length; i++) {
                points[i] = {};
                points[i].id        = result[i].permitId;
                points[i].latitude  = result[i].loc[0];
                points[i].longitude = result[i].loc[1];
                points[i].effluentType = result[i].effluentType;
                points[i].effectiveDate = result[i].effectiveDate;
                points[i].siteType = result[i].siteType;
                points[i].holder = result[i].holder;
                if(result[i].revocationDate != null) {
                    points[i].revocationDate = result[i].revocationDate;
                }
            }
        res.status(200).send(points);
        res.end();
        }
    });
});

//:lat/:lon
router.get('/getNearestPermit/:lat/:lon', function(req, res) {

    // Cast all parametters into floats
    var lat = parseFloat(req.params.lat);
    var lon = parseFloat(req.params.lon);

    // Get the documents collection
    var eprTable = db.collection("eprmodels");

    // Find the nearest permit
    eprTable.find({
        loc: {
            $near: [ lat, lon ]
        },
        revocationDate: {
            $exists: false
        }

    }).toArray(function (err, result) {
        if (err) {
            console.log(err);
            res.send([]);
        } else {            
            var point = {};
            point.id            = result[0].permitId;
            point.latitude      = result[0].loc[0];
            point.longitude     = result[0].loc[1];
            point.effluentType  = result[0].effluentType;
            point.effectiveDate = result[0].effectiveDate;
            point.siteType      = result[0].siteType;
            point.holder        = result[0].holder;
            if(result[0].revocationDate != null) {
                point.revocationDate = result[0].revocationDate;
            }
            point.distance      = getDistanceBetweenTwoLatLonInKm(lat, lon, point.latitude, point.longitude);
            console.log(point);
            res.status(200).send(point);
            res.end();
        }
    });
});

// Every Saturday at 03:59 repopulate wims data
var wims_update_rule = new schedule.RecurrenceRule();
wims_update_rule.hour = 3;
wims_update_rule.minute = 59;
wims_update_rule.dayOfWeek = 6;

var update_wims = schedule.scheduleJob(wims_update_rule, function(){
    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'MongoDB connection error:'));
    
    db.collection("wimsmodels").drop(); 
   
    var wimsSchema = Schema(
        {
            waterbodyId: String,
            loc:         [Number],
            lastActive:  String
        }
    );

    var wimsModel = mongoose.model('wimsModel', wimsSchema);

    db.collection("wimsmodels").createIndex({
	loc: "2d"
    })

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
                console.log('error with:' + error);
            })
        }, {concurrency: 10}).then(() => {
            process.exit()
        })
    }).catch(function (error) {
        console.log('error in 2');
    });
});


function osgbToWGS84(easting, northing) {
    var source = new Proj4js.Proj('EPSG:27700');
    var dest = new Proj4js.Proj('WGS84');

    var result = Proj4js.transform(source, dest, [easting, northing]);

    return [result.y, result.x]
}

function isArray(what) {
    return Object.prototype.toString.call(what) === '[object Array]';
}

function handleError(err) {
    console.log('Error saving the permit: ' + err);
}

// Every Saturday at 02:59 repopulate epr data
var epr_update_rule = new schedule.RecurrenceRule();
epr_update_rule.hour = 2;
epr_update_rule.minute = 59;
epr_update_rule.dayOfWeek = 6;

var update_epr = schedule.scheduleJob(epr_update_rule, function(){
    const db = mongoose.connection;
    db.on('error', console.error.bind(console, 'MongoDB connection error:'));
    db.collection("eprmodels").drop();
    
    const eprSchema = Schema({
        permitId: String,
        effectiveDate: String,
        revocationDate: String,
        holder: String,
        effluentType: String,
        siteType: String,
        loc: [Number],
    });

    const eprModel = mongoose.model('eprModel', eprSchema);

    db.collection("eprmodels").createIndex({
	    loc: "2d"
    })
	
    const urls = [
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fwaste-site&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fagriculture&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=5000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=10000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=15000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=20000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=25000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=30000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=35000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=40000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=45000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=50000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=55000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=60000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=65000&_limit=5000',
    'https://environment.data.gov.uk/public-register/water-discharges/registration.json?effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=70000&_limit=5000',
    ];

    Promise.map(urls, url => axios.get(url).then((response) => {
        const res = response.data;
        const items = res.items;
        if (items != null) {
            items.forEach((item) => {
                const permit = {};
                permit.permitId       = item['@id'];
                permit.effectiveDate  = item.effectiveDate;
                permit.effluentType   = item.effluentType.comment;
                permit.siteType       = item.site.siteType.comment;
                
                const holder          = item.holder;
                if (isArray(holder)) {
                    permit.holder = holder[0].name;
                } else {
                    permit.holder = holder.name;
                }
                
                if (item.revocationDate != null) {
                    permit.revocationDate = item.revocationDate;
                }
                
                const easting  = item.site.location.easting;
                const northing = item.site.location.northing;
                permit.loc = osgbToWGS84(easting, northing);
                
                eprModel.create(permit, (err, doc) => {
                    if (err) return handleError(err);
                    console.log(`permitId: ${permit.permitId}; revocationDate: ${permit.revocationDate}`);
                });
            });
        }
    }).catch((error) => {
        console.log(error + "ERROR");
    }), { concurrency: 5 }).then(() => {
        process.exit();
    });
});

module.exports = router;
