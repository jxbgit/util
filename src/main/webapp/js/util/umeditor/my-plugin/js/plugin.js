/**
*注册一个名为“textIndent”的插件，用于首行缩进2个字符
*/
var hadExecuteTextIndent = false;
UM.plugins['textindent'] = function () {

    UM.commands[ 'textindent' ] = {
		
        execCommand: function (cmdName) {
            //在这里实现具体的命令的行为
            //当调用 editor.execCommand("textIndenttt") 时，该方法就会被调用
			if(!hadExecuteTextIndent){
				$("#editor>p").css('textIndent','2em');
				hadExecuteTextIndent = true;
			}else{
				$("#editor>p").css('textIndent','0');
				hadExecuteTextIndent = false;
			}
        },
		
        queryCommandState: function (cmdName) {
            //这里返回只能是 1, 0, -1
            //1代表当前命令已经执行过了
            //0代表当前命令未执行
            //-1代表当前命令不可用
			
            //在这里总是返回0， 这样做可以使保存按钮一直可点击
            return 0;
        },
		
        //声明该插件不支持“撤销／保存”功能， 这样就不会触发ctrl+z 和ctrl+y的记忆功能
        notNeedUndo: 1
		
    };
};
