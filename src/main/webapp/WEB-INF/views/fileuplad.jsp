<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	
	<title>gram{DB} | JSON/Binary Storage Service</title>
	
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="RESTful systems using Jersey and Spring">
	<meta name="author" content="Ant103">
	
	<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
	<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript">
  $(function() {
    var file;

    // Set an event listener on the Choose File field.
    $('#fileselect').bind("change", function(e) {
      var files = e.target.files || e.dataTransfer.files;
      // Our file var now holds the selected file
      file = files[0];
    });

    // This function is called when the user clicks on Upload to Parse. It will create the REST API request to upload this image to Parse.
    $('#uploadbutton').click(function() {
      var serverUrl = 'http://localhost:8080/1/files/' + file.name;

      $.ajax({
        type: "POST",
        beforeSend: function(request) {
          console.info(request)
          request.setRequestHeader("X-Application-Id", 'a99b085b5fa013e4075411ba0a859577fcadd359');
          request.setRequestHeader("X-API-Key", '13YxAEzkBdnkZCkZrI+YVYHMeLotDVsW107CtDnpHUEuxsb+mRqpFYVfH5MYirkLsxBbjtJrwC0=');
          request.setRequestHeader("Content-Type", 'application/octet-stream');
        },
        url: serverUrl,
        data: file,
        processData: false,
        contentType: false,
        dataType:'json',
        success: function(data) {
          console.info(data);
        },
        error: function(data) {
          console.info(data);
        }
      });
    });


  });
</script>
</head>
<body>





<input type="file" name="fileselect" id="fileselect"></input>
<input id="uploadbutton" type="button" value="Upload to Parse"/>
 


</body>
</html>