import urllib.request
import re

def test_rss(url):
    print(f"Testing RSS: {url}")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
        with urllib.request.urlopen(req) as response:
            data = response.read().decode('utf-8')
            items = re.findall(r'<item>(.*?)</item>', data, re.DOTALL)
            if items:
                print("First Item:\n", items[0])
            else:
                print("No items found.")
    except Exception as e:
        print(f"Error: {e}")

test_rss("https://www.limetorrents.fun/searchrss/matrix/")
