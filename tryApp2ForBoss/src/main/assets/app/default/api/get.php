<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>闪盟定制专家·后台管理系统</title>
    <link href="http://www.zsagia.com/static/v2/public/static/css/main.css" type="text/css" rel="stylesheet">
</head>
<body>
<div class="page-box page-of-diamonds">
    <div class="diamond-search-form container" id="diamond-search-form">
        <form>
            <div class="basic">
                <div class="item shape">
                    <div class="label">形状</div>
                    <div class="option active" data-value="1">圆形</div>
                    <div class="option" data-value="2">公主方</div>
                    <div class="option" data-value="8">心形</div>
                    <div class="option" data-value="10">垫形</div>
                    <div class="option" data-value="5">椭圆</div>
                    <div class="option" data-value="6">梨形</div>
                    <div class="option" data-value="4">雷迪恩</div>
                    <div class="option" data-value="3">祖母绿</div>
                    <div class="option" data-value="9">三角形</div>
                    <div class="option" data-value="7">马眼形</div>
                    <div class="option" data-value="100">其他</div>
                </div>
                <div class="item color">
                    <div class="label">颜色</div>
                    <div class="option" data-value="2">D</div>
                    <div class="option" data-value="4">E</div>
                    <div class="option" data-value="8">F</div>
                    <div class="option" data-value="16">G</div>
                    <div class="option" data-value="32">H</div>
                    <div class="option" data-value="64">I</div>
                    <div class="option" data-value="128">J</div>
                    <div class="option" data-value="256">K</div>
                    <div class="option" data-value="512">L</div>
                    <div class="option" data-value="1024">M</div>
                </div>
                <div class="item clarity">
                    <div class="label">净度</div>
                    <div class="option" data-value="3">FL/IF</div>
                    <div class="option" data-value="4">VVS1</div>
                    <div class="option" data-value="8">VVS2</div>
                    <div class="option" data-value="16">VS1</div>
                    <div class="option" data-value="32">VS2</div>
                    <div class="option" data-value="64">SI1</div>
                    <div class="option" data-value="128">SI2</div>
                    <div class="option" data-value="512">I1</div>
                </div>
                <div class="item cut ex3">
                    <div class="label">切工</div>
                    <div class="option" data-value="2">EX完美</div>
                    <div class="option" data-value="4">VG很好</div>
                    <div class="option" data-value="8">GD好</div>
                </div>
                <div class="item polish ex3">
                    <div class="label">抛光</div>
                    <div class="option" data-value="2">EX完美</div>
                    <div class="option" data-value="4">VG很好</div>
                    <div class="option" data-value="8">GD好</div>
                </div>
                <div class="item symmetry ex3">
                    <div class="label">对称</div>
                    <div class="option" data-value="2">EX完美</div>
                    <div class="option" data-value="4">VG很好</div>
                    <div class="option" data-value="8">GD好</div>
                </div>
                <div class="item report">
                    <div class="label">证书</div>
                    <div class="option" data-value="1">GIA</div>
                    <div class="option" data-value="4">IGI</div>
                    <div class="option" data-value="8">HRD</div>
                </div>
                <div class="item fluorescence ex3">
                    <div class="label">荧光</div>
                    <div class="option" data-value="1">N无</div>
                    <div class="option" data-value="6">VSL/F轻</div>
                    <div class="option" data-value="8">M中</div>
                    <div class="option" data-value="16">S强</div>
                    <div class="option special">3EX无荧光</div>
                </div>
            </div>
            <div class="extra">
                <div class="row">
                    <div class="item carat" id="carat">
                        <div class="label">重 量：</div>
                        <div class="option"><input type="text" class="input" data-role="min" id="q_carat1"> -- <input type="text" class="input" data-role="max" id="q_carat2"></div>
                        <ul class="option-assist">
                            <li data-min="0.30" data-max="0.39">0.30 - 0.39</li>
                            <li data-min="0.40" data-max="0.49">0.40 - 0.49</li>
                            <li data-min="0.50" data-max="0.69">0.50 - 0.69</li>
                            <li data-min="0.70" data-max="0.99">0.70 - 0.99</li>
                            <li data-min="1.00" data-max="1.49">1.00 - 1.49</li>
                            <li data-min="1.50" data-max="1.99">1.50 - 1.99</li>
                        </ul>
                    </div>
                    <div class="item">
                        <div class="label">咖 奶：</div>
                        <div class="option"><label class="checkbox"><input type="checkbox" name="q_no_milky_shade" id="q_no_milky_shade"><i class="icon"></i><span class="text">不咖不奶</span></label><label class="checkbox"><input type="checkbox" name="q_shade_milky_shade" id="q_shade_milky_shade"><i class="icon"></i><span class="text">浅咖不奶</span></label></div>
                    </div>
                                    </div>
                <div class="row">
                    <div class="item">
                        <div class="label">
                            <select class="select full-width" id="q_id_type">
                                <option value="id" selected="selected">货号</option>
                                <option value="report_no">证书号</option>
                            </select>
                        </div>
                        <div class="option">
                            <input type="text" class="input" id="q_id">
                        </div>
                    </div>
                    <div class="item">
                        <div class="label">视 图：</div>
                        <div class="option"><label class="checkbox"><input type="checkbox" name="q_is_image" id="q_is_image"><i class="icon"></i><span class="text">有图</span></label><label class="checkbox"><input type="checkbox" name="q_is_movie" id="q_is_movie"><i class="icon"></i><span class="text">有视频</span></label></div>
                    </div>
                </div>
                <div class="row">
                    <div class="item area">
                        <div class="label">地 点：</div>
                        <div class="option"><label class="checkbox"><input type="checkbox" name="q_is_outside" id="q_is_outside"><i class="icon"></i><span class="text">国外</span></label><label class="checkbox"><input type="checkbox" name="q_is_inside" id="q_is_inside"><i class="icon"></i><span class="text">港深</span></label></div>
                    </div>
                </div>
                <div class="row">
                    <div class="item">
                        <div class="label">每页：</div>
                        <div class="option">
                            <select class="select" id="q_perpage">
                                <option value="10">10条</option>
                                <option value="25" selected="selected">25条</option>
                                <option value="50">50条</option>
                                <option value="100">100条</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="actions"><button class="button button-primary button-middle" id="btn_search">搜索</button><button class="button button button-middle" id="btn_reset">重置</button></div>
                </div>
            </div>
        </form>
    </div>
    
