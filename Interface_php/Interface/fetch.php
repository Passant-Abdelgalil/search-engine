<?php
require_once __DIR__ . "./vendor/autoload.php";
$con = new MongoDB\Client("mongodb://localhost:27017");
$db=$con->Indexer;
$tbl=$db->test9;
$se=$_POST["search"];
$documentlist=$tbl->find(['Word' => new \MongoDB\BSON\Regex($se)]);     //try to find the Regex of the word from mongodb
if($documentlist != null){
//$len=sizeof ($documentlist);
$output="";
foreach($documentlist as $doc){
 $output.= '<li class="list-group-item link-class">'.$doc["Word"].'</li>';   //get all the like wordes and but it as output
 echo $output;
}

}

?>