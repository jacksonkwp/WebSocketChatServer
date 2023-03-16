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
		
		if (msg.hasOwnProperty('message')) {
	        var log = document.getElementById('log');
	        console.log('recieved: ' + msg.message);
	        log.innerHTML += msg.message + '\n';
		}
    };
}

function send() {
	var msg = {
		'message': document.getElementById('msg').value
	}
	console.log('sent: ' + msg.message);
    ws.send(JSON.stringify(msg));
}

function updateStatus() {
	var msg = {
		'online': document.getElementById('online').checked
	}
	console.log('online: ' + msg.online);
	ws.send(JSON.stringify(msg));
}
