function sensorsSetup(server_url,web_url){
	  //console.log(data)
     var saIndex= server_url.indexOf("/sa?"); 
	 var baseUrl=server_url.substring(0,saIndex);
	 (function(para) {
		  var p = para.sdk_url, n = para.name, w = window, d = document, s = 'script',x = null,y = null;
		  w['sensorsDataAnalytic201505'] = n;
		  w[n] = w[n] || function(a) {return function() {(w[n]._q = w[n]._q || []).push([a, arguments]);}};
		  var ifs = ['track','quick','register','registerPage','registerOnce','clearAllRegister','trackSignup', 'trackAbtest', 'setProfile','setOnceProfile','appendProfile', 'incrementProfile', 'deleteProfile', 'unsetProfile', 'identify','login','logout','trackLink','clearAllRegister'];
		  for (var i = 0; i < ifs.length; i++) {
		    w[n][ifs[i]] = w[n].call(null, ifs[i]);
		  }
		  if (!w[n]._t) {
		    x = d.createElement(s), y = d.getElementsByTagName(s)[0];
		    x.async = 1;
		    x.src = p;
		    y.parentNode.insertBefore(x, y);
		    w[n].para = para;
		  }
		})({
		  sdk_url: '/resources/shop/default/js/sensorsdatasdk.min.js',
		  name: 'sensorsdata',
		  config_url: baseUrl+'/config/',
		  web_url: web_url,
	   	  server_url: server_url,
		  heatmap:{collect_elements: 'all' }
		});
		sensorsdata.quick('autoTrack');
   	if(typeof(Storage) == "undefined"){
   		sensorsdata.track('LocalStorageNotSupport', {
		});
   	}
}
var sensors_url= "sensors_url";
var sensors_web= "sensors_web";
var sensors_timeout= "sensors_timeout";
var cacheUrl=localStorage.getItem(sensors_url);
var cacheWebUrl=localStorage.getItem(sensors_web);
var cacheTimeout=localStorage.getItem(sensors_timeout);
if (typeof(cacheWebUrl) !== "undefined" &&cacheTimeout && typeof(cacheUrl) !== "undefined"&& typeof(cacheTimeout) !== "undefined"&&cacheTimeout>=new Date().getTime()&& typeof(Storage) !== "undefined") {
	if(cacheUrl!=''&&cacheWebUrl!=''&&cacheUrl!='null'&&cacheWebUrl!='null'){
		sensorsSetup(cacheUrl,cacheWebUrl);
	}
}else{
$.ajax({
  url: "/analytic/urlAndProject.jhtml",
  type: 'get',
  dataType: 'json',
  timeout: 1000,
  success: function (result, status) {
	  var serverUrl=result.server;
	  var webUrl=result.web;
	  if (typeof(serverUrl) !== "undefined"&& typeof(webUrl) !== "undefined") {
		  localStorage.setItem(sensors_url, serverUrl);
		  localStorage.setItem(sensors_web, webUrl);
		  localStorage.setItem(sensors_timeout, new Date().getTime()+1000*60*60*24);
		  if(sensors_url!=''&&webUrl!=''&&sensors_url!='null'&&webUrl!='null'){
			  sensorsSetup(sensors_url,webUrl);
		  }
	  }
  },
  fail: function (err, status) {
    console.log(err);
    alert("系统错误，请刷新页面重试");
  }
})
}







