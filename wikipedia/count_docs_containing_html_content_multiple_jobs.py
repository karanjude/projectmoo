import sys
import BeautifulSoup
import cStringIO
import re
import pp
from itertools import *
import os
import traceback

e = open("error.txt", "a")

def get_html_text(dom, result):
    if dom is None:
        return

    if dom.name == "script" or dom.name == "style":
        return

    if dom.name not in result:
        result[dom.name] = 0
    
    result[dom.name] += 1
    

    for child in dom:
        if type(child) is BeautifulSoup.Tag:
            get_html_text(child, result)


def dump_text(data, o):
    html_dom = BeautifulSoup.BeautifulSoup(data)
    r = {}
    get_html_text(html_dom, r)
    del html_dom
    return r

def process_html_file(file_name, out_put_file_name):
    f = open(file_name.strip())
    data = f.read()
    f.close()

    o = open(out_put_file_name, "a")
    r = dump_text(data, o)
    rr = " ".join(["%s:%d" % (k,v) for k,v in r.iteritems()])
    #file_name = re.escape(file_name.encode('utf8','ignore'))
    try:
        o.write(file_name)
        o.write(" ")
        o.write(rr)
        o.write("\n")
    except Exception, e:
        e.write("ERROR IN FILE:" + html_file_path)
        e.write("\n")
        e.write(str(e))
        eraceback.print_exc(file=f)
        e.write("\n\n")
    o.close()
    f.close()
    
if __name__ == "__main__":
    file_name = sys.argv[1]
    f = open(file_name)    

    output_file_name = sys.argv[2]
    
    ppservers = ()
    job_server = pp.Server(ppservers = ppservers)
    jobs =  []

    n = job_server.get_ncpus()
    full_path = os.path.abspath(sys.argv[1])
    dir_name = os.path.dirname(full_path)

    files = f.readlines()
    outputs = range(1,n+1)
    outputs = ["f" + str(i) for i in outputs]
    
    for file_to_process in files[1:]:
        file_to_process = file_to_process.strip()
        file_to_process = os.path.abspath(os.path.join(dir_name, file_to_process))
        file_to_process = file_to_process.strip()

        jobs.append((file_to_process, job_server.submit(
                    process_html_file, 
                    (file_to_process,output_file_name),
                    (
                        dump_text,
                        get_html_text,
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
    e.close()

