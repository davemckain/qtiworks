/***********************************************************

Common JS for the QTIWorks Webapp.

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

************************************************************/

$(document).ready(function() {
    /* Create fake hyperlinks for POST forms */
    $(".postLink").each(function() {
        var form = $(this);
        var submitText = form.find("*[type='submit']").attr('value');
        var link = $("<a href='#'>" + submitText + "</a>");
        link.click(function() { form.submit(); return false; });
        form.after(link);
        form.hide();
    });
});
