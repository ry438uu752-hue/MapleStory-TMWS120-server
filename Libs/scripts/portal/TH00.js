function enter(pi) {
    if (!pi.haveItem(4031722)) {
	pi.playerMessage("你身上沒有太陽火花的庇護，無法進入。");
    } else {
	pi.warp(501030105);
    }
}