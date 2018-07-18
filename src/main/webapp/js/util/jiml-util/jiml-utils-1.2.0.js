/**
 * V 1.2.0
 * 版	第一部分代表涉及功能模块的增加
 * 本	第二部分代表涉及方法的增加或方法形参的增加（低版本升级高版本可能会出现不兼容）
 * 构	第三部分代表仅方法内部的 bug 修复或优化
 * 成
 *
 * V 1.1.0 -> V 1.2.0
 * 删除一个分页方法，并且修改分页方法及消息提示方法的形参
 */
/////////格式化时间 start////////////////////////////////
/**
 * 格式化时间
 * 对Date的扩展，将 Date 转化为指定格式的字符串
 *
 * 年(y)													可以用 1-4 个占位符
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q)	可以用 1-2 个占位符
 * 毫秒(S)												只能用  1  个占位符(是 1-3 位的数字)
 * eg:
 * (new Date()).format("yyyy-MM-dd hh:mm:ss.S")==> 2006-07-02 08:09:04.423
 * (new Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04
 * (new Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04
 * (new Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04
 * (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */
Date.prototype.format=function(fmt) {
    var o = {
    "M+" : this.getMonth()+1, //月份
    "d+" : this.getDate(), //日
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时
    "H+" : this.getHours(), //小时
    "m+" : this.getMinutes(), //分
    "s+" : this.getSeconds(), //秒
    "q+" : Math.floor((this.getMonth()+3)/3), //季度
    "S" : this.getMilliseconds() //毫秒
    };
    var week = {
    "0" : "/u65e5",
    "1" : "/u4e00",
    "2" : "/u4e8c",
    "3" : "/u4e09",
    "4" : "/u56db",
    "5" : "/u4e94",
    "6" : "/u516d"
    };
    if(/(y+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    if(/(E+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);
    }
    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}
/////////格式化时间 end////////////////////////////////
/////////分页 start////////////////////////////////
//3种样式
/**
 *
 * @param maxPage				总页数
 * @param nowPage				当前页
 * @param tagId					分页所在标签id值
 * @param onclickFunctionName	页码点击事件触发的方法名
 * @param data {size:number,num:number}	可选参数。size：分页大小（默认8，最小3）；num：页码区编号（默认为空），同一页面有多个分页时必须赋此值。
 */
var $functionName$;
function pagination(maxPage,nowPage,tagId,onclickFunctionName,data) {
	var parentTag = $("#"+tagId);
	parentTag.empty();
	if(maxPage<2){return;}
	if(nowPage<1){
		nowPage = 1;
	} else if(nowPage>maxPage){
		nowPage = maxPage;
	}
	var pagesize = 8;
	var num = "";
	if(data){
		pagesize = data.size!=undefined?data.size<3?3:data.size:pagesize;
		num = data.num!=undefined?data.num:num;
	}
	$functionName$ = onclickFunctionName;
	var pageDiv="";
	pageDiv += '<div class="paginationPageDiv'+num+'"><a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+((nowPage-1)<1?1:(nowPage-1))+')" href="javascript:;">上一页</a>';
	if(maxPage>pagesize+4){
		if(nowPage<pagesize+2){
			for(var i=1;i<=pagesize+2;i++){
				if(i==nowPage){
					pageDiv += '<a class="paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}else{
					pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}
			}
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+maxPage+')" href="javascript:;">'+maxPage+'</a>';
		}else if(nowPage>maxPage-pagesize-1){
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'(1)" href="javascript:;">1</a>';
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			for (i=maxPage-pagesize-1;i<=maxPage;i++){
				if(i==nowPage){
					pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}else{
					pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}
			}
		}else {
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'(1)" href="javascript:;">1</a>';
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			for (i=1;i<=pagesize;i++){
				if(nowPage-Math.ceil(pagesize/2)+i==nowPage){
					pageDiv += '<a class="paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+nowPage+')" href="javascript:;">'+nowPage+'</a>';
				}else{
					pageDiv += '<a class = "paginationPage'+num+'" onclick="'+onclickFunctionName+'('+(nowPage-Math.ceil(pagesize/2)+i)+')" href="javascript:;">'+(nowPage-Math.ceil(pagesize/2)+i)+'</a>';
				}
			}
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+maxPage+')" href="javascript:;">'+maxPage+'</a>';
		}
	}else{
		var i = 1;
		while (i <= maxPage) {
			if(i==nowPage){
				pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
			}else{
				pageDiv += '<a class = "paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
			}
			i++;
		}
	}
	pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+((nowPage+1)>maxPage?maxPage:(nowPage+1))+')" href="javascript:;">下一页</a><input id="paginationPageInput'+num+'" value="'+(maxPage/2).toFixed(0)+'" style="vertical-align:middle;border-radius: 3px;border: 1px solid #dfdfdf;padding: 5px 10px;width: 3em;font: inherit;text-align: center;" onkeypress="$tiaozhuan$(event,'+num+')"/> <a class="paginationPage'+num+'" onclick="$tiaozhuan$(false,'+num+')" href="javascript:;">跳转</a></div>';
	parentTag.append(pageDiv);
	var fontSize = $("#"+tagId+" a").eq(1).width();
	$(".paginationPageDiv"+num).css({"user-select": "none","-webkit-user-select":"none","-moz-user-select":"none","-ms-user-select":"none"});
	$(".paginationPageDiv"+num+" a").css({"min-width":fontSize*2+"px","text-align":"center","vertical-align":"middle","color":"#000","margin":"0 3px","text-decoration":"none","border-radius":"3px","border":"1px solid #dfdfdf","display":"inline-block","padding":"5px 10px"});
	$(".paginationNowPage"+num).css("background","#09F");
	$(".paginationNowPage"+num).css("color","#FFF");
	$(".paginationPage"+num).hover(function(){
		$(this).css({"vertical-align":"middle","background":"#09F","color":"#FFF","text-decoration":"none","border-radius":"3px","border":"1px solid #09F","display":"inline-block","padding":"5px 10px"});
	},function(){
		$(this).css({"vertical-align":"middle","background":"#fff","color":"#000","text-decoration":"none","border-radius":"3px","border":"1px solid #dfdfdf","display":"inline-block","padding":"5px 10px"});
	});
	$(".pagination_dddspan"+num).each(function(){
		$(this).css("padding","0 "+(($(this).prev().width()-$(this).width())/2+10+1+3)+"px");
	})
}
/**
 *
 * @param maxPage				总页数
 * @param nowPage				当前页
 * @param tagId					分页所在标签id值
 * @param onclickFunctionName	页码点击事件触发的方法名
 * @param data {size:number,num:number}	可选参数。size：分页大小（默认8，最小5）；num：页码区编号（默认为空），同一页面有多个分页时必须赋此值。
 */
