import { getClient } from "./settings.js";

const client = getClient();
// const sessionDisconnectLifetimeSeconds = 5; //should be like 3600 for production

let sessionID;
let gameCode;
let myProfile;
let amLeader;
let players;

let createdServer = false;
let startedGame = false;

//math consts
const SQRT_3 = Math.sqrt(3);
const HALF_SQRT_3 = SQRT_3 / 2;
const ONE_THIRD = 1/3;
const TWO_THIRD = 2/3;
const ROOT_THREE_OVER_THREE = SQRT_3/3;

//visual parameters
const SIZE_BUFFER = 1.03; //spent maybe three hours hassling with this multiplier only to decide it should probably just be 1 - keeping it for now?
const VALUE_CHIP_SIZE = 2/9;

const dicePermutations = {
    2: 1,
    3: 2,
    4: 3,
    5: 4,
    6: 5,
    7: 6,
    8: 5,
    9: 4,
    10: 3,
    11: 2,
    12: 1
}

class Player {
    constructor(id, name, color, isBot, isLeader, isMe) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isBot = isBot;
        this.isLeader = isLeader;
        this.isMe = isMe;
    }
}

class Vertex {
    constructor(div, jquery, x, y) {
        this.div = div;
        this.jquery = jquery;
        this.x = x;
        this.y = y;
    }
}

let vertexMap = new Map();

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

// function attemptReconnect() {
    // let id = getCookie("id");
    // let name = getCookie("name");
    // console.log("Attempted reconnect, got id \"" + id + "\", name \"" + name + "\"");
    // if(id === "" || name === "") {
    //     console.log("Reconnect failed. Prompting new user session.")
    //     return;
    // }
    // client.publish({
    //     destination: "/app/reconnect",
    //     body: JSON.stringify({'name': name, 'id': id})
    // });
    // console.log("Recovered disconnected session with id \"" + id + "\", name \"" + name + "\"");
    // document.getElementById("name-input").style.display = 'none';
    // document.getElementById("reconnect-alert").style.display = 'block';
// }

client.onConnect = (frame) => { //bp DNR
    console.log('Connected: ' + frame);
    subscribeMessageHandlers();
};

client.onDisconnect = (frame) => {
    console.log("Disconnected: " + frame);
}

client.onWebSocketError = (error) => { //bp DNR
    console.error('Error with websocket', error);
};

