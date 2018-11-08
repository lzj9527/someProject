function isDefine(para) {
	//console.log(para);
    if ( typeof para == 'undefined' || para == "" || para == null || para == undefined || para == 'undefined')
        return false;
    else
        return true;
}

function getUrlParam(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r!=null) return unescape(r[2]); return null; 
}


function json2url(json){
    var arr = [];
    for(var i  in json){
        if(json[i] !== null && json[i] !== ''){
            arr.push(i + '=' + encodeURIComponent(json[i]));
        }
    }
    return arr.join('&');
}
function getKey() {        
    var storage = window.localStorage;
    return  key = storage["use_key"] || getUrlParam('key');
}

// 跳转到指定的 url
function hrefTo(url) {
    window.location = url;
}

// 修正软键盘弹出遮挡输入框问题
