[![Travis build status](https://travis-ci.org/mike10004/har-replay.svg?branch=master)](https://travis-ci.org/mike10004/har-replay)
[![AppVeyor build status](https://ci.appveyor.com/api/projects/status/tfhj96elsi8ytf82?svg=true)](https://ci.appveyor.com/project/mike10004/har-replay)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.mike10004/har-replay.svg)](https://repo1.maven.org/maven2/com/github/mike10004/har-replay/)

har-replay
==========

Java library and executable for serving recorded HTTP responses from a HAR 
file. To use it, you provide a HAR file, the library instantiates an HTTP
proxy server, and you configure your web browser to use the proxy server. 
The proxy intercepts each request and responds with the corresponding 
pre-recorded response from the HAR.

Quick Start
-----------

### Package

Look in https://repo1.maven.org/maven2/com/github/mike10004/har-replay-dist 
for the latest `.deb` file or build the parent project to produce one. Execute

    $ sudo dpkg --install /path/to/har-replay-deb  

to install the package. (Replace the filename with the downloaded file or the 
build product.) 

Execute

    $ har-replay --port 56789 /path/to/my.har

to start an HTTP proxy on port 56789 serving responses from `/path/to/my.har`.

### Library

Maven dependency:

    <dependency>
        <groupId>com.github.mike10004</groupId>
        <artifactId>har-replay-vhs</artifactId>
        <version>0.26</version> <!-- use latest version -->
    </dependency>

See Maven badge above for the actual latest version. Example code:

    File harFile = new File("my-session.har");
    ReplaySessionConfig sessionConfig = ReplaySessionConfig.usingTempDir().build(harFile);
    VhsReplayManagerConfig config = VhsReplayManagerConfig.getDefault();
    ReplayManager replayManager = new VhsReplayManager(config);
    try (ReplaySessionControl sessionControl = replayManager.start(sessionConfig)) {
        URL url = new URL("http://www.example.com/");
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", sessionControl.getListeningPort()));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
        try {
            System.out.format("served from HAR: %s %s %s%n", conn.getResponseCode(), conn.getResponseMessage(), url);
            // do something with the connection...
        } finally {
            conn.disconnect();
        }
    }

(Imports are from the library and the `java.net` package.)

The unit tests contain some examples of using the library with an Apache HTTP 
client and a Selenium Chrome WebDriver client. 

FAQ
---

### How do I create a HAR?

See https://toolbox.googleapps.com/apps/har_analyzer/ for instructions on using
your web browser to create a HAR file. Another option is to use 
[browserup-proxy](https://github.com/browserup/browserup-proxy) to capture a
HAR. The BrowserUp method captures some requests the web browser hides from you
(because they are trackers or fetch data for browser internals).

Debugging Travis Builds
-----------------------

If the Travis build is failing, you can test locally with Docker by running 
`./travis-debug.sh`. However, the `mvn verify` command appears to exit early 
but does not report a nonzero exit code, so it's not clear whether it's 
actually succeeding or something funky is going on. It should be useful for 
debugging failures that happen earlier, though. If the failure you see on 
Travis happens later on in `mvn verify`, you can follow the Travis
[Troubleshooting in a local container](https://docs.travis-ci.com/user/common-build-problems/)
instructions, which say to execute:

    $ docker run --name travis-debug -dit $TRAVIS_IMAGE /sbin/init
    $ docker exec -it travis-debug bash -l 

This puts you inside the container, where you can `su -l travis`, clone the 
repo, and proceed manually from there.
