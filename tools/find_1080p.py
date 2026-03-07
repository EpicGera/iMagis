import urllib.request
import re
import sys
from concurrent.futures import ThreadPoolExecutor, as_completed

TARGET_CHANNELS = ["el trece", "telefe", "canal 9", "america", "canal 26", "a24", "c5n"]

PLAYLISTS = [
    'https://raw.githubusercontent.com/iptv-org/iptv/master/streams/ar.m3u',
    'https://radiosargentina.com.ar/TVAR.m3u',
    'https://iptv-org.github.io/iptv/countries/ar.m3u'
]

def fetch_content(url):
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
        return urllib.request.urlopen(req, timeout=10).read().decode('utf-8')
    except Exception as e:
        return None

def parse_m3u(content):
    channels = []
    lines = content.split('\n')
    current_name = ""
    for line in lines:
        line = line.strip()
        if line.startswith("#EXTINF"):
            # Try to grab the name after the last comma
            parts = line.split(",")
            if len(parts) > 1:
                current_name = parts[-1].strip().lower()
        elif line and not line.startswith("#"):
            if current_name:
                channels.append((current_name, line))
                current_name = ""
    return channels

def get_resolution(m3u8_content):
    # Find all resolutions like RESOLUTION=1920x1080
    matches = re.findall(r'RESOLUTION=(\d+x\d+)', m3u8_content)
    if not matches:
        return "Unknown"
    
    # Sort resolutions (e.g. 1920x1080 -> 1080)
    res_list = []
    for m in matches:
        w, h = m.split('x')
        res_list.append((int(h), m))
    
    res_list.sort(key=lambda x: x[0], reverse=True)
    return f"{res_list[0][1]} ({res_list[0][0]}p)"

def test_stream(name, url):
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
        m3u8_content = urllib.request.urlopen(req, timeout=10).read().decode('utf-8')
        
        # Check if it's a master playlist
        if 'EXT-X-STREAM-INF' in m3u8_content:
            res = get_resolution(m3u8_content)
            return f"[MASTER] {name.upper()}: {res} -> {url}"
        elif 'EXTINF' in m3u8_content:
            return f"[DIRECT] {name.upper()}: Stream alive (Resolution inside chunks) -> {url}"
        else:
            return f"[INVALID] {name.upper()} -> content not recognized m3u8"
    except Exception as e:
        return f"[FAIL] {name.upper()} -> {str(e)}"

def run():
    print("Fetching and parsing aggregated lists...")
    all_channels = []
    for p in PLAYLISTS:
        content = fetch_content(p)
        if content:
            parsed = parse_m3u(content)
            all_channels.extend(parsed)
            print(f"Parsed {len(parsed)} channels from {p}")

    # Filter targets
    targets = []
    for c_name, c_url in all_channels:
        for t in TARGET_CHANNELS:
            # We want exact or close matches to avoid taking "Telefe Cordoba" when asking for "Telefe" unless necessary
            if t in c_name:
                targets.append((c_name, c_url))

    # Deduplicate URLs
    unique_targets = {}
    for n, u in targets:
        unique_targets[u] = n

    print(f"\nFound {len(unique_targets)} unique stream URLs matching our targets. Testing them now...\n")

    working = []
    with ThreadPoolExecutor(max_workers=15) as executor:
        futures = {executor.submit(test_stream, n, u): u for u, n in unique_targets.items()}
        for future in as_completed(futures):
            res = future.result()
            print(res)
            if "[MASTER]" in res or "[DIRECT]" in res:
                working.append(res)
    
    print("\n=== FINAL WORKING STREAMS ===")
    for w in working:
        print(w)

if __name__ == '__main__':
    run()