function pagination2(maxPage,nowPage,tagId,onclickFunctionName,data) {
	var parentTag = $("#"+tagId);
	parentTag.empty();
	if(maxPage<2){return;}
	if(nowPage<1){
		nowPage = 1;
	} else if(nowPage>maxPage){
		nowPage = maxPage;
	}
	var pagesize = 8;
	var num = "";
	if(data){
		pagesize = data.size!=undefined?data.size<5?5:data.size:pagesize;
		num = data.num!=undefined?data.num:num;
	}
	$functionName$ = onclickFunctionName;
	var pageDiv="";
	pageDiv += '<div class="paginationPageDiv'+num+'"><a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+((nowPage-1)<1?1:(nowPage-1))+')" href="javascript:;">上一页</a>';
	if(maxPage>pagesize+3){
		if(parseInt((nowPage-1)/pagesize) == 0){
			for(var i=1;i<=pagesize;i++){
				if(i==nowPage){
					pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}else{
					pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}
			}
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+maxPage+')" href="javascript:;">'+maxPage+'</a>';
		}else if(parseInt((nowPage-1)/pagesize) == parseInt(maxPage/pagesize)){
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'(1)" href="javascript:;">1</a>';
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			for (i=maxPage-pagesize-1;i<=maxPage;i++){
				if(i==nowPage){
					pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}else{
					pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}
			}
		}else {
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'(1)" href="javascript:;">1</a>';
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			for (i=parseInt((nowPage-1)/pagesize)*pagesize+1;i<=parseInt((nowPage-1)/pagesize)*pagesize+pagesize;i++){
				if(i==nowPage){
					pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}else{
					pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
				}
			}
			pageDiv += '<span class="pagination_dddspan'+num+'">...</span>';
			pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+maxPage+')" href="javascript:;">'+maxPage+'</a>';
		}
	}else{
		var i = 1;
		while (i <= maxPage) {
			if(i==nowPage){
				pageDiv += '<a class = "paginationNowPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
			}else{
				pageDiv += '<a class = "paginationPage'+num+'" onclick="'+onclickFunctionName+'('+i+')" href="javascript:;">'+i+'</a>';
			}
			i++;
		}
	}
	pageDiv += '<a class="paginationPage'+num+'" onclick="'+onclickFunctionName+'('+((nowPage+1)>maxPage?maxPage:(nowPage+1))+')" href="javascript:;">下一页</a><input id="paginationPageInput'+num+'" value="'+(maxPage/2).toFixed(0)+'" style="vertical-align:middle;border-radius: 3px;border: 1px solid #dfdfdf;padding: 5px 10px;width: 3em;font: inherit;text-align: center;" onkeypress="$tiaozhuan$(event,'+num+')"/> <a class="paginationPage'+num+'" onclick="$tiaozhuan$(false,'+num+')" href="javascript:;">跳转</a></div>';
	parentTag.append(pageDiv);
	$(".paginationPageDiv"+num).css({"user-select": "none","-webkit-user-select":"none","-moz-user-select":"none","-ms-user-select":"none"});
	$(".paginationNowPage"+num).css({"vertical-align":"middle","background":"#09F","color":"#FFF","margin":"0 3px","text-decoration":"none","border-radius":"3px","border":"1px solid #09f","display":"inline-block","padding":"5px 10px"});
	$(".paginationPage"+num).css({"vertical-align":"middle","color":"#000","margin":"0 3px","text-decoration":"none","border-radius":"3px","border":"1px solid #dfdfdf","display":"inline-block","padding":"5px 10px"});
	$(".paginationPage"+num).hover(function(){
		$(this).css({"vertical-align":"middle","background":"#09F","color":"#FFF","text-decoration":"none","border-radius":"3px","border":"1px solid #09F","display":"inline-block","padding":"5px 10px"});
	},function(){
		$(this).css({"vertical-align":"middle","background":"#fff","color":"#000","text-decoration":"none","border-radius":"3px","border":"1px solid #dfdfdf","display":"inline-block","padding":"5px 10px"});
	});
	$(".pagination_dddspan"+num).each(function(){
		$(this).css("padding","0 "+(($(this).prev().width()-$(this).width())/2+10+1+3)+"px");
	})
}

