<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>demo</title>
	<script type="text/javascript" charset="utf-8" src="/js/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="/js/util/jiml-util/jiml-utils-1.2.0.js"></script>
</head>
<body>
<!-- 百度编辑器demo start -->
	<div><a href="/js/util/umeditor/index.html" target="_black">百度编辑器demo</a></div>
<!-- 百度编辑器demo end -->

<!-- 页码加载demo start -->
	<div id="pageList1"><!-- 页码1演示 --></div>
	<div id="pageList2"><!-- 页码2演示 --></div>
		<script type="text/javascript">
			$(function(){
				paging1(10);
				paging2(10);
			})
			function paging1(nowPage) {
				pagination(33, nowPage, "pageList1", "paging1", {num:1});
			}
			function paging2(nowPage) {
				pagination2(33, nowPage, "pageList2", "paging2");
			}
		</script>
<!-- 页码加载demo start -->
	<div>
		<button type="button" onclick="queueShowMessage({message:'123'})">队列提示消息</button>
		<button type="button" onclick="showMessage({message:'123'})">及时提示消息</button>
	</div>
<!-- 验证码 -->
    <img src="/yzm.do" onclick="this.setAttribute('src','/yzm.do?x='+Math.random())" alt="验证码" title="点击更换" />
<!-- 获取json/jsonp格式数据-->
    <a href="javascript:;" onclick="getJson('json')">获取json格式数据</a>
    <a href="javascript:;" onclick="getJson('jsonp')">获取jsonp格式数据</a>
    <p id="jsonResult"></p>
    <script type="text/javascript">
    	function getJson(dataType) {
    		$.ajax({
    			url : "/"+ dataType +".do",
    			type : "post",
    			data : {"data":1},
    			dataType : dataType,
    			success : function(result) {
    				$("#jsonResult").empty().html(JSON.stringify(result));
    			},
    			error : function() {
    				$("#jsonResult").empty().html("获取失败。");
    			}
    		});
		}
    </script>
</body>
</html>