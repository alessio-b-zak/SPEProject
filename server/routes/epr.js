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
    permitId: String,
    holder: String,
    effluentType: String,
    siteType: String,
    localAuthority: String,
    easting: Number,
    northing: Number,
  }
);

var eprModel = mongoose.model('eprModel', eprSchema);

const urls = [
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fwaste-site&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fagriculture&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=5000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=10000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=15000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=20000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=25000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=30000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=35000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=40000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=45000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=50000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=55000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=60000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=65000&_limit=5000',
  'https://environment.data.gov.uk/public-register/water-discharges/registration.json?easting=423373&northing=360021&dist=1000&effluentType=http%3A%2F%2Fenvironment.data.gov.uk%2Fpublic-register%2Fwater-discharges%2Fdef%2Feffluent-type%2Fsewage-not-water-company&_offset=70000&_limit=5000',
];

Promise.map(urls, url => axios.get(url).then((response) => {
    const res = response.data;
    const items = res.items;
    if (items != null) {
        items.map((item) => {
            if (item.revocationDate == null) {    
                const permit = [];
                permit.permitId = item['@id'];
                permit.effluentType = item.effluentType.comment;
                permit.localAuthority = item.localAuthority.label;
                permit.siteType = item.site.siteType.comment;
                permit.easting = item.site.location.easting;
                permit.northing = item.site.location.northing;
                const holder = item.holder.name;
                if (isArray(holder)) {
                    permit.holder = holder[0];
                } else {
                    permit.holder = holder;
                }
                eprModel.create(permit, function (err, entry) {
                    // saved!
                    console.log(`permitId: ${permit.permitId}; holder: ${permit.holder}; siteType: ${permit.siteType}`);
                    // return null;
                });
            }
            // return null;
        });
    }
}).catch((error) => {
    console.log(error);
}), { concurrency: 2 }).then(() => {
    process.exit();
});

function isArray(what) {
    return Object.prototype.toString.call(what) === '[object Array]';
}

