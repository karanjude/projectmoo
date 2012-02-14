#!/usr/bin/env python
#-*- coding:utf-8 -*-

import re
import sys
from bs4 import *
import os
import MySQLdb
import cgi
import traceback

TITLE = 1
HEADING = 2
LINK = 4

file_index = open("data/file_index.txt","a")
t_f = open("data/title.txt","a")
h1_f = open("data/h1.txt","a")
h2_f = open("data/h2.txt","a")
h3_f = open("data/h3.txt","a")
h4_f = open("data/h4.txt","a")
h5_f = open("data/h5.txt","a")
h6_f = open("data/h6.txt","a")
a_f = open("data/a.txt","a")

file_map = {}
file_map["title"] = t_f
file_map["h1"] = h1_f
file_map["h2"] = h2_f
file_map["h3"] = h3_f
file_map["h4"] = h4_f
file_map["h5"] = h5_f
file_map["h6"] = h6_f
file_map["a"] = a_f

def print_entity(f_h, entity):
    if entity.string is None:
        return

    s = re.escape(entity.string.encode('utf8','ignore'))
    f_h.write(s)
    f_h.write("\n")

def print_file_entity(f_h, entity):
    f_h.write(entity)
    f_h.write("\n")

def dump_entity(f_h,entity):
    if entity.string is None:
        return


    ss = re.escape(file_path.encode('utf8','ignore'))

    sql = """INSERT INTO WIKIPAEDIA_CONTENT(local_url, field_type, field_content) VALUES('%s',%s,'%s')""" % (ss, type, s)
    print_message(sql)
    c.execute(sql)
        

def extract_title(html_file, dom):
    titles = dom.findAll('title')
    for title in titles:
        print_entity(file_map['title'],title)

def extract_headings(html_file, dom):
    headings = ['h1','h2','h3','h4','h5','h6']
    for heading in headings:
        all_headings = dom.findAll(heading)
        for h in all_headings:
            print_entity(file_map[heading],h)

def extract_links(html_file, dom):
    links = dom.findAll('a')
    for link in links:
        print_entity(file_map['a'],link)

def extract_content_for_page(html_file):
    f = open(html_file)
    content = f.read()
    f.close()

    dom = BeautifulSoup(content)
    extract_title(html_file, dom)
    extract_headings(html_file, dom)
    extract_links(html_file, dom)
    del dom

if __name__ == "__main__":
    f = open("data/error.log","a")
    
    full_path = os.path.abspath(sys.argv[1])
    input = open(full_path)
    dir_name = os.path.dirname(full_path)

    for html_file_path in input:
        html_file_path = html_file_path.strip()
        try:
            html_file_path = os.path.abspath(os.path.join(dir_name, html_file_path))
            print_file_entity(file_index, html_file_path)
            extract_content_for_page(html_file_path)
        except Exception, e:
            f.write("ERROR IN FILE:" + html_file_path)
            f.write("\n")
            f.write(str(e))
            traceback.print_exc(file=f)
            f.write("\n\n")

    f.close()

    for k,v in file_map.iteritems():
        file_map[k].close()
        
    
