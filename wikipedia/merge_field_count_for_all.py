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

if __name__ == "__main__":
    file_name = sys.argv[1]
    f = codecs.open(file_name, encoding='utf-8')

    c = 3
    n = int(f.readline().strip())
    r = build_count_map(f.readline().strip())
    for l in f:
        l = l.strip()
        if c % 2 != 0:
            n += int(l)
        else:
            r = merge_counts(r, build_count_map(l))
        c += 1
    f.close()

    r = top50(r)
    s = " ".join(["%s:%s" % (k,v) for (k,v) in r])

    o = open(sys.argv[2],"w")
    o.write(str(n))
    o.write("\n")
    o.write(s)
    
    o.close()
