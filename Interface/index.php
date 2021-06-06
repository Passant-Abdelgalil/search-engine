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
 
  <div class="container" style="width:900px; margin-top:150px;">
   <h2 align="center" class="display-1">RAPR Search Engine</h2>
    
   <br /><br />
   <form  action="search.php?id=0" >
   <div align="center" class="input-group mb-3">
    <input autocomplete="off" type="text" name="search" id="search" placeholder="Search RAPR" class="form-control" aria-label="Search RAPR" aria-describedby="icon" />
    <button  type="submit" class="input-group-text" id="icon"><img src="https://img.icons8.com/nolan/25/search.png"/></button>
   </div>
   <ul class="list-group" id="result"></ul>
   </form>
   <br />
  </div>
 </body>
</html>

<script>
$(document).ready(function(){
 $('#search').keyup(function(){
  var searchField = $('#search').val();        //get the word fron search input
  if(searchField ==''){

  }else{
    $('#result').html('');                    //to do Ajax and get the data in result unored list
    $.ajax ( { url : "fetch.php" ,
     method : "post" ,
     data : {search:searchField},
     dataType : "text" ,
      success : function ( data ) { 
          $('#result').html ( data ) } } ) ;
  }
 // var expression = new RegExp(searchField, "i");
});
 
 $('#result').on('click', 'li', function() {
  var click_text = $(this).text().split('|');
  $('#search').val($.trim(click_text[0]));
  $("#result").html('');
 });
});

</script>
