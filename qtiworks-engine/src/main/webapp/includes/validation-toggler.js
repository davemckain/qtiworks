/* Toggles the validation result */

$(document).ready(function() {
function setupTogglers(h4Query) {
  h4Query.next('div').toggle();
  h4Query.append("<span class='toggler plus'>\u25ba</span><span class='toggler minus'>\u25bc</span>");
  h4Query.click(function() {
    $(this).next().toggle();
    $(this).children('.toggler').toggle();
  });
}
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > .details > .resultPanel > .details > .resultPanel h4'));
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > .details > .resultPanel > h4'));
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > h4'));
});
