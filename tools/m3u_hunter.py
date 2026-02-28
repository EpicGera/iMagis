# FILE_PATH: tools/m3u_hunter.py
# ACTION: CREATE
# DESCRIPTION: A Python PC tool that scrapes webpages or Pastebin archives for M3U links, validates them, and saves the working ones to a text file.
#---------------------------------------------------------

import requests
from bs4 import BeautifulSoup
import re
import time
from colorama import Fore, Style, init
import os

init(autoreset=True)

class M3uHunter:
    def __init__(self):
        # We spoof a standard browser to avoid getting instantly blocked by forums/pastebins
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36'
        }
        # Regex to find any URL ending in m3u or m3u8. 
        self.m3u_regex = re.compile(r'https?://[^\s<>"]+?\.m3u8?')

    def scrape_page(self, target_url):
        print(f"{Fore.CYAN}[*] Scraping target: {target_url}{Style.RESET_ALL}")
        found_links = set()

        try:
            response = requests.get(target_url, headers=self.headers, timeout=10)
            if response.status_code == 200:
                # Parse the HTML with BeautifulSoup
                soup = BeautifulSoup(response.text, 'html.parser')
                
                # Extract all text and hrefs to run our Regex against
                text_content = soup.get_text()
                for link in soup.find_all('a', href=True):
                    text_content += f" {link['href']} "

                # Find all matches
                matches = self.m3u_regex.findall(text_content)
                for match in matches:
                    found_links.add(match)

                print(f"{Fore.YELLOW}[+] Found {len(found_links)} potential M3U links. Validating now...{Style.RESET_ALL}")
                return list(found_links)
            else:
                print(f"{Fore.RED}[!] Failed to load page. HTTP Status: {response.status_code}{Style.RESET_ALL}")
                return []
        except Exception as e:
            print(f"{Fore.RED}[!] Error scraping page: {e}{Style.RESET_ALL}")
            return []

    def validate_stream(self, url):
        """Silently pings the stream to see if the server is alive."""
        try:
            # We pretend to be VLC here, as some IPTV servers block standard web scrapers
            vlc_headers = {'User-Agent': 'VLC/3.0.16 LibVLC/3.0.16'}
            with requests.get(url, headers=vlc_headers, stream=True, timeout=5) as r:
                if r.status_code == 200:
                    return True
        except:
            return False
        return False

    def hunt(self, target_url):
        raw_links = self.scrape_page(target_url)
        working_links = []

        for index, link in enumerate(raw_links):
            print(f"Testing {index + 1}/{len(raw_links)}: {link[:50]}...", end=" ")
            
            if self.validate_stream(link):
                print(f"{Fore.GREEN}ALIVE ✅{Style.RESET_ALL}")
                working_links.append(link)
            else:
                print(f"{Fore.RED}DEAD ❌{Style.RESET_ALL}")
            
            # Sleep slightly so we don't accidentally DDoS the servers
            time.sleep(1)

        self.save_to_file(working_links)

    def save_to_file(self, working_links):
        if not working_links:
            print(f"\n{Fore.RED}No working links found on this page.{Style.RESET_ALL}")
            return

        filename = "master_list.txt"
        
        # Append the new working links to your master file
        with open(filename, "a", encoding="utf-8") as file:
            for link in working_links:
                file.write(link + "\n")
        
        print(f"\n{Fore.GREEN}SUCCESS! {len(working_links)} active streams saved to {filename}.{Style.RESET_ALL}")
        print("Copy the contents of this file to your GitHub Gist to auto-update your FireTVs.")

if __name__ == "__main__":
    print(f"{Fore.MAGENTA}=== M3U GREY LIST HUNTER ==={Style.RESET_ALL}")
    url_to_scrape = input("Enter the URL to scrape (e.g., a Pastebin link or Forum page): ")
    
    hunter = M3uHunter()
    hunter.hunt(url_to_scrape)