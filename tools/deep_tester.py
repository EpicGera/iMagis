import urllib.request
import urllib.error
import ssl
import re

# Ignore SSL errors which frequently happen with IPTV providers
ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

HEADERS = {
    'User-Agent': 'ExoPlayer/2.18.7 (Linux; Android 13) ExoPlayerLib/2.18.7',
    'Accept': '*/*()',
    'Connection': 'keep-alive'
}

CHANNELS_TO_TEST = {
    "Locomotion": "http://51.222.85.85:81/hls/loco/index.m3u8",
    "TN": "http://190.104.226.30/Live/870787012c00961adaf9b2304d704b57/tn_720.m3u8",
    "Canal 26": "https://stream-gtlc.telecentro.net.ar/hls/canal26hls/main.m3u8",
    "America TV": "https://prepublish.f.qaotic.net/a07/americahls-100056/playlist_720p.m3u8",
    "Telefe 1": "https://cdn-cirion01.sensa.com.ar/output/ARR2/TelefeRosarioH/playlist.m3u8",
    "Telefe 2": "http://190.104.226.30/Live/870787012c00961adaf9b2304d704b57/telefe_720.m3u8",
    "Canal 9 1": "https://stream.arcast.live/ahora/ahora/playlist.m3u8",
    "Canal 9 2": "https://unlimited1-saopaulo.dps.live/televidaar/televidaar.smil/playlist.m3u8",
    "TyC Sports": "https://live-04-11-tyc24.vodgc.net/tyc24/index_tyc24_1080.m3u8"
}

def resolve_url(base, path):
    if path.startswith('http'):
        return path
    if base.endswith('/'):
        return base + path
    return base[:base.rfind('/') + 1] + path

def deep_test_stream(name, url):
    print(f"\n--- Testing {name} ---")
    print(f"URL: {url}")
    try:
        req = urllib.request.Request(url, headers=HEADERS)
        res = urllib.request.urlopen(req, context=ctx, timeout=10)
        content = res.read().decode('utf-8')
        
        # Is it a master playlist?
        if 'EXT-X-STREAM-INF' in content:
            print("Found MASTER playlist. Selecting highest quality stream...")
            lines = content.split('\n')
            target_playlist = None
            for i, line in enumerate(lines):
                if 'EXT-X-STREAM-INF' in line:
                    target_playlist = lines[i+1].strip()
                    break # Take the first one (often highest bandwidth in good playlists, but good enough for test)
            
            if target_playlist:
                sub_url = resolve_url(url, target_playlist)
                print(f"-> Redirecting to sub-playlist: {sub_url}")
                return deep_test_stream(name + " (Sub)", sub_url)
            else:
                print("[FAIL] Could not parse sub-playlist from master.")
                return False

        # Is it a media playlist?
        elif 'EXTINF' in content:
            print("Found MEDIA playlist. Attempting to download first .ts chunk...")
            lines = content.split('\n')
            for line in lines:
                line = line.strip()
                if line and not line.startswith('#'):
                    chunk_url = resolve_url(url, line)
                    print(f"-> Downloading chunk: {chunk_url.split('/')[-1]}")
                    try:
                        chunk_req = urllib.request.Request(chunk_url, headers=HEADERS)
                        chunk_res = urllib.request.urlopen(chunk_req, context=ctx, timeout=10)
                        # Read a tiny bit of the stream to prove it sends data
                        data = chunk_res.read(1024)
                        if len(data) > 0:
                            print("[OK] SUCCESS: Chunk downloaded successfully! Stream is strictly ALIVE.")
                            return True
                        else:
                            print("[FAIL] Chunk returned 0 bytes.")
                            return False
                    except Exception as e:
                        print(f"[FAIL] Chunk download failed: {e}")
                        return False
            print("[FAIL] No chunks found in media playlist.")
            return False
        else:
            print("[FAIL] Not a valid M3U8 string.")
            return False

    except Exception as e:
        print(f"[FAIL] Manifest unreachable: {e}")
        return False

for name, url in CHANNELS_TO_TEST.items():
    deep_test_stream(name, url)
