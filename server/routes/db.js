var mongo = require('mongodb'),
  Server = mongo.Server,
  Db = mongo.Db;

var server = new Server('localhost', 27017, {auto_reconnect: true});
var db = new Db('exampleDb', server);

db.open(function(err, db) {
  if(err) {
    console.log('Unable to connect to the mongoDB server. Error:', err);
  } else {
    console.log("We are connected");
	//Create a collection if it does not exist.
	var images = db.collection("images");	  
	var image_1 = {comment: 'image1', loc: {long : '-2.611741', lat : '51.448787'}}
	var image_2 = {comment: 'image2', loc: {long : '-3.611741', lat : '52.448787'}}
	images.insert([image_1, image_2]);
	
	images.find(
	   {
		 loc: {
		   $geoWithin: {
			  $polygon: [ [ -2.634702, 51.469556 ], 
						  [ -2.609305, 51.456596 ],
						  [ -2.598833, 51.442560 ],
						  [ -2.623243, 51.443339 ] ]
			}
		 }
	   }
	).toArray(function (err, result) {
		if (err) {
        console.log(err);
      } else {
        console.log('Found:', result);
      }
	});
  }
});