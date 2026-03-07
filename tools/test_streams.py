import urllib.request
import urllib.error
import ssl
import sys

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
                if '#EXTM3U' in content or content.startswith('#'):
                    return True, "Valid Playlist"
                elif content.startswith(b'\x47'): # TS sync byte
                    return True, "Valid TS Stream"
                else:
                    return True, "HTTP 200 but unknown content"
            return False, f"HTTP {response.status}"
    except urllib.error.HTTPError as e:
        return False, f"HTTP {e.code}"
    except urllib.error.URLError as e:
        return False, f"URL Error: {e.reason}"
    except Exception as e:
        return False, str(e)

if __name__ == "__main__":
    urls = [
        "http://locomotiontv.com/envivo/loco_channel/stream.m3u8",
        "https://live-01-02-eltrece.vodgc.net/eltrecetv/index.m3u8",
        "https://livetrx01.vodgc.net/eltrecetv/index.m3u8",
        "https://linear-402.frequency.stream/dist/localnow/402/hls/master/playlist.m3u8",
        "https://cdn.eltrecetv.com.ar/eltrece/index.m3u8"
    ]
    
    # Also fetch the main AR playlists to parse and test some channels
    import re
    playlists = [
        "https://iptv-org.github.io/iptv/countries/ar.m3u",
        "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist_argentina.m3u8",
        "https://sesteva.github.io/m3u/argentina.m3u"
    ]
    
    target_channels = ["TN", "C5N", "Crónica", "A24", "LN+", "Canal 26", "Telefe", "El Trece", "Canal 9", "América", "TyC Sports", "ESPN", "Fox Sports"]
    
    found_streams = {}
    
    for pl in playlists:
        try:
            print(f"\nFetching playlist: {pl}")
            req = urllib.request.Request(pl, headers={'User-Agent': 'Mozilla/5.0'})
            ctx = ssl.create_default_context()
            ctx.check_hostname = False
            ctx.verify_mode = ssl.CERT_NONE
            with urllib.request.urlopen(req, context=ctx, timeout=10) as response:
                lines = response.read().decode('utf-8').splitlines()
                
                current_name = None
                for line in lines:
                    line = line.strip()
                    if line.startswith("#EXTINF"):
                        current_name = line.split(',')[-1].strip()
                    elif line and not line.startswith("#") and current_name:
                        # Check if it matches our targets
                        for target in target_channels:
                            if target.lower() in current_name.lower():
                                if target not in found_streams:
                                    found_streams[target] = []
                                found_streams[target].append(line)
                        current_name = None
        except Exception as e:
            print(f"Failed to fetch {pl}: {e}")

    print("\n--- Testing Target Streams ---")
    
    # Test all specific URLs
    for url in urls:
        ok, msg = check_stream(url)
        print(f"[{'OK' if ok else 'FAIL'}] {url} -> {msg}")
        
    print("\n--- Testing Scraped Streams ---")
    for name, stream_urls in found_streams.items():
        print(f"\n{name}:")
        working_count = 0
        for i, url in enumerate(list(set(stream_urls))[:5]): # Test up to 5 unique per channel
            ok, msg = check_stream(url)
            if ok:
                print(f"  [OK] {url}")
                working_count += 1
            else:
                pass # print(f"  [FAIL] {url}")
        if working_count == 0:
            print("  NO WORKING STREAMS FOUND")
