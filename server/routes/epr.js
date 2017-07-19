'use strict';

const axios = require('axios');

const Promise = require('bluebird');

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

const mongoose = require('mongoose');

const Schema = mongoose.Schema;

mongoose.connect('mongodb://localhost:27017/local', {
    useMongoClient: true,
});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

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
    console.log(error);
}), { concurrency: 5 }).then(() => {
    process.exit();
});

