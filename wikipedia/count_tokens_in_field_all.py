import sys
import re
import urllib2
import HTMLParser

count = {}

def denormalize(s):
    s = re.sub(r'(?<!\\)\\', '', s)
    s = urllib2.unquote(s)
    s = s.decode('utf8','ignore')
    s = re.sub('[(),!\'\.\$\/\[\]\^\"]','',s)
    s = re.sub('\-', ' ',s)
    s = re.sub('\s+',' ',s)
    return s

def tokenize(l):
    l = l.strip()
    l = denormalize(l)
    l = l.lower()
    return l.split()

def process_token_count(l):
    tokens = tokenize(l)

    for token in tokens:
        if token not in count:
            count[token] = 0

        count[token] += 1

def dump_count(o):
    x = [(k,v) for k,v in count.iteritems()]
    n = 0
    for i in x:
        n += i[1]

    x = sorted(x, key = lambda y: y[1] , reverse = True)
    x = x[0:50]

    o.write(str(n))
    o.write("\n")
    s = " ".join(["%s:%s" % (repr(y[0]),repr(y[1])) for y in x])
    o.write(s)
    o.write("\n")
    o.flush()

if __name__ == "__main__":
    file_name = sys.argv[1]
    output = sys.argv[2]

    f = open(file_name)
    o = open(output, "w")

    c = 0
    for l in f:
        process_token_count(l)
        c += 1
        if c % 10 == 0:
            dump_count(o)
            count = {}
            print c
    f.close()

    dump_count(o)

    o.close()
