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

var eprSchema = Schema(
  {
    permitId:       String,
    holder:         String,
    effluentType:   String,
    siteType:       String,
    localAuthority: String,
    easting:        Number,
    northing:       Number
  }
);

var eprModel = mongoose.model('eprModel', eprSchema);

var urls = [
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fwaste-site&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fagriculture&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=5000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=10000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=15000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=20000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=25000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=30000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=35000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=40000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=45000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=50000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=55000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=60000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=65000&_limit=5000",
    "https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=70000&_limit=5000"
    ]

console.log("Starting API calls...")
Promise.map(urls, function(url){
    return axios.get(url).then(function (response) {
        // console.log(response.data)
        var res = response.data
        var items = res["items"];
        if (items != null){
            items.map((item) => {
                if(item["revocationDate"] == null) {
                    console.log("Revocation date null")
                    console.log("0")
                    var permit = []
                    console.log("1")
                    permit.permitId = item["@id"]
                    console.log("2")
                    permit.effluentType = item["effluentType"]["comment"]
                    console.log("3")
                    permit.localAuthority = item["localAuthority"]["label"]
                    console.log("4")
                    permit.siteType = item["site"]["siteType"]["comment"]
                    console.log("5")
                    permit.easting = item["site"]["easting"]
                    console.log("6")
                    permit.northing = item["site"]["northing"]
                    console.log("7")
                    var holder = item["holder"]["name"]
                    if(isArray(holder)) {
                        permit.holder = holder[0]
                    } else {
                        permit.holder = holder
                    }
                    console.log(permit)
                    eprModel.create(permit, function (err, entry) {
                        if (err) return handleError(err);
                        // saved!
                        console.log(`permitId: ${permit.permitId}; holder: ${permit.holder}; siteType: ${permit.siteType}`);
                    });
                }
            })
        }  
    }).catch(function (error) {
        console.log("Oh man, error again!");
    })
}, {concurrency: 2}).then(() => {
    process.exit()
})

