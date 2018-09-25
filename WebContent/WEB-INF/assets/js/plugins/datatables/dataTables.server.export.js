$.fn.dataTable.TableTools.buttons.download = $.extend(
    true,
    {},
    $.fn.dataTable.TableTools.buttonBase,
    {
    	
        "sButtonText": "Download",
        "sUrl":      "",
        "sType":     "GET",
        "fnData":    false,
        "fnClick": function( button, config ) {
            var dt = new $.fn.dataTable.Api( this.s.dt );
            var data = dt.ajax.params();
            
           
 
            // Optional static additional parameters
            // data.customParameter = ...;
 
            if ( config.fnData ) {
                config.fnData( data );
            }
 
            var iframe = $('<iframe/>', {
                    id: "RemotingIFrame"
                }).css( {
                    border: 'none',
                    width: 0,
                    height: 0
                } )
                .appendTo( 'body' );
                 
            var contentWindow = iframe[0].contentWindow;
            contentWindow.document.open();
            contentWindow.document.close();
             
            var form = contentWindow.document.createElement( 'form' );
            form.setAttribute( 'method', config.sType );
            form.setAttribute( 'action', config.sUrl );
 /*
            var input = contentWindow.document.createElement( 'input' );
            input.name = 'billerDownloadMandate';
            input.value = stringify_aoData(data);
             
            form.appendChild( input );*/
            contentWindow.document.body.appendChild( form );
            form.submit();
        }
    }
);
