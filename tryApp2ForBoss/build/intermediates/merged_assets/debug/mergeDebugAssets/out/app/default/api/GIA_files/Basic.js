function Basic(options){
    this.reset();
    this.create();
    this.setOption(options);
	this._initContainer();
    this.render();
    this.initElement();
    this.init();
}

Basic.prototype = {
    constructor:Basic,
    $container:$("body"),
    container:document.body,
    $els:null,
    $el:null,
    els:null,
    events:null,
    reset:function(){
        this.$els = {};
        this.els = {};
    },
    template: null,
    setOption:function(options){
        var self = this;
        _.each(options,function(value,key,list){
            self[key] = value;
        });
    },
	_initContainer:function(){
        //this.$container = this.$els.container = $container;
        //this.els.container = $container[0];
        this.container = this.$container[0];
    },
    onCreate:function(){},
    create:function(){
        //_.proxy(this.onCreate,this);
        this.triggerEvent("onCreate");
    },
    init:function(){
    },
    //createRoot:function(){
    //},
    onPreRender:function(){},
    onRender:function(){},
    renderHtml:function(){
        var html;
        if(this.template){
            html = this.template(this.data);
            this.$el = $(html);
            if(this.$el.length > 1){
                this.$el = $("<div />").append(this.$el);
            }
        }
        this._initEl();
        this.initElement();
    },
	_initEl:function(){
        this.el = this.$el[0];
    },
    emptyContainer:function(){
        this.$container.html("");
    },
    render:function(){
        //_.proxy(this.onPreRender,this);
        this.triggerEvent("onPreRender");
        this.renderHtml();
        //_.proxy(this.onRender,this);
        this.triggerEvent("onRender");
    },
    //_render:function(){
        //this.createRoot();
        //this.renderHtml();
    //},
    initElement:function(){
    },
    //setData:function(data){
    //    this.data = data;
    //},
    theme:function(theme){
        this.$el.addClass(theme);
    },
    //getEvent:function(event){
    //    return this.events && this.events[event];
    //},
    triggerEvent:function(event,args){
        this.events && _.proxy(this.events[event],this, args);
        _.proxy(this[event],this,args);
    },
    on:function(event,fn){
        this.events[event] = fn;
    },
    off:function(event){
        this.events[event] = null;
    },
    onPreShow:function(){},
    onShow:function(){},
    isInContainer:function(){
        return $.contains(this.container,this.el);
    },
    show:function(needRender){
        //_.proxy(this.onPreShow,this);
        this.triggerEvent("onPreShow");
        if(needRender){
            //this._render();
            this.renderHtml();
        }
        if(!this.isInContainer()){
            this.$container.append(this.$el);
        }
        this.$el.css("display","");
        //_.proxy(this.onShow,this);
        this.triggerEvent("onShow");
    },
    onHide:function(){},
    onPreHide:function(){},
    hide:function(){
        //_.proxy(this.onPreHide,this);
        this.triggerEvent("onPreHide");
        this.$el.css("display","none");
        //this.$el.remove();
        //_.proxy(this.onHide,this);
        this.triggerEvent("onHide");
    },
    onElementEvent:function(event,selector,fn){
        if(_.isFunction(fn)){
            this.$el.on(event,selector, $.proxy(fn,this));
        }
    },
    offElementEvent:function(event,selector,fn){
        this.$el.off(event,selector,fn);
    },
    onBeforeRefresh:function(){},
    onRefresh:function(){},
    refresh:function(){
        //_.proxy(this.onBeforeRefresh,this);
        this.triggerEvent("onBeforeRefresh");
        this.$el.remove();
        //this._render();
        this.renderHtml();
        //_.proxy(this.onRefresh,this);
        this.triggerEvent("onRefresh");
    },
    destroy:function(){
        this.$el.remove();
    },
    own:function(key,value){
        if(!(key in this) || this.hasOwnProperty(key)){
            this[key] = value;
        }
    },
    getProperty:function(key){
        return this[key];
    }
};

_.mixin({
    //对象继承
    inherit: function(parent, protoProps, staticProps) {
        //var parent = this;
        var child;

        // The constructor function for the new subclass is either defined by you
        // (the "constructor" property in your `extend` definition), or defaulted
        // by us to simply call the parent's constructor.
        if (protoProps && _.has(protoProps, 'constructor')) {
            child = protoProps.constructor;
        } else {
            child = function(){ return parent.apply(this, arguments); };
        }

        // Add static properties to the constructor function, if supplied.
        _.extend(child, parent, staticProps);

        // Set the prototype chain to inherit from `parent`, without calling
        // `parent`'s constructor function.
        var Surrogate = function(){ this.constructor = child; };
        Surrogate.prototype = parent.prototype;
        child.prototype = new Surrogate;

        // Add prototype properties (instance properties) to the subclass,
        // if supplied.
        if (protoProps) _.extend(child.prototype, protoProps);

        // Set a convenience property in case the parent's prototype is needed
        // later.
        child.__super__ = parent.prototype;

        return child;
    },
    proxy:function(func, context,args) {
        var fn;
        if(typeof func == "function"){
            fn = _.bind(func, context,args);
            return fn();
        }
    },
    getQueryString:function(param,url){
        var reg = new RegExp("(^|&)" + param + "=([^&]*)(&|$)", "i");
        if(url){
            //不含?格式的地址
            if(!url.match(/\?/)){
                url = "?";
            } else {
                url = url.replace(/^.*\?/,"?");
            }
        } else {
            url = window.location.search;
        }
        //url = url || window.location.search;
        var r = url.substr(1).match(reg);
        if (r != null) return r[2];
        return null;
    },
    getTemplateById:function(templateID){
        return _.template($("#"+templateID).html());
    },
    getTemplateByHtml:function(html){
        return _.template(html);
    },
    renderTemplate:function(templateID,data){
        var tplFn = _.getTemplateById(templateID);
        return tplFn(data);
    },
    removeParam:function(param,url){
        if(!url){
            url = window.location.href;
        }
        var value = _.getQueryString(param,url);
        //var reg = new RegExp("(^|&)" + param + "=([^&]*)(&|$)", "i");
        //  ?param=xxx&p=yyy
        url = url.replace(new RegExp("\\?" + param + "=([^&]*)&", "i"),"?");
        //  ?param=xxx
        url = url.replace(new RegExp("\\?" + param + "=([^&]*)$", "i"),"");
        // ?p=xx&param=yy&p2=zz
        url = url.replace(new RegExp("&" + param + "=([^&]*)&", "i"),"&");
        // ?p=xx&param=yy
        url = url.replace(new RegExp("&" + param + "=([^&]*)$", "i"),"");
        return url;
    },
    replaceParam:function(url,key,value){
        var oldValue = _.getQueryString(key,url);
        var urlArr,_url;
        if(oldValue){
            var reg = new RegExp("\\?"+key+"=" + oldValue);
            if(reg.test(url)){
                urlArr = url.split("?" + key + "=" + oldValue);
                _url = urlArr[0] + "?" + key + "=" + value;
            } else {
                urlArr = url.split("&" + key + "=" + oldValue);
                _url = urlArr[0] + "&" + key + "=" + value;
            }
            if(urlArr[1] !== undefined){
                _url += urlArr[1];
            }
        } else {
        	if (url.match(/\?/)) {
        		_url = url + "&" + key + "=" + value;
        	} else {
        		_url = url + "?" + key + "=" + value;
        	}
        }
        return _url;
    },
    //UA:navigator.userAgent,
    isPad:function(){
        return navigator.userAgent.match(/iPad/);
    }
});