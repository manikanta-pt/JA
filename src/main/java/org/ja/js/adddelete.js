	$("#addrow").click(function(event) {
		addRow();
	});

function addRow()
{
	var rowCount = $('#result tr').length;
	var content = $('#resultrow0').html();
	resultContent = content.replace(/0/g, rowCount - 1);
	// $(resultContent).find("input").val("");
	$(resultContent).find("select").val("true");
	$(resultContent).find("input[type='text']").val("");
	$('#result > tbody:last').append("<tr>" + resultContent + "</tr>");
	$('#result tr:last').find("input").val("");	
	patternvalidation();
}
//enable this to implement delete function . use the appropriate id and url
/*function deleteThisRow(obj) {

	//This is to show loading effect till the row is deleted.
	$('.loader-class').modal('show', {backdrop: 'static'});

	setTimeout(function(){
		$('.loader-class').modal('hide');
	}, 3000);

	var idx=$(obj).data('idx');
//fix below line and URL	
	var categoryPropertyId = $('input[name="categoryProperties['+ $(obj).data('idx') +'].id"]').val();
	var tbl = document.getElementById('result');

	if(categoryPropertyId)
	{
		$.ajax({
			url: '/egassets/assetcategory/deleteCategoryProperty?categoryPropertyId=' +categoryPropertyId,
			type: "GET", 
			success: function(response) {
				tbl.deleteRow(idx);
				regenerateTable();
			},
			error: function(response){
				console.log("Failed");
			}
		});
	}
	else
	{
		tbl.deleteRow((idx+1));
		regenerateTable();
	}

}

function regenerateTable()
{
	//starting index for table fields
	var idx=0;

	jQuery("#result tbody tr").each(function() {
		jQuery(this).find("input, select, button").each(function() {
			var customAttrs={};
			if($(this).attr('id'))
			{
				console.log('coming inside!');
				customAttrs['id']=function(_,id){
					return id.replace(/\[.\]/g, '['+ idx +']'); 
				};
			}
			if($(this).attr('name'))
			{
				customAttrs['name']=function(_,id){
					return id.replace(/\[.\]/g, '['+ idx +']'); 
				};
			}
			if($(this).attr('data-idx'))
			{
				customAttrs['data-idx']=function(_,dataIdx){
					return idx;
				};
			}		
			jQuery(this).attr(customAttrs);

		});
		idx++;
	});
}
*/
