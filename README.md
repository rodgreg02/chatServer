
# Chat Server | Mindera

- A Mindera chat project with events and payloads.

## Events

### connect|username

- Description: Request server to assign his name to his socket.
- Event: `connect`
- Payload: `username:STRING`
- Receiver: `Server`

### send_message|message

- Description: Request server to send the payload information to all connected users.
- Event: `send_message`
- Payload: `message:STRING`
- Receiver: `Server`

### new_user|username|timestamp

- Description: Send connect information to all connected clients.
- Event: `connected`
- Payload: `message:STRING`, `timestamp:LOCALTIME`
- Receiver: `Client`

### new_message|timestamp|username|message

- Description: Send message information to all connected clients.
- Event: `new_message`
- Payload: `timestamp:LOCALTIME`, `username:STRING`, `message:STRING` 
- Receiver: `Client`


## Contributors

- [Marco Silva](https://www.github.com/ocramgit)
- [Francisco Silva](https://www.github.com/FranciscoSilvaMgLPT/)
- [Rodrigo Gregório](https://www.github.com/rodgreg02)
