var stompClient = null;
var notificationCount = 0;


$(document).ready(function() {
    console.log("Index page is ready");
    connect();

    $("#send").click(function() {
        sendMessage();
    });

    $("#send-private").click(function() {
        sendPrivateMessage();
    });

    $("#notifications").click(function() {
        resetNotificationCount();
    });
});

function connect() {

    var queryString = window.location.search;
    var urlParams = new URLSearchParams(queryString);
    var accessToken = urlParams.get('accessToken');


    var socket = new SockJS('http://localhost:5555/ws');
    stompClient = Stomp.over(socket);
     stompClient.connect({'Authorization': 'Bearer ' + x }, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();

        stompClient.subscribe('/user/queue/private-messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/user/queue/errors', function (message) {
            showMessage(JSON.parse(message.body).status + JSON.parse(message.body).message );
        });

        stompClient.subscribe('/user/topic/private-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
    });
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/ws/message", {}, JSON.stringify({'messageContent': $("#message").val()}));
}

function sendPrivateMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message", {}, JSON.stringify({'messageContent': $("#private-message").val()}));
}

function updateNotificationDisplay() {
    if (notificationCount == 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}