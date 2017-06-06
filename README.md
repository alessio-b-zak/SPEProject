# Tutorial

### Motivation
In the last decade, there has been a noticeable transition towards mobile and cloud solutions. With the exponential increase in the amount of data produced and stored by businesses, governments and individuals, grouping and accessing the data has become paramount for software developers. A wide variety of APIs has been created to accommodate the demands of the striving users of today’s fast-paced data-driven era. A common task is to store and access images or videos. For instance, in 2012, Facebook processed more than 500TB of data daily [\[1\]](https://www.cnet.com/news/facebook-processes-more-than-500-tb-of-data-daily/).

As such, this article, written as a tutorial, exhibits our progress made so far in the Software Product Engineering unit. It presents how to connect an Android application to a RESTful API, request images subject to some geographical constraints from a node.js webserver which is connected to a MongoLab database, as well as post images and save them into the database. 
For this tutorial, the following technologies are used: Java using the Android Studio IDE for the client (i.e. the Android app), node.js and express.js for the web-server and MongoDB for the database.

### Solution

From a high-level perspective, the architecture of the system splits into four parts [Fig 1]. The client Android application is connected to both an API and a node.js Application Web-Server. The Application Server handles the requests from the client, accessing and storing information in the Database Server. The Database is hosted on MongoLab, a Database-as-a-Service provider.

|![alt-text](https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/sysarchlayout.png?raw=true "System Architecture Layout")|
|:---:|
|`Fig. 1: System Architecture Layout`|

##### Connecting to the API

The client will connect to the WIMS API supplied by the Water Quality Archive, which provides historic water information gathered from various sampling points. Each sampling point contains several samples, and each sample is described by multiple chemical measurements. The RESTful API sends data in a JSON format and contains a collection of extraneous parameters, such that the data needs to be filtered.

The client directly calls the API through a `GET` request in order to fetch all the sampling points in a certain radius from the location of the device. This is done by instantiating `SamplingPointAPI` which extends the abstract `AsyncTask` class. This class provides an abstraction for UI threads, making it easier and faster to manipulate data in the background. In the following part of the tutorial, the process will be exemplified using source code.

Depending on the data that is retrieved, a new Java class might or might not be required. For this particular application, a private class called `SamplingPoint` is created, containing the id of the sampling point, its coordinates and its type, with getters and setters. More parameters can easily be added if required in further stages of development.

The next step is connecting to the API and pulling the data. To perform this, a separate class called `SamplingPointsAPI` extending `AsyncTask` is created [\[AsyncTask\]] (https://developer.android.com/reference/android/os/AsyncTask.html). An asynchronous task is prefered for this application, as communication is a bottleneck and during the time data is received in the background, the Android application can perform other tasks, such as loading UI elements. According to the official Android Developer reference guide, `AsyncTask` should preferably be used for short operations which last at most a few seconds; this makes it ideal for our usage, as the requested data is in the form of either `JSON` or compressed images, not occupying much memory and, therefore, being fast to download.

A connection to the API is established through an URL built using a `URI builder`. The URI builder is a helper class for building and manipulating URI references. It is quite intuitive, allowing its users to input the authority, set the paths and add parameters to the queries. You can see how the WIMS API URL for the request is built using the code below [http://environment.data.gov.uk/water-quality/id/sampling-point?lat=51.45&long=-2.62&dist=4] (http://environment.data.gov.uk/water-quality/id/sampling-point?lat=51.45&long=-2.62&dist=4) is an example of an URI that returns all the sampling points within a radius of 4 miles from the point with the coordinates [51.45, 2.62]. Appending `&samplingPointStatus=open` parameter helps filter out irrelevant results (Sampling points which are no longer in use).

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

A custom class called [InputStreamToJSON] (https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/android/app/src/main/java/com/bitbusters/android/speproject/InputStreamToJSON.java) is written to convert a retrieved `InputStream` into the `ArrayList` of previously defined `SamplingPoint`’s. It uses a package `JsonReader` which allows easy manipulation of `JSON` objects using simple commands, permitting the extraction of the data within arrays/objects. A user can open the object or array using the `beginObject()` and `beginArray()` methods, respectively. The name of the next field can be read using the `nextName()` method and if it matches with one of the required data fields, it can extract it using the `nextString()` method, as well as a few other primitive data types (`Int`, `Double`, `Boolean`, `Long`). When the data field is not required, it can be skipped using the `skipValue()` method. A developer should not forget to close any open arrays and objects using the `endArray()` and `endObject()` methods, respectively.

In the case of WIMS API data, the response of the query is an object which contains metadata about the request and an array containing sampling points objects with multiple fields. The metadata about the request is ignored by skipping values until the “items” field is parsed. Then, while there is still data to be read, the `readMessage` method is called to read in sampling point objects. This method loops through the fields of the object and stores relevant information  (id, lat, long, samplingPointType) by checking if name is equal to one of the relevant fields, if not, the value is skipped using `skipValue()`.

Before explaining how the classes extending `AsyncTask` are implemented, it is worth mentioning that the result must be passed back to the UI thread by applying the **Observer Pattern**. This entails using a listener interface which signals the caller of the `AsyncTask` that the processing is done and the results are returned.

|![alt-text](https://www.lucidchart.com/publicSegments/view/3db66936-1676-4c9c-be9d-c863c9396354/image.jpeg "The Observer Pattern")|
|:---:|
|`Fig. 2: The Observer Pattern`|

##### Image handling

When dealing with images, there are two cases to consider: retrieving and posting images via `GET` and `POST` requests, respectively. In order to implement these procedures, both the Android client and the node.js web server need configuring.

The [GET request] (https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/android/app/src/main/java/com/bitbusters/android/speproject/ImagesDownloader.java), that is getting all the images in the geographical area denoted by the client’s screen, is simpler to build and send. From the client side, it requires only four  parameters to be sent, i.e. the coordinates of the top-left and bottom-right corners of the screen. On the server the other two points are interpolated and a `geoWithin` query to the Database Server is sent. The query returns the metadata and location on the node.js server of the images in the polygon denoted by the four points, i.e. the corners of the client’s screen. The [Application server] (https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/server/routes/index.js) then sends the images and the metadata to the client (the Android app). However, on the client side, the response is harder to interpret, because the images are represented as a stream of bytes. The interpretation is done in the [`InputStreamToImage`] (https://github.com/alessio-b-zak/SPEProject/blob/feature-APIconnect/android/app/src/main/java/com/bitbusters/android/speproject/InputStreamToImage.java) class, again, using `JsonReader`.

Moving on to the `POST` request, the communication is established similar to the `GET` request using the `URI builder` and `HttpURLConnection` class. The key difference is that now the Android app client sends the data and the Node.js server receives and stores it. On the client side,  the metadata associated to the image is stored in the header of the request, whilst the content of the image file is stored in the body. The Node.js server generates an identifier for the image, stores it and then adds an entry with its metadata on the MongoDB database. Storing the image is done using a File System call and the unique identifier is generated by counting the number of images already stored in the database. 

The implementation for the `POST` request is skipped as it is very similar to the previous examples.

### Limitations and Alternatives

An alternative approach would have been to acces the database directly from the mobile application. This has the advantage of lower latency, as there is no longer an intermediate node.js server and it is easier and more straightforward to implement. However, this approach has a big security risk, as the database is at the full control of an attacker who decomposes the mobile app to source code. Even if the database could be accessed only with a password, this can be retrieved from the source code.

In our current approach, requests are sent to the node.js server, which is the only one that can query and modify data from the database. The mongoDB database is hosted on [https://mlab.com] (https://mlab.com) and can only be accesed with an username and a password. The password is 128 bits long and is randomly generated.

There are however vulnerabilities on the node.js server. Some invalid queries to the server can crash it. On the same time, some invalid responses to the client can crush the application. Those problems can be solved by validating the input and the output of the `GET` and `POST` methods on the client and server side.

Another shortfall of the implementation is that the user can upload photos and comments of anything. This problem can be solved by having each upload checked by a person, or automatically on the server.

At the moment, all the request are made through `HTTP`, which means the data sent across the Internet is unsecure and can be intercepted and read.

The web-server was implemented using node.js and the express.js framework. An alternative would have been the Django framework using Python. A major reason why we chose node.js was because of its asynchronous I/O, making servers capable to serve more clients at the same time.

### Conclusion
This tutorial has presented our back-end progress so far in the Software Product Engineering Unit. It demonstrates how to connect an Android application to a RESTful API, as well as managing communication between the Android application and a node.js server.
The solution presented is a good starting point for creating a secure and efficient system architecture that can be scaled up easily. No prior experience in this area led to trial and error of several technologies and toolchains, which contributed to slow progress. At this stage of the development, there are evident limitations and vulnerabilities. However, those have been noted and a plan for solving them has been composed.
