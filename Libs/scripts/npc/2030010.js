/* Amon
 * Last Mission : Zakum's Altar (280030000)
 
*/

function start() {
    
cm.sendYesNo("你想離開#r？#k只要不斷線就不用重新開始。你確定要離開嗎#r？#k");
}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(211042300, 2);

    }
    
cm.dispose();

}