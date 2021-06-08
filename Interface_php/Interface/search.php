<?php
require_once __DIR__ . "./vendor/autoload.php";
$con = new MongoDB\Client("mongodb://localhost:27017");
$db=$con->Indexer;
$tbl=$db->test7;
$insettbl=$db->test9;
$startpage=filter_input(INPUT_GET,'id',FILTER_VALIDATE_INT);
$word=$id=filter_input(INPUT_GET,'search',FILTER_SANITIZE_STRING);

//write the word to file to be able to get it in java program
$file = fopen("C:\\Stem\\test.txt","w");
fwrite($file,$word);
fclose($file);

//run java program to do Query Processor on the word
shell_exec("cd .. && cd .. && cd .. && cd Stem && javac -cp C:\\Stem\\org.apache.lucene.core-3.5.0.jar src\Stemmerclass.java");
$wordwithstem=shell_exec('java -classpath C:\\Stem;C:\\Stem\\org.apache.lucene.core-3.5.0.jar src.Stemmerclass');
$wordwiths= substr($wordwithstem, 0, -1);

//inset the word in database to do suggestion mechanism when search for it again
if($word !="" && $startpage==0 && $insettbl->findOne(['Word'=> $word])==null ){
$insettbl->insertOne(["Word" => $word]);
}
$document=$tbl->findOne(['Word'=> $wordwiths]);
//controlle the buttoms next and previous to make it able or disable
$var1="abled";
$var2="abled";
if($document !=null){
$len=sizeof ($document["pages"]);
if($startpage==0){
   $var1="disabled"; 
}
if($len<=$startpage+10){
    $var2="disabled";
}
}


?>
<!DOCTYPE html>
<html>
 <head>
  <title>RAPR Search Engine</title>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <!-- CSS only -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
  <link rel='stylesheet' href='css/style.css' />
  <link rel="icon" href="css/icon.png">
 </head>
 <body>
 
  <div class="container" style="width:900px; margin-top:30px;">
   <h2 align="center" class="display-1">RAPR Search Engine</h2>
    
   <br /><br />
   <form  action="search.php?id=0" >
   <div align="center" class="input-group mb-3">
    <input type="text" value="<?php echo $word ?>" name="search" id="search" placeholder="Search RAPR" class="form-control" aria-label="Search RAPR" aria-describedby="icon" />
    <button name="submit" type="submit" class="input-group-text" id="icon"><img src="https://img.icons8.com/nolan/25/search.png"/></button>
   </div>
   <ul class="list-group" id="result"></ul>
   </form>
   </div>
   <div class="con"  >
   <!--show the data if it is exist -->
   <?php if($document != null){  $i=0; foreach($document["pages"] as $page) { $i++; if($i>=$startpage && $i<$startpage+10) {?>
    <div class="page con" >
        <small> <?php echo $page["URL"]; ?> </small>
        <h3 class="title"> 
        <a href="<?=$page["URL"]?>">   <?php echo $page["title"]; ?> </a> </h3>
        <?php  foreach($page["Sentance"] as $sen){ ?>
        <p class="sentance"> 
          <?php echo $sen ?> </p>
           <?php }   ?> 
    </div>

    <hr>
 <?php } } } if($document==null) {?>
     <h1 style="margin:70px;">There is No result in RAPR Search Engine <img src="https://img.icons8.com/cute-clipart/64/000000/nothing-found.png"/></h1>
     <?php }?>
    </div>
  <!--buttoms that controlle the pages -->
 <div style="margin:auto; margin-top:30px; width:900px "> 
 <a href="search.php?id=<?=$startpage-10?>&search=<?=$word?>"   type="button" name="submit" class="btn btn-secondary btn-lg <?=$var1?>">Previous</a>
 <a href="search.php?id=<?=$startpage+10?>&search=<?=$word?>" style="float:right;" type="button" class="btn btn-secondary btn-lg <?=$var2?>">Next</a>
 </div>

   <br />
 
 </body>
</html>