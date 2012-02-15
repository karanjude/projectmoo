import sys
import BeautifulSoup
import cStringIO
import re
import pp
from itertools import *
import os

def add_text(node, output):
    if node is None:
        return

    s = node.strip()
    if len(s) > 0:
        s = re.escape(s.encode('utf8','ignore'))
        output.write(s)
        output.write(" ")

def get_html_text(dom, output):
    if dom is None:
        return

    if dom.name == "script" or dom.name == "style":
        return

    for child in dom:
        if type(child) is BeautifulSoup.NavigableString: 
            add_text(child, output)
        elif type(child) is BeautifulSoup.Tag:
            get_html_text(child, output)

def dump_text(data, o):
    html_dom = BeautifulSoup.BeautifulSoup(data)
    get_html_text(html_dom, o)
    del html_dom

def process_html_file(file_name, out_put_file_name):
    f = open(file_name.strip())
    data = f.read()
    f.close()

    o = open(out_put_file_name, "a")
    dump_text(data, o)
    o.close()
    f.close()
    
if __name__ == "__main__":
    file_name = sys.argv[1]
    f = open(file_name)    

    #output_file_name = sys.argv[2]
    
    ppservers = ()
    job_server = pp.Server(ppservers = ppservers)
    jobs =  []

    n = job_server.get_ncpus()
    full_path = os.path.abspath(sys.argv[1])
    dir_name = os.path.dirname(full_path)

    files = f.readlines()
    outputs = range(1,n+1)
    outputs = ["f" + str(i) for i in outputs]
    
    for file_to_process,output_file_name in zip(files[1:],cycle(outputs)):
        file_to_process = file_to_process.strip()
        file_to_process = os.path.abspath(os.path.join(dir_name, file_to_process))
        file_to_process = file_to_process.strip()

        jobs.append((file_to_process, job_server.submit(
                    process_html_file, 
                    (file_to_process,output_file_name),
                    (
                        dump_text,
                        add_text,
                        get_html_text
                        ),
                    (
                        "sys",
                        "BeautifulSoup",
                        "cStringIO",
                        "re",
                        "pp",
                        "itertools",
                        "os",
                        ),
                    )))

    c = 1 
    total = len(files) - 1
    for file_to_process, job in jobs:
        print "processing job ", c , " of ", total, file_to_process , job()
        c += 1
 
    f.close()

