package com.example.network

object PythonServerScript {

    const val SCRIPT_FILENAME = "laptop_remote_server.py"

    val SCRIPT_CODE: String = """
# ========================================================
# Laptop Remote WiFi - Companion Server for PC / Mac / Linux
# سيرفر التحكم باللابتوب عبر الواي فاي
# ========================================================
# المتطلبات / Requirements:
# pip install flask pyautogui pillow
# ثم قم بتشغيل السكربت / Run with:
# python laptop_remote_server.py
# ========================================================

import os
import sys
import json
import socket
import subprocess
from flask import Flask, request, jsonify, send_file
import io

try:
    import pyautogui
    pyautogui.FAILSAFE = False
    pyautogui.PAUSE = 0.001
except ImportError:
    print("Warning: pyautogui is not installed. Run: pip install pyautogui")
    pyautogui = None

try:
    from PIL import ImageGrab
except ImportError:
    ImageGrab = None

app = Flask(__name__)

def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('10.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP

@app.route('/ping', methods=['GET'])
def ping():
    return jsonify({
        "status": "connected",
        "hostname": socket.gethostname(),
        "platform": sys.platform,
        "os": "Windows" if sys.platform.startswith("win") else ("macOS" if sys.platform == "darwin" else "Linux")
    })

@app.route('/mouse', methods=['POST'])
def mouse_move():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    dx = float(data.get('dx', 0))
    dy = float(data.get('dy', 0))
    pyautogui.moveRel(dx, dy)
    return jsonify({"status": "ok"})

@app.route('/click', methods=['POST'])
def mouse_click():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    btn = data.get('button', 'left')
    if btn == 'left':
        pyautogui.click()
    elif btn == 'right':
        pyautogui.rightClick()
    elif btn == 'double':
        pyautogui.doubleClick()
    elif btn == 'middle':
        pyautogui.middleClick()
    return jsonify({"status": "ok"})

@app.route('/scroll', methods=['POST'])
def mouse_scroll():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    dy = int(data.get('dy', 0))
    pyautogui.scroll(dy)
    return jsonify({"status": "ok"})

@app.route('/type', methods=['POST'])
def keyboard_type():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    text = data.get('text', '')
    pyautogui.write(text, interval=0.01)
    return jsonify({"status": "ok"})

@app.route('/key', methods=['POST'])
def keyboard_key():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    key = data.get('key', '')
    key_mapping = {
        'enter': 'enter', 'backspace': 'backspace', 'esc': 'escape',
        'tab': 'tab', 'space': 'space', 'win': 'win',
        'up': 'up', 'down': 'down', 'left': 'left', 'right': 'right',
        'f1': 'f1', 'f2': 'f2', 'f3': 'f3', 'f4': 'f4', 'f5': 'f5',
        'f6': 'f6', 'f7': 'f7', 'f8': 'f8', 'f9': 'f9', 'f10': 'f10',
        'f11': 'f11', 'f12': 'f12'
    }
    target = key_mapping.get(key.lower(), key.lower())
    pyautogui.press(target)
    return jsonify({"status": "ok"})

@app.route('/shortcut', methods=['POST'])
def keyboard_shortcut():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    keys = data.get('keys', [])
    if keys:
        pyautogui.hotkey(*keys)
    return jsonify({"status": "ok"})

@app.route('/media', methods=['POST'])
def media_control():
    if not pyautogui:
        return jsonify({"error": "pyautogui not installed"}), 500
    data = request.get_json(force=True)
    action = data.get('action', '')
    media_map = {
        'volume_up': 'volumeup',
        'volume_down': 'volumedown',
        'mute': 'volumemute',
        'play_pause': 'playpause',
        'next': 'nexttrack',
        'prev': 'prevtrack'
    }
    if action in media_map:
        pyautogui.press(media_map[action])
    return jsonify({"status": "ok"})

@app.route('/system', methods=['POST'])
def system_control():
    data = request.get_json(force=True)
    action = data.get('action', '')
    is_win = sys.platform.startswith('win')
    
    if action == 'lock':
        if is_win:
            os.system('rundll32.exe user32.dll,LockWorkStation')
        else:
            os.system('pmset displaysleepnow')
    elif action == 'sleep':
        if is_win:
            os.system('rundll32.exe powrprof.dll,SetSuspendState 0,1,0')
        else:
            os.system('pmset sleepnow')
    elif action == 'shutdown':
        if is_win:
            os.system('shutdown /s /t 10')
        else:
            os.system('sudo shutdown -h now')
    elif action == 'restart':
        if is_win:
            os.system('shutdown /r /t 10')
        else:
            os.system('sudo shutdown -r now')
            
    return jsonify({"status": "ok"})

@app.route('/app', methods=['POST'])
def app_control():
    data = request.get_json(force=True)
    action = data.get('action', 'launch')
    target = data.get('target', '')
    
    # Common app aliases
    win_apps = {
        'chrome': 'start chrome',
        'edge': 'start msedge',
        'firefox': 'start firefox',
        'code': 'code',
        'word': 'start winword',
        'excel': 'start excel',
        'powerpoint': 'start powerpnt',
        'spotify': 'start spotify',
        'vlc': 'start vlc',
        'calc': 'start calc',
        'notepad': 'start notepad',
        'explorer': 'explorer',
        'cmd': 'start cmd',
        'taskmgr': 'start taskmgr',
        'settings': 'start ms-settings:'
    }
    
    if action == 'launch':
        cmd = win_apps.get(target.lower(), target)
        if sys.platform.startswith('win'):
            subprocess.Popen(cmd, shell=True)
        else:
            subprocess.Popen(['open', '-a', target] if sys.platform == 'darwin' else [target])
    return jsonify({"status": "ok", "app": target})

@app.route('/screen', methods=['GET'])
def screen_snapshot():
    if not ImageGrab:
        return jsonify({"error": "PIL ImageGrab not available"}), 500
    img = ImageGrab.grab()
    img.thumbnail((800, 450))
    buffer = io.BytesIO()
    img.save(buffer, 'JPEG', quality=65)
    buffer.seek(0)
    return send_file(buffer, mimetype='image/jpeg')

if __name__ == '__main__':
    ip = get_local_ip()
    port = 8080
    print("\n" + "="*50)
    print("  LAPTOP REMOTE SERVER IS READY! ")
    print("  سيرفر التحكم باللابتوب جاهز للاتصال! ")
    print(f"  Connect from phone to: http://{ip}:{port}")
    print("="*50 + "\n")
    app.run(host='0.0.0.0', port=port, threaded=True)
""".trimIndent()
}
