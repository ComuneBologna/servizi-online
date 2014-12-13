$(document).ready(function() {
	//Initial Settings
	
	//Get all Portlets div
	var allPortlets = $('div[id^="imieidati_precaching_"]');
	
	for(var i = 0; i < allPortlets.length; i++){
		var divPortletId = allPortlets[i].id;
		
		//Get PortletName from div created on the page
		var singlePortletName = divPortletId.replace(/^imieidati_precaching_/, '');
		
		var postUrl = $('#'+singlePortletName+'_ajax_url').text();
		var portal = $('#'+singlePortletName+'_portal').text();
		var hidePortlet = $('#'+singlePortletName+'_hidePortlet').text()=='false';
		console.log("portale identificato: "+portal);
		console.log("predispongo caching per portlet: "+singlePortletName);
		console.log("nascondo portlet: "+hidePortlet);
		var caching = new IMieiDatiCaching(singlePortletName,postUrl, portal, hidePortlet);
		caching.start();
	}
	
});

function IMieiDatiCaching(portletName, postUrl, portal, hidePortlet){
	this.pn = portletName;
	this.url = postUrl;
	this.portal = portal;
	this.hidePortlet = hidePortlet;
	if(portal=='liferay' && hidePortlet)
		this.hideLiferayPortlet();
}
IMieiDatiCaching.prototype.hideLiferayPortlet = function(){
	//$('div[id^=p_p_id_I_Miei_Dati_Caching_Portlet_WAR_IMieiDati10SNAPSHOT_ .portlet-borderless-bar').length
	//if($('[id^=p_p_id_'+this.pn+'_WAR_IMieiDati10SNAPSHOT_] .portlet-borderless-bar').length==0)
		$('[id^=p_p_id_'+this.pn+'_WAR_IMieiDati10SNAPSHOT_]').css({display:'none'});
};
IMieiDatiCaching.prototype.start = function(){
	console.log("start caching");
	var postUrl = this.url;
	$.ajax({
		url: postUrl,
		type: 'POST',
		data: {cmd : "ListaSezioni",token : "132465"},
		success:function(json){
			var sectionsArray = JSON.parse(json);
			console.log("ottenute "+sectionsArray.length+" sezioni");
			for(var index = 0; index < sectionsArray.length; index++){
				var postdata = {cmd : "CacheSezione",token : "132465",id : sectionsArray[index].id, codice : sectionsArray[index].id};
				console.log("postData: "+postdata);
				$.ajax({
					url: postUrl,
					type: 'POST',
					data: postdata,
					success:function(response){
						console.log("cached section "+postdata.id);
					},
					error: function (request, status, error) {
						console.log(status);
						console.log(error);
			            console.log(request.responseText);
			        }
				});
			}

		},
		error: function (request, status, error) {
            console.log(request.responseText);
        }
	});
};