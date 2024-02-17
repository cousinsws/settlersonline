//replace with read from application.properties?
const ip = 'localhost';
const port = '8080';

export function getClient() {
    return new StompJs.Client({ //bp DNR
        brokerURL: 'ws://' + ip + ':' + port + '/settlers-app'
    })
}