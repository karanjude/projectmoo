import sys
import codecs

def build_count_map(l1):
    parts = l1.split()
    x = [(x.split(':')) for x in parts]
    r = {}
    for xx in x:
        try:
            k = xx[0]
            k = k.replace("u'","")
            k = k.replace("'","")
            if k.startswith("&#") or k.startswith("\\u") or k.startswith("\\x") or k == "|":
                continue
            v = int(xx[1])
            r[k] = v
        except:
            pass
    return r

def merge_counts(r1, r2):
    for k,v in r2.iteritems():
        if k not in r1:
            r1[k] = r2[k]
        else:
            r1[k] += r2[k]
    return r1

def top50(r):
    l = [(k,int(v)) for k,v in r.iteritems()]
    l = sorted(l, key = lambda x: x[1], reverse = True)
    return l[0:50]

def build_data_for_file(r , file_name):
    f = open(file_name)
    l = f.readlines()

    if len(l) < 2:
        f.close()
        return 0
    
    l1 = int(l[0].strip())
    l2 = l[1].strip()
    r1 = build_count_map(l2)
    merge_counts(r,r1)
    f.close()

    return l1
        

if __name__ == "__main__":
    file_name = sys.argv[1]


    r = {}
    n = 0
    for i in range(1,len(sys.argv)):
        n += build_data_for_file(r , sys.argv[i])

    r = top50(r)
    s = " ".join(["%s:%s" % (k,v) for (k,v) in r])

    print n
    print s
