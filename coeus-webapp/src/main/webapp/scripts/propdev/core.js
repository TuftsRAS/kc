/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
var Kc = Kc || {};
Kc.PropDev = Kc.PropDev || {};
(function (namespace, $) {
    $(document).on("ready", function() {
        var navigateActionLinks = jQuery.find("a[class='uif-actionLink']").filter(function (a) {
            var data = jQuery(a).data('submit_data');
            return (data != undefined && data.methodToCall != undefined && data.methodToCall == 'navigate');
        });

        navigateActionLinks.forEach(function(a) {
            jQuery(a).addClass('navigateCheckDirty');
        });

        jQuery('.navigateCheckDirty').click(function(e) {
        	return namespace.navigateCheckDirty(e);
        });
    });

    namespace.navigateCheckDirty = function(e) {
        var link = jQuery(e.target);
        var dirtyFields = jQuery('.dirty');
        var data = link.data('submit_data');

        if (data == undefined) {
            data = {};
        }

        data['actionParameters[pageIsDirty]'] = (dirtyFields != undefined && dirtyFields.length > 0);
        link.attr('data-submit_data', JSON.stringify(data));
	};

    namespace.updateSponsorName = function(sponsorCode, nameSelector) {
		$.getJSON(window.location.pathname, 
				{'sponsorCode': sponsorCode, 'methodToCall': 'getSponsor'}, 
				function(json) {
					var sponsorName = null;
					if (json !== null) {
						sponsorName = json['sponsorName'];
					}
					$(nameSelector).html(sponsorName);
				});
	};
	namespace.sponsorSuggestSelect = function(event, ui) {
		$(event.target).val(ui.item.value);
		$(event.target).parents('.uif-inputField:first').find('.informationalText').html(ui.item.sponsorName);
	};
    namespace.markActiveMenuLink = function(index) {
        $("#" + kradVariables.NAVIGATION_ID + " li." + kradVariables.ACTIVE_CLASS).removeClass(kradVariables.ACTIVE_CLASS);

        var pageId = getCurrentPageId();
        var liParents = $("a[data-menuname='" + pageId + index +"']").parents("li");
        liParents.addClass(kradVariables.ACTIVE_CLASS);
    }
    namespace.setHeaderHeight = function() {
        $("#PropDev-DefaultView_header").height($("#PropDev-DefaultView_headerRightGroup").height());
    }
})(Kc.PropDev, jQuery);
