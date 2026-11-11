/* Amon
 * Last Mission : Zakum's Altar (280030000)
 
*/

function start() {
    
cm.sendYesNo("#b你確定要離開嗎 #r？#k");
}


function action(mode, type, selection) {
    
if (mode == 1) {
	
cm.warp(501030104, 2);

    }
    
cm.dispose();

}