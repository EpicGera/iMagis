import urllib.request
import urllib.error
import ssl
import sys
import concurrent.futures

def check_stream(url, timeout=3):
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    req = urllib.request.Request(url, headers={
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Accept': '*/*'})
    
    try:
        with urllib.request.urlopen(req, context=ctx, timeout=timeout) as response:
            if response.status == 200:
                print(f"[OK] {url}")
                return True
            return False
    except Exception as e:
        # print(f"[FAIL] {url}")
        return False

def check_channel(entry):
    lines = entry.split('\n')
    if len(lines) >= 2:
        url = lines[-1].strip()
        if check_stream(url):
            return entry
    return None

if __name__ == "__main__":
    playlist_url = "https://iptv-org.github.io/iptv/countries/ar.m3u"
    
    print(f"Fetching playlist: {playlist_url}")
    req = urllib.request.Request(playlist_url, headers={'User-Agent': 'Mozilla/5.0'})
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE
    
    try:
        with urllib.request.urlopen(req, context=ctx, timeout=10) as response:
            content = response.read().decode('utf-8')
    except Exception as e:
        print(f"Failed to fetch {playlist_url}: {e}")
        sys.exit(1)

    lines = content.splitlines()
    entries = []
    
    current_entry = ""
    for line in lines:
        if line.startswith("#EXTINF"):
            current_entry = line + "\n"
        elif not line.startswith("#") and line.strip() and current_entry:
            current_entry += line.strip()
            entries.append(current_entry)
            current_entry = ""
            
    print(f"Total entries found: {len(entries)}")
    
    working_entries = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
        results = list(executor.map(check_channel, entries))
        
    working_entries = [r for r in results if r is not None]
    
    print(f"\nWorking entries: {len(working_entries)}")
    
    with open('ar_working.m3u', 'w', encoding='utf-8') as f:
        f.write("#EXTM3U\n")
        f.write("\n".join(working_entries) + "\n")
        
    print("Saved carefully filtered M3U file to ar_working.m3u")
