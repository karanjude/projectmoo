import sys
import BeautifulSoup
import cStringIO
from BeautifulSoup import *
import re

def add_text(node, output):
    if node is None:
        return

    s = node.strip()
    if len(s) > 0:
        s = re.escape(s.encode('utf8','ignore'))
        print s
        output.write(s)
        output.write(" ")

def get_html_text(dom, output):
    if dom is None:
        return

    if dom.name == "script" or dom.name == "style":
        return

    for child in dom:
        if type(child) is NavigableString: 
            add_text(child, output)
        elif type(child) is Tag:
            get_html_text(child, output)

def dump_text(data, o):
    html_dom = BeautifulSoup(data)
    get_html_text(html_dom, o)
    del html_dom

if __name__ == "__main__":
    file_name = sys.argv[1]
    output_file_name = sys.argv[2]
    f = open(file_name)
    data = f.read()
    f.close()
    o = open(output_file_name, "w")
    dump_text(data, o)
    o.close()
