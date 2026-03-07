import re
import sys

m3u_path = r'c:\Users\JDB\Documents\iMagis\app\src\main\res\raw\ar_working.m3u'

try:
    with open(m3u_path, 'r', encoding='utf-8') as f:
        text = f.read()

    # Find and replace tvg-logo containing imgur or ibb.co with empty string
    text = re.sub(r'tvg-logo="https?://(i\.imgur\.com|i\.ibb\.co)[^"]*"', 'tvg-logo=""', text)
    text = re.sub(r"tvg-logo='https?://(i\.imgur\.com|i\.ibb\.co)[^']*'", 'tvg-logo=""', text)

    with open(m3u_path, 'w', encoding='utf-8') as f:
        f.write(text)
    
    print("Successfully removed broken imgur and ibb.co logos from ar_working.m3u")
except Exception as e:
    print(f"Error: {e}")
    sys.exit(1)
