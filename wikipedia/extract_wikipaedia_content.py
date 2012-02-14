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

def print_entity(entity):
    if entity.string is not None:
        print  entity.string

def print_message(message):
    print 
    print  message
    print 


def dump_entity(file_path, entity, type):
    if entity.string is None:
        return

    s = re.escape(entity.string.encode('utf8','ignore'))
    ss = re.escape(file_path.encode('utf8','ignore'))

    sql = """INSERT INTO WIKIPAEDIA_CONTENT(local_url, field_type, field_content) VALUES('%s',%s,'%s')""" % (ss, type, s)
    print_message(sql)
    c.execute(sql)
        

def extract_title(html_file, dom, c):
    print_message("extracting title....")
 
    titles = dom.findAll('title')
    for title in titles:
        print_entity(title)
        dump_entity(html_file, title, TITLE)
        

def extract_headings(html_file, dom, c):
    print_message("extracting headings....")

    headings = ['h1','h2','h3','h4','h5','h6']
    for heading in headings:
        all_headings = dom.findAll(heading)
        for h in all_headings:
            print_entity(h)
            dump_entity(html_file, h, HEADING)

def extract_links(html_file, dom, c):
    print_message("extracting links.......")

    links = dom.findAll('a')
    for link in links:
        print_entity(link)
        dump_entity(html_file, link, LINK)

def extract_content_for_page(html_file, c):
    f = open(html_file)
    content = f.read()
    f.close()

    dom = BeautifulSoup(content)
    extract_title(html_file, dom, c)
    extract_headings(html_file, dom, c)
    extract_links(html_file, dom, c)
    del dom

if __name__ == "__main__":
    f = open("error.log","a")
    db = MySQLdb.connect(user="root", db="wikipaedia", passwd="", charset = "utf8")
    c = db.cursor()
    
    full_path = os.path.abspath(sys.argv[1])
    input = open(full_path)
    dir_name = os.path.dirname(full_path)

    for html_file_path in input:
        html_file_path = html_file_path.strip()
        try:
            html_file_path = os.path.abspath(os.path.join(dir_name, html_file_path))
            print_message(html_file_path)
            extract_content_for_page(html_file_path, c)
            db.commit()
        except Exception, e:
            f.write("ERROR IN FILE:" + html_file_path)
            f.write("\n")
            f.write(str(e))
            traceback.print_exc(file=f)
            f.write("\n\n")

    c.close()
    db.close()
    f.close()
        
    
