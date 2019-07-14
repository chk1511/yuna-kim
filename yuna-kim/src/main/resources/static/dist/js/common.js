$(function () {

    if ($('table#dataTable').length > 0) {
        $('table#dataTable tfoot th').each(function (i) {
            var title = $('table#dataTable thead th').eq($(this).index()).text();
            $(this).html('<input type="text" placeholder="Search ' + title + '" data-index="' + i + '" class="form-control" style="width: 100%"/>');
        });

        var table = $('table#dataTable').DataTable();

        if (table.columns()[0].length > 0) {

            var columnsToggle = $('<div class="columns-toggle">Toggle column : </div>');

            table.columns().every(function (idx) {
                var that = this;

                $('input', this.footer()).on('keyup change', function () {
                    if (that.search() !== this.value) {
                        that
                            .search(this.value)
                            .draw();
                    }
                });

                if (idx > 0) {
                    columnsToggle.append(" - ")
                }
                columnsToggle.append('<a class="toggle-vis" data-column="' + idx +'">' + this.header().innerHTML + '</a>')
            });

            columnsToggle.insertBefore($('table#dataTable'));
        }

        $('a.toggle-vis').on( 'click', function (e) {
            e.preventDefault();

            // Get the column API object
            var column = table.column( $(this).attr('data-column') );

            // Toggle the visibility
            column.visible( ! column.visible() );
        } );

        $(table.table().container()).on('keyup change', 'tfoot input', function () {
            if (table.search() !== this.value) {
                table
                    .column($(this).data('index'))
                    .search(this.value)
                    .draw();
            }
        });

        $('button#tableRefresh').on('click', function () {
            table.state.clear();
            window.location.reload();
        });
    }
});

function clearForm(frmElements) {
    for (var i = 0; i < frmElements.length; i++) {

        var fieldType = frmElements[i].type.toLowerCase();

        switch (fieldType) {
            case "text":
            case "password":
            case "textarea":
            case "hidden":
                frmElements[i].value = "";
                break;
            case "radio":
            case "checkbox":
                if (frmElements[i].checked) {
                    frmElements[i].checked = false;

                }
                break;
            case "select-one":
            case "select-multi":
                frmElements[i].selectedIndex = -1;
                break;
            default:
                break;
        }
    }
}

function icheckBoxReset(obj) {

    console.log($(obj), $(obj).attr('checked'), $(obj).attr('checked'));
    if ($(obj).attr('checked') == 'checked') {
        if ($(obj).parent().find('.btn-default')) {
            $(obj).trigger('click');
        }
        // $(obj).parent().removeClass('btn-default').removeClass('off').addClass('btn-primary');
    } else {
        // obj.parent().removeClass('btn-primary').addClass('btn-default').addClass('off');
    }
}