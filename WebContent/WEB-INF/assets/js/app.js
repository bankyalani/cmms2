/* 
 * Copyright 2014 NIBSS
 * http://www.nibss-plc.com.ng
 * 
 */
(function ($) {

    App = {
        HTTP_POST: 'POST',
        HTTP_GET: 'GET',
        HTTP_PUT: 'PUT',
        HTTP_DELETE: 'DELETE'
    };
    App.ui = {
        datatable: function (options) {
            var SELECTED_CLASS = "active selected";
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
                        console.log(obj.name + "=" + obj.value);
                        o[obj.name] = obj.value;
                    }
                    else {
                        o[idx] = obj;
                    }
                });
                var jqForm = $('#' + options.id).parents('div.panel-primary:first').find("div.panel-body form:first");
                if (jqForm.length) {
                    //o['filters'] = App.util.serializeFormToJson(jqForm);
                   // alert(JSON.stringify(o['filters']));
                }
                 o['filters']=jqForm.length?App.util.serializeFormToJson(jqForm):{};
                return JSON.stringify(o);
            };

            $('#' + options.id).dataTable({
                //"bProcessing": true,
                "bPaginate": true,
                "bLengthChange": true,
                "bFilter": false,
                "bSort": false,
                "bInfo": true,
                "bAutoWidth": false,
                // "aLengthMenu": [[10, 25, 50, 100], ["10 Per Page", "25 Per Page", "50 Per Page", "100 Per Page"]],
                // "sDom": 'T<"text-info"l><"text-info"i><"bottom"rpt><"clear">',
                "bServerSide": true,
                "sAjaxSource": options.uri,
                "oLanguage": {
                    "sZeroRecords": options.noRecords ? options.noRecords : "No records found"
                },
                "fnRowCallback": $.isFunction(options.rowCallback) ? options.rowCallback : null,
                "fnServerData": function (sSource, aoData, fnCallback) {
                    $.ajax({
                        dataType: 'json',
                        contentType: "application/json",
                        type: 'POST',
                        url: sSource,
                        data: stringify_aoData(aoData),
                        success: fnCallback,
                        error: function (e) {
                            alert(e);
                        }
                    });
                }
            });

            this.reload = function () {
                var dataTable = $('#' + options.id).DataTable();

                var oSettings = dataTable.fnSettings();
                dataTable.fnClearTable(this);
//                for (var i = 0; i < json.aaData.length; i++)
//                {
//                    dataTable.oApi._fnAddData(oSettings, json.aaData[i]);
//                }
                oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
                dataTable.fnDraw();
            };
            this.selectRow = function (rowTR) {
                $(rowTR).addClass(SELECTED_CLASS);
            };
            this.unselectRow = function (rowTR) {
                $(rowTR).removeClass(SELECTED_CLASS);
            };
            this.toggleRowSelection = function (rowTR, status) {
                $(rowTR).toggleClass(SELECTED_CLASS, status);
            };
            this.selectAll = function () {
                $('#' + options.id + ' tbody tr td:first input[type="checkbox"]').prop('checked', true);
                $('#' + options.id + " tbody tr").addClass(SELECTED_CLASS);
            };
            this.unselectAll = function () {
                $('#' + options.id + ' tbody tr td:first input[type="checkbox"]').prop('checked', false);
                $('#' + options.id + " tbody tr").romoveClass(SELECTED_CLASS);
            };
            this.toggleAllSelection = function (status) {
                $('#' + options.id + ' tbody tr td:first input[type="checkbox"]').prop('checked', status);
                $('#' + options.id + " tbody tr").toggleClass(SELECTED_CLASS, status);
            };
            this.getSelectedRows = function () {
                return $('#' + options.id).dataTable()._('tr.selected');
            };
            this.getSelectedRowsValueAt = function (index) {
                index = index ? index : 0;
                var values = [];
                $.each($('#' + options.id).dataTable()._('tr.selected'), function (_index, value) {
                    alert(value);
                    if ($.isArray(value) && index < value.length)
                        values.push(value[index]);
                });
                return values;
            };
            $('.panel-heading span.filter').on('click', function (e) {
                var $this = $(this),
                        $panel = $this.parents('.panel');
                $panel.find('.panel-body').slideToggle();
                if ($this.css('display') != 'none') {
                    $panel.find('.panel-body input').focus();
                }
            });
            $('[data-toggle="tooltip"]').tooltip();
            window[options.id] = this;
        }
    };
    App.util = {
        showMessage: function (messages, success, modal) {
            if (!messages)
                return;
            var body = modal ? '.modal-body' : '.box-body';
            var css1 = success ? 'alert-success' : 'alert-danger';
            var css2 = success ? 'fa-check' : 'fa-times-circle-o';
            App.util.hideMessage();
            if (!$(body + '>.alert-container').length) {
                $(body).prepend('<div class="alert-container"></div>');
            }
            $.each($.isArray(messages) ? messages : [messages], function (index, msg) {
                var css11 = css1;
                var css22 = css2;
                if (msg.severity) {
                    css11 = msg.severity === 'info' ? 'alert-success' : 'alert-danger';
                    css22 = msg.severity === 'info' ? 'fa-check' : 'fa-times-circle-o';
                    msg = msg.summary ? msg.summary : msg;
                }
                var htmlDom = '<div class="alert ' + css11 + ' col-md-11">'
                        + '<i class="fa ' + css22 + '"></i>'
                        + msg + '</div>';
                $(body + '>.alert-container').prepend(htmlDom);
            });
        },
        hideMessage: function (modal) {
            var body = modal ? '.modal-body' : '.box-body';
            $(body + '>.alert-container').empty();
        },
        showFieldMessage: function (options, messages) {
            if (!messages)
                return;
            $.each($.isArray(messages) ? messages : [messages], function (index, msg) {

                var field = (msg.fieldId && options.form) ? options.form.find("input[name='" + msg.fieldId + "']") : null;
                if (field && field.length) {
                    var inlineMsg = field.next('.inline-msg');
                    if (!inlineMsg || inlineMsg.length === 0) {
                        inlineMsg = $('<label class="inline-msg"></label>');
                        inlineMsg.addClass(msg.severity === 'info' ? 'inline-success' : 'inline-error');
                        field.parent().append(inlineMsg);
                    }
                    inlineMsg.text(msg.summary);
                    field.parent().addClass(msg.severity === 'info' ? 'has-success' : 'has-error');
                } else if (!msg.fieldId) {
                    App.util.showMessage(msg.summary, msg.severity === 'info');
                }
            });
        },
        serializeFormToJson: function (jqForm) {
            var o = {};
            var a = jqForm.serializeArray();
            $.each(a, function () {
                if (o[this.name] !== undefined) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        }

    };

    App.ajax = {
        submitForm: function (url, jqForm) {
            $(body + '>.alert-container').empty();
        },
        doAction: function (options) {
            if (!options.uri) {
                return;
            }
            var success = options.success;
            var _options = $.extend(true, options || {}, {
                type: options.type ? options.type : (options.form ? App.HTTP_POST : App.HTTP_GET),
                url: options.uri,
                data: (options.form ? options.form.serialize() + (options.data ? '&' + options.data : '') : (options.data ? options.data : '')),
                dataType: "json",
                cache: false,
                success: function (response) {
                    if (response && response.messages) {
                        App.util.showFieldMessage(options, response.messages);
                    }
                    if ($.isFunction(success)) {
                        success.call(this, response);
                    }
                    if (response.valid) {
                        if (options.modal && options.modal.modal)
                            options.modal.modal('hide');
                        if (options.datatable && options.datatable.reload)
                            options.datatable.reload();
                    }

                }
            });
            $.ajax(_options);
        }, //
        loadModalPage: function (options) {
            $.blockUI({message: null, css: {border: '1px solid #ccc'}});
            setTimeout(function () {
                $('#' + options.id + ' .modal-body').load(options.uri, options.data ? options.data : {}, function (response, status, xhr) {
                    $.unblockUI();
                    if (status !== 'error') {
                        $('#' + options.id).modal({show: true});
                        if ($.isFunction(options.success)) {
                            options.success.call(this, response);
                        }
                    } else if (options.fail && $.isFunction(options.fail)) {
                        if ($.isFunction(options.fail)) {
                            options.fail.call(this, response);
                        }
                    }
                });
            }, 500);
        },
        loadModalContent: function (options) {
            if ($('#' + options.id).is(':visible'))
                $('#' + options.id + ' .modal-body').block({message: 'Loading content', css: {border: '1px solid #999'}});
            setTimeout(function () {
                $('#' + options.id + ' .modal-body').load(options.uri, options.data ? options.data : {}, function (response, status, xhr) {
                    if ($('#' + options.id).is(':visible'))
                        $('#' + options.id + ' .modal-body').unblock();
                    if (status !== 'error') {
                        if ($.isFunction(options.success)) {
                            options.success.call(this, response);
                        }
                    } else if (options.fail && $.isFunction(options.fail)) {
                        if ($.isFunction(options.fail)) {
                            options.fail.call(this, response);
                        }
                    }
                });
            }, 500);
        },
        loadContent: function (options) {
            if (!options.uri) {
                return;
            }
            var success = options.success;
            var _options = $.extend(true, options || {}, {
                type: options.form ? App.HTTP_POST : App.HTTP_GET,
                url: App.PPR_PATH + options.uri,
                data: (options.form ? options.form.serialize() + (options.data ? '&' + options.data : '') : (options.data ? options.data : '')),
                dataType: "html",
                cache: false,
                success: function (response) {
                    if ($.isFunction(success)) {
                        success.call(this, response);
                    }
                }
            });
            //_options.data = (_options.data ? _options.data + '&' : '') + 'renderMode=partial';

            $.ajax(_options);
        }
    };

})(jQuery);

