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


if __name__ == "__main__":
    file_name = sys.argv[1]
    output = sys.argv[2]

    f = open(file_name)
    c = 0
    for l in f:
        process_token_count(l)
        c += 1
        if c % 10 == 0:
            print c
    f.close()

    x = [(k,v) for k,v in count.iteritems()]
    n = 0
    for i in x:
        n += i[1]
    del count
    
    x = sorted(x, key=lambda y: y[1], reverse=True)

    o = open(output, "w")
    o.write(str(n))
    o.write("\n")
    
    x = x[0:50]
    s = " ".join(["%s:%s" % (y[0],y[1]) for y in x])

    print s

    o.write(s)
    o.close()
