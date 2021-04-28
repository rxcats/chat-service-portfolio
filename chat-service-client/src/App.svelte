<script>
    import {writable} from 'svelte/store'
    import {v4 as uuidv4} from 'uuid';
    import {Button, Card, Container, ListGroup, ListGroupItem} from 'sveltestrap';
    import InputGroup from 'sveltestrap/src/InputGroup.svelte';
    import InputGroupText from 'sveltestrap/src/InputGroupText.svelte';
    import Input from 'sveltestrap/src/Input.svelte';

    const colors = [
        '#e21400', '#91580f', '#f8a700', '#f78b00',
        '#58dc00', '#287b00', '#a8f07a', '#4ae8c4',
        '#3b88eb', '#3824aa', '#a700ff', '#d300e7'
    ];

    let url = 'ws://localhost:8080/ws';
    const userId = uuidv4();
    let username = 'Guest';
    let message = '';
    let userColor = colors[Math.floor(Math.random() * colors.length)];
    let messageStore = writable([]);
    let messages;
    let isConnected = false;

    const messageParse = (data) => {
        console.log(data);
        switch (data.uri) {
            case '/chat/message':
                data.body.now = data.now;
                messageStore.update(msg => msg.concat(data.body));
                break;
            case '/chat/leave':
                data.body.now = data.now;
                data.body.message = 'left.';
                messageStore.update(msg => msg.concat(data.body));
                break;
            case '/chat/join':
                data.body.now = data.now;
                data.body.message = 'join.';
                messageStore.update(msg => msg.concat(data.body));
                break;
        }
    }

    messageStore.subscribe(data => {
        messages = data.reverse();
    });

    const ws = new WebSocket(url);

    ws.onopen = () => {

    }

    ws.onmessage = (event) => {
        messageParse(JSON.parse(event.data));
    }

    ws.onclose = () => {

    }

    ws.onerror = () => {

    }

    const sendMessage = (msg) => {
        ws.send(JSON.stringify(msg));
    }

    const login = () => {
        sendMessage({
            uri: '/chat/login',
            body: {
                userId: userId,
                username: username,
                userColor: userColor
            }
        });
    }

    const join = () => {
        sendMessage({
            uri: '/chat/join'
        });
    }

    const leave = () => {
        sendMessage({
            uri: '/chat/leave'
        });
    }

    const onConnect = () => {
        if (ws.readyState !== 1) {
            alert('서버에 접속할 수 없습니다.')
            isConnected = false;
            return;
        }

        if (isConnected) {
            alert('이미 접속한 상태입니다.');
            return;
        }

        login();
        join();
        isConnected = true;
    }

    const onConnectEnter = evt => {
        if (evt.charCode === 13) {
            onConnect();
        }
    }

    const onClose = () => {
        if (!isConnected) {
            alert('이미 접속 종료된 상태입니다.');
            return;
        }

        leave();
        isConnected = false;
    }

    const onSend = () => {
        if (!isConnected) {
            alert('접속 상태가 아닙니다.');
            return;
        }

        if (message.length === 0) {
            alert('메시지를 입력하세요.');
            return;
        }

        sendMessage({
            uri: '/chat/message',
            body: {
                message: message
            }
        });

        message = '';
    }

    const onSendEnter = evt => {
        if (evt.charCode === 13) {
            onSend();
        }
    }

</script>

<Container>
    <InputGroup class="mt-3 mb-3">
        <InputGroupText>Address</InputGroupText>
        <Input bind:value={url} placeholder="url" aria-label="url"/>
        <InputGroupText>@</InputGroupText>
        <Input type="color" bind:value={userColor} disabled/>
        <Input bind:value={username} on:keypress={onConnectEnter} placeholder="username" aria-label="username"/>
        <Button on:click={onConnect} outline color="info">Connect</Button>
        <Button on:click={onClose} outline color="danger">Close</Button>
    </InputGroup>

    <InputGroup class="mt-3 mb-3">
        <InputGroupText>Message</InputGroupText>
        <Input bind:value={message} on:keypress={onSendEnter} placeholder="message" aria-label="message"/>
        <Button on:click={onSend} outline color="success">Send</Button>
    </InputGroup>

    <Card body class="overflow-auto" style="max-height: 500px">
        <ListGroup>
            {#each messages as msg}
                <ListGroupItem>
                    <div class="row">
                        <span class="col" style="color: {msg.userColor}">{msg.username}: {msg.message}</span>
                        <span class="col text-end" style="color: {msg.userColor}">{msg.now}</span>
                    </div>
                </ListGroupItem>
            {/each}
        </ListGroup>
    </Card>
</Container>
