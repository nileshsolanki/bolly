Note: I made this project solely for eductational purposes.

This project is a torrent streamer i.e given a magnet link it can stream content from it. It is built upon [TorrentStreamServer](https://github.com/TorrentStream/TorrentStreamServer-Android). It can run on android smartphones and TVs

These are a few screenshots from the app


<img width="280" src="https://github.com/nileshsolanki/bolly/blob/master/screenhsots/ss3.jpeg"><img width="280" src="https://github.com/nileshsolanki/bolly/blob/master/screenhsots/ss2.jpeg"><img width="280" src="https://github.com/nileshsolanki/bolly/blob/master/screenhsots/ss1.jpeg">


![alt text](https://github.com/nileshsolanki/bolly/blob/master/screenhsots/bollytv.png)




To build the app you will have to replace the following in ```app/src/main/java/com/android/bolly/constants/Tmdb.java```

1. ```APIKEY``` - your api key obtained from tmdb.org (required)
2. ```HOMEPAGE``` - your app's home page
3. ```SERVER_BASE_URL``` - your server that serves magnet links
