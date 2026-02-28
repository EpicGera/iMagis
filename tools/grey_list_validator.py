import requests
import time

def validate_stream(url):
    try:
        # We pretend to be a VLC player, otherwise they might block us
        headers = {
            'User-Agent': 'VLC/3.0.16 LibVLC/3.0.16'
        }
        # We only download the first few bytes to check if it's alive
        with requests.get(url, headers=headers, stream=True, timeout=5) as r:
            if r.status_code == 200:
                return True
    except:
        return False
    return False

def check_playlist(m3u_url):
    print(f"Checking Playlist: {m3u_url}...")
    try:
        response = requests.get(m3u_url, timeout=10)
        lines = response.text.split('\n')
        
        valid_streams = 0
        total_streams = 0
        
        print("Sampling first 5 channels...")
        
        for line in lines:
            line = line.strip()
            if line.startswith("http"):
                total_streams += 1
                if total_streams <= 5: # Only check first 5 to save time
                    is_working = validate_stream(line)
                    status = "Working" if is_working else "DEAD"
                    print(f"Stream {total_streams}: {status}")
                    if is_working: valid_streams += 1
        
        if valid_streams > 0:
            print(f"\n[SUCCESS] This list is ACTIVE. Add it to your app!")
        else:
            print(f"\n[FAIL] This list might be dead. Try another.")
            
    except Exception as e:
        print(f"Error downloading playlist: {e}")

if __name__ == "__main__":
    url = input("Enter the M3U URL you found: ")
    check_playlist(url)
