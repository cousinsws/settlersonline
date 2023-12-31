//replace with read from application.properties?
const ip = '192.168.1.111';
const port = '8080';

export function getClient() {
    return new StompJs.Client({ //bp DNR
        brokerURL: 'ws://' + ip + ':' + port + '/settlers-app'
    })
};