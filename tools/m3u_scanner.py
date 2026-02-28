import requests
import time
# You might need to install colorama: pip install colorama
try:
    from colorama import Fore, Style, init
    init(autoreset=True)
except ImportError:
    # Fallback if colorama is not installed
    class Fore:
        CYAN = ""
        GREEN = ""
        RED = ""
        YELLOW = ""
    class Style:
        RESET_ALL = ""

class M3uScanner:
    def __init__(self):
        # We use GitHub's public search API. 
        self.api_url = "https://api.github.com/search/code?q=extension:m3u+EXTINF"
        self.headers = {
            "Accept": "application/vnd.github.v3+json",
            "User-Agent": "M3uScannerTool/1.0"
        }

    def search_github_playlists(self):
        print(f"{Fore.CYAN}[*] Scanning GitHub for public M3U playlists...{Style.RESET_ALL}")
        
        try:
            response = requests.get(self.api_url, headers=self.headers)
            if response.status_code == 200:
                data = response.json()
                items = data.get('items', [])
                
                print(f"{Fore.GREEN}[+] Found {len(items)} potential playlist files.{Style.RESET_ALL}\n")
                
                valid_playlists = []
                
                for item in items:
                    raw_url = item['html_url'].replace('github.com', 'raw.githubusercontent.com').replace('/blob/', '/')
                    repo_name = item['repository']['full_name']
                    
                    print(f"Checking: {repo_name}...")
                    if self.validate_playlist(raw_url):
                        print(f"{Fore.GREEN}   -> VALID! {raw_url}{Style.RESET_ALL}")
                        valid_playlists.append(raw_url)
                    else:
                        print(f"{Fore.RED}   -> Invalid or empty.{Style.RESET_ALL}")
                    
                    # Sleep to avoid hitting API rate limits immediately
                    time.sleep(2)
                    
                return valid_playlists
            elif response.status_code == 403:
                print(f"{Fore.RED}[!] API Rate Limit Exceeded. Try again later or use an API Token.{Style.RESET_ALL}")
            else:
                print(f"{Fore.RED}[!] Error: {response.status_code}{Style.RESET_ALL}")
                
        except Exception as e:
            print(f"{Fore.RED}[!] Crash: {e}{Style.RESET_ALL}")
        return []

    def validate_playlist(self, url):
        """Checks if the URL actually returns M3U content."""
        try:
            r = requests.get(url, timeout=5)
            if r.status_code == 200 and "#EXTINF" in r.text:
                return True
        except:
            pass
        return False

if __name__ == "__main__":
    scanner = M3uScanner()
    links = scanner.search_github_playlists()
    
    print(f"\n{Fore.YELLOW}--- SCAN COMPLETE ---{Style.RESET_ALL}")
    print("Copy these links into your Android App:")
    for link in links:
        print(link)
