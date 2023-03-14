var ws;

document.addEventListener('DOMContentLoaded', function () {
    var connectButton = document.getElementById('connect-button');
    var sendButton = document.getElementById('send-button');

    connectButton.addEventListener('click', connect);
    sendButton.addEventListener('click', send);
});

function connect() {
    var username = document.getElementById("username").value;
    var host = document.location.host;
    var pathname = document.location.pathname;

    ws = new WebSocket("ws://" + host + pathname + "chat/" + username);

    ws.onmessage = function (event) {
        var log = document.getElementById("log");
        console.log(event.data);
        log.innerHTML += event.data + "\n";
    };
}

function send() {
    var content = document.getElementById("msg").value;
    ws.send(content);
}
