import bottle, os, sys, signal

clients = {}

pics = {}
pics_cnt = 0

if len(sys.argv) > 1:
    clients, pics, pics_cnt = eval(sys.argv[1])

def get_client_ip():
    return bottle.request.environ['REMOTE_ADDR'].split(':')[0]

def add_pic(data):
    global pics_cnt
    pics[pics_cnt] = data
    pics_cnt += 1

@bottle.post('/register')
def reg():
    ip = get_client_ip()
    if ip not in clients:
        clients[ip] = -1
        data = bottle.request.body.read()
        add_pic((data, ip, '', 'reg'))
    return b'ok'

@bottle.post('/shoot')
def shoot():
    ip = get_client_ip()
    name = clients.get(ip, None)
    if isinstance(name, str):
        data = bottle.request.body.read()
        add_pic((data, ip, name, 'shoot'))
    return b'ok'

@bottle.route('/check')
def checkState():
    data = clients.get(get_client_ip(), -1)
    if data == None:
        return b'd'
    elif isinstance(data, str):
        return b'a'
    else:
        return b'p'

@bottle.post('/images')
def images():
    ensure_local()
    return {'data': [{'id': i, 'ip': ip, 'name': name, 'type': k} for i, (_, ip, name, k) in reversed(sorted(pics.items()))]}

@bottle.route('/images/<idx:int>')
def get_image(idx):
    ensure_local()
    bottle.response.set_header('Cache-Control', 'no-cache')
    try: return pics[idx][0]
    except KeyError: bottle.abort(404)

@bottle.post('/dismiss/<idx:int>')
def remove_image(idx):
    ensure_local()
    try: del pics[idx]
    except KeyError: pass
    return b''

@bottle.post('/kill/<ip>')
def kill(ip):
    ensure_local()
    clients[ip] = None
    return b''

@bottle.post('/register/<ip>')
def register(ip):
    ensure_local()
    name = bottle.request.body.read().decode('utf-8')
    clients[ip] = name
    return b''

def ensure_local():
    if get_client_ip() != '127.0.0.1':
        bottle.abort(403)

@bottle.route('/')
def main():
    return open('fotoohota.html', 'rb').read()

def usr_hdlr(*args):
    os.execv(sys.executable, [sys.executable, sys.argv[0], repr((clients, pics, pics_cnt))])

signal.signal(signal.SIGUSR1, usr_hdlr)

bottle.run(host='', port=8080)
