<script type="text/javascript" language="javascript">
//首页->0
    //打开新页面,index页面索引
    function openWindow(index, title, url){
    	if (isIOS()) {
    		var message = {'method':'openWindow',
    				   	   'index':index.toString(),
    				       'title':title,
    				       'url':url,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.openWindow(index, title, url);
    	}
    }
    
    //打开弹出页面，index页面索引
    function openPopWindow(index, title, url){
    	if (isIOS()) {
    		var message = {'method':'openPopWindow',
    				       'index':index.toString(),
    				       'title':title,
    				       'url':url,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.openPopWindow(index, title, url);
    	}
    } 
    
    //设置产品筛选条件
    //price价格, weight重量, color颜色, clarity净度
	function setFilterCondition(price, weight, color, clarity) {
		if (isIOS()) {
			var message = {'method':'setFilterCondition',
    				       'price':price,
    				       'weight':weight,
    					   'color':color,
    					   'clarity':clarity,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.setFilterCondition(price, weight, color, clarity);
		}
	}
    
    //打开详情页
    function openDetailWindow(goodsId, url){
    	if (isIOS()) {
    		var message = {'method':'openDetailWindow',
    		  		   	   'goodsId':goodsId,
    				       'url':url,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.openDetailWindow(goodsId, url);
    	}
    }
    
    //打开对戒详情页
    function openDetailWindowInCoupleRing(goodsId, url){
    	if (isIOS()) {
   	 		var message = {'method':'openDetailWindowInCoupleRing',
    				       'goodsId':goodsId,
    				       'url':url,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
   	 	} else {
    		window.android.openDetailWindowInCoupleRing(goodsId, url);
    	}
    }
    
    //JIA选钻后打开详情页
    function openDetailWindowFromJIA(goodsId, url, jiaJson){
    	if (isIOS()) {
    		var message = {'method':'openDetailWindowFromJIA',
    				   	   'goodsId':goodsId,
    				       'url':url,
    				       'jiaJson':jiaJson,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.openDetailWindowFromJIA(goodsId, url, jiaJson);
    	}
    }
    
    //JIA选钻后添加至购物车
    function appendShoppingCartFromJIA(goodsId, jiaJson){
    	if (isIOS()) {
   	 		var message = {'method':'appendShoppingCartFromJIA',
    				       'goodsId':goodsId,
    				       'jiaJson':jiaJson,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
   	 	} else {
    		window.android.appendShoppingCartFromJIA(goodsId, jiaJson);
    	}
    }
    
    //登录
    function login(){
    	if (isIOS()) {
    		var message = {'method':'login',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.login();
    	}
    }
    
    //注册
    function register(){
    	if (isIOS()) {
    		var message = {'method':'register',};
	        window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.register();
		}
	}
    
    //登录或注册完成
    function onLoginFinished(userName, userKey){
    	if (isIOS()) {
    		var message = {'method':'onLoginFinished',
    				  	   'userName':userName,
    				   	   'userKey':userKey,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onLoginFinished(userName, userKey);
		}
	}
    
    //登录或注册失败
    function onLoginFailed(errorText){
		if (isIOS()) {
			var message = {'method':'onLoginFailed',
    				   	   'errorText':errorText,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
    		window.android.onLoginFailed(errorText);
		}
	}
    
    //门店价格设置有改变
    function onPriceSetChanged() {
    	if (isIOS()) {
    		var message = {'method':'onPriceSetChanged',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onPriceSetChanged();
    	}
    }
    
    //门店价格设置成功
    function onPriceSetSucceed() {
    	if (isIOS()) {
    		var message = {'method':'onPriceSetSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onPriceSetSucceed();
    	}
    }
    
    //购物车添加成功
	function onAddShoppingCartSucceed() {
		if (isIOS()) {
    		var message = {'method':'onAddShoppingCartSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onAddShoppingCartSucceed();
    	}
	}
    
    //购物车删除成功
	function onDeleteShoppingCartSucceed() {
		if (isIOS()) {
    		var message = {'method':'onDeleteShoppingCartSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onDeleteShoppingCartSucceed();
    	}
	}
    
    //下载GIA证书
	function downloadGIACer(title, url){
		if (isIOS()) {
			var message = {'method':'downloadGIACer',
						   'title':title,
    					   'url':url,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
    		window.android.downloadGIACer(title, url);
		}
	}
    
	//清理缓存
	function clearCache(){
		if (isIOS()) {
			var message = {'method':'clearCache',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
        } else {
			window.android.clearCache();
		}
	}
	
	//检查更新
	function checkVersion(){
		if (isIOS()) {
			var message = {'method':'checkVersion',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.checkVersion();
		}
	}
	
	//更新资源文件
	function updateResource(){
		if (isIOS()) {
			var message = {'method':'updateResource',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.updateResource();
		}
	}
	
	//提示信息
	function showToast(text){
		if (isIOS()) {
			var message = {'method':'showToast',
    				   	   'text':text,};
	        window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.showToast(text);
		}
	}
	
	//刷新当前页面
	function refresh(){
		if (isIOS()) {
			var message = {'method':'refresh',};
			window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.refresh();
		}
	}
	
	//显示加载动画
	function showLoadingIndicator(){
		if (isIOS()) {
			var message = {'method':'showLoadingIndicator',};
			window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.showLoadingIndicator();
		}
	}
	
	//隐藏加载动画
	function hideLoadingIndicator(){
		if (isIOS()) {
			var message = {'method':'hideLoadingIndicator',};
			window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.hideLoadingIndicator();
		}
	}
	
	function isIOS()
	{
    	var pda_user_agent_list = new Array("iPhone", "iPod", "iPad");
    	var user_agent = navigator.userAgent.toString();
    	for (var i=0; i < pda_user_agent_list.length; i++) {
        	if (user_agent.indexOf(pda_user_agent_list[i]) >= 0) {
            	return true;
        	}
    	}
    	return false;
	}

	/**
	* 以下为本地代码调用JS接口
	**/
	//购物车删除
	function deleteShoppingCartFromNative() {
		//TODO
	}
	
	//门店价格设置保存
	function saveShopPriceFromNative() {
		//TODO
	}
	
	//申请添加门店
	function applyForAddShopFromNative() {
		//TODO
	}
</script>