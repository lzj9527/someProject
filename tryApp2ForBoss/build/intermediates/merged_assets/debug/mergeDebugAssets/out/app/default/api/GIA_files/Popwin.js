var PopwinTpl = '<div class="popwin-layer<% print(_.isPad() ? " ipad" : "")%>"><div class="popwin"><div class="popwin-header"><div class="text"><%=title%></div><div class="popwin-actions"><a class="icon-close"></a></div></div><div class="popwin-body"><%= html %></div></div></div>';

var Popwin = _.inherit(Basic,{
    template: _.getTemplateByHtml(PopwinTpl),
    events:{
        onRender:function(){
            this.onElementEvent("click",".popwin-actions .icon-close",function(){
                this.hide();
            });
        }
    },
    renderContent:function(html,callback){
        this.$el.find(".popwin-body").html(html);
        this.initElement();
        if(typeof callback == "function"){
            callback.call(this);
        }
    },
    setTitle:function(title){
        this.$el.find(".popwin-header .text").text(title);
    }
});