/* Controller for AJAX Up-Conversion / Semantic Enrichment functionality
 *
 * Requirements:
 *
 * jquery.js (at least version 1.5.0)
 *
 * Author: David McKain
 *
 * $Id: UpConversionAJAXController.js 2608 2011-04-14 09:44:15Z davemckain $
 *
 * Copyright (c) 2008-2011, The University of Edinburgh
 * All Rights Reserved
 */

/************************************************************/

var UpConversionAJAXController = (function() {

    var upConversionServiceUrl = null; /* Caller must fill in */
    var delay = 500;

    var STATUS_EMPTY               = 0;
    var STATUS_WAITING_CLIENT      = 1;
    var STATUS_WAITING_SERVER      = 2;
    var STATUS_SUCCESS             = 3;
    var STATUS_PARSE_ERROR         = 4;
    var STATUS_UPCONVERSION_FAILED = 5;
    var STATUS_UNKNOWN_ERROR       = 6;
    var STATUS_AJAX_ERROR          = 7;

    var EMPTY_INPUT = "(Empty Input)";

    /************************************************************/

    var UpConversionAJAXControl = function(_messageContainerId, _bracketedRenderingContainerId) {
        this.messageContainerId = _messageContainerId;
        this.bracketedRenderingContainerId = _bracketedRenderingContainerId;
        this.pmathSemanticSourceContainerId = null;
        this.pmathBracketedSourceContainerId = null;
        this.cmathSourceContainerId = null;
        this.maximaSourceContainerId = null;
        var currentXHR = null;
        var currentTimeoutId = null;
        var thisControl = this;

        /* Schedules verification on the given data after a short delay
         * that batches requests together for efficiency.
         *
         * Use null or blank input to signify "empty input". The UI will be updated accordingly.
         */
        this._verifyLater = function(verifyInputData) {
            if (upConversionServiceUrl==null) {
                throw new Error("upConversionServiceUrl is null - no verification can be done");
            }
            this._resetTimeout();
            if (verifyInputData!=null && verifyInputData.length>0) {
                this._updateUpConversionContainer(STATUS_WAITING_SERVER);
                currentTimeoutId = window.setTimeout(function() {
                    thisControl._callVerifier(verifyInputData);
                    currentTimeoutId = null;
                }, delay);
            }
            else {
                this._clearVerificationResult();
            }
        };

        this._clear = function() {
            this._resetTimeout();
            this._clearVerificationResult();
        };

        this._resetTimeout = function() {
            if (currentTimeoutId!=null) {
                window.clearTimeout(currentTimeoutId);
                currentTimeoutId = null;
            }
        };

        /* Calls up the AJAX verification service on the given data, causing a UI update once
         * the results are returned.
         *
         * Use null input to signify "empty input". The UI will be updated instantly.
         */
        this._callVerifier = function(verifyInputData) {
            currentXHR = jQuery.ajax({
                type: 'POST',
                url: upConversionServiceUrl,
                dataType: 'json',
                data: {input: verifyInputData },
                success: function(data, textStatus, jqXHR) {
                    if (currentXHR==jqXHR) {
                        currentXHR = null;
                        thisControl._showVerificationResult(data);
                    }
                },
                error: function(jqXHR, textStatus, error) {
                    thisControl._updateUpConversionContainer(STATUS_AJAX_ERROR, error);
                }
            });
        };

        this._showVerificationResult = function(jsonData) {
            /* We consider "valid" to mean "getting as far as CMathML" here */
            var cmath = jsonData['cmath'];
            if (cmath!=null) {
                var bracketedMathML = jsonData['pmathBracketed'];
                this._updateUpConversionContainer(STATUS_SUCCESS, bracketedMathML);
            }
            else if (jsonData['cmathFailures']!=null) {
                this._updateUpConversionContainer(STATUS_UPCONVERSION_FAILED);
            }
            else if (jsonData['errors']!=null) {
                var html = '<ul>';
                for (var i in jsonData['errors']) {
                    html += '<li>' + jsonData['errors'][i] + '</li>'
                }
                html += '</ul>';
                this._updateUpConversionContainer(STATUS_PARSE_ERROR, null, html);
            }
            else {
                this._updateUpConversionContainer(STATUS_UNKNOWN_ERROR);
            }

            /* Maybe show various sources, if requested */
            if (this.pmathSemanticSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.pmathSemanticSourceContainerId),
                    jsonData['pmathSemantic'] || 'Could not generate Semantic Presentation MathML');
            }
            if (this.pmathBracketedSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.pmathBracketedSourceContainerId),
                    jsonData['pmathBracketed'] || 'Could not generate Bracketed Presentation MathML');
            }
            if (this.cmathSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.cmathSourceContainerId),
                    cmath || 'Could not generate Content MathML');
            }
            if (this.maximaSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.maximaSourceContainerId),
                    jsonData['maxima'] || 'Could not generate Maxima syntax');
            }
        };

        this._clearVerificationResult = function() {
            this._updateUpConversionContainer(STATUS_EMPTY);
            if (this.pmathSemanticSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.pmathSemanticSourceContainerId), EMPTY_INPUT);
            }
            if (this.pmathBracketedSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.pmathBracketedSourceContainerId), EMPTY_INPUT);
            }
            if (this.cmathSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.cmathSourceContainerId), EMPTY_INPUT);
            }
            if (this.maximaSourceContainerId!=null) {
                this._showPreformatted(jQuery("#" + this.maximaSourceContainerId), EMPTY_INPUT);
            }
        };

        this._updateUpConversionContainer = function(status, mathElementString, errorContent) {
            var bracketedRenderingContainer = jQuery("#" + this.bracketedRenderingContainerId);
            var messageContainer = jQuery("#" + this.messageContainerId);
            /* Set up if not done already */
            if (messageContainer.children().size()==0) {
                messageContainer.html("<div class='upConversionAJAXControlMessage'></div>"
                    + "<div class='upConversionAJAXControlError'></div>");
                bracketedRenderingContainer.attr('class', 'upConversionAJAXControlPreview');
            }
            var statusContainer = messageContainer.children().first();
            var errorContainer = statusContainer.next();
            switch(status) {
                case STATUS_EMPTY:
                    errorContainer.hide();
                    bracketedRenderingContainer.hide();
                    statusContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage');
                    this._showMessage(statusContainer, '\xa0');
                    break;

                case STATUS_WAITING_CLIENT:
                case STATUS_WAITING_SERVER:
                    errorContainer.hide();
                    bracketedRenderingContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage waiting');
                    this._showMessage(statusContainer, 'Verifying your input...');
                    statusContainer.show();
                    break;

                case STATUS_SUCCESS:
                    errorContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage success');
                    this._showMessage(statusContainer, 'I have interpreted your input as:');
                    this._showMathML(bracketedRenderingContainer, mathElementString);
                    statusContainer.show();
                    bracketedRenderingContainer.show();
                    break;

                case STATUS_PARSE_ERROR:
                    bracketedRenderingContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage failure');
                    this._showMessage(statusContainer, 'SnuggleTeX could not parse your input:');
                    this._showMessage(errorContainer, errorContent);
                    statusContainer.show();
                    errorContainer.show();
                    break;

                case STATUS_UPCONVERSION_FAILED:
                    errorContainer.hide();
                    bracketedRenderingContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage failure');
                    this._showMessage(statusContainer, 'Sorry, I could not make sense of your input');
                    this._showMessage(errorContainer, null);
                    statusContainer.show();
                    break;

                case STATUS_UNKNOWN_ERROR:
                    errorContainer.hide();
                    bracketedRenderingContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage error');
                    this._showMessage(statusContainer, 'Unexpected error');
                    this._showMessage(errorContainer, null);
                    statusContainer.show();
                    break;

                case STATUS_AJAX_ERROR:
                    bracketedRenderingContainer.hide();
                    statusContainer.attr('class', 'upConversionAJAXControlMessage error');
                    this._showMessage(statusContainer, 'Communication error');
                    this._showMessage(errorContainer, errorContent);
                    statusContainer.show();
                    errorContainer.show();
                    break;
            }
        };

        this._showMessage = function(containerQuery, html) {
            UpConversionAJAXController.replaceContainerContent(containerQuery, html || "\xa0");
        };

        this._showMathML = function(containerQuery, mathmlString) {
            UpConversionAJAXController.replaceContainerMathMLContent(containerQuery, mathmlString);
        };

        this._showPreformatted = function(containerQuery, text) {
            UpConversionAJAXController.replaceContainerPreformattedText(containerQuery, text);
        };
    };

    UpConversionAJAXControl.prototype.setPMathSemanticSourceContainerId = function(id) {
        this.pmathSemanticSourceContainerId = id;
    };

    UpConversionAJAXControl.prototype.setPMathBracketedSourceContainerId = function(id) {
        this.pmathBracketedSourceContainerId = id;
    };

    UpConversionAJAXControl.prototype.setCMathSourceContainerId = function(id) {
        this.cmathSourceContainerId = id;
    };

    UpConversionAJAXControl.prototype.setMaximaSourceContainerId = function(id) {
        this.maximaSourceContainerId = id;
    };

    UpConversionAJAXControl.prototype.showVerificationResult = function(jsonData) {
        this._showVerificationResult(jsonData);
    };

    UpConversionAJAXControl.prototype.verifyLater = function(verifyInputData) {
        this._verifyLater(verifyInputData);
    };

    UpConversionAJAXControl.prototype.clear = function() {
        this._clear();
    };

    return {
        replaceContainerContent: function(containerQuery, content) {
            containerQuery.empty();
            if (content!=null) {
                containerQuery.append(content);
            }
        },

        replaceContainerPreformattedText: function(containerQuery, content) {
            if (navigator.platform.substr(0,3)=='Win') { /* Convert to Windows line endings first */
                content = content.replace(/\n/g, "\r\n");
            }
            containerQuery.text(content);
        },

        replaceContainerMathMLContent: function(containerQuery, mathmlString) {
            containerQuery.each(function() {
                var mathJax = MathJax.Hub.getAllJax(this.id);
                if (mathJax.length==1) {
                    MathJax.Hub.Queue(["Text", mathJax[0], mathmlString]);
                }
                else {
                    throw new Error("Expected 1 MathJax element, but got " + mathJax.length);
                }
            });
        },

        createUpConversionAJAXControl: function(messageContainerId, bracketedRenderingContainerId) {
            if (messageContainerId==null) {
                throw new Error("messageContainerId must not be null");
            }
            if (bracketedRenderingContainerId==null) {
                throw new Error("bracketedRenderingContainerId must not be null");
            }
            return new UpConversionAJAXControl(messageContainerId, bracketedRenderingContainerId);
        },

        getUpConversionServiceUrl: function() { return upConversionServiceUrl },
        setUpConversionServiceUrl: function(url) { upConversionServiceUrl = url },

        getDelay: function() { return delay },
        setDelay: function(newDelay) { delay = newDelay }
    };

})();

