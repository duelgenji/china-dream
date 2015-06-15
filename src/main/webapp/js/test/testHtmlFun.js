/**
 * 
 */

$(function(){
	$("#queryList").click(function(){
		
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'inquiry.id=25',
			dataType: 'json',
			url:"../../inquiry/getInqueryList.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}
		});
	});
	
	$("#testDetailAjax").click(function(){
		
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'inquiry.id=25',
			dataType: 'json',
			url:"../../inquiry/getAInquiry.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}
		});
	});
	
	$("#testRegPerson").click(function(){
		
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'user.nickName=zhangymax',
			dataType: 'json',
			url:"../../reg/register_p.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}			
		});
	});
	
	$("#testLogin").click(function(){
		
//		document.forms[0].action = "../../login/login.action";
//		document.forms[0].submit();
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'user.userName=mychinadreams@163.com&user.password=1234561',
			dataType: 'json',
			url:"../../login/login.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}
		});
	});
	
	$("#testMyInquiry").click(function(){
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'inquiry.userName=1',
			dataType: 'json',
			url:"../../inquiry/myInquiry.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}
		});		
	});
	
	$("#testCreateNewInquiry").click(function(){
		
		$.ajax({
			type:"get",
			contentType:"contentType=text/html; charset=utf-8",
			data: 'inquiry.title=询价测试&inquiry.isOpen=1&user.userName=yee',
			dataType: 'json',
			url:"../../inquiry/createAInquiry.action",
			async:false,
			success: function(data){
				//alert((data.data)[0].countOfZan);
			}
		});			
	});
});

