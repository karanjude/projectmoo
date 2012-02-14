import sys
from bs4 import *

def print_entity(entity):
    if entity.string is not None:
        print >> sys.stderr, entity.string

def print_message(message):
    print >> sys.stderr
    print >> sys.stderr, message
    print >> sys.stderr

def extract_title(dom):
    print_message("extracting title....")
 
    titles = dom.findAll('title')
    for title in titles:
        print_entity(title)

def extract_headings(dom):
    print_message("extracting headings....")

    headings = ['h1','h2','h3','h4','h5','h6']
    for heading in headings:
        all_headings = dom.findAll(heading)
        for h in all_headings:
            print_entity(h)

def extract_links(dom):
    print_message("extracting links.......")

    links = dom.findAll('a')
    for link in links:
        print_entity(link)

def extract_content_for_page(html_file):
    f = open(html_file)
    content = f.read()
    f.close()

    dom = BeautifulSoup(content)
    extract_title(dom)
    extract_headings(dom)
    extract_links(dom)


if __name__ == "__main__":
    extract_content_for_page(sys.argv[1])
