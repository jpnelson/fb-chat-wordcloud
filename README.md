Facebook chat wordcloud
=======================

This project generates a word cloud from a message chatlog, exported from facebook.

Downloading your facebook chatlog
---------------------------------
* Download your complete message log from http://facebook.com. You can export your data by navigating to Settings -> Download a copy of your Facebook data
* Extract and navigate to ```yourusername/html/messages.html```
* Copy messages.html to the project directory

Generating the word cloud
---------------------------------
To build,
```sh
ant create_run_jar
```

To run,
```sh
./fb-chat-word-cloud.jar filename name1 name2 outputfule
```
or
```sh
./fb-chat-word-cloud.jar filename name1 name2
```
(you may need to ```sh chmod +x fb-chat-word-cloud.jar``` first
* "filename" is most likely messages.htm, your downloaded message log. Make sure you place this in the same directory as the runnable jar
* "name1" and "name2" are the names you want to generate the cloud for, so, your name and your friend's name. Make sure to enclose the names with quotes ("Firstname Lastname") to preserve spacing
* a file will be written (cloud.png by default, otherwise, "outputfile" will be used)

Setting WordCloud preferences
--------------------------------
See the file "wordCloud.WordCloudPreferences.java" to set many of the parameters for word cloud generation. Parameters are explained in the comments.


JSoup dependency
--------------------------------
This project uses [JSoup](https://github.com/jhy/jsoup/) to parse the html message log. This is included in the project.
