$( "#add-row" ).click(function( event ) {
	var rowCount = $('#result tr').length;
	var content= $('#resultrow0').html();
	resultContent=   content.replace(/0/g,rowCount-1);   
	$(resultContent).find("input").val("");
	$('#result > tbody:last').append("<tr>"+resultContent+"</tr>"); 
	$('#result tr:last').find("input").val("");   
	patternvalidation(); 
});    