client.onStompError = (frame) => { //bp DNR
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function connectToGameServer(name, gameCode, leader) {
    client.reconnectDelay = 5000;
    client.onConnect = (frame) => {
        console.log("Connected " + frame);
        subscribeMessageHandlers();
        client.publish({
            destination: '/topic/connect',
            body: JSON.stringify({
                profile: {
                    ID: getCookie("sessionID"),
                    type: "HUMAN",
                    name: name,
                    color: "GREEN",
                    isLeader: leader
                },
                gameCode: gameCode
            })
        });
    }
    client.activate();
}

$(document).ready(function() {
    console.log('Docu');
    $('#play-button').click(function() {
        console.log("CLICKED PLAY");
        connectToGameServer($("#name").val(), $("#joinCode").val().toUpperCase(), false);
    });
    $('#create-game').click(createAndConnectServer);
    $('#bot-add').click(function() {addBot("ADD")});
    $('#bot-remove').click(function() {addBot("REMOVE")});
    $('#game-start-button').click(startGame);
});

function addBot(botChangeType) {
    $.post("/queue/game/" + gameCode + "/addBot", {postType: botChangeType});
}

async function createAndConnectServer() {
    if(createdServer) {
        return; //no spam for u
    }
    createdServer = true;
    console.log("CLICKED CREATE");
    const codeResponse = await fetch("/queue/create-server");
    console.log(codeResponse);
    let code = (await codeResponse.json())[0];
    console.log(code);
    connectToGameServer($("#name").val(), code, true);
}

function putCookie(k, v, lifetimeSeconds) {
    var now = new Date();
    var time = now.getTime();
    var expireTime = time + 1000*lifetimeSeconds;
    now.setTime(expireTime);
    document.cookie = k + "=" + v + ";expires="+now.toUTCString()+";SameSite=Lax;path=/";
}

// function receiveSession(sessionMessage) {
//     console.log("Connected with ID: " + JSON.parse(sessionMessage.body).id);
//     let msg = JSON.parse(sessionMessage.body);
//     let id = msg.id;
//     let name = msg.name;
//     let reconnected = msg.reconnected;
//     sessionID = id;
//     sessionName = name;
//     putCookie("name", name, sessionDisconnectLifetimeSeconds)
//     putCookie("id", id, sessionDisconnectLifetimeSeconds)
//     document.getElementById("id-title-banner").innerHTML = "\"" + name + "\" (#" + id + ")";
//     console.log("Reconnected: " + reconnected);
// }

function subscribeMessageHandlers() { //will keep as BP but edit within
    client.subscribe('/user/queue/joingameserver', recieveServerJoin);
}

function recieveServerJoin(payload) {
    console.log(JSON.parse(payload.body));
    myProfile = JSON.parse(payload.body).profile;
    sessionID = myProfile.ID;
    amLeader = myProfile.isLeader;
    gameCode = JSON.parse(payload.body).gameCode;
    console.log("id = " + sessionID);
    if(JSON.parse(payload.body).result === "BAD_CODE") {
        console.log("BAD_CODE > deactivate")
        $("#invalid-code").show();
        client.deactivate();
        return;
    }
    $('#join-input').fadeOut();
    $('#game-lobby').fadeIn();
    //else TODO handle reconnect (diff)
    playerConnectToServer();
    client.subscribe('/queue/game/' + gameCode + '/playerconnect', playerConnectToServer);
    client.subscribe('/queue/game/' + gameCode + '/gamestart', joinGame);
}

function playerConnectToServer() {
    updatePlayerList().then();
}

async function updatePlayerList() {
    $('#game-lobby-code').text('Join with code: ' + gameCode);
    const playersResponse = await fetch('/queue/game/' + gameCode + '/getPlayers');
    console.log(playersResponse);
    let playerList = await playersResponse.json();
    console.log(players);
    $('#connected-players').empty();
    players = [];
    playerList.sort().forEach(pl => {
        console.log(pl);
        let color = pl.color;
        let name = pl.name;
        let isBot = pl.type === "ROBOT";
        let isLeader = pl.isLeader;
        let playerID = pl.ID;
        let isMe = playerID === sessionID;
        if(isMe) {
            myProfile = pl;
            amLeader = isLeader;
        }
        players.push(new Player(playerID, name, color, isBot, isLeader, isMe));
        $('#connected-players').append("<div class='connected-player'>" + (isLeader ? "&#9733;" : "") + name + (isMe ? " (You)" : "") + (isBot ? " (BOT)" : "") + "</div>");
    })
    let numConnected = players.length;
    if(amLeader) {
        $("#bot-panel").show();
        if(numConnected >= 3) {
            $("#game-start-button").fadeIn();
            $("#waiting-for-players").hide();
        } else {
            $("#game-start-button").hide();
            $("#waiting-for-players").show();
        }
    } else {
        $("#bot-panel").hide();
    }
    $('#num-connected').html('Connected (' + numConnected + '/' + (numConnected > 4 ? 6 : 4) + ')');
}

function startGame() {
    if(startedGame) {
        return; //no spam for u
    }
    startedGame = true;
    $.post("/queue/game/" + gameCode + "/start");
}

function sendToGamePage() {
    $('#lobby-page').hide();
    $('#game-page').show();
}

async function joinGame(message) {
    sendToGamePage();
    $('body').css("background","#643010");
    const body = JSON.parse(message.body);
    const scenario = body.scenario;
    console.log("Recieved gamestart from " + body.gameCode + ":" + message.body);
    const size = scenario === "THREE_FOUR" ? 12 : 9;
    drawTiles(body.tileMap, size);
    drawVertices(body.landVertices, size);
    drawPorts(body.ports, size);
}

function drawTiles(tilemap, size) {
    let tiles = $('#tiles');
    tiles.empty();
    tilemap.forEach(pair => {
        const coordinate = pair.coordinate;
        const tile = pair.tile;
        const [x, y] = toScreenCoordinates(coordinate, size); //tbh i got no idea why you dont use translatecoords here but issok!
        tiles.append(getTile(tile, x, y, size));
    });
}

function getTile(tile, x, y, size) {
    return  "<div class='tile' style='width: " + size + "vh;height: " + 2*size + "vh; transform: translate(" + (x) + "vh, " + (y) + "vh) rotate(120deg) ;'>" +
                "<div class='tile-in1'>" +
                    "<div class='tile-in2' style='background-image: url(" + toTileImage(tile.resource) + ")'>" +
                        (tile.resource != null ? getValueChip(tile, size) : "") +
                    "</div>" +
                "</div>" +
            "</div>";
}

function getValueChip(tile, size) {
    return  "<div class='value-chip' style='padding: " + (size * VALUE_CHIP_SIZE) + "vh; box-sizing: border-box'>" +
                "<div class='value-icon " + ((tile.rollValue === 6 || tile.rollValue === 8) ? "valuable" : "") + "'>" + //red if 6/8
                    "<div class='value-icon-number'>" + tile.rollValue + "</div>" +
                    "<div class='value-icon-dots'>" + getValueDots(tile.rollValue) + "</div>" +
                "</div>" +
             "</div>";
}

function getValueDots(rollValue) {
    const perm = dicePermutations[rollValue];
    const pad = (dicePermutations[6] - perm) / 2;
    return "&nbsp;".repeat(pad) + "&centerdot;".repeat(perm) + "&nbsp;".repeat(pad); //&nbsp; is "non-breaking-space" so the padding doesnt get deleted by compiler (or smth idk)
}

function drawVertices(landVertices, size) {
    let vertices = $("#vertices");
    vertices.empty();
    landVertices.forEach(vertex => {
        const [sx, sy] = toScreenCoordinates(getVertexCoordinate(vertex.tileCoordinate, vertex.direction), size);
        const [x, y] = toTranslateCoordinates(sx, sy, size);
        let vDiv = $("<div class='vertex' id='lastV' style='transform: translate(" + x + "," + y + ")'></div>");
        vDiv.click(function() {onVertexClick(vertex);});
        vDiv.appendTo($('#vertices'));
        const v = $("#lastV");
        vertexMap.set(JSON.stringify(vertex), new Vertex(v[0], v, sx, sy));
        v.removeAttr('id');
    });
}

function drawPorts(ports, size) {
    ports.forEach(coordinateport => {
        const anchor = coordinateport.anchor;
        anchor.r++;
        const port = coordinateport.port;
        const resource = port.resource;
        const portVertices = port.vertices;
        const [sx, sy] = toScreenCoordinates(anchor, size);
        const [x, y] = toTranslateCoordinates(sx, sy, size);
        let portDiv = $("<div class='port' style='background-image: url(" + toTileImage(resource) + "); transform: translate(" + x + "," + y + ")'></div>");
        portDiv.appendTo($('#ports'));
        const gangwayLenFactor = 2/5;
        const h = size * ROOT_THREE_OVER_THREE;
        const w = 1;
        portVertices.forEach(vertex => { // (there are 2)
            //x, y, sx, and sy still in scope!
            const vcoord = getVertexCoordinate(vertex.tileCoordinate, vertex.direction);
            const [vsx, vsy] = toScreenCoordinates({q: (anchor.q + vcoord.q)/2, r: (anchor.r + vcoord.r)/2}, size); //mean position btwn anchor and portvertex
            const [dsx, dsy] = [vsx - sx, vsy - sy];
            const theta = Math.atan(dsx/dsy); //math!
            // using our own version of toTranslateCoordinates here - need to move up, not down
            const [x, y] = toTranslateCoordinates(vsx, vsy, size);
            $("<div class='gangway' style='width: " + w + "vh; height: " + (h * gangwayLenFactor) + "vh; " +
                "transform: translate(" + x + ", calc(" + y + ")) rotate(" + (-theta) + "rad)'></div>")
                .appendTo($('#ports')); //dont need to save it im never touching these again
        });
    });
}

//returns in coordinate object form
function getVertexCoordinate(coordinate, direction) {
    if(direction === "NORTH") {
        return {q:(coordinate.q + ONE_THIRD), r:(coordinate.r - TWO_THIRD + 1)};
    }
    return {q:(coordinate.q - ONE_THIRD), r:(coordinate.r - ONE_THIRD + 1)};
}

function onVertexClick(vertex) {
    console.log("Vertex @ " + JSON.stringify(vertex.tileCoordinate) + " to " + vertex.direction + " clicked!"); //TODO
}

function toScreenCoordinates(coordinate, size) {
    const q = coordinate.q;
    const r = coordinate.r;
    return [(SIZE_BUFFER * (q*size + r*size/2)) - size/2, SIZE_BUFFER * (r*size*HALF_SQRT_3) - size];
}

function toTranslateCoordinates(x, y, size) {
    return ["calc(-50% + " + (x - (SIZE_BUFFER-1)*size / 2) + "vh)", "calc(25% + " + (y - (SIZE_BUFFER-1)*size) + "vh)"];
}

function toTileImage(resource) {
    if(resource == null) {
        return "\"images/tile/desert.png\"";
    }
    return "\"images/tile/" + resource.toLowerCase() + ".png\"";
}