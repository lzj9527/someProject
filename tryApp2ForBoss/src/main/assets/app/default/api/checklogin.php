<?php
	global $_W, $_GPC;
	$token = $_GPC['key'];
	/*if(empty($token)){
		output_error('请登录',array('resultCode'=>-1));
	}*/
	$sql = 'select m.*, au.p_xs from ' . tablename('ewei_shop_member') . ' m left join ' . tablename('uni_account_users') .  ' au on m.uniacid = au.uniacid where token="'.$token.'" limit 1';
	$member_info = pdo_fetch($sql);
	$_W['uniacid'] = $member_info['uniacid'] ? $member_info['uniacid'] : 2;
	$_W['p_xs'] = 1.01;
	/*if(empty($this->member_info)){
		output_error('登录失败,请重新登录',array('resultCode'=>-1));
	}*/
?>