<?php
	$url = "http://www.zsagia.com/diamond_new/1--4--4--0--4--2--1--8.html?q_carat1=&q_carat2=&q_id=&q_id_type=id&q_perpage=25"; //初始url
    $handle = fopen($url, "r");
    if ($handle) {
        $content = stream_get_contents($handle);
    } else {
        $content = '';
    } 
	$preg = '/<table[^>]+>(.*)<\/table>/isU';
     $preg = '/<tbody id="diamond-list"(.*)<\/table>/isU';
	if (preg_match_all($preg, $content, $matchs))
	{
		$result    = $matchs[1];
	}
   // print_r($result);
   
   //得出ID号，成为id数组
	$preg_id = '/<tr data-diamond-id="(.*)">/isU';
	if (preg_match_all($preg_id, $content, $matchsid))
	{
		$result_id    = $matchsid[1];
	}
     $preg_content = '/<td>(.*)<\/tr>/isU';
     //得出钻石参数内容组
	if (preg_match_all($preg_content, $content, $matchs1))
	{
		$result_content    = $matchs1[0];
	}
   // echo sizeof($result_content);
    
    
    //把每一页数据取出来
    $preg_data = '/<td>(.*)<\/td>/isU'; 
    for($i = 0;$i < count($result_content);$i++){
  // echo "$result_content[$i]<br/>";
    if (preg_match_all($preg_data,$result_content[$i], $matchsdata))
		{
		$result_data[$i]    = $matchsdata[1];
		}
     		$data[$i][0] ="<tr><td>".$result_id[$i]."</td>" ; //id
            $data[$i][1] ="<td>".$result_data[$i][0]."</td>"; //形状
             $data[$i][2] ="<td>".$result_data[$i][1]."</td>";//颜色
              $data[$i][3] ="<td>".$result_data[$i][2]."</td>";//
               $data[$i][4] ="<td>".$result_data[$i][3]."</td>";
                $data[$i][5] ="<td>".$result_data[$i][4]."</td>";
                 $data[$i][6] ="<td>".$result_data[$i][5]."</td>";
                  $data[$i][7] ="<td>".$result_data[$i][6]."</td>";
                   $data[$i][8] ="<td>".$result_data[$i][7]."</td>";
                    $data[$i][9] ="<td>".$result_data[$i][8]."</td>";
                     $data[$i][10] ="<td>".$result_data[$i][9]."</td>";
                      $data[$i][11] ="<td>".$result_data[$i][10]."</td>";
                       $data[$i][12] ="<td>".$result_data[$i][11]."</td>";
                        $data[$i][13] ="<td>".$result_data[$i][12]."</td>";
                         $data[$i][14] ="<td>".$result_data[$i][13]."</td>";
                          $data[$i][15] ="<td>".$result_data[$i][14]."</td>";
                           $data[$i][16] ="<td>".$result_data[$i][15]."</td>";
                          	 $data[$i][17] ="<td>".$result_data[$i][17]."</td>";
                             $data[$i][18] ="<td data-id='$result_id[$i]'><a class='btn_buy' href='javascript:void(0)'>选购</a></td></tr>";
               
  }
  ?>
