
let stompClient = null;
let currentUsername = window._chatUsername || null;


function connect(username) {
  currentUsername = username;

  const wsUrl = (window._petServiceUrl || 'http://localhost:8081') + '/ws-chat';
  const socket = new SockJS(wsUrl);
  stompClient = Stomp.over(socket);

  stompClient.debug = null;

  setStatus('connecting');

  stompClient.connect({}, function onConnected() {
    setStatus('connected');

    stompClient.subscribe('/topic/public', function(message) {
      const chatMessage = JSON.parse(message.body);
      displayMessage(chatMessage);
    });

    stompClient.send('/app/chat.join', {}, JSON.stringify({
      sender: currentUsername,
      content: '',
      type: 'JOIN'
    }));

    document.getElementById('usernamePrompt') && (document.getElementById('usernamePrompt').style.display = 'none');
    document.getElementById('chatArea').style.display = 'block';
    document.getElementById('messageInput').focus();

  }, function onError(err) {
    setStatus('disconnected');
    console.error('WebSocket connection error:', err);
    setTimeout(() => connect(currentUsername), 5000);
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
    setStatus('disconnected');
  }
}


function sendMessage() {
  const input = document.getElementById('messageInput');
  const content = input.value.trim();

  if (!content || !stompClient || !stompClient.connected) return;

  const chatMessage = {
    sender: currentUsername,
    content: content,
    type: 'CHAT'
  };

  stompClient.send('/app/chat.send', {}, JSON.stringify(chatMessage));
  input.value = '';
}


function joinChat() {
  const input = document.getElementById('usernameInput');
  const name = (input ? input.value.trim() : null) || 'Guest';
  connect(name);
}


function displayMessage(message) {
  const area = document.getElementById('messageArea');
  const div = document.createElement('div');

  if (message.type === 'JOIN' || message.type === 'LEAVE') {
    div.className = 'text-center';
    div.innerHTML = `
      <small class="text-muted">
        <i class="fa ${message.type === 'JOIN' ? 'fa-sign-in-alt' : 'fa-sign-out-alt'} me-1"></i>
        ${escHtml(message.content)}
        <span class="ms-2 text-muted" style="font-size:0.75em">${escHtml(message.timestamp)}</span>
      </small>`;
  } else {
    const isOwn = message.sender === currentUsername;
    div.className = `d-flex ${isOwn ? 'justify-content-end' : 'justify-content-start'}`;
    div.innerHTML = `
      <div class="card ${isOwn ? 'bg-primary text-white' : 'bg-white'} shadow-sm" style="max-width:70%">
        <div class="card-body py-2 px-3">
          <div class="fw-bold small ${isOwn ? 'text-white-50' : 'text-muted'}">${escHtml(message.sender)}</div>
          <div>${escHtml(message.content)}</div>
          <div class="text-end" style="font-size:0.7em; opacity:0.7">${escHtml(message.timestamp)}</div>
        </div>
      </div>`;
  }

  area.appendChild(div);
  area.scrollTop = area.scrollHeight;
}


function setStatus(state) {
  const badge = document.getElementById('connectionStatus');
  if (!badge) return;
  const labels = window._i18nChat || {};
  if (state === 'connected') {
    badge.className = 'badge bg-success';
    badge.textContent = labels.connected || 'Connected';
  } else if (state === 'disconnected') {
    badge.className = 'badge bg-danger';
    badge.textContent = labels.disconnected || 'Disconnected';
  } else {
    badge.className = 'badge bg-warning text-dark';
    badge.textContent = labels.connecting || 'Connecting...';
  }
}

function escHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

window.addEventListener('DOMContentLoaded', () => {
  if (window._chatUsername) {
    connect(window._chatUsername);
  }
});
