/* Toggles the validation result */

$(document).ready(function() {
function setupTogglers(h3Query) {
  h3Query.next('div').toggle();
  h3Query.append("<span class='toggler plus'>+</span><span class='toggler minus'>-</span>");
  h3Query.click(function() {
    $(this).next().toggle();
    $(this).children('.toggler').toggle();
  });
}
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > .details > .resultPanel > .details > .resultPanel h3'));
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > .details > .resultPanel > h3'));
setupTogglers($('.validationResult > .resultPanel > .details > .resultPanel > h3'));
});
