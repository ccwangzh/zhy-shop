window.onload=function(){
	
	var tabimg=document.getElementsByClassName('tabimg');
	for(var i=0; i < tabimg.length; i++){		
		var oid = tabimg[i];
		 tabImg(oid)
	}

	var playimg=document.getElementsByClassName('playimg')
	for(var i=0; i < playimg.length; i++){		
		var oplay = playimg[i];
		playImg(oplay)
	}

}


function tabImg(oid){

	
	var oCont=oid.getElementsByClassName('cont')[0];
	var aCli=oCont.getElementsByClassName('cli');
	var oNav=oid.getElementsByClassName('nav')[0];
	var aNli=oNav.getElementsByTagName('li')	
	

	for(var i=0; i <aNli.length; i++){
		aNli[i].index = i;
		aNli[i].onclick=function(){
			
			for(var k=0; k <aNli.length; k++){				
				aNli[k].className ='';
				aCli[k].className ='cli';
			}

			this.className = 'active';
			aCli[this.index].className = 'cli active';	
		}
	}

}



function playImg(oplay){

	var oCont=oplay.getElementsByClassName('tabcont')[0];
	var aCli=oCont.getElementsByTagName('li');
	var oNav=oplay.getElementsByClassName('tabnav')[0];
	var aNli=oNav.getElementsByTagName('li')	
	var timer = null;
	var nowindex = 0;

	for(var i=0; i <aNli.length; i++){
		aNli[i].index = i;
		aNli[i].onclick=function(){
			clearInterval(timer)
			nowindex = this.index	
			play()
		}
	}
	function play(){
		for(var k=0; k <aNli.length; k++){				
			aNli[k].className ='';
			aCli[k].className ='';
		}

		aNli[nowindex].className = 'now';
		aCli[nowindex].className = 'now';	
	}

	function autoplay(){
		nowindex++;
		if(nowindex == aCli.length){
			nowindex = 0;
		}
		play()
	}
	timer = setInterval(autoplay,2000)

	oplay.onmouseover=function(){
		clearInterval(timer)
	}
	oplay.onmouseout=function(){
		timer = setInterval(autoplay,2000)
	}

}

