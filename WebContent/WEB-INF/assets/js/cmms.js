var Links = {};

$(".dataTables_wrapper").addClass("table table-striped table-hover table-responsive");
$('.panel-heading span.filter').on('click', function (e) {
	var $this = $(this),
	$panel = $this.parents('.panel');
	$panel.find('.panel-body').slideToggle();
	/*if ($this.css('display') != 'none') {
		$panel.find('.panel-body input').focus();
	}*/
});

$(document).ready(function() { 
	$(".select2").select2();
});
$('.modal').on('hidden.bs.modal', function () {
	$(this).find('input').val('');
	$(this).find('select').prop('selectedIndex',0);
	$(this).find("#responseMessage").hide();
	$(this).find(".responseMessage").hide();
	$(this).find("#login-responseMessage").hide();

});
$(function(){
	$('.validate').validate();
});

$("#change-password-modal-form").validate({
	submitHandler: function(form) {
		// handle form processing here
		$.post(form.action, $('#change-password-modal-form').serialize(), function(response) {
			if(response){
				$("#login-responseMessage").html("");
				if (response.status=='SUCCESS'){
					$("#login-responseMessage").removeClass("alert-danger");
					$("#login-responseMessage").addClass("alert alert-success");
					$("#login-responseMessage").html("<i class='fa fa-check'></i>Password changed successfully!");
				}else if(response.status=='FAILED'){
						$("#login-responseMessage").addClass("alert alert-danger");
						$("#login-responseMessage").removeClass("alert-success");
						for (var i = 0; i < response.errorMessageList.length; i++) {

							var item = response.errorMessageList[i];
							$("#login-responseMessage").html($("#login-responseMessage").html()+item+"<br/>");
							//var $controlGroup = $('#' + item.fieldName + 'ControlGroup');
						}
				}
			}

		});
		return false;
	},
	highlight: function(element, errorClass) {
		$(element).fadeOut(function() {
			$(element).fadeIn();
		});
	},
});

var stringify_aoData = function (aoData) {
	var o = {};
	var modifiers = ['mDataProp_', 'sSearch_', 'iSortCol_', 'bSortable_', 'bRegex_', 'bSearchable_', 'sSortDir_'];
	jQuery.each(aoData, function (idx, obj) {
		if (obj.name) {
			for (var i = 0; i < modifiers.length; i++) {
				if (obj.name.substring(0, modifiers[i].length) == modifiers[i]) {
					var index = parseInt(obj.name.substring(modifiers[i].length));
					var key = 'a' + modifiers[i].substring(0, modifiers[i].length - 1);
					if (!o[key]) {
						o[key] = [];
					}
					//console.log('index=' + index);
					o[key][index] = obj.value;
					//console.log(key + ".push(" + obj.value + ")");
					return;
				}
			}
			// console.log(obj.name + "=" + obj.value);
			o[obj.name] = obj.value;
		}
		else {
			o[idx] = obj;
		}
	});
	return JSON.stringify(o);
};

var stringify_data = function (aoData) {
	console.log(aoData);
	var o = {};
	var modifiers = ['data', 'name', 'searchable', 'orderable', 'search'];
	var modifiers_1 = ['columns','draw','start','length'];
	jQuery.each(aoData, function (idx, obj) {

		if (obj.name) {

			for (var i = 0; i < modifiers_1.length; i++) {
				console.log(obj.name+'=' + obj.value);
				if ($.isArray(obj.value)) {
					//console.log("value="+obj.value);

					for(var j=0; j<modifiers.length; j++){

						//	if(obj.value[0][modifiers[j]]){

						var index = j;
						var key = obj.name;
						var value=obj.value[0][modifiers[j]];
						if (!o[key]) {
							o[key] = [];
						}
						console.log('index=' + index);
						o[key][index] = obj.value[0][modifiers[j]];
						console.log(key + ".push(" + value + ")");
						return;
						//}
					}
					o[obj.name] = obj.value;
				}
			}
		}else {
			o[idx] = obj;
		}
	});
	return JSON.stringify(o);
};