<table width="100%" cellpadding="0" cellspacing="0" class="table zebra" id="diamond-data-table">
            <thead>
            <tr>
                <th>货号</th>
                <th>形状</th>
                <th><a href="javascript:" class="sortable" id="order-by-carat-link">重量</a></th>
                <th>颜色</th>
                <th>净度</th>
                <th>切工</th>
                <th>抛光</th>
                <th>对称</th>
                <th>荧光</th>
                <th>直径</th>
                <th>咖色</th>
                <th>奶色</th>
                <th>证书</th>
                <th>证书号</th>
                <th>图片</th>
                <th>视频</th>
                <th>国际美金</th>
                            <th><a href="javascript:" class="sortable" id="order-by-price-link">售价</a></th>
                <th>选购</th>
                        </tr>
            </thead>
            <tbody id="diamond-list">
  <?php
  for($i = 0;$i < count($result_content);$i++){
  	for($n = 0;$n < 19;$n++){
    	echo $data[$i][$n];
  		}
  }
    //print_r($data);
    //print_r($result_content);
	//print_r($result_id);exit;
?>
</tbody>
        </table>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/jquery-1.11.3.min.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/underscore-min.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/documentExternalClick.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/Basic.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/Checkbox.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/Popwin.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/libs/Pagination.js"></script>
