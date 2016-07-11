var getUrl = window.location;
var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
$(function() {
	//Date range picker
	var today = moment();
	var tomorrow = moment(today).add('days', 1);
	$('#validityDateRange').daterangepicker({
		format : 'DD/MM/YYYY',
		minDate : tomorrow,
		showDropdowns : true,
		opens: 'right',
		cancelClass : 'alert-danger',
		locale : {
			applyLabel : 'Apply',
			cancelLabel : 'Cancel',
			fromLabel : '<b>Commences</b>',
			toLabel : '<b>Expires</b>',
			weekLabel : 'W',
			customRangeLabel : 'Mandate Validity Period'
		}
	});
});
$(function() {
	$("#biller_Product")
	.on(
			"change",
			function(e) {
				// mostly used event, fired to the original element when the value changes
				$.ajaxSetup({ cache: true});
				$.getJSON(baseUrl+'/product/getActiveProducts/'
						+ $(this).val(), null, function(j) {
					var options = '<option value="">--select--</option>';
					var data = new Array();
					for (var i = 0; i < j.length; i++) {
						data.push({
							id : j[i].id,
							text : j[i].value
						});
					}

					$('#product')[0].options.length = 0;
					/* $('#product').select2({
							"data" : data
						}).trigger('change'); */
					for (var i = 0; i < j.length; i++) {
						options += '<option value="' + j[i].id + '">' + j[i].value + '</option>'; 
					}

					$("#product").html(options);
				});
			}).trigger('change');
});

$(function() {
	$("#product")/* .select2({
			placeholder : "Select a Product"
		}) */.on(
				"change",
				function(e) {
					if ($(this).val() == null)
						return false;
					$.getJSON(baseUrl+"/product/getProductAmount/"
							+ $(this).val(), null, function(j) {
						console.log("j=" + j);
						if (j != null && j != 0 && j != "") {
							$("#amount").prop('readonly', true);
							$("#amount").prop('value', j);
							$('#amount').number(true, 2);
							var val = $('#amount').val();
							$('#amount_').prop('value', val !== '' ? val : 0);
							$("#fixedAmountMandate option:eq(1)").prop('selected', true);
							$("#fixedAmountMandate").prop('disabled',true);
						} else {
							$("#amount").prop('readonly', false);
							$("#amount").prop('value', '');
							$("#fixedAmountMandate option:eq(0)").prop('selected', true);
							$("#fixedAmountMandate").prop('disabled',false);
						}

					}

					);

				}).trigger('change');

});
$(function() {
	$('#amount').on("change", function(e) {
		$('#amount').number(true, 2);
		var val = $('#amount').val();
		$('#amount_').prop('value', val !== '' ? val : 0);
	}).trigger('change');
});

$("a.tooltipLink").tooltip();

$(function(){
	$("#mandate").validate({
		errorPlacement: function(error,element) {
			element.addClass("validation-failed");
			return true;
		},
		unhighlight: function (element) { // un-hightlight error inputs
			$(element)
			.removeClass('validation-failed'); 
			$(element)
			.removeClass('error');
		},
		focusCleanup: true
	});
});


$(function(){
	$("#verify-account-number").click(function(){
		bankCode=$("#mandate #bank");
		accountNumber=$("#mandate #accountNumber");
		accountName=$("#mandate #accountName");
		submitMandate=$("#mandate #submit-mandate");
		accountName.val('');
		submitMandate.addClass('disabled');
		
		
		if(bankCode == ""){
			alert("Please select a Bank");
			return false;
		}
		if(accountNumber==null || accountNumber == ""){
			alert("Please provide an account number");
			return false;
		}
		$.blockUI();
		$.ajax({
			contentType:'application/xml',
			url:baseUrl+"/scommon/account/validate/"+bankCode.val()+"/"+accountNumber.val(),
			success:function(response){
				$.unblockUI();
				if(response){
				  $xml = $(response),
				  $responseCode = $xml.find( "ResponseCode");
				  responseCode = $responseCode.text();
				  if(responseCode!=null && responseCode=='00'){
					  accountName.val($xml.find( "AccountName").text());
					  submitMandate.removeClass('disabled');
				  }else{
					  alert("Account number validation failed. Error Code {"+responseCode+"}");
				  }
				}else{
					alert("An error has occurred, please try again later.");
				}
				
			}
		});
		
		
	});
});