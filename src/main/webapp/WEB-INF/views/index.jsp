<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	
	<title>gram{DB} | JSON/Binary Storage Service</title>
	
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="RESTful systems using Jersey and Spring">
	<meta name="author" content="Ant103">
	
	<script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
	<script src="https://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
	
	<style>
		body{font-family: Arial,Helvetica,sans-serif; font-size: 9pt;}
		button{border: 1px solid; background: #fff;}
		textarea{ width: 100%; padding:5px;font-size:13px;color:#888;border:1px solid #ccc;background-color:#fff;-webkit-box-sizing:border-box;-box-sizing:border-box;-webkit-appearance:none;border-radius:0;resize:none; }
		input{ -webkit-appearance:none;border-radius:0; }
		input::-webkit-outer-spin-button, input::-webkit-inner-spin-button{ -webkit-appearance:none;margin:0; }
		input::-webkit-input-placeholder{ text-indent:0;color:#999; }
		input[type="text"], input[type="tel"], input[type="number"], input[type="email"],
		input[type="password"]{ padding:5px; width:100%;min-height:30px;text-indent:8px;font-size:13px;color:#888;border:1px solid #ccc;background-color:#fff;-webkit-box-sizing:border-box;-box-sizing:border-box; }
		input:disabled{ opacity:0.5; }
		
		#ajax_load_indicator{color: white; background: red; padding: 5px;  position: fixed; top: 0px; left: 0px; display: none;}
		
		ul{-webkit-margin-before: 1em; -webkit-margin-after: 1em; -webkit-margin-start: 0px; -webkit-margin-end: 0px; -webkit-padding-start: 0px;}
		li{list-style-type: none; float: left;}
		div{word-break:break-all;}
	</style>
</head>

<script type="text/javascript">
$( document ).ready(function() {
    $('#orSignUpBtn').click(function(){
		var confrimPassword = $('#confrimPassword').val();
		var orPassword = $('#orPassword').val();
		var orEmail = $('#orEmail').val();
	
		if(orEmail==''){
			$('#orStatus').html('Enter the email.');
			$('#orEmail').focus();
			return false;
		}
		
		if(orPassword==''){
			$('#orStatus').html('Enter the password.');
			$('#orPassword').focus();
			return false;
		}
		
		if(confrimPassword==''){
			$('#orStatus').html('Enter the confrim password.');
			$('#confrimPassword').focus();
			return false;
		}
	
		if(orPassword == confrimPassword) {
			var data = '{"_id":"'+orEmail+'", "password":"'+orPassword+'"}';
			request('post', true, '/common/signup', data, 'application/json; charset=UTF-8', 'json', false, orCallback);
		} else {
			$('#orStatus').html('Confirm your password is incorrect.');
			$('#confrimPassword').val('');
			$('#orPassword').val('');
		}
	});
	
	$('#arRegistration').click(function(){
	 
		var arApplicationName = $('#arApplicationName').val();
		var arDescription = $('#arDescription').val();
		var arPassword = $('#arPassword').val();
		var arEmail = $('#arEmail').val();
	
		if(arEmail==''){
			$('#arStatus').html('Enter the email.');
			$('#arEmail').focus();
			return false;
		}
		
		if(arPassword==''){
			$('#arStatus').html('Enter the password.');
			$('#arPassword').focus();
			return false;
		}
		
		if(arApplicationName==''){
			$('#arStatus').html('Enter the arApplication name.');
			$('#arApplicationName').focus();
			return false;
		}

		if(arDescription==''){
			arDescription='Not written.'
		}

		var data = '{"email":"'+arEmail+'", "password":"'+arPassword+'", "applicationName":"'+arApplicationName+'", "description":"'+arDescription+'"}';
		request('post', true, '/common/application/registraion', data, 'application/json; charset=UTF-8', 'json', false, arCallback);
		
	});
	
	$('#lstFind').click(function(){
	 
		var lstApplicationName = $('#lstApplicationName').val();
		var lstPassword = $('#lstPassword').val();
		var lstEmail = $('#lstEmail').val();
	
		if(lstEmail==''){
			$('#arStatus').html('Enter the email.');
			$('#lstEmail').focus();
			return false;
		}
		
		if(lstPassword==''){
			$('#arStatus').html('Enter the password.');
			$('#lstPassword').focus();
			return false;
		}
		
		var data = {email:lstEmail, password:lstPassword, applicationName:lstApplicationName};
		
		if(lstApplicationName==''){
			data = {email:lstEmail, password:lstPassword};
		}

		
		request('get', true, '/common/application/find', data, 'application/json; charset=UTF-8', 'json', false, lstCallback);
		
	});
	
});

var orCallback = function(data, textStatus, xhr)
{
	$('#orStatus').html(data.status);
}

var arCallback = function(data, textStatus, xhr)
{
	$('#arStatus').html(data.status);
}
 
var lstCallback = function(data, textStatus, xhr)
{
	$('#lstStatus').html(data.status);
	var list = data.list;
	var col = '<ul>';
	for (var int = 0; int < list.length; int++) {
		col += '<li><fieldset>';
		col += '<legend>'+list[int].applicationName+'</legend>';
		col += '<div>appId : '+list[int]._id+'</div>';
		col += '<div>apiKey : '+list[int].apiKey+'</div>';
		col += '</fieldset></li>';
	}
	col += '</ul>';	
	
	$('#resultList').html(col);
	
}

var request = function(httpMethod, async, url, data, contentType, dataType, cache, cb) {
	//console.info(httpMethod+'|'+async+'|'+url+'|'+data+'|'+contentType+'|'+dataType+'|'+cache);
    $.ajax({
        type: httpMethod
        , async: async
        , url: url
		, contentType : contentType
        , data: data
		, dataType: dataType
		, cache: cache
        , beforeSend: function() {
             $('#ajax_load_indicator').show().fadeIn('fast'); 
          }
        , success: cb
        , error: function(data, status, err) {
        	console.log(status+'error forward : '+data);
            alert(err);
          }
        , complete: function() { 
        	$('#ajax_load_indicator').fadeOut();
          }
    });
}

</script>

<body>

<div id="ajax_load_indicator">Processing</div>

<!-- Wrap all page content here -->
<div id="wrap">
	<h4>JSON/Binary Storage Service</h4>
	<h1>gram{DB}</h1>
	<br/>
	<fieldset>
	<legend>Owner Registration</legend>
	<div><label>Email</label></div>
	<div><input id="orEmail" type="email" placeholder="  abc@pealabs.net"/></div>
	<div><label>Password</label></div>
	<div><input id="orPassword" type="password" placeholder="  Combination of alphanumeric characters, including special characters."/></div>
	<div><label>Confirm password</label></div>
	<div><input id="confrimPassword" type="password" placeholder="  Please re-enter."/></div>
	<div><button id="orSignUpBtn">Sign up</button>&nbsp;<label id="orStatus"></label></div>
	</fieldset>
	<p/>
	<fieldset>
	<legend>Application Registration</legend>
	<div><label>Email</label></div>
	<div><input id="arEmail" type="email" placeholder="  abc@pealabs.net"/></div>
	<div><label>Password</label></div>
	<div><input id="arPassword" type="password" placeholder="  Combination of alphanumeric characters, including special characters."/></div>
	<div><label>Application Name</label></div>
	<div><input id="arApplicationName" type="text" placeholder="  Please name a nice." /></div>
	<div><label>Description</label></div>
	<div><textarea id="arDescription" rows="5"></textarea></div>
	<div><button id="arRegistration">Registration</button>&nbsp;<label id="arStatus"></label></div>
	</fieldset>
	<p/>
	<fieldset>
	<legend>The list of registered applications</legend>
	<div><label>Email</label></div>
	<div><input id="lstEmail" type="email" placeholder="  abc@pealabs.net"/></div>
	<div><label>Password</label></div>
	<div><input id="lstPassword" type="password" placeholder="  Combination of alphanumeric characters, including special characters."/></div>
	<div><label>Application Name</label></div>
	<div><input id="lstApplicationName" type="text" placeholder="  If do not enter, retrieve a list of all." /></div>
	<div><button id="lstFind">Find</button>&nbsp;<label id="lstStatus"></label></div>
	<div id="resultList" class="table">
	</div>
	</fieldset>
	<p/>
	<fieldset>
	<div align="right">powered by pealabs.net</div>
	</fieldset>


</div>

</body>
</html>