<?php
// include '../../../../../../framework/bootstrap.inc.php';
include 'html.php';
// include 'checklogin.php';
// Create DOM from URL

$index = strripos($_GET['reurl'], '--') - 1;
$_GET['reurl'][$index] = 1;

$html = file_get_html($_GET['reurl']);

$item = array();
$item['total_diamonds'] = $html->getElementById('total_diamonds')->innertext;//$html->find('span[id=total_diamonds]');
// $item['total_diamonds'] = 0;
if (!$item['total_diamonds']) {
    $reurl = urlencode($_GET['reurl']);
    $url = 'http://120.78.225.191/addons/ewei_shop/template/app/default/api/gia.php?callback=' 
            . $_GET['callback'] . '&reurl=' . $reurl;
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_TIMEOUT, 600);
    $html = curl_exec($ch);
    echo $html;
    die;
}

foreach($html->find('tr[data-diamond-id]') as $k=>$tr) {
    $td12_status = 'show';
     foreach($tr->find('td') as $key=>$td) {
        if($key == 0 || $key == 12 || $key == 13){
            if(($key == 12 || $key == 13) && $td12_status =='hide'){
                //$item['list'][$k][$key] = '无';
                $item['list'][$k][$key] = $td->innertext;
            }else{
                $item['list'][$k][$key] = $td->innertext;
            }  
        }elseif($key == 2 && intval($td->plaintext) < 1){
            //$td12_status = 'hide';
            $item['list'][$k][$key] = $td->plaintext;
        }else{
            $item['list'][$k][$key] = $td->plaintext;
        }

        

    }
}
/*foreach ($item['list'] as &$value) {
    $value[18] = floor($value[18] * $_W['p_xs']);
}*/
$item['total'] = $html->getElementById('total')->innertext; //$html->find('span[id=total]');
$item['page_size'] = count($item['list']);//$html->find('select[id=q_perpage] option[selected]')->innertext;

if( $item['total'] ){
    if( $item['total'] < $item['page_size'] ){ $page_count = 1; }               //如果总数据量小于$PageSize，那么只有一页
    if( $item['total'] % $item['page_size'] ){                                  //取总数据量除以每页数的余数
        $page_count = (int)($item['total'] / $item['page_size']) + 1;           //如果有余数，则页数等于总数据量除以每页数的结果取整再加一
    }else{
        $page_count = $item['total'] / $item['page_size'];                      //如果没有余数，则页数等于总数据量除以每页数的结果
    }
}
else{
    $page_count = 0;
}

$item['page'] = 1; //暂未开发

$item['total_pages'] = $page_count;
$jsoncallback = $_REQUEST['callback'];

exit($jsoncallback.'('.json_encode($item).')');
