import re
import urllib.request
import urllib.error
import ssl

def check_stream(url, timeout=5):
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    req = urllib.request.Request(url, headers={
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Accept': '*/*'})
    
    try:
        with urllib.request.urlopen(req, context=ctx, timeout=timeout) as response:
            if response.status == 200:
                content = response.read(1024).decode('utf-8', errors='ignore')
                if '#EXTM3U' in content or content.startswith('#') or content.startswith('HLS'):
                    return True, "Valid Playlist"
                elif content.startswith(b'\x47'): # TS sync byte
                    return True, "Valid TS Stream"
                else:
                    return True, "HTTP 200 (unknown format)"
            return False, f"HTTP {response.status}"
    except urllib.error.HTTPError as e:
        return False, f"HTTP {e.code}"
    except Exception as e:
        return False, str(e)

with open('app/src/main/java/com/example/imagis/data/ChannelStore.kt', 'r', encoding='utf-8') as f:
    content = f.read()

urls = re.findall(r'"(https?://[^"]+\.m3u8?)"', content)
urls = list(set(urls))

working = []
failing = []

print(f"Found {len(urls)} URLs to test.")
for url in urls:
    ok, msg = check_stream(url)
    if ok:
        print(f"[OK] {url}")
        working.append(url)
    else:
        print(f"[FAIL] {url} -> {msg}")
        failing.append(url)

print("\n--- Summary ---")
print(f"Working: {len(working)}")
print(f"Failing: {len(failing)}")
