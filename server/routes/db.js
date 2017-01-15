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
	var image_1 = {comment: 'image1', loc: [-2.574137, 51.449208]}
	var image_2 = {comment: 'image2', loc: [-3.611741, 52.448787]}
	images.insert([image_1, image_2]);
	images.find(
	   {
		 loc: {
		   $geoWithin: {
			  $polygon: [ [ -2.577967, 51.450632 ], 
						  [ -2.572055, 51.450672 ],
						  [ -2.572066, 51.447911 ],
						  [ -2.577881, 51.448018 ],
						  [ -2.577967, 51.450632 ] ]
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