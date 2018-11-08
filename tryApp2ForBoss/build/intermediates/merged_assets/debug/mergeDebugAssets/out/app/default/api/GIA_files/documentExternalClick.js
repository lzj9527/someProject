;(function($){
    var callbacks = $.Callbacks();
    var uuid = 0;
    $.extend({
        documentExternalClick:{
            data:{},
            _callbacks:callbacks,
            add:function(callback){
                if(!callbacks.has(callback)){
                    callbacks.add(callback);
                }
            },
            remove:function(callback){
                callbacks.remove(callback);
            },
            init:function(){
                $(document).on("click.documentExternalClick",function(e){
                    callbacks.fire(e);
                });
            }
        }
    });
    $.documentExternalClick.init();
})(jQuery);