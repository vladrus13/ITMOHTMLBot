/**
	UTILITIES JQUERY PLUGIN
	[jquery.utilities.js]
	
	- LICENSE -
	This software is licensed under the
	MIT License (MIT).
	http://opensource.org/licenses/MIT
	
	@version		1.0.1.0
	@pubdate		15/04/2016
	@updated		15/04/2016
*/

window.utilities = {

	// Options for <fpsSaver> prototype
	fpsSaver: {
		className: "scrolling",
		timer: null,
		delay: 500,
		enabled: false
	}
	
};

// Aliasing short name to <window.utilities>
var utils = window.utilities;

;(function($){

	/**
		FPS saver prototype
	*/
	$.fn.fpsSaver = function(){
		return this.each(function(){
			var $body = $("body");
			
			// Binding events
			$(this).bind("scroll", function(){
				clearTimeout(utils.fpsSaver.timer);
				
				if (!utils.fpsSaver.enabled) {
					$body.addClass(utils.fpsSaver.className);
					utils.fpsSaver.enabled = true;
				}
				
				utils.fpsSaver.timer = setTimeout(function(){
					$body.removeClass(utils.fpsSaver.className);
					utils.fpsSaver.enabled = false;
				}, utils.fpsSaver.delay);
			});
		});
	};
	
})(jQuery);

// Initialization
$(function(){
	//$(window).fpsSaver();
	if ($.fn.swipebox) {
		$(".swipebox").swipebox();
	}

	function showPartners() {
		const $partnersItem = $('.partners__item');
		const $partnersBtn = $('#partners_btn');
		$partnersItem.each(function (i,item) {
			if(i < 8) {
				$(item).addClass('show');
			}
		});

		$partnersBtn.click(function () {
			$partnersItem.each(function (i,item) {
				$(item).addClass('show');
			});
			$(this).addClass('hidden');
		});
	}

	showPartners();
});