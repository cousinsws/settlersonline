const client = new StompJs.Client({ //bp DNR
    brokerURL: 'ws://192.168.1.71:8080/settlers-app'
});

client.onConnect = (frame) => { //bp DNR
    console.log('Connected: ' + frame);
    subscribeMessageHandlers();
    sendSampleConnectPacket();
};

client.onWebSocketError = (error) => { //bp DNR
    console.error('Error with websocket', error);
};

client.onStompError = (frame) => { //bp DNR
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function connect() { //bp DNR
    console.log("Attempting connection...");
    client.activate();
}

//can call this ourselves, but idrk what happens on page-close
function disconnect() { //bp DNR
    client.deactivate();
    console.log("Disconnected");
}

function subscribeMessageHandlers() { //will keep as BP but edit within
    client.subscribe('/client/sample', (sampleMessage) => {
        console.log(JSON.parse(sampleMessage.body).sample);
    });
}

function sendSampleConnectPacket() {
    client.publish({
        destination: "/app/connect",
        body: JSON.stringify({'connectMessage': (new Date()).toString()})
    });
}

$(function () { //runs on page load, basically
    $("form").on('submit', (e) => e.preventDefault()); //no idea
    console.log("Docu func");
    connect();
});