/* Bootstrap style pagination control */
$.extend( $.fn.dataTableExt.oPagination, {
	"bootstrap": {
		"fnInit": function( oSettings, nPaging, fnDraw ) {
			var oLang = oSettings.oLanguage.oPaginate;
			var fnClickHandler = function ( e ) {
				e.preventDefault();
				if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
					fnDraw( oSettings );
				}
			};

			$(nPaging).addClass('clear pull-right').append(
					'<ul class="pagination pagination-xs" style="margin:0px">'+
					'<li class="first disabled"><a href="#">&larr; '+oLang.sFirst+'</a></li>'+
					'<li class="prev disabled"><a href="#">'+oLang.sPrevious+'</a></li>'+
					'<li class="next disabled"><a href="#">'+oLang.sNext+' </a></li>'+
					'<li class="last disabled"><a href="#">'+oLang.sLast+' &rarr; </a></li>'+
					'</ul>'
			);
			var els = $('a', nPaging);
			$(els[0]).bind( 'click.DT', { action: "first" }, fnClickHandler );
			$(els[1]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
			$(els[2]).bind( 'click.DT', { action: "next" }, fnClickHandler );
			$(els[3]).bind( 'click.DT', { action: "last" }, fnClickHandler ); 
		},

		"fnUpdate": function ( oSettings, fnDraw ) {
			var iListLength = 5;
			var oPaging = oSettings.oInstance.fnPagingInfo();
			var an = oSettings.aanFeatures.p;
			var i, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

			if ( oPaging.iTotalPages < iListLength) {
				iStart = 1;
				iEnd = oPaging.iTotalPages;
			}
			else if ( oPaging.iPage <= iHalf ) {
				iStart = 1;
				iEnd = iListLength;
			} else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
				iStart = oPaging.iTotalPages - iListLength + 1;
				iEnd = oPaging.iTotalPages;
			} else {
				iStart = oPaging.iPage - iHalf + 1;
				iEnd = iStart + iListLength - 1;
			}

			for ( i=0, iLen=an.length ; i<iLen ; i++ ) {
				// Remove the middle elements
				$('li:gt(1)', an[i]).filter(':not(:nth-last-child(2),:last)').remove();

				// Add the new list items and their event handlers
				for ( j=iStart ; j<=iEnd ; j++ ) {
					sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
					$('<li '+sClass+'><a href="#">'+j+'</a></li>')
					.insertBefore( $('li:nth-last-child(2)', an[i])[0] )
					.bind('click', function (e) {
						e.preventDefault();
						oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
						fnDraw( oSettings );
					} );
				}

				// Add / remove disabled classes from the static elements
				if ( oPaging.iPage === 0 ) {
					$('li:first', an[i]).addClass('disabled');
					$('li:first +li', an[i]).addClass('disabled');

				} else {
					$('li:first', an[i]).removeClass('disabled');
					$('li:first + li', an[i]).removeClass('disabled');
				}

				if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
					$('li:last', an[i]).addClass('disabled');
					$('li:nth-last-child(2)', an[i]).addClass('disabled');
				} else {
					$('li:last', an[i]).removeClass('disabled');
					$('li:nth-last-child(2)', an[i]).removeClass('disabled');
				}
			}
		}
	}
} );
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings )
{
	return {
		"iStart":         oSettings._iDisplayStart,
		"iEnd":           oSettings.fnDisplayEnd(),
		"iLength":        oSettings._iDisplayLength,
		"iTotal":         oSettings.fnRecordsTotal(),
		"iFilteredTotal": oSettings.fnRecordsDisplay(),
		"iPage":          oSettings._iDisplayLength === -1 ?
				0 : Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
				"iTotalPages":    oSettings._iDisplayLength === -1 ?
						0 : Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
	};
};

//$.fn.dataTableExt.sErrMode = 'throw'; //remove datatable alert error


//format currency
Number.prototype.toMoney = function(decimals, decimal_sep, thousands_sep)
{ 
	var n = this,
	c = isNaN(decimals) ? 2 : Math.abs(decimals), //if decimal is zero we must take it, it means user does not want to show any decimal
			d = decimal_sep || '.', //if no decimal separator is passed we use the dot as default decimal separator (we MUST use a decimal separator)

			t = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep, //if you don't want to use a thousands separator you can pass empty string as thousands_sep value

					sign = (n < 0) ? '-' : '',

							//extracting the absolute value of the integer part of the number and converting to string
							i = parseInt(n = Math.abs(n).toFixed(c)) + '', 
							j = ((j = i.length) > 3) ? j % 3 : 0; 
	return sign + (j ? i.substr(0, j) + t : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : ''); 
}


function showAjaxModal(url){
	
	//clear the title
	jQuery('#modal_ajax .modal-title').html('');
	// SHOWING AJAX PRELOADER IMAGE
	jQuery('#modal_ajax .modal-body').html('<div style="text-align:center;margin-top:200px;"><img src=" ' + Links.LoadingImageUrl + '" /></div>');
	// LOADING THE AJAX MODAL
	jQuery('#modal_ajax').modal('show', {backdrop: 'true'});
	
	// SHOW AJAX RESPONSE ON REQUEST SUCCESS
	$.ajax({
		url: url,
		success: function(response)
		{
			headerObject= $(response).find("#title");
			jQuery('#modal_ajax .modal-title').html(headerObject.html());
			jQuery('#modal_ajax .modal-body').html(response);
		}
	});
}
function confirm_modal(delete_url){
	jQuery('#modal-4').modal('show', {backdrop: 'static'});
	document.getElementById('delete_link').setAttribute('href' , delete_url);
}

function alert(msg){
	jQuery('#notification-modal').modal('show', {backdrop: 'static'});
	$('#notification-modal .modal-body').html(msg);
}