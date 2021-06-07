# Interface

It is used PHP and mongodb to show the data

### index.php

It has the search bar and use AJAX to give suggestion mechanism


### search.php

The page where show the result of the search 

### fetch.php

This file fetch the data from mongodb database (which has been stored from queries submitted by all users) to give suggestions 

# Stem

It is use Java to preform Query Processor

### Stemmerclass.java 

it is use org.apache.lucene.analysis library to preform stemming in words

### test.txt

text file to be the shared source between java and php

## What you need to download

[MongoDB](https://www.mongodb.com/try#community)

[XAMPP](https://www.apachefriends.org/download.html)

[PHP Driver](https://pecl.php.net/package/mongodb)

[Composer](https://getcomposer.org/download/)

#### Follow the steps in video

[MongoDB PHP](https://www.youtube.com/watch?v=9gEPiIoAHo8)



## Folders Locations
put Interface in directory [C:\xampp\htdocs]

and Stem Folder in [C:\]

## To Test

Visit [http://localhost/interface/index.php]

and test it by search for one word

![index](https://github.com/abeerhbadr/crawler-based-search-engine/blob/Interface/pic1.png?raw=true)

![search](https://github.com/abeerhbadr/crawler-based-search-engine/blob/Interface/pic2.png?raw=true)
