/*
 *
 * Requirements:
 *
 * jquery.js
 *
 * Author: David McKain
 *
 * Copyright (c) 2012-2013, The University of Edinburgh
 * All Rights Reserved
 */

/************************************************************/

var AuthorMode = (function() {
    return {
        setupAuthorModeToggler: function() {
            var hider = $("<span class='clicky'>Hide author mode</span>");
            hider.click(function() {
                $('.authorMode').toggle();
            });
            $('.authorModeNote').append(hider);

            var shower = $("<span class='clicky authorMode hide'>Show author mode</span>");
            shower.click(function() {
                $('.authorMode').toggle();
            });
            $('.authorModeNote').after(shower);
        }
    };

})();
