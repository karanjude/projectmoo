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
        print >> sys.stderr, entity.string

def print_message(message):
    print >> sys.stderr
    print >> sys.stderr, message
    print >> sys.stderr

def dump_entity(file_path, entity, type):
    if entity.string is None:
        return

    s = re.escape(unicode(entity.string).encode('utf8','ignore'))

    sql = """INSERT INTO WIKIPAEDIA_CONTENT(local_url, field_type, field_content) VALUES('%s',%s,'%s')""" % (file_path, type, s)
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


if __name__ == "__main__":
    f = open("error.log","a")
    try:
        full_path = os.path.abspath(sys.argv[1])
        print_message(full_path)
        db = MySQLdb.connect(user="root", db="wikipaedia", passwd="", charset = "utf8")
        c = db.cursor()
        extract_content_for_page(full_path, c)
        db.commit()
        c.close()
        db.close()
    except Exception, e:
        f.write(str(e))
        traceback.print_exc(file=f)
        f.write("\n\n")
        
    f.close()
        
    
