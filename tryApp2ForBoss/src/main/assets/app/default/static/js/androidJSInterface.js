//打开新页面,index页面索引
function openWindow(index, title, url, pathUrl) {
    if (isIOS()) {
        var message = {
            'method': 'openWindow',
            'index': index.toString(),
            'title': title,
            // 'url': url,
            'pathUrl': pathUrl
            // 'isEmployee':isEmployee
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openWindow(index, title, url);
    }
}

// 传递索引值给 安卓(IOS)
function selectOrderIndex(index) {
    if (isIOS()) {
        var message = {
            'method': 'selectOrderIndex',
            'index': index.toString()
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.selectorderindex(index);
    }
}

// 打开微信
function openHrefToWechat() {
    if (isIOS()) {
        var message = {
            'method': 'openHrefToWechat'
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openHrefToWechat();
    }
}

// //打开弹出页面，index页面索引
// function openPopWindow(index, title, url) {
//     if (isIOS()) {
//         var message = {
//             'method': 'openPopWindow',
//             'index': index.toString(),
//             'title': title,
//             'url': url,
//         };
//         window.webkit.messageHandlers.iosapp.postMessage(message);
//     } else {
//         window.android.openPopWindow(index, title, url);
//     }
// }

// 打开详情页
function openDetailWindow(goodsId, url) {
    if (isIOS()) {
        var message = {
            'method': 'openDetailWindow',
            'goodsId': goodsId,
            'url': url,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openDetailWindow(goodsId, url);
    }
}

// 打开对戒详情页
function openDetailWindowInCoupleRing(goodsId, url) {
    if (isIOS()) {
        var message = {
            'method': 'openDetailWindowInCoupleRing',
            'goodsId': goodsId,
            'url': url,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openDetailWindowInCoupleRing(goodsId, url);
    }
}

// 打开 特价裸钻 详情页
function openDetailWindowInDiamond(goodsId, url) {
    if (isIOS()) {
        var message = {
            'method': 'openDetailWindowInDiamond',
            'goodsId': goodsId,
            'url': url
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openDetailWindowInDiamond(goodsId, url);
    }
}

//GIA选钻后打开详情页
function openDetailWindowFromJIA(goodsId, url, jiaJson) {
    if (isIOS()) {
        var message = {
            'method': 'openDetailWindowFromJIA',
            'goodsId': goodsId,
            'url': url,
            'jiaJson': jiaJson,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.openDetailWindowFromJIA(goodsId, url, jiaJson);
    }
}

//GIA选钻后添加至购物车
function appendShoppingCartFromJIA(goodsId, jiaJson) {
    if (isIOS()) {
        var message = {
            'method': 'appendShoppingCartFromJIA',
            'goodsId': goodsId,
            'jiaJson': jiaJson,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.appendShoppingCartFromJIA(goodsId, jiaJson);
    }
}

//登录
function login() {
    if (isIOS()) {
        var message = {
            'method': 'login',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.login();
    }
}

//注册
function register() {
    if (isIOS()) {
        var message = {
            'method': 'register',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.register();
    }
}

//登录或注册完成
function onLoginFinished(userName, userKey, isEmployee) {
    if (isIOS()) {
        var message = {
            'method': 'onLoginFinished',
            'userName': userName,
            'userKey': userKey,
            'isEmployee': isEmployee
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
        console.log(message);
    } else {
        window.android.onLoginFinished(userName, userKey, isEmployee);
    }
}

//登录或注册失败
function onLoginFailed(errorText) {
    if (isIOS()) {
        var message = {
            'method': 'onLoginFailed',
            'errorText': errorText
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onLoginFailed(errorText);
    }
}

//清理缓存
function clearCache() {
    if (isIOS()) {
        var message = {
            'method': 'clearCache',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.clearCache();
    }
}

//检查更新
function checkVersion() {
    if (isIOS()) {
        var message = {
            'method': 'checkVersion',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.checkVersion();
    }
}

//提示信息
function showToast(text) {
    if (isIOS()) {
        var message = {
            'method': 'showToast',
            'text': text
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.showToast(text);
    }
}

function isIOS() {
    var pda_user_agent_list = new Array("iPhone", "iPod", "iPad");
    var user_agent = navigator.userAgent.toString();
    for (var i = 0; i < pda_user_agent_list.length; i++) {
        if (user_agent.indexOf(pda_user_agent_list[i]) >= 0) {
            return true;
        }
    }
    return false;
}

/**
 * 以下为本地代码调用JS接口
 **/
function deleteOrderFromNative() {
    refresh();
}


//设置产品筛选条件
//price价格, weight重量, color颜色, clarity净度
function setFilterCondition(price, weight, color, clarity) {
    if (isIOS()) {
        var message = {
            'method': 'setFilterCondition',
            'price': price,
            'weight': weight,
            'color': color,
            'clarity': clarity,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.setFilterCondition(price, weight, color, clarity);
    }
}

//购物车删除
function deleteShoppingCartFromNative() {
    deleteShoppingCart();
}

// 门店价格设置 测试 这里的接口 跟 Gia 的接口请求需要进行合并 【2018.6.14 决定暂时不作修改】
function updatePrice(id, v1, v2) {
    var storage = window.localStorage;
    var key = storage["use_key"];
    // key = '17d375be1a87810c5d6f0176c167e84c'; // 测试
    console.log('http://www.zsmtvip.com/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=priceset&key=' + key + '&id=' + id + '&xs=' + v1 + '&xs_gia=' + "" + '&ws=' + v2 + '&act=edit');

    $.ajax({
        url: 'http://www.zsmtvip.com/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=priceset&key=' + key + '&id=' + id + '&xs=' + v1 + '&xs_gia=' + "" + '&ws=' + v2 + '&act=edit',
        type: "get",
        dataType: 'jsonp',
        success: function (data) {
            if (data.resultCode == "0") {
                //console.log('here');
                console.log('ok');
            } else {
                //showToast(data.error);
                console.log('error');
            }
        },
        error: function () {
            console.log('error');
        }
    })
}

//门店价格设置保存 gia系数另有接口请求 这方法主要是给 原生 保存零售价格以及尾数设置，把 v1 以及 v2 的请求也带到 gia系数保存的方法中 【2018.6.14 决定暂时不作修改】
function saveShopPriceFromNative() { // 供ios端使用的一个方法 这个方法将需要废弃掉。  测试
    var resultFlag = true;
    $(".inputvalue").each(function () {
        var id = $(this).data('shopid'); // 门店的 id 只要在 dom 结构中定义了自定义的属性 例如 data-shopid ，那么使用 JQ 的 .data 方法就可以获取到对应的值
        var reg = /^(-?\d*)\.?\d{1,3}$/;
        var v1 = $(this).find('.inputvalue1').val();
        var v2 = $(this).find('.inputvalue2').val();
        // var v3 = $(this).find('.inputvalue3').val();

        console.log(id);// 测试

        if ((!reg.test(v1))) {
            //showToast("只能输入数字(最多三位小数)!");
            resultFlag = false;
            return false;
        } else {
            if (v2 != '' && (!reg.test(v2))) {
                //showToast("尾数输入数字(或者留空)!");
                resultFlag = false;
                return false;
            } else {
                updatePrice(id, v1, v2); // 确保输入合法之后就请求后台进行参数的更改
            }
        }
    });
    if (resultFlag) {
        showToast('修改完成');
        onPriceSetSucceed();
    } else {
        showToast('录入格式不正确，请检查！');
        hideLoadingIndicator();
        onBackPressed();
    }
}

// gia系数请求 首次设置分段系数的时候
function updateGiaPrice_creat(id, v3, v4, v5, v6) {
    var storage = window.localStorage;
    var key = storage["use_key"];
    // key = '17d375be1a87810c5d6f0176c167e84c'; // 测试
    var gia_xsArray = '[{"rid":"1","xs": ' + v3 + ' },{"rid":"2","xs": ' + v4 + ' },{"rid":"3","xs": ' + v5 + ' },{"rid":"4","xs": ' + v6 + ' }]';

    $.ajax({
        url: appUrl + "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=member_xs",
        type: "POST",
        data: {
            key: key,
            act: "xs",
            openid: id,
            gia_xs: gia_xsArray
        },
        // dataType: "jsonp",// 测试 的时候使用
        success: function (data) {
            console.log(data);
            if (data.resultCode == "0") {
                //console.log('here');
                console.log('ok');
            } else {
                //showToast(data.error);
                console.log('error');
            }
        },
        error: function () {
            console.log('error');
        }
    })
}

// gia系数请求修改方法 已进行首次系数创建
function updateGiaPrice_modify(id, xsId1, xsId2, xsId3, xsId4, v3, v4, v5, v6) {
    var storage = window.localStorage;
    var key = storage["use_key"];
    // key = '17d375be1a87810c5d6f0176c167e84c'; // 测试
    var gia_xsArray = // id的值是活的，根据门店 openid 发生改变
        '[{"id":"' + xsId1 + '","xs": ' + v3 + ' }, {"id":"' + xsId2 + '","xs": ' + v4 + ' }, {"id":"' + xsId3 + '","xs": ' + v5 + ' }, {"id":"' + xsId4 + '","xs": ' + v6 + ' }]';
    $.ajax({
        url: appUrl + "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=member_xs",
        type: "POST",
        data: {
            key: key,
            act: "xs",
            openid: id,
            // xs:v1,
            // ws:v2,
            gia_xs: gia_xsArray
        },
        // dataType: "jsonp", // 测试 时使用
        success: function (data) {
            console.log(data);
            if (data.resultCode == "0") {
                //console.log('here');
                console.log('ok');
            } else {
                //showToast(data.error);
                console.log('error');
            }
        },
        error: function () {
            console.log('error');
        }
    })
}

//门店gia系数价格设置保存 测试修改 修改传参的 id
function saveShopGiaPriceFromNative() { // 供ios端使用的一个方法 测试
    var resultFlag = true;
    var reg = /^(-?\d*)\.?\d{1,3}$/;
    var xsArrayLength;
    var openidArray = [], allGiaXsArray = [];

    $(".giaXS_ClassificationBox ").each(function () { // 根据门店数创建的 系数盒数
        xsArrayLength = $(this).data("xsarraylength");
        var id = $(this).data('shopid'),
            giaXsArray = [],
            // v1 = $(this).find('.inputvalue1').val(),
            // v2 = $(this).find('.inputvalue2').val(),

            v3 = $(this).find('.inputvalue3').val(),
            v4 = $(this).find('.inputvalue4').val(),
            v5 = $(this).find('.inputvalue5').val(),
            v6 = $(this).find('.inputvalue6').val(),

            xsId1 = $(this).find(".giaRangeItem1").data("xsid1"),
            xsId2 = $(this).find(".giaRangeItem2").data("xsid2"),
            xsId3 = $(this).find(".giaRangeItem3").data("xsid3"),
            xsId4 = $(this).find(".giaRangeItem4").data("xsid4");

        if ( (!reg.test(v3)) || (!reg.test(v4)) || (!reg.test(v5)) || (!reg.test(v6))) {
            // showToast("只能输入数字(最多三位小数)!");
            console.log("只能输入数字(最多三位小数)!");
            resultFlag = false;
            return false;
        } else {
            openidArray.push(id);
            giaXsArray.push(
                // {"openid": id}, [{"xsid":xsId1,"rid":"1","xs": v3}, {"xsid":xsId2,"rid":"2","xs": v4}, {"xsid":xsId3,"rid":"3","xs": v5}, {"xsid":xsId4,"rid":"4","xs": v6}]
                {"openid": id}, [{"xsid":xsId1,"rid":"1","xs": v3}, {"xsid":xsId2,"rid":"2","xs": v4}, {"xsid":xsId3,"rid":"3","xs": v5}, {"xsid":xsId4,"rid":"4","xs": v6}]
            );
            allGiaXsArray.push(giaXsArray);
        }

        return id, xsArrayLength;
    });

    console.log(xsArrayLength);
    console.log(openidArray);
    console.log(allGiaXsArray);

    if (openidArray.length > 0 && xsArrayLength == 0) {
        console.log("初次创建");
        $.each(allGiaXsArray, function (i, item) {
            console.log(item);
            // console.log(item[0].openid);
            // console.log(item[1][0].xs);
            updateGiaPrice_creat(item[0].openid,item[1][0].xs, item[1][1].xs, item[1][2].xs, item[1][3].xs);
        });

    } else {
        // console.log(allGiaXsArray.length);
        console.log("修改系数");
        $.each(allGiaXsArray, function (i, item) {
            console.log(item);
            // console.log(item[0].openid);
            // console.log(item[1][0].xs);
            // console.log(item[1][0].xsid);
            updateGiaPrice_modify(item[0].openid,item[1][0].xsid,item[1][1].xsid,item[1][2].xsid,item[1][3].xsid,item[1][0].xs, item[1][1].xs, item[1][2].xs, item[1][3].xs);
        });
    }

    if (resultFlag) {
        showToast('修改完成'); // 测试
        // console.log("修改完成")
        onPriceSetSucceed(); // 测试
    } else {
        showToast('录入格式不正确，请检查！'); // 测试
        // console.log("录入格式不正确,请检查!")
        hideLoadingIndicator(); // 测试
        onBackPressed(); // 测试
    }
}

// 返回上一级
function onBackPressed() {
    if (isIOS()) {
        var message = {
            'method': 'onBackPressed'
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onBackPressed();
    }
}


//刷新当前页面
function refresh() {
    if (isIOS()) {
        var message = {
            'method': 'refresh',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.refresh();
    }
}

//加载动画
function showLoadingIndicator() {
    if (isIOS()) {
        var message = {
            'method': 'showLoadingIndicator',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.showLoadingIndicator();
    }
}

//隐藏加载动画
function hideLoadingIndicator() {
    if (isIOS()) {
        var message = {
            'method': 'hideLoadingIndicator',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.hideLoadingIndicator();
    }
}

//门店价格设置有改变
function onPriceSetChanged() {
    if (isIOS()) {
        var message = {
            'method': 'onPriceSetChanged'
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onPriceSetChanged();
    }
}


//门店价格设置成功
function onPriceSetSucceed() {
    if (isIOS()) {
        var message = {
            'method': 'onPriceSetSucceed'
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onPriceSetSucceed();
    }
}


//下载pdf
function downloadJIACer(url) {
    if (isIOS()) {
        var message = {
            'method': 'downloadJIACer',
            'url': url,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.downloadJIACer(url);
    }
}

//下载GIA证书
function downloadGIACer(title, url) {
    if (isIOS()) {
        var message = {
            'method': 'downloadGIACer',
            'title': title,
            'url': url,
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.downloadGIACer(title, url);
    }
}


//购物车添加成功
function onAddShoppingCartSucceed() {
    if (isIOS()) {
        var message = {
            'method': 'onAddShoppingCartSucceed',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onAddShoppingCartSucceed();
    }
}


//购物车删除成功
function onDeleteShoppingCartSucceed() {
    if (isIOS()) {
        var message = {
            'method': 'onDeleteShoppingCartSucceed',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onDeleteShoppingCartSucceed();
    }
}


// 购物车页面跳转
function onDeleteShoppingCartSucceed() {
    if (isIOS()) {
        var message = {
            'method': 'onDeleteShoppingCartSucceed',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        window.android.onDeleteShoppingCartSucceed();
    }
}

// 返回到购物车商品列表页面
function backShoppingCartShopListView() {
    if (isIOS()) {
        var message = {
            'method': 'backShoppingCartShopListView',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        // 安卓没有这个功能 
        //window.android.backShoppingCartShopListView();
    }
}

// 跳转到收货地址页面
function jumpShoppingCartSelectAddress() {
    if (isIOS()) {
        var message = {
            'method': 'jumpShoppingCartSelectAddress',
        };
        window.webkit.messageHandlers.iosapp.postMessage(message);
    } else {
        // 安卓没有这个功能
        //   window.android.jumpShoppingCartSelectAddress();
    }
}

