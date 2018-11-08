<script type="text/javascript" language="javascript">
//��ҳ->0
    //����ҳ��,indexҳ������
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
    
    //�򿪵���ҳ�棬indexҳ������
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
    
    //���ò�Ʒɸѡ����
    //price�۸�, weight����, color��ɫ, clarity����
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
    
    //������ҳ
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
    
    //�򿪶Խ�����ҳ
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
    
    //JIAѡ��������ҳ
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
    
    //JIAѡ�����������ﳵ
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
    
    //��¼
    function login(){
    	if (isIOS()) {
    		var message = {'method':'login',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.login();
    	}
    }
    
    //ע��
    function register(){
    	if (isIOS()) {
    		var message = {'method':'register',};
	        window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.register();
		}
	}
    
    //��¼��ע�����
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
    
    //��¼��ע��ʧ��
    function onLoginFailed(errorText){
		if (isIOS()) {
			var message = {'method':'onLoginFailed',
    				   	   'errorText':errorText,};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
    		window.android.onLoginFailed(errorText);
		}
	}
    
    //�ŵ�۸������иı�
    function onPriceSetChanged() {
    	if (isIOS()) {
    		var message = {'method':'onPriceSetChanged',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onPriceSetChanged();
    	}
    }
    
    //�ŵ�۸����óɹ�
    function onPriceSetSucceed() {
    	if (isIOS()) {
    		var message = {'method':'onPriceSetSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onPriceSetSucceed();
    	}
    }
    
    //���ﳵ��ӳɹ�
	function onAddShoppingCartSucceed() {
		if (isIOS()) {
    		var message = {'method':'onAddShoppingCartSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onAddShoppingCartSucceed();
    	}
	}
    
    //���ﳵɾ���ɹ�
	function onDeleteShoppingCartSucceed() {
		if (isIOS()) {
    		var message = {'method':'onDeleteShoppingCartSucceed',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
    	} else {
    		window.android.onDeleteShoppingCartSucceed();
    	}
	}
    
    //����GIA֤��
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
    
	//������
	function clearCache(){
		if (isIOS()) {
			var message = {'method':'clearCache',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
        } else {
			window.android.clearCache();
		}
	}
	
	//������
	function checkVersion(){
		if (isIOS()) {
			var message = {'method':'checkVersion',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.checkVersion();
		}
	}
	
	//������Դ�ļ�
	function updateResource(){
		if (isIOS()) {
			var message = {'method':'updateResource',};
        	window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.updateResource();
		}
	}
	
	//��ʾ��Ϣ
	function showToast(text){
		if (isIOS()) {
			var message = {'method':'showToast',
    				   	   'text':text,};
	        window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.showToast(text);
		}
	}
	
	//ˢ�µ�ǰҳ��
	function refresh(){
		if (isIOS()) {
			var message = {'method':'refresh',};
			window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.refresh();
		}
	}
	
	//��ʾ���ض���
	function showLoadingIndicator(){
		if (isIOS()) {
			var message = {'method':'showLoadingIndicator',};
			window.webkit.messageHandlers.iosapp.postMessage(message);
		} else {
			window.android.showLoadingIndicator();
		}
	}
	
	//���ؼ��ض���
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
	* ����Ϊ���ش������JS�ӿ�
	**/
	//���ﳵɾ��
	function deleteShoppingCartFromNative() {
		//TODO
	}
	
	//�ŵ�۸����ñ���
	function saveShopPriceFromNative() {
		//TODO
	}
	
	//��������ŵ�
	function applyForAddShopFromNative() {
		//TODO
	}
</script>