var Pagination = _.inherit(Basic,{
    current:1,
    //total:0,
    //countPerPage:25,
    pages:1, // 总页数
    ajax:false,//ajax模式
    onchange:function(){},
    onRefresh:function(){},
    setContainer:function(){},
    renderHtml:function(){},
    onRender:function(){
        this.$el = this.$container;
    },
    initElement:function(){
        this.$els.$page = this.$el.find(".ipt-jump");
    },
    init:function(){
        this.onElementEvent("click",".next",function(){
            var current = this.current + 1;
            if(current <= this.pages){
                this.current++;
                this.gotoPage(current);
            }
        });

        this.onElementEvent("click",".prev",function(){
            var current = this.current - 1;
            if(current > 0){
                this.current--;
                this.gotoPage(current);
            }
        });

        this.onElementEvent("click",".btn-jump",function(){
            var current = parseInt(this.$els.$page.val(),10);
            this.gotoPage(current);
        });
    },
    refresh:function(pages){
        this.current = 1;
        this.pages = pages;
        this.triggerEvent("onRefresh"); // 触发 onRefresh 事件
    },
    gotoPage:function(pageIndex){
        if(pageIndex > 0 && pageIndex <= this.pages){
            this.current = pageIndex;
            if(!this.ajax){
                window.location.href = _.replaceParam(window.location.href,"page",pageIndex);
            }
            this.triggerEvent("onchange");
        }
    }
});