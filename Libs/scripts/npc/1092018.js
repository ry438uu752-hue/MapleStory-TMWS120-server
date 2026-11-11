/**
	Trash Can - Aboard the Nautilus
**/

var status = -1;

function action(mode, type, selection) {
    if (status == -1) {
	status = 0;
	cm.sendNext("你從垃圾桶裡撿到一張皺巴巴的紙，內容似乎很重要。");
    } else if (status == 0) {
	if (cm.haveItem(4031839)) {
	    cm.sendOk("這個垃圾桶好像沒有別的東西了");
	    cm.safeDispose();
	} else {
	    cm.gainItem(4031839,1);
	    cm.sendOk("我無法看懂裡面的內容……只看到上面寫著盲俠");
	    cm.safeDispose();
	}
    }
}