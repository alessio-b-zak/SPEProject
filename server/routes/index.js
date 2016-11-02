'use strict';

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
var wims = new XMLHttpRequest();

wims.open("GET", "http://environment.data.gov.uk/water-quality/id/sampling-point?_limit=60000", false);
wims.send();

// status 200 = OK
console.log(wims.status);
console.log(wims.statusText);
//console.log(wims.responseText);

var json = JSON.parse(wims.responseText);
var count = Object.keys(json.items).length;
console.log(count);

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
