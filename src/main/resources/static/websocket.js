var ws;

document.addEventListener('DOMContentLoaded', function () {
    var connectButton = document.getElementById('connect-button');
    var sendButton = document.getElementById('send-button');
    var statusRadios = document.getElementsByName('status');

    connectButton.addEventListener('click', connect);
    sendButton.addEventListener('click', send);
    for(let radio in statusRadios) {
	    statusRadios[radio].addEventListener('change', updateStatus);
	}
});

function connect() {
    var username = document.getElementById('username').value;
    var host = document.location.host;
    var pathname = document.location.pathname;

    ws = new WebSocket('ws://' + host + pathname + 'chat/' + username);

    ws.onmessage = function (event) {
    var msg = JSON.parse(event.data);
		console.log("rm "+ JSON.stringify(msg))
		
		if (msg.hasOwnProperty('message')) {
	        var log = document.getElementById('log');
	        console.log('recieved: ' + msg.message);
	        log.innerHTML += msg.message + '\n';
		}
		if (msg.hasOwnProperty('users')) {
			var us = JSON.parse(msg.users)
			console.log('len ' + us.length);
			let html = '';
			for (u in us) {
				html  += '<div style="clear: both"><div style="float: left;width: 100px">' + us[u].name + '</div><div style="float: left; width: 100px">' + (us[u].online?'online':'dnd') +'</div></div>';
				console.log(us[u].name);
			}
			var users = document.getElementById('users');

			users.innerHTML = html;
		}
    };
}

function send() {
    var msg = {
        'message': document.getElementById('msg').value
    };
    ws.send(JSON.stringify(msg));
}


function updateStatus() {
    var msg = {
        'online': document.getElementById('online').checked
    };
    ws.send(JSON.stringify(msg));
}