function $tiaozhuan$(e,num) {
	if(e){
		var keynum;
		if (window.event) { // IE
			keynum = e.keyCode;
		} else if (e.which) { // Netscape/Firefox/Opera
			keynum = e.which;
		}
		if (keynum != 13) {return;}
	}
	$(".paginationTZTemp"+num).remove();
	var tzTemp = '<a class="paginationTZTemp'+num+'" onclick="'+$functionName$+'('+$("#paginationPageInput"+num).val()+')" href="javascript:;" style="display:none;"></a>';
	var pageDiv = $(".paginationPageDiv"+num);
	pageDiv.append(tzTemp);
	$(".paginationTZTemp"+num).click();
}
/////////分页 end////////////////////////////////
/////////加载动画 start////////////////////////////////
(function() {
    // 创建 <style> 标签
    var style = document.createElement("style");
    // 对WebKit hack :(
	style.appendChild(document.createTextNode("@keyframes audioWave{25%{background:linear-gradient(#3498db,#3498db)0 50%,linear-gradient(#9b59b6,#9b59b6)0.625em 50%,linear-gradient(#9b59b6,#9b59b6)1.25em 50%,linear-gradient(#9b59b6,#9b59b6)1.875em 50%,linear-gradient(#9b59b6,#9b59b6)2.5em 50%;background-repeat:no-repeat;background-size:0.5em 2em,0.5em 0.25em,0.5em 0.25em,0.5em 0.25em,0.5em 0.25em}37.5%{background:linear-gradient(#9b59b6,#9b59b6)0 50%,linear-gradient(#3498db,#3498db)0.625em 50%,linear-gradient(#9b59b6,#9b59b6)1.25em 50%,linear-gradient(#9b59b6,#9b59b6)1.875em 50%,linear-gradient(#9b59b6,#9b59b6)2.5em 50%;background-repeat:no-repeat;background-size:0.5em 0.25em,0.5em 2em,0.5em 0.25em,0.5em 0.25em,0.5em 0.25em}50%{background:linear-gradient(#9b59b6,#9b59b6)0 50%,linear-gradient(#9b59b6,#9b59b6)0.625em 50%,linear-gradient(#3498db,#3498db)1.25em 50%,linear-gradient(#9b59b6,#9b59b6)1.875em 50%,linear-gradient(#9b59b6,#9b59b6)2.5em 50%;background-repeat:no-repeat;background-size:0.5em 0.25em,0.5em 0.25em,0.5em 2em,0.5em 0.25em,0.5em 0.25em}62.5%{background:linear-gradient(#9b59b6,#9b59b6)0 50%,linear-gradient(#9b59b6,#9b59b6)0.625em 50%,linear-gradient(#9b59b6,#9b59b6)1.25em 50%,linear-gradient(#3498db,#3498db)1.875em 50%,linear-gradient(#9b59b6,#9b59b6)2.5em 50%;background-repeat:no-repeat;background-size:0.5em 0.25em,0.5em 0.25em,0.5em 0.25em,0.5em 2em,0.5em 0.25em}75%{background:linear-gradient(#9b59b6,#9b59b6)0 50%,linear-gradient(#9b59b6,#9b59b6)0.625em 50%,linear-gradient(#9b59b6,#9b59b6)1.25em 50%,linear-gradient(#9b59b6,#9b59b6)1.875em 50%,linear-gradient(#3498db,#3498db)2.5em 50%;background-repeat:no-repeat;background-size:0.5em 0.25em,0.5em 0.25em,0.5em 0.25em,0.5em 0.25em,0.5em 2em}}"));
    style.appendChild(document.createTextNode("@keyframes snake{0%{box-shadow:1.375em 0em#debf23,1.375em 0em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em 0.29721em#b8b64c,2.75em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em 0.18368em#92ae75,4.125em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em-0.18368em#6ca59d,5.5em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em-0.29721em#469cc6,6.875em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52)}20%{box-shadow:1.375em 0.29721em#b8b64c,1.375em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em 0.18368em#92ae75,2.75em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em-0.18368em#6ca59d,4.125em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em-0.29721em#469cc6,5.5em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em 0.0em#debf23,6.875em 0.0em 0.625em-0.3125em rgba(0,0,0,0.52)}40%{box-shadow:1.375em 0.18368em#92ae75,1.375em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em-0.18368em#6ca59d,2.75em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em-0.29721em#469cc6,4.125em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em 0.0em#debf23,5.5em 0.0em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em 0.29721em#b8b64c,6.875em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52)}60%{box-shadow:1.375em-0.18368em#6ca59d,1.375em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em-0.29721em#469cc6,2.75em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em 0.0em#debf23,4.125em 0.0em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em 0.29721em#b8b64c,5.5em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em 0.18368em#92ae75,6.875em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52)}80%{box-shadow:1.375em-0.29721em#469cc6,1.375em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em 0.0em#debf23,2.75em 0.0em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em 0.29721em#b8b64c,4.125em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em 0.18368em#92ae75,5.5em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em-0.18368em#6ca59d,6.875em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52)}100%{box-shadow:1.375em 0.0em#debf23,1.375em 0.0em 0.625em-0.3125em rgba(0,0,0,0.52),2.75em 0.29721em#b8b64c,2.75em-0.29721em 0.625em-0.3125em rgba(0,0,0,0.52),4.125em 0.18368em#92ae75,4.125em-0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),5.5em-0.18368em#6ca59d,5.5em 0.18368em 0.625em-0.3125em rgba(0,0,0,0.52),6.875em-0.29721em#469cc6,6.875em 0.29721em 0.625em-0.3125em rgba(0,0,0,0.52)}}"));
	style.appendChild(document.createTextNode("@keyframes spinDisc{50%{border-top-color:#3498db;border-bottom-color:#3498db;background-color:#2ecc71}100%{transform:rotate(1turn)}}"));
	style.appendChild(document.createTextNode("@keyframes rot{to{transform:rotate(360deg)}}@keyframes gw{0%{box-shadow:0.70711em 0.70711em 0 0.125em#2ecc71,-0.70711em 0.70711em 0 0.39017em#9b59b6,-0.70711em-0.70711em 0 0.5em#3498db,0.70711em-0.70711em 0 0.39017em#f1c40f}25%{box-shadow:0.70711em 0.70711em 0 0.39017em#2ecc71,-0.70711em 0.70711em 0 0.5em#9b59b6,-0.70711em-0.70711em 0 0.39017em#3498db,0.70711em-0.70711em 0 0.125em#f1c40f}50%{box-shadow:0.70711em 0.70711em 0 0.5em#2ecc71,-0.70711em 0.70711em 0 0.39017em#9b59b6,-0.70711em-0.70711em 0 0.125em#3498db,0.70711em-0.70711em 0 0.39017em#f1c40f}75%{box-shadow:0.70711em 0.70711em 0 0.39017em#2ecc71,-0.70711em 0.70711em 0 0.125em#9b59b6,-0.70711em-0.70711em 0 0.39017em#3498db,0.70711em-0.70711em 0 0.5em#f1c40f}100%{box-shadow:0.70711em 0.70711em 0 0.125em#2ecc71,-0.70711em 0.70711em 0 0.39017em#9b59b6,-0.70711em-0.70711em 0 0.5em#3498db,0.70711em-0.70711em 0 0.39017em#f1c40f}}"));
	style.appendChild(document.createTextNode("@keyframes circSquare{50%{width:1.25em;height:1.25em;border-radius:50%;transform:rotate(0.5turn);box-shadow:-2.5em 0 0#2ecc71,2.5em 0 0#e74c3c,-2.5em 0 0#3498db,2.5em 0 0#f1c40f}80%,100%{transform:rotate(1turn)}}"));
	// 将 <style> 元素加到页面中
    document.head.appendChild(style);
})();
var $loadingData$ = {type: 1, show: false};
$(window).resize(function(){
	loading($loadingData$);
});
/**
 * 加载动画
 * @param data 	对形参封装的对象。{type：number, show: boolean}
 *		type  	动画样式，默认使用样式1
 *		show 	是否显示动画，默认显示
 */
function loading(data){
	var type = 1;
	var show = true;
	if(data){
		type = data.type!=undefined?data.type:type;
		show = data.show!=undefined?data.show:show;
		$loadingData$ = {type: type, show: show};
	}
	if($("#loader--audioWave").length==0){
		var imgTag = document.createElement('div');
		imgTag.setAttribute("id", "loader--audioWave");
		$("body").append(imgTag);
	}
	if(show!=undefined&&!show){
		$("#loader--audioWave").hide();
	}
	var w = window.innerWidth;
	var h = window.innerHeight;
	var height = 0;
	var width = 0;
	switch(type)
	{
	    case 1:
	    	height = 32;
	    	width = 48;
	    	$("#loader--audioWave").css({
				"background":" linear-gradient(#9b59b6, #9b59b6) 0 50%, linear-gradient(#9b59b6, #9b59b6) 0.625em 50%, linear-gradient(#9b59b6, #9b59b6) 1.25em 50%, linear-gradient(#9b59b6, #9b59b6) 1.875em 50%, linear-gradient(#9b59b6, #9b59b6) 2.5em 50%",
				"background-repeat":" no-repeat",
				"background-size":" 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em",
				"animation":" audioWave 1.5s linear infinite"
			});
	        break;
	    case 2:
	    	height = 0;
	    	width = 0;
	    	$("#loader--audioWave").css({
	    		"box-shadow": "-0.625em -0.625em 0 0.625em #9b59b6, 0.625em -0.625em 0 0.625em #9b59b6, -0.625em 0.625em 0 0.625em #9b59b6, 0.625em 0.625em 0 0.625em #9b59b6",
	    		"animation": "circSquare 1.5s ease-in-out infinite"
			});
	        break;
	    case 3:
	    	height = 4;
	    	width = 4;
	    	$("#loader--audioWave").css({
	    		"box-shadow": "0.70711em 0.70711em 0 0em #2ecc71, -0.70711em 0.70711em 0 0.17678em #9b59b6, -0.70711em -0.70711em 0 0.25em #3498db, 0.70711em -0.70711em 0 0.17678em #f1c40f",
	        	"animation": "gw 1s ease-in-out infinite, rot 2.8s linear infinite"
	    	});
	        break;
	    case 4:
	    	height = 32;
	    	width = 32;
	    	$("#loader--audioWave").css({
	    		"border": "solid 0.5em #9b59b6",
		        "border-right-color": "transparent",
		        "border-left-color": "transparent",
		        "padding": "0.5em",
		        "border-radius": "50%",
		        "background": "#3498db",
		        "background-clip": "content-box",
		        "animation": "spinDisc 1.5s linear infinite"
	    	});
	        break;
	    case 5:
	    	height = 0;
	    	width = 0;
	    	$("#loader--audioWave").css({
	    		"border-radius": "50%",
		        "transform": "translate(-4.125em)",
		        "box-shadow": "1.375em 0em #debf23, 1.375em 0em 0.625em -0.3125em rgba(0, 0, 0, 0.52), 2.75em 0.29721em #b8b64c, 2.75em -0.29721em 0.625em -0.3125em rgba(0, 0, 0, 0.52), 4.125em 0.18368em #92ae75, 4.125em -0.18368em 0.625em -0.3125em rgba(0, 0, 0, 0.52), 5.5em -0.18368em #6ca59d, 5.5em 0.18368em 0.625em -0.3125em rgba(0, 0, 0, 0.52), 6.875em -0.29721em #469cc6, 6.875em 0.29721em 0.625em -0.3125em rgba(0, 0, 0, 0.52)",
		        "animation": "snake 2s linear infinite"
	    	});
	    	break;
	    default:
	    	$("#loader--audioWave").css({
				"height":" 32px",
				"width":"48px",
				"background":" linear-gradient(#9b59b6, #9b59b6) 0 50%, linear-gradient(#9b59b6, #9b59b6) 0.625em 50%, linear-gradient(#9b59b6, #9b59b6) 1.25em 50%, linear-gradient(#9b59b6, #9b59b6) 1.875em 50%, linear-gradient(#9b59b6, #9b59b6) 2.5em 50%",
				"background-repeat":" no-repeat",
				"background-size":" 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em, 0.5em 0.25em",
				"animation":" audioWave 1.5s linear infinite"
			});
	}
	$("#loader--audioWave").css("width", width + "px");
	$("#loader--audioWave").css("height", height + "px");
	$("#loader--audioWave").css("position", "fixed");
	$("#loader--audioWave").css("top", (h/2-height/2) + "px");
	$("#loader--audioWave").css("left", (w/2-width/2) + "px");
	$("#loader--audioWave").css("z-index", "999999");
}
/////////加载动画 end////////////////////////////////
/////////显示提示信息 start////////////////////////////////
var $dataArray$ = new Array();//提示消息缓存
var $showEnd$ = true;//队列显示时，当前消息是否已显示完成。
/**
 * 短暂显示提示信息（队列显示，所有参数均可为空）
 *
 * @param data		对形参封装的对象{message:String, top: number, left: number, size: number, time:number}
 * 		message		提示内容
 *  	top			固定定位top值（默认值相对窗口顶部三分之一）
 *  	left		固定定位left值（默认值中间）
 *  	size 		提示信息字体大小（默认值20,单位px）
 *  	time 		提示信息稳定显示时长（默认值1500，单位毫秒）
 */