<script src="http://www.zsagia.com/static/v2/public/static/js/main.js"></script>
<script>
    var $doc = $(document);
    var user_type = 3;
    var $form = $("#diamond-search-form");
    
    $form.on("click",".basic .option",function(){
        var $option = $(this);
        
        if($option.is(".special")){
        	if ($(this).is(".active")) {
        		$form.find(".ex3 .option:not(.special)").removeClass("active");
                $form.find(".cut .option:eq(0)").removeClass("active");
                $form.find(".polish .option:eq(0)").removeClass("active");
                $form.find(".symmetry .option:eq(0)").removeClass("active");
                $form.find(".fluorescence .option:eq(0)").removeClass("active");
                $(this).removeClass("active");
        	} else {
        		$form.find(".ex3 .option:not(.special)").removeClass("active");
                $form.find(".cut .option:eq(0)").addClass("active");
                $form.find(".polish .option:eq(0)").addClass("active");
                $form.find(".symmetry .option:eq(0)").addClass("active");
                $form.find(".fluorescence .option:eq(0)").addClass("active");
                $(this).addClass("active");
        	}
        } else {
            $option.toggleClass("active");
            if ($form.find(".cut .option:eq(0)").is(".active") && $form.find(".polish .option:eq(0)").is(".active") && $form.find(".symmetry .option:eq(0)").is(".active") && $form.find(".fluorescence .option:eq(0)").is(".active")) {
            	$(".special").addClass("active");
            } else {
            	$(".special").removeClass("active");
            }
        }
    });
    
    //重量快捷选择层交互
    var $carat = $("#carat");
    var $caratOptionAssist = $carat.find(".option-assist");
    var $min = $carat.find(":text[data-role='min']");
    var $max = $carat.find(":text[data-role='max']");
    var showCaratOptionAssist = function(){
        $caratOptionAssist.show();
    };
    
    var hideCaratOptionAssist = function(){
        $caratOptionAssist.hide();
    };
    
    $carat.on("focus input",":text",function(){
        showCaratOptionAssist();
        var min = $min.val() - 0;
        var max = $max.val() - 0;
        $caratOptionAssist.children().removeClass("active").filter(function(){
            var $this = $(this);
            return $this.data("min") - 0 == min && $this.data("max") - 0 == max;
        }).addClass("active");
    });
    
    $carat.on("click",".option-assist > li",function(){
        var $this = $(this);
        $this.addClass("active").siblings().removeClass("active");
        $min.val($this.data("min"));
        $max.val($this.data("max"));
        hideCaratOptionAssist();
    });
    
    $.documentExternalClick.add(function(e){
        if(!$(e.target).closest($carat)[0]){
            hideCaratOptionAssist();
        }
    });

    //美化checkbox
    var syncCallback = function($checkbox){
        var $label = this.getLabel();
        if(this.isChecked()){
            $label.addClass("active");
        } else {
            $label.removeClass("active");
        }
    };

    var checkboxSettings = {
        onchange:syncCallback,
        oninit:syncCallback
    };

    Checkbox.init("q_no_milky_shade",checkboxSettings);//不咖不奶
    Checkbox.init("q_shade_milky_shade",checkboxSettings);//浅咖不奶
    Checkbox.init("q_is_image",checkboxSettings);//有图
    Checkbox.init("q_is_movie",checkboxSettings);//有视频
    Checkbox.init("q_is_outside",checkboxSettings);//地区
    Checkbox.init("q_is_inside",checkboxSettings);//地区

    //ajax刷新数据列表
    var handleSearchData = function(data){
        $diamondList.html(diamondListTpl({
            data:data
        }));
        
        $("#total_diamonds").text(data.total_diamonds);
        $("#total").text(data.total);
    };

    var searchRequest = function(params,isSearch){
        var url = 'http://' + location.hostname + '/diamond_new/' + params;
        alert(url);
        $.getJSON(url, function(data){
            handleSearchData(data);
            if(isSearch){
                topPagination.refresh(data.total_pages);
                bottomPagination.refresh(data.total_pages);
                clearSortStyle();
            } else {
                topPagination.updatePaging();
                bottomPagination.updatePaging();
            }
            if (user_type == 3) {
            	$.getJSON('http://' + location.hostname + '/consumer/cart_new/',function(data){
                    parent.renderCart(data);
                });
            }
            
        });
    };

    var pageChange = function(){
        queryString = _.replaceParam(queryString,"page",this.current);
        searchRequest(queryString,false);
    };

    var createPagination = function(id){
        return new Pagination({
            $container:$("#" + id),
            ajax:true,
            current:1,
            pages:defPages,
            onRefresh:function(){
                this.updatePaging();
            },
            onchange:function(){
                pageChange.call(this);
                if(topPagination == this){
                    bottomPagination.current = this.current;
                    bottomPagination.updatePaging();
                } else {
                    topPagination.current = this.current;
                    topPagination.updatePaging();
                }
            },
            updatePaging:function(){
                this.$el.find(".paging").text(this.current + " / " + this.pages);
            },
            onShow:function(){
                this.updatePaging();
            }
        })
    };

    //分页组件
    var queryString = "";//数据请求参数
    var defPages = 5080;
    var topPagination = createPagination("pagination-top");
    var bottomPagination = createPagination("pagination-bottom");

    topPagination.show();
    bottomPagination.show();

    //搜索
    var diamondListTpl = _.getTemplateById("tpl-diamond-list");
    var $diamondList = $("#diamond-list");
    $("#btn_search").click(function(e){
    	e.preventDefault();
    	
    	//全部参数数组
    	var params = [];
    	
    	//形状参数
    	var $shape = $(".shape .option.active");
    	
    	var param_shape = [];
    	
    	$.each($shape, function(){
    		param_shape.push($(this).data('value'));
    	});

    	param_shape = param_shape.join('-');
    	param_shape = param_shape ? param_shape : 0;
    	
    	params.push(param_shape);
    	
    	//获取参数和值函数
        var getOptionValue = function($obj){
            var param_value = [];
            
            $.each($obj, function(){
                param_value.push($(this).data('value'));
            });
            
            param_value = eval(param_value.join('+'));
            param_value = param_value ? param_value : 0;
            return param_value;
        };
        
    	//颜色参数
    	var $color = $(".color .option.active");
    	var param_color = getOptionValue($color);
    	params.push(param_color);
    	    	
    	//净度参数
        var $clarity = $(".clarity .option.active");
        var param_clarity = getOptionValue($clarity);
        params.push(param_clarity);
        
        //切工参数
        var $cut = $(".cut .option.active");
        var param_cut = getOptionValue($cut);
        params.push(param_cut);
        
        //抛光参数
        var $polish = $(".polish .option.active");
        var param_polish = getOptionValue($polish);
        params.push(param_polish);
        
        //对称参数
        var $symmetry = $(".symmetry .option.active");
        var param_symmetry = getOptionValue($symmetry);
        params.push(param_symmetry);
        
        //证书参数
        var $report = $(".report .option.active");
        var param_report = getOptionValue($report);
        params.push(param_report);
        
        //荧光参数
        var $fluorescence = $(".fluorescence .option.active:not('.special')");
        var param_fluorescence = getOptionValue($fluorescence);
        params.push(param_fluorescence);
        
        //alert(params);
        
        //重量参数
        var q_carat1 = $("#q_carat1").val();
        var q_carat2 = $("#q_carat2").val();
        
        //货号或证书号
        var q_id_type = $("#q_id_type").val();
        var q_id = $("#q_id").val();
        
        //每页条数
        var q_perpage = $("#q_perpage").val();
        
        //咖奶
        var q_no_milky_shade = $("#q_no_milky_shade").prop("checked");
        var q_shade_milky_shade = $("#q_shade_milky_shade").prop("checked");
        
        //图和视频
        var q_is_image = $("#q_is_image").prop("checked");
        var q_is_movie = $("#q_is_movie").prop("checked");
        
        //国外、深港
        var q_is_outside = $("#q_is_outside").prop("checked");
        var q_is_inside = $("#q_is_inside").prop("checked");
        
        //组装url
        var url = params.join("--") + '.html';
        url = url + '?q_carat1=' + q_carat1 + '&q_carat2=' + q_carat2 + '&q_id=' + q_id + '&q_id_type=' + q_id_type + '&q_perpage=' + q_perpage;
        
        if (q_no_milky_shade) {
        	url = url + '&q_no_milky_shade=1';
        }
        
        if (q_shade_milky_shade) {
        	url = url + '&q_shade_milky_shade=1';
        }
        
        if (q_is_image) {
        	url = url + '&q_is_image=1';
        }
        
        if (q_is_movie) {
        	url = url + '&q_is_movie=1';
        }
        
        if (q_is_outside) {
        	url = url + '&q_is_outside=1';
        }
        
        if (q_is_inside) {
            url = url + '&q_is_inside=1';
        }

        queryString = url;
        searchRequest(queryString,true);
    });
    
    //重置
    $("#btn_reset").click(function(e){
    	e.preventDefault();
    	location.reload();
    });

    //选购
    $doc.on("click", ".btn_buy", function(){
    	var dia_id = $(this).closest("td").data("id");
    	url = 'http://' + location.hostname + '/consumer/cart_new/add/' + dia_id;

        $.getJSON(url,function(data){
            parent.renderCart(data);
        });
    });

    if (user_type == 3) {
        parent.queryCart();
    }
    
    //证书弹出窗
    var $dataTable = $("#diamond-data-table");

    var reporterWin = {};
    $dataTable.on("click", ".report-link", function(e){
    	e.preventDefault();
    	var $link = $(this);
    	var diamond_id = $link.closest("tr").data("diamond-id");
    	var url = $(this).attr("href");
    	url = 'http://' + location.hostname + url;
    	var popwin = reporterWin[diamond_id];
    	
    	if(!popwin){
    		popwin = reporterWin[diamond_id] = new Popwin({
                data:{
                    title:"钻石证书",
                    html: _.renderTemplate("tpl-reporter",{
                        url: url
                    })
                },
                onShow:function(){
                }
            });
    		popwin.theme("reporter-win");
    	}
    	
    	popwin.show();
    });

    //图片弹出窗
    var imageWin = {};
    $dataTable.on("click",".icon-img",function(e){
        e.preventDefault();
        var $tr = $(this).closest("tr");
        var diamondId = $tr.data("diamond-id");
        var src = $(this).attr("href");

        if(!imageWin[diamondId]){
            imageWin[diamondId] = new Popwin({
                data:{
                    title:"查看图片",
                    html: _.renderTemplate("tpl-image", {
                        src:src
                    })
                }
            });
            //imageWin[diamondId].theme("diamond-image-win");
            imageWin[diamondId].theme("image-win");
        }
        imageWin[diamondId].show();
    });
    
    //视频弹出窗
    var movieWin = {};
    $dataTable.on("click", ".icon-video", function(e){
        e.preventDefault();
        var $link = $(this);
        var diamond_id = $link.closest("tr").data("diamond-id");
        var url = $(this).attr("href");
        var popwin = movieWin[diamond_id];
        
        if(!popwin){
            popwin = movieWin[diamond_id] = new Popwin({
                data:{
                    title:"钻石视频",
                    html: _.renderTemplate("tpl-movie",{
                        url: url
                    })
                },
                onShow:function(){
                    //this.$el.find(".popwin-body").height(600);
                }
            });
            popwin.theme("video-win");
        }
        
        popwin.show();
    });

    //修改排序方式
    var clearSortStyle = function(){
        $dataTable.find(".sortable").removeClass("asc desc");
    };

    var orderBy = function(by){
        var _by = _.getQueryString("q_orderby",queryString);
        var orderdir = _.getQueryString("q_orderdir",queryString);
        var $link = $("#order-by-" + by + "-link");
        clearSortStyle();
        //当前是按其他方式排序
        if(_by != by){
            queryString = _.replaceParam(queryString,"q_orderby",by);
            queryString = _.replaceParam(queryString,"q_orderdir","asc");
            $link.addClass("asc");
        } else {
            if(orderdir == "asc"){
                queryString = _.replaceParam(queryString,"q_orderdir","desc");
                $link.addClass("desc");
            } else {
                queryString = _.replaceParam(queryString,"q_orderdir","asc");
                $link.addClass("asc");
            }
        }
        searchRequest(queryString,false);
    };
    //排序
    var sortParam = ["carat","price","discount"];
    
    _.each(sortParam,function(n,i){
    	$doc.on("click","#order-by-" + n + "-link",function(){
            orderBy(n);
        });
    });
</script>
</body>