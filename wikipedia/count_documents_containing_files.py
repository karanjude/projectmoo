import sys

count = {}
count['h1'] = 0
count['h2'] = 0
count['h3'] = 0
count['h4'] = 0
count['h5'] = 0
count['h6'] = 0
count['a'] = 0
count['title'] = 0

def process_tag_count(l):
    l = l.strip()
    parts = l.split()
    if len(parts) < 1:
        return

    parts = parts[1:]
    for item in parts:
        k,v = item.split(':')
        v = int(v)
        if k in count:
            count[k] += 1

    #print " ".join(["%s:%s" % (k,v) for k,v in count.iteritems()])



if __name__ == "__main__":
    file_name = sys.argv[1]
    f = open(file_name)
    
    for l in f:
        process_tag_count(l)
    f.close()

    print " ".join(["%s:%s" % (k,v) for k,v in count.iteritems()])
