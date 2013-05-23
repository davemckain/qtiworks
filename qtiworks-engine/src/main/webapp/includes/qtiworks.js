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

    /* Add expand/collapse functionality to each div.resultPanel */
    function setupTogglers(h4Query) {
        h4Query.next('div').toggle();
        h4Query.append("<span class='toggler plus'>\u25ba</span><span class='toggler minus'>\u25bc</span>");
        h4Query.click(function() {
            $(this).next().toggle();
            $(this).children('.toggler').toggle();
        });
    }
    setupTogglers($('.resultPanel > .details > .resultPanel > .details > .resultPanel > .details > .resultPanel h4'));
    setupTogglers($('.resultPanel > .details > .resultPanel > .details > .resultPanel > h4'));
    setupTogglers($('.resultPanel > .details > .resultPanel > h4'));

    /* Form progressive enhancement - show target page (which should be XML) in a dialog box */
    $('.showXmlInDialog').submit(function() {
        var title = $(this).attr('title');
        var action = $(this).attr('action');
        $.get(action, function(data, textStatus, jqXHR) {
            if (document.getElementById('prettifier')==null) {
                var script = document.createElement('script');
                script.src = 'https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js';
                script.id = 'prettifier';
                $('head').append(script);
            }
            var xmlDiv = $("<pre class='prettyprint xmlSource'></pre>");
            xmlDiv.text(jqXHR.responseText);
            xmlDiv.dialog({
                modal: true,
                width: $(window).width() * 0.8,
                height: $(window).height() * 0.6,
                title: title
            });
        });
        return false;
    });
});
