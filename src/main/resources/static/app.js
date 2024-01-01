import { getClient } from "./settings.js";

const client = getClient();
const sessionDisconnectLifetimeSeconds = 5; //should be like 3600 for production

let sessionID;
let sessionName;

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function attemptReconnect() {
    let id = getCookie("id");
    let name = getCookie("name");
    console.log("Attempted reconnect, got id \"" + id + "\", name \"" + name + "\"");
    if(id === "" || name === "") {
        console.log("Reconnect failed. Prompting new user session.")
        return;
    }
    client.publish({
        destination: "/app/reconnect",
        body: JSON.stringify({'name': name, 'id': id})
    });
    console.log("Recovered disconnected session with id \"" + id + "\", name \"" + name + "\"");
    document.getElementById("name-input").style.display = 'none';
    document.getElementById("reconnect-alert").style.display = 'block';
}

client.onConnect = (frame) => { //bp DNR
    console.log('Connected: ' + frame);
    subscribeMessageHandlers();
    attemptReconnect();
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

function putCookie(k, v, lifetimeSeconds) {
    var now = new Date();
    var time = now.getTime();
    var expireTime = time + 1000*lifetimeSeconds;
    now.setTime(expireTime);
    document.cookie = k + "=" + v + ";expires="+now.toUTCString()+";SameSite=Lax;path=/";
}

function recieveSession(sessionMessage) {
    console.log("Connected with ID: " + JSON.parse(sessionMessage.body).id);
    let msg = JSON.parse(sessionMessage.body);
    let id = msg.id;
    let name = msg.name;
    let reconnected = msg.reconnected;
    sessionID = id;
    sessionName = name;
    putCookie("name", name, sessionDisconnectLifetimeSeconds)
    putCookie("id", id, sessionDisconnectLifetimeSeconds)
    document.getElementById("id-title-banner").innerHTML = "\"" + name + "\" (#" + id + ")";
    console.log("Reconnected: " + reconnected);
}

function subscribeMessageHandlers() { //will keep as BP but edit within
    client.subscribe('/user/client/givesession', recieveSession);
}

function connectSession(name) {
    console.log("RAN #connectSession with " + name)
    client.publish({
        destination: "/app/connect",
        body: JSON.stringify({'name': name})
    });
}

$(function () { //runs on page load, basically
    console.log("Docu func");
    connect();
});

$("#name-input")[0].addEventListener("submit", e => {
    let name = $("#name")[0].value;
    e.preventDefault();
    if(name == "") {
        return;
    }
    connectSession(name);
});