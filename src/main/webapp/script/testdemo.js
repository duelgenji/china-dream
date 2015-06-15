/**
 * 
 */
window.onload = function(){

	document.getElementById("btnDemo").onclick = function(){
		alert("kkk");
		document.forms[0].action =  './testDemo/testDemo.action?email=1111';
		document.forms[0].submit();		
	};	
};