function queueShowMessage(data){
	if(!data){
		return;
	}else{
		if(!data.message){
			return;
		}
	}
	$dataArray$.unshift(data);
	if($dataArray$.length==1){
		myInterval = setInterval(
			function(){
				if($showEnd$){
					if($dataArray$.length==0){
						clearInterval(myInterval);
						return;
					}
					showMessage($dataArray$.pop());
					$showEnd$ = false;
				}
			}
			,1
		);
	}
}
/**
 * 短暂显示提示信息（及时显示，所有参数均可为空）
 *
 * @param data		对形参封装的对象{message:String, top: number, left: number, size: number, time:number}
 * 		message		提示内容
 *  	top			固定定位top值（默认值相对窗口顶部三分之一）
 *  	left		固定定位left值（默认值中间）
 *  	size 		提示信息字体大小（默认值20,单位px）
 *  	time 		提示信息稳定显示时长（默认值1500，单位毫秒）
 */
var $myTimeout$;
function showMessage(data) {
	if(!data){
		return;
	}else{
		if(!data.message){
			return;
		}
	}
	
	var w = window.innerWidth;
	var h = window.innerHeight;
	
	var message = data.message;
	var messageLength = message.length;
	var fontSize = data.size!=undefined?data.size:20;
	var messageTop = data.top!=undefined?data.top:h/3;
	var messageLeft = data.left!=undefined?data.left:w/2-messageLength*fontSize/2;
	var showTime = data.time!=undefined?data.time:1500;
	
	var time2 = 500;//动画时长
	if($("#jiml_showMessage").length==0){
		var messageDiv = document.createElement('div');
		messageDiv.setAttribute("id", "jiml_showMessage");
		$("body:first").append(messageDiv);
	}
	$("#jiml_showMessage").css({"position":"fixed","top":messageTop+"px","left":messageLeft+"px","background-color":"rgba(0, 153, 255, 0.42)","color":"white","border-radius":"5px","font-size":fontSize+"px","padding":fontSize/4+"px","z-index":"9999999", "display":"none"});
	$("#jiml_showMessage").html(message);
	$("#jiml_showMessage").fadeIn(time2);
	clearTimeout($myTimeout$);
	$myTimeout$ = setTimeout(
					function(){
						$("#jiml_showMessage").fadeOut(time2);
						setTimeout("$showEnd$ = true",time2);//队列显示时使用的标志。
					}
					,showTime
				);
}
/////////显示提示信息 end////////////////////////////////