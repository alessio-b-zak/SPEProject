# SPEProject
Open-water data catchment application

## Tutorial

### Motivation
In the last decade, there has been a noticeable transition towards mobile and cloud solutions. With the exponential increase in the amount of data produced and stored by businesses, governments and individuals, grouping and accessing the data has become paramount for software developers. A wide variety of APIs has been created to accommodate the demands of the striving users of today’s fast-paced data-driven era. A common task is to store and access images or videos. For instance, in 2012, Facebook processed more than 500TB of data daily [\[1\]](https://www.cnet.com/news/facebook-processes-more-than-500-tb-of-data-daily/).

As such, this article, written as a tutorial, exhibits our progress made so far in the Software Product Engineering unit. It presents how to connect an Android application to a RESTful API, request images subject to some geographical constraints from a node.js webserver which is connected to a MongoLab database, as well as post images and save them into the database. 
For this tutorial, the following technologies are used: Java using the Android Studio IDE for the client (i.e. the Android app), node.js and express.js for the web-server and MongoDB for the database.

### Solution

From a high-level perspective, the architecture of the system splits into four parts (figure system architecture layout). The client Android application is connected to both an API and a node.js Application Web-Server. The Application Server handles the requests from the client, accessing and storing information in the Database Server. The Database is hosted on MongoLab, a Database-as-a-Service provider.

![alt-text](https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/sysarchlayout.png?raw=true "System Architecture Layout")

The client will connect to the WIMS API supplied by the Water Quality Archive, which provides historic water information gathered from various sampling points. Each sampling point contains several samples, and each sample is described by multiple chemical measurements. The RESTful API sends data in a JSON format and contains a collection of extraneous parameters, such that the data needs to be filtered.

The client directly calls the API through a GET request in order to fetch all the sampling points in a certain radius from the location of the device. This is done by instantiating SamplingPointAPI and ImageUploader, which extend the abstract AsyncTask class. This class provides an abstraction for UI threads, making it easier and faster to manipulate data in the background. In the following part of the tutorial, the process will be exemplified using source code.

Depending on the data that is retrieved, a new Java class might or might not be required. For this particular application, a private class called SamplingPoint is created, containing the id of the sampling point, its coordinates and its type, with getters and setters. More parameters can easily be added if required in further stages of development.

The next step is connecting to the API and pulling the data. To perform this, a separate class called SamplingPointsAPI extending AsyncTask is created [\[AsyncTask\]](https://developer.android.com/reference/android/os/AsyncTask.html). An asynchronous task is prefered for this application, as communication is a bottleneck and during the time data is received in the background, the Android application can perform other tasks, such as loading UI elements. According to the official Android Developer reference guide, AsyncTask should preferably be used for short operations which last at most a few seconds; this makes it ideal for our usage, as the requested data is in the form of either JSON or compressed images, not occupying much memory and, therefore, being fast to download.
 
A connection to the API is established through an URL built using a URI builder. The URI builder is a helper class for building and manipulating URI references. It is quite intuitive, allowing user to input the authority, set the paths and add parameters to the queries. You can see how the WIMS API URL for the request is built using the code below. 

```java
builder.scheme("http")
       .authority("environment.data.gov.uk")
       .appendPath("water-quality")
       .appendPath("id")
       .appendPath("sampling-point")
       .appendQueryParameter("lat", params[0])
       .appendQueryParameter("long", params[1])
       .appendQueryParameter("dist", "4")
       .appendQueryParameter("samplingPointStatus", "open");

String myUrl = builder.build().toString();
URL url = new URL(myUrl);
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setReadTimeout(10000 /* milliseconds */);
conn.setConnectTimeout(15000 /* milliseconds */);
conn.setRequestMethod("GET");
conn.setDoInput(true);
// Starts the query
conn.connect();
int response = conn.getResponseCode();
Log.d(DEBUG_TAG, "Url is: " + url);
Log.d(DEBUG_TAG, "The response is: " + response);
InputStream inputStream = conn.getInputStream();
InputStreamToJSON inputStreamToJSON = new InputStreamToJSON();
samplingPoints = inputStreamToJSON.readJsonStream(inputStream);
```

A custom class called InputStreamToJSON is written to convert a retrieved InputStream into the ArrayList of previously defined SamplingPoint’s. It uses a package JsonReader which allows easy manipulation of JSON objects using simple commands, permitting the extraction of the data within arrays/objects. A user can open the object or array using the beginObject() and beginArray() methods, respectively. The name of the next field can be read using the nextName() method and if it matches with one of the required data fields, it can extract it using the nextString() method, as well as a few other primitive data types (Int, Double, Boolean, Long). When the data field is not required, it can be skipped using the skipValue() method. A developer should not forget to close any open arrays and objects using the endArray() and endObject() methods, respectively.

Before explaining how the classes extending AsyncTask are implemented, it is worth mentioning that the result must be passed back to the UI thread by applying the Observer Pattern. This entails using a listener interface which signals the caller of the AsyncTask that the processing is done and the results are returned.

***Image handling***

When dealing with images, there are two cases to consider: retrieving and posting images via GET and POST requests, respectively. In order to implement these procedures, both the Android client and the node.js web server need configuring.

The GET request, that is getting all the images in the geographical area denoted by the client’s screen, is simpler to build and send. From the client side, it requires only four  parameters to be sent, i.e. the coordinates of the top-left and bottom-right corners of the screen. On the server side, the other two points are interpolated. When the request is received, a geoWithin query to the Database Server is sent. The query returns the metadata and location on the node.js server of the images in the polygon denoted by the four points, i.e. the corners of the client’s screen. The Application server then sends the images and the metadata to the client(Android app). However, on the client side, the response is harder to interpret, because the images are represented as a stream of bytes. The interpretation is done in the InputStreamToImage class, again, using JsonReader.
In Android Studio, the client connects to the server using the HttpURLConnection class. As can be seen, from the code snippet, first an URL is build using the Uri.Builder and then the client sends the request by opening the connection.


Moving on to the POST request, first an Image class is created, containing a Bitmap image, its coordinates and a comment. Then, a new class inheriting AsyncTask is created, called ImageUploader. This class creates a POST request and encodes the bitmap as a byte array. Therefore, the image will be in the form of a sequence of bytes. After the request is made, the web-server will decode it in order to fetch the image and other parameters accordingly.

### Limitations and Alternatives
At the moment, all the request are made through HTTP, which means the data sent across the Internet is unsecure and can be intercepted and read. Another limitation is that the input is not sanitized and allows for files of any size to be sent. This can lead to server crashes (...).

### Conclusion
