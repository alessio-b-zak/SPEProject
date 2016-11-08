'use strict';

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});


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

//Get the file contents
// TODO: fix ReferenceError: File is not defined
/*
var txtFile = new File('sampling-points.txt');
txtFile.writeln(JSON.stringify(json.items));
txtFile.close();
*/

//console.log(JSON.parse(wims.responseText).length);

/*
var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database('test.db');

db.serialize(function() {

  db.run('CREATE TABLE lorem (info TEXT)');
  var stmt = db.prepare('INSERT INTO lorem VALUES (?)');

  for (var i = 0; i < 10; i++) {
    stmt.run('Ipsum ' + i);
  }

  stmt.finalize();

  db.each('SELECT rowid AS id, info FROM lorem', function(err, row) {
    console.log(row.id + ': ' + row.info);
  });
});

db.close();
*/
module.exports = router